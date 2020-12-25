package com.company;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements IFigure {
    private RealPoint o;
    private int n;
    private double radius;
    List<RealPoint> markers = new ArrayList<>();

    Polygon(RealPoint o, double radius, int n) {
        this.o = o;
        this.n = n;
        this.radius = radius;
        points(o, radius);
    }

    @Override
    public void setRadius(Double r) {
        this.radius = r;
        points(o, r);
    }

    @Override
    public RealPoint getCenter() {
        return o;
    }

    @Override
    public void transfer(RealPoint newO) {
        this.o = newO;
        points(newO, radius);
    }

    private void points(RealPoint center, Double radius) {

        markers.clear();
        double da = 2 * Math.PI / n;
        for (int i = 0; i < n; i++) {
            double dx1 = radius * Math.cos(da * i) + center.getX();
            double dy1 = radius * Math.sin(da * i) + center.getY();
            markers.add(new RealPoint(dx1, dy1));
        }
    }

    @Override
    public boolean checkIfClicked(RealPoint rp) {
        double x = rp.getX();
        double y = rp.getY();
        return x >= o.getX() - radius && x <= o.getX() + radius && y >= o.getY() - radius && y <= o.getY() + radius; //true, если клик внутри фигуры
    }

    @Override
    public List<RealPoint> getMarkers() {
        List<RealPoint> points = new ArrayList<>(this.markers);
        return points;
    }
}