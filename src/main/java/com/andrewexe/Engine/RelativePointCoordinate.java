package com.andrewexe.Engine;

import javafx.geometry.Point2D;


/**
 * Класс, который представляет относительные координаты точки в игровом окне.
 * Используется для хранения относительных координат и абсолютных координат точки.
 */
public class RelativePointCoordinate {
    private int x;
    private int y;
    // X, Y выступают как относительные координаты,
    // в то время как coordinates представляет абсолютные координаты в окне
    private Point2D coordinates;

    public RelativePointCoordinate(int x, int y, Point2D coordinates) {
        this.x = x;
        this.y = y;
        this.coordinates = coordinates;
    }

    public RelativePointCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.coordinates = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    public double getAbsX() {
        return coordinates.getX();
    }

    public double getAbsY() {
        return coordinates.getY();
    }
}
