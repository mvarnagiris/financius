package com.code44.finance.graphs.line;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmoothPathMaker implements PathMaker {
    @Override
    public Path makePath(List<PointF> points) {
        final Path path = new Path();

        final List<List<PointF>> continuousCurves = prepareListOfContinuousCurves(points);
        for (List<PointF> curve : continuousCurves) {
            addCurveToPath(path, curve);
        }

        return path;
    }

    private void addCurveToPath(Path outPath, List<PointF> curve) {
        List<Pair<PointF, PointF>> bezierControlPoints = Collections.emptyList();
        if (curve.size() > 1) {
            bezierControlPoints = getBezierControlPoints(curve);
        }

        for (int i = 0, size = curve.size(); i < size; i++) {
            final PointF point = curve.get(i);
            if (i == 0) {
                outPath.moveTo(point.x, point.y);
            } else {
                final Pair<PointF, PointF> bezierControlPointPair = bezierControlPoints.get(i - 1);
                outPath.cubicTo(bezierControlPointPair.first.x, bezierControlPointPair.first.y, bezierControlPointPair.second.x, bezierControlPointPair.second.y, point.x, point.y);
            }
        }
    }

    private List<List<PointF>> prepareListOfContinuousCurves(List<PointF> points) {
        final List<List<PointF>> continuousCurves = new ArrayList<>();

        List<PointF> curve = null;
        for (PointF point : points) {
            if (point == null) {
                if (curve != null) {
                    continuousCurves.add(curve);
                }
                curve = null;
            } else {
                if (curve == null) {
                    curve = new ArrayList<>();
                }
                curve.add(point);
            }
        }
        if (curve != null) {
            continuousCurves.add(curve);
        }

        return continuousCurves;
    }

    private List<Pair<PointF, PointF>> getBezierControlPoints(List<PointF> knots) {
        if (knots == null) {
            throw new NullPointerException("Knots cannot be null.");
        }

        int controlPointsSize = knots.size() - 1;
        if (controlPointsSize < 1) {
            throw new IllegalArgumentException("At least two knot points required.");
        }

        // Special case: Bezier curve should be a straight line.
        if (controlPointsSize == 1) {
            final PointF firstControlPoint = new PointF((2 * knots.get(0).x + knots.get(1).x) / 3, (2 * knots.get(0).y + knots.get(1).y) / 3);
            final PointF secondControlPoint = new PointF(2 * firstControlPoint.x - knots.get(0).x, 2 * firstControlPoint.y - knots.get(0).y);

            final List<Pair<PointF, PointF>> bezierControlPoints = new ArrayList<>();
            bezierControlPoints.add(Pair.create(firstControlPoint, secondControlPoint));
            return bezierControlPoints;
        }

        // Calculate first Bezier control points
        // Right hand side vector
        float[] rhs = new float[controlPointsSize];

        // Set right hand side X values
        for (int i = 1; i < controlPointsSize - 1; ++i) {
            rhs[i] = 4 * knots.get(i).x + 2 * knots.get(i + 1).x;
        }
        rhs[0] = knots.get(0).x + 2 * knots.get(1).x;
        rhs[controlPointsSize - 1] = (8 * knots.get(controlPointsSize - 1).x + knots.get(controlPointsSize).x) / 2.0f;
        // Get first control points X-values
        float[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < controlPointsSize - 1; ++i) {
            rhs[i] = 4 * knots.get(i).y + 2 * knots.get(i + 1).y;
        }
        rhs[0] = knots.get(0).y + 2 * knots.get(1).y;
        rhs[controlPointsSize - 1] = (8 * knots.get(controlPointsSize - 1).y + knots.get(controlPointsSize).y) / 2.0f;
        // Get first control points Y-values
        float[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        //firstControlPoints = new PointF[n];
        //secondControlPoints = new PointF[n];
        final List<Pair<PointF, PointF>> bezierControlPoints = new ArrayList<>();
        for (int i = 0; i < controlPointsSize; ++i) {
            // First control point
            final PointF firstControlPoint = new PointF(x[i], y[i]);

            // Second control point
            final PointF secondControlPoint;
            if (i < controlPointsSize - 1) {
                secondControlPoint = new PointF(2 * knots.get(i + 1).x - x[i + 1], 2 * knots.get(i + 1).y - y[i + 1]);
            } else {
                secondControlPoint = new PointF((knots.get(controlPointsSize).x + x[controlPointsSize - 1]) / 2, (knots.get(controlPointsSize).y + y[controlPointsSize - 1]) / 2);
            }

            bezierControlPoints.add(Pair.create(firstControlPoint, secondControlPoint));
        }

        return bezierControlPoints;
    }

    private float[] getFirstControlPoints(float[] rhs) {
        int n = rhs.length;
        float[] x = new float[n]; // Solution vector.
        float[] tmp = new float[n]; // Temp workspace.

        float b = 2.0f;
        x[0] = rhs[0] / b;
        for (int i = 1; i < n; i++) // Decomposition and forward substitution.
        {
            tmp[i] = 1 / b;
            b = (i < n - 1 ? 4.0f : 3.5f) - tmp[i];
            x[i] = (rhs[i] - x[i - 1]) / b;
        }
        for (int i = 1; i < n; i++) {
            x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
        }

        return x;
    }
}
