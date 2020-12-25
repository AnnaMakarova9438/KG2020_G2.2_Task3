package com.company;

import java.awt.*;
import java.util.List;

public class DrawPolygon implements PolygonDrawer {
    private LineDrawer ld;
    private ScreenConverter sc;

    public DrawPolygon(LineDrawer ld, ScreenConverter sc) {
        this.ld = ld;
        this.sc = sc;
    }

    @Override
    public void drawPolygon(IFigure f, Color c) {
        List<RealPoint> realPoints = f.getMarkers();
        int n = realPoints.size();
        for (int i = 0; i < n - 1; i++) {
            ld.drawLine(sc.r2s(realPoints.get(i)), sc.r2s(realPoints.get(i + 1)), c);
        }
        ld.drawLine(sc.r2s(realPoints.get(n - 1)), sc.r2s(realPoints.get(0)), c);
    }
}