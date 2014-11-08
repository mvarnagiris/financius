package com.code44.finance.graphs.line;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

public class SharpPathMaker implements PathMaker {
    @Override public Path makePath(List<PointF> points) {
        final Path path = new Path();

        for (int i = 0, size = points != null ? points.size() : 0; i < size; i++) {
            final PointF point = points.get(i);
            if (point == null) {
                continue;
            }

            if (i == 0 || points.get(i - 1) == null) {
                path.moveTo(point.x, point.y);
            } else {
                path.lineTo(point.x, point.y);
            }
        }

        return path;
    }
}
