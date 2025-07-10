package com.andrewexe.Engine;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Renderer {
    /**
     * Класс, который отвечает за отрисовку уровня и игрока.
     */
    private final int FPS = 60;
    private final int STEREO_OFFSET = 2;
    public static final int ROWS = 30;
    private double cellSize = 0; // Размер ячейки сетки, будет вычислен при инициализации
    private int width;
    private int height;
    private final Color GRID_COLOR = Color.rgb(120, 200, 240);
    private List<Point2D> gridIntersections;

    private Group grids;
    private Random rnd;

    public Pane getRoot() {
        return root;
    }

    // JavaFX
    private final Pane root;

    private Renderable player;
    private Renderable level;

    public Renderer(int width, int height) {
        this.root = new Pane();
        this.rnd = new Random();
        this.grids = new Group();
        this.width = width;
        this.height = height;
        this.gridIntersections = new ArrayList<>();
        this.grids.setCache(true);
        this.grids.setAutoSizeChildren(true);
        getGrids(width, height);

    }


    /**
     * Обновление игрового поля каждый кадр
     */
    public void update(double deltaTime) {
    }

    /**
     * Рисует клеточки для работы с масштабом и относительными координатами
     */
    private void getGrids(int width, int height) {
        int cellHeight = (int) height / ROWS;

        System.out.println(height);
        for (int i = 0; i < width; i += cellHeight) { // x
            Line line = new Line();
            line.setStartX(i);
            line.setStartY(0);
            line.setEndX(i);
            line.setEndY(this.height);
            line.setStroke(GRID_COLOR);
            this.grids.getChildren().add(line);
        }
        for (int j = 0; j < height; j += cellHeight) {
            Line line = new Line();
            line.setStartX(0);
            line.setStartY(j);
            line.setEndX(this.width);
            line.setEndY(j);
            line.setStroke(GRID_COLOR);
            this.grids.getChildren().add(line);
        }
        this.root.getChildren().add(grids); // отрисовка сетки
        getGridIntersections();
        RelativePointCoordinate vec_start = getRelativePoint(3, 2); // Начало вектора
        RelativePointCoordinate vec_end = getRelativePoint(6, 3); // Конец вектора
        drawStereoVector(vec_start, vec_end);
        //drawDots();
    }


    /**
     * Метод для получения точек пересечения линий сетки.
     * Важен для работы относительных координат.
     */
    private void getGridIntersections() {
        List<Line> lines = getGridList();
        this.cellSize = (double) height / ROWS; // Размер ячейки сетки
        // Отличается от такого же в методе drawGrids тем
        // что дает точное значение, а не округленное
        gridIntersections = RelativeCoordinateSystem.findGridIntersections(lines, cellSize);
    }

    /**
     * Отрисовка точек пересечения сетки.
     * Используется для отладки и визуализации относительных координат.
     */
    private void drawDots() {
        for (Point2D pt : gridIntersections) {
            drawIntersectionCircle(pt);
        }
    }

    /**
     * Получить список линий сетки.
     * Используется для получения точек пересечения.
     */
    private List<Line> getGridList() {
        List<Line> lines = new ArrayList<>();
        for (var node : grids.getChildren()) {
            if (node instanceof Line) {
                lines.add((Line) node);
            }
        }
        return lines;
    }

    /**
     * Нарисовать ОДНУ точку пересечения сетки.
     * Используется для отладки и визуализации относительных координат.
     */
    private void drawIntersectionCircle(Point2D pt) {
        Circle dot = new Circle(pt.getX(), pt.getY(), 3, Color.LIMEGREEN);
        dot.setStroke(Color.DARKGREEN);
        dot.setOnMouseClicked(e -> {
            RelativePointCoordinate rel = getRelativeCoordinates(pt);
            System.out.println("Clicked at: (" + pt.getX() + ", " + pt.getY() + ") | relative: (" + rel.getX() + ", " + rel.getY() + ")");
        });
        root.getChildren().add(dot);
    }

    /**
     * Получить относительные координаты точки (от левого нижнего угла)
     */
    private RelativePointCoordinate getRelativeCoordinates(Point2D pt) {
        int x = (int) Math.round(pt.getX() / cellSize);
        int y = (int) Math.round((height - pt.getY()) / cellSize);
        return new RelativePointCoordinate(x, y, pt);
    }

    private RelativePointCoordinate getRelativePoint(int x, int y){
        return new RelativePointCoordinate(x, y, getAbsoluteCoordinates(x, y));
    }
    /**
     * Получить абсолютные координаты точки из относительных координат.
     * Используется для отрисовки векторов и других элементов.
     */
    private Point2D getAbsoluteCoordinates(int x, int y) {
        double x_abs = x * cellSize;
        double y_abs = height - y * cellSize; // Координаты от левого нижнего угла
        return new Point2D(x_abs, y_abs);
    }

    private int getRelXFromAbsX(int absX) {
        return (int) Math.round(absX / cellSize);
    }

    private int getRelYFromAbsY(int absY) {
        return (int) Math.round((height - absY) / cellSize);
    }

    /**
     * Нарисовать RelativeVector по относительным координатам (x1, y1) -> (x2, y2).
     * Является базовым методом для отрисовки карты в 2D без ортографии.
     */
    public void drawRelativeVector(RelativePointCoordinate start, RelativePointCoordinate end) {
        RelativeVector vector = new RelativeVector(start, end);
        root.getChildren().add(vector);
    }

    private RelativeVector getProjectionVector(RelativeVector originalVector) {
        // Получаем координаты начала и конца вектора
        int startX = getRelXFromAbsX((int)originalVector.getStartX()); // abs
        int startY = getRelYFromAbsY((int)originalVector.getStartY()); // abs
        int endX = getRelXFromAbsX((int)originalVector.getEndX()); // abs
        int endY = getRelYFromAbsY((int)originalVector.getEndY()); // abs

        // Создаем новый вектор с учетом смещения
        RelativePointCoordinate stereoStart = getRelativePoint(startX - STEREO_OFFSET, startY + STEREO_OFFSET);
        RelativePointCoordinate stereoEnd = getRelativePoint(endX - STEREO_OFFSET, endY + STEREO_OFFSET);

        return new RelativeVector(stereoStart, stereoEnd);

    }

    public void drawStereoVector(RelativePointCoordinate start, RelativePointCoordinate end){
        RelativeVector front = new RelativeVector(start, end);
        RelativeVector projection = getProjectionVector(front);
        front.setStroke(Color.RED);
        projection.setStroke(Color.BLUE);
        front.setStrokeWidth(2);
        projection.setStrokeWidth(2);
        root.getChildren().addAll(front, projection);
    }
}
