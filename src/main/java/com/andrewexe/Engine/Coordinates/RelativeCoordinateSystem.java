package com.andrewexe.Engine.Coordinates;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс, который представляет собой систему координат для работы с линиями и точками.
 * Предназначен для создания относительной системы координат.
 */
public class RelativeCoordinateSystem {

    public static List<Point2D> findGridIntersections(List<Line> lines, double cellSize) {
        List<Point2D> intersections = new ArrayList<>();

        // Проверяем все пары линий
        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                findIntersection(lines.get(i), lines.get(j))
                        .map(pt -> snapToGrid(pt, cellSize))
                        .ifPresent(intersections::add);
            }
        }

        return intersections;
    }

    private static Point2D snapToGrid(Point2D point, double cellSize) {
        double gridX = Math.round(point.getX() / cellSize) * cellSize;
        double gridY = Math.round(point.getY() / cellSize) * cellSize;
        return new Point2D(gridX, gridY);
    }

    private static Optional<Point2D> findIntersection(Line line1, Line line2) {
        double x1 = line1.getStartX(), y1 = line1.getStartY();
        double x2 = line1.getEndX(), y2 = line1.getEndY();
        double x3 = line2.getStartX(), y3 = line2.getStartY();
        double x4 = line2.getEndX(), y4 = line2.getEndY();

        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

        if (denom == 0) return Optional.empty();

        double u1 = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double u2 = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        if (u1 >= 0 && u1 <= 1 && u2 >= 0 && u2 <= 1) {
            double x = x1 + u1 * (x2 - x1);
            double y = y1 + u1 * (y2 - y1);
            return Optional.of(new Point2D(x, y));
        }

        return Optional.empty();
    }
}
