package com.code44.finance.graphs.line;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

public interface PathMaker {
    public Path makePath(List<PointF> points);
}
