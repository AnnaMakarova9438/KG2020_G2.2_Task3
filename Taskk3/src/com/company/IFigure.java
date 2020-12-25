package com.company;

import java.util.List;

public interface IFigure {
    List<RealPoint> getMarkers();
    boolean checkIfClicked(RealPoint rp);
    void setRadius(Double r);
    void transfer(RealPoint to);
    RealPoint getCenter();
}