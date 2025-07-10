package com.andrewexe.Engine.Map;

import com.andrewexe.Engine.Coordinates.RelativePoint;
import com.andrewexe.Engine.Primitives.RelativeVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MapLevel {

    private final ArrayList<RelativeVector> vectors;

    public MapLevel() {
        this.vectors = new ArrayList<>();
    }

    public void loadMap(ArrayList<RelativeVector> vectors) {
        this.vectors.clear();
        this.vectors.addAll(vectors);
    }

    public ArrayList<RelativeVector> getVectors() {
        return vectors;
    }

    /**
     * Сохраняет уровень в текстовый файл, где каждая строка — x1 y1 x2 y2
     */
    public void saveToFile(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            for (RelativeVector v : vectors) {
                int x1 = (int) v.getStartX();
                int y1 = (int) v.getStartY();
                int x2 = (int) v.getEndX();
                int y2 = (int) v.getEndY();
                writer.write(x1 + " " + y1 + " " + x2 + " " + y2 + "\n");
            }
        }
    }

    /**
     * Загружает уровень из текстового файла, где каждая строка — x1 y1 x2 y2
     */
    public void loadFromFile(String filename) throws IOException {
        vectors.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 4) continue;
                int x1 = Integer.parseInt(parts[0]);
                int y1 = Integer.parseInt(parts[1]);
                int x2 = Integer.parseInt(parts[2]);
                int y2 = Integer.parseInt(parts[3]);
                RelativePoint start = new RelativePoint(x1, y1, null);
                RelativePoint end = new RelativePoint(x2, y2, null);
                vectors.add(new RelativeVector(start, end));
            }
        }
    }

}
