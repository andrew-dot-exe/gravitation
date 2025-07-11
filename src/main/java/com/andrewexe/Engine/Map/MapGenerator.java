package com.andrewexe.Engine.Map;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class MapGenerator {
    // Параметры генерации карты
    public static final int MAX_HEIGHT = 80; // максимальная высота (ось Y)
    public static final int SEGMENT_LENGTH = 10; // длина одного сегмента по X
    public static final int MAP_LENGTH = 60; // количество сегментов
    public static final int MIN_HEIGHT = 2; // минимальная высота

    public static ArrayList<Line> generateMap(){
        ArrayList<Line> mapLines = new ArrayList<>();
                double x = 0;
                double y = (Math.random() * (MAX_HEIGHT-  MIN_HEIGHT)) + MIN_HEIGHT; // начальная высота в диапазоне MIN_HEIGHT..MAX_HEIGHT

                for (int i = 0; i < MAP_LENGTH; i++) {
                    double nextY = y + (Math.random() - 0.5) * 6; // случайное изменение высоты
                    nextY = Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, nextY)); // ограничение по MIN_HEIGHT и MAX_HEIGHT

                    Line segment = new Line(x, y, x + SEGMENT_LENGTH, nextY);
                    segment.setStroke(Color.BLACK);
                    mapLines.add(segment);

                    x += SEGMENT_LENGTH;
                    y = nextY;
                }

                return mapLines;
    }

}
