package com.andrewexe.Engine.Map;

import com.andrewexe.Engine.Coordinates.Point;

import java.util.ArrayList;
import java.util.List;

public class MapGenerator {
    // Параметры генерации карты
    private final int MAX_HEIGHT = 20; // максимальная высота (ось Y)
    private final int SEGMENT_LENGTH = 3; // длина одного сегмента по X
    private final int MAP_LENGTH = 60; // количество сегментов
    private final int MIN_HEIGHT = 2; // минимальная высота

    /**
     * Генерирует карту в стиле Gravity Defied как массив Point
     */
    public List<Point> generateGravityDefiedPoints() {
        List<Point> points = new ArrayList<>();
        java.util.Random random = new java.util.Random();
        int prevY = MAX_HEIGHT / 2;
        Point prevPoint = new Point(0, prevY);
        points.add(prevPoint);
        for (int i = 1; i <= MAP_LENGTH; i++) {
            int deltaY = random.nextInt(7) - 3; // [-3,3] плавные перепады
            if (random.nextDouble() < 0.1) {
                deltaY += random.nextBoolean() ? random.nextInt(6) : -random.nextInt(6); // иногда резкие трамплины
            }
            int newY = Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, prevY + deltaY));
            Point nextPoint = new Point(i * SEGMENT_LENGTH, newY);
            points.add(nextPoint);
            prevPoint = nextPoint;
            prevY = newY;
        }
        return points;
    }
}
