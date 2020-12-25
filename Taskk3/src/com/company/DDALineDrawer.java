package com.company;

import java.awt.*;

public class DDALineDrawer implements LineDrawer {
    private PixelDrawer pd;

    public DDALineDrawer(PixelDrawer pd) {
        this.pd = pd;
    }

    @Override
    public void drawLine(ScreenPoint p1, ScreenPoint p2, Color c) {
        int x1 = p1.getX(), y1 = p1.getY();
        int x2 = p2.getX(), y2 = p2.getY();
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (x1 > x2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
                y1 = y2;
            }
            double k = dy / dx;
            for (int j = x1; j <= x2; j++) {
                double i = k * (j - x1) + y1;
                pd.setPixel(j, (int) i, c);
            }
        } else {
            if (y1 > y2) {
                int tmp = y1;
                y1 = y2;
                y2 = tmp;
                x1 = x2;
            }
            double kObrat = dx / dy;
            for (int i = y1; i <= y2; i++) {
                double j = kObrat * (i - y1) + x1;
                pd.setPixel((int) j, i, c);
            }
        }
    }
}
