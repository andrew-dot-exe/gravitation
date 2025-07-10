package com.andrewexe.Engine.Coordinates;

/*
    * Класс, который представляет относительные координаты точки.
    * Используется для хранения относительных координат точки.
    * Этот класс нужен только для  хранения, классом для отрисовки является RelativePoint.
 */
public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

}
