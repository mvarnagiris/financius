package com.code44.finance.graphs.line;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

public class SmoothPathMaker implements PathMaker {
    public static void getCurveControlPoints(PointF[] knots, PointF[] firstControlPoints, PointF[] secondControlPoints) {
        if (knots == null) {
            throw new NullPointerException("knots");
        }

        int n = knots.length - 1;
        if (n < 1) {
            throw new IllegalArgumentException("At least two knot points required - knots");
        }

        if (n == 1) { // Special case: Bezier curve should be a straight line.
            //firstControlPoints = new PointF[1];
            // 3P1 = 2P0 + P3
            firstControlPoints[0].x = (2 * knots[0].x + knots[1].x) / 3;
            firstControlPoints[0].y = (2 * knots[0].y + knots[1].y) / 3;

            //secondControlPoints = new PointF[1];
            // P2 = 2P1 – P0
            secondControlPoints[0].x = 2 * firstControlPoints[0].x - knots[0].x;
            secondControlPoints[0].y = 2 * firstControlPoints[0].y - knots[0].y;
            return;
        }

        // Calculate first Bezier control points
        // Right hand side vector
        float[] rhs = new float[n];

        // Set right hand side X values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * knots[i].x + 2 * knots[i + 1].x;
        }
        rhs[0] = knots[0].x + 2 * knots[1].x;
        rhs[n - 1] = (8 * knots[n - 1].x + knots[n].x) / 2.0f;
        // Get first control points X-values
        float[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * knots[i].y + 2 * knots[i + 1].y;
        }
        rhs[0] = knots[0].y + 2 * knots[1].y;
        rhs[n - 1] = (8 * knots[n - 1].y + knots[n].y) / 2.0f;
        // Get first control points Y-values
        float[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        //firstControlPoints = new PointF[n];
        //secondControlPoints = new PointF[n];
        for (int i = 0; i < n; ++i) {
            // First control point
            firstControlPoints[i] = new PointF(x[i], y[i]);
            // Second control point
            if (i < n - 1) {
                secondControlPoints[i] = new PointF(2 * knots
                        [i + 1].x - x[i + 1], 2 *
                        knots[i + 1].y - y[i + 1]);
            } else {
                secondControlPoints[i] = new PointF((knots
                        [n].x + x[n - 1]) / 2,
                        (knots[n].y + y[n - 1]) / 2
                );
            }
        }
    }

    private static float[] getFirstControlPoints(float[] rhs) {
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

    @Override
    public Path makePath(List<PointF> points) {
        final Path path = new Path();

        PointF[] knots = points.toArray(new PointF[points.size()]);
        PointF[] firstControlPoints = new PointF[points.size()];
        PointF[] secondControlPoints = new PointF[points.size()];
        getCurveControlPoints(knots, firstControlPoints, secondControlPoints);

        for (int i = 0, size = points != null ? points.size() : 0; i < size; i++) {
            final PointF point = points.get(i);
            if (point == null) {
                continue;
            }

            if (i == 0 || points.get(i - 1) == null) {
                path.moveTo(point.x, point.y);
            } else {
                path.cubicTo(firstControlPoints[i - 1].x, firstControlPoints[i - 1].y, secondControlPoints[i - 1].x, secondControlPoints[i - 1].y, point.x, point.y);
            }
        }

        return path;
    }
}
