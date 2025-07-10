package com.andrewexe.Engine;

import com.andrewexe.Engine.Coordinates.Point;
import com.andrewexe.Engine.Coordinates.RelativeCoordinateSystem;
import com.andrewexe.Engine.Coordinates.RelativePoint;
import com.andrewexe.Engine.Map.MapGenerator;
import com.andrewexe.Engine.Map.MapLevel;
import com.andrewexe.Engine.Primitives.RelativeVector;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        RelativePoint vec_start = getRelativePoint(3, 2); // Начало вектора
        RelativePoint vec_end = getRelativePoint(6, 3); // Конец вектора
        //drawStereoVector(vec_start, vec_end);
        drawGravityDefiedMap();
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
            RelativePoint rel = getRelativeCoordinates(pt);
            System.out.println("Clicked at: (" + pt.getX() + ", " + pt.getY() + ") | relative: (" + rel.getX() + ", " + rel.getY() + ")");
        });
        root.getChildren().add(dot);
    }

    /**
     * Получить относительные координаты точки (от левого нижнего угла)
     */
    private RelativePoint getRelativeCoordinates(Point2D pt) {
        int x = (int) Math.round(pt.getX() / cellSize);
        int y = (int) Math.round((height - pt.getY()) / cellSize);
        return new RelativePoint(x, y, pt);
    }

    private RelativePoint getRelativePoint(int x, int y){
        return new RelativePoint(x, y, getAbsoluteCoordinates(x, y));
    }

    private RelativePoint getRelativePoint(Point pt) {
        return new RelativePoint(pt.getX(), pt.getY(), getAbsoluteCoordinates(pt.getX(), pt.getY()));
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
    public void drawRelativeVector(RelativePoint start, RelativePoint end) {
        RelativeVector vector = new RelativeVector(start, end);
        root.getChildren().add(vector);
    }

    private RelativeVector getProjectionVector(RelativeVector originalVector) {
        // Получаем координаты начала и конца вектора в относительных координатах
        int startX = getRelXFromAbsX((int)originalVector.getStartX());
        int startY = getRelYFromAbsY((int)originalVector.getStartY());
        int endX = getRelXFromAbsX((int)originalVector.getEndX());
        int endY = getRelYFromAbsY((int)originalVector.getEndY());

        // Вычисляем направление вектора
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0) length = 1; // чтобы не делить на 0

        // Вектор нормали (перпендикулярный исходному, для параллельного смещения)
        double nx = -dy / length;
        double ny = dx / length;

        // Смещаем обе точки на STEREO_OFFSET вдоль нормали
        double offsetX = nx * STEREO_OFFSET;
        double offsetY = ny * STEREO_OFFSET;

        RelativePoint stereoStart = getRelativePoint((int)Math.round(startX + offsetX), (int)Math.round(startY + offsetY));
        RelativePoint stereoEnd = getRelativePoint((int)Math.round(endX + offsetX), (int)Math.round(endY + offsetY));

        return new RelativeVector(stereoStart, stereoEnd);
    }

    public void drawStereoVector(RelativePoint start, RelativePoint end){
        RelativeVector front = new RelativeVector(start, end);
        RelativeVector projection = getProjectionVector(front);
        front.setStroke(Color.RED);
        projection.setStroke(Color.BLUE);
        front.setStrokeWidth(2);
        projection.setStrokeWidth(2);
        root.getChildren().addAll(front, projection);
    }

    public void drawGravityDefiedMap() {
        MapGenerator generator = new MapGenerator();
        List<Point> points = generator.generateGravityDefiedPoints();
        String filename = "level_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        ArrayList<RelativeVector> vectors = new ArrayList<>();
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            RelativePoint relStart = getRelativePoint(p1);
            RelativePoint relEnd = getRelativePoint(p2);
            drawRelativeVector(relStart, relEnd);
            vectors.add(new RelativeVector(relStart, relEnd));
        }
        try {
            MapLevel level = new MapLevel();
            level.loadMap(vectors);
            level.saveToFile(filename);
            System.out.println("Level saved to: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Спавн игрока в середине первого сегмента (на первом векторе)
        if (points.size() > 1) {
            Point p1 = points.get(0);
            Point p2 = points.get(1);
            // Находим координаты середины первого сегмента
            double x = (p1.getX() + p2.getX()) / 2.0;
            double y = (p1.getY() + p2.getY()) / 2.0;
            Point2D abs = getAbsoluteCoordinates((int)x, (int)y);
            drawSimpleBike(400, 300, Math.toRadians(0)); // Рисуем велосипед в центре экрана
        }
    }

    /**
     * Рисует примитивный велосипед из фигур (два круга и прямоугольник)
     */
    public void drawSimpleBike(double x, double y, double angle) {
        double wheelRadius = cellSize * 0.7;
        double frameLength = cellSize * 2.5;
        double frameHeight = cellSize * 0.3;
        // Центры колёс
        double x1 = x - frameLength / 2 * Math.cos(angle);
        double y1 = y - frameLength / 2 * Math.sin(angle);
        double x2 = x + frameLength / 2 * Math.cos(angle);
        double y2 = y + frameLength / 2 * Math.sin(angle);

        // Центр велосипеда (середина между колёсами)
        double centerX = (x1 + x2) / 2;
        double centerY = (y1 + y2) / 2;

        javafx.scene.shape.Circle wheel1 = new javafx.scene.shape.Circle(x1, y1, wheelRadius);
        javafx.scene.shape.Circle wheel2 = new javafx.scene.shape.Circle(x2, y2, wheelRadius);
        wheel1.setFill(javafx.scene.paint.Color.TRANSPARENT);
        wheel1.setStroke(javafx.scene.paint.Color.BLACK);
        wheel2.setFill(javafx.scene.paint.Color.TRANSPARENT);
        wheel2.setStroke(javafx.scene.paint.Color.BLACK);

        // Рама (прямоугольник между колёсами)
        double frameX = centerX - frameLength / 2;
        double frameY = centerY - frameHeight / 2;
        javafx.scene.shape.Rectangle frame = new javafx.scene.shape.Rectangle(frameX, frameY, frameLength, frameHeight);
        frame.setFill(javafx.scene.paint.Color.RED);
        frame.setRotate(Math.toDegrees(angle));
        frame.setArcHeight(frameHeight * 0.7);
        frame.setArcWidth(frameHeight * 0.7);

        Group bikeGroup = new Group();
        bikeGroup.getChildren().addAll(wheel1, wheel2, frame);

        root.getChildren().add(bikeGroup);
    }
}
