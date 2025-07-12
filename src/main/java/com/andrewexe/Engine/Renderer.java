package com.andrewexe.Engine;

import com.andrewexe.Engine.Map.MapGenerator;
import com.andrewexe.Engine.Player.Player;
import com.andrewexe.Game.PlayerModel;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    /**
     * Класс, который отвечает за отрисовку уровня и игрока.
     */

    private final int WINDOW_WIDTH = 1280;
    private final int WINDOW_HEIGHT = 720;

    private final int VISIBLE_SEGMENTS = 10; // Количество видимых сегментов карты

    private final int FPS = 60;
    private final int STEREO_OFFSET = 2;
    public static final int ROWS = 30;
    private double cellSize = 0; // Размер ячейки сетки, будет вычислен при инициализации
    private int width;
    private int height;
    private final Color GRID_COLOR = Color.rgb(120, 200, 240);

    private ArrayList<Line> verticalLines = new ArrayList<>();
    private ArrayList<Line> horizontalLines = new ArrayList<>();
    private ArrayList<Line> mapLines;

    private List<Point2D> gridIntersections;


    // сетка нужна как помощник и как элемент стилизации
    private Group grids;

    public Pane getRoot() {
        return root;
    }

    // JavaFX
    private final Pane root;

    private GameObject level;

    private Canvas infoCanvas;
    private GraphicsContext infoGC;

    public Renderer(int width, int height) {
        this.root = new Pane();
        this.grids = new Group();
        this.width = width;
        this.height = height;
        this.gridIntersections = new ArrayList<>();
        this.grids.setCache(true);
        this.grids.setAutoSizeChildren(true);
        this.getGrids();
        this.drawMap(VISIBLE_SEGMENTS);
        this.drawLastSegmentStartLine();

        //this.drawPlayerCentered();

//        this.infoCanvas = new Canvas(400, 40); // ширина и высота области для текста
//        this.infoGC = infoCanvas.getGraphicsContext2D();
//        this.root.getChildren().add(infoCanvas);
    }

    private void drawRawMap(){
        List<Line> mapLines = MapGenerator.generateMap();
        for (Line line : mapLines) {
            line.setStroke(Color.BLACK);
            this.root.getChildren().add(line);
        }
    }

    private Line getSegment(int segmentIndex) {
        if (segmentIndex < 0 || segmentIndex >= mapLines.size()) {
            System.out.println("Invalid segment index: " + segmentIndex);
            return null;
        }
        return mapLines.get(segmentIndex);
    }

    private void drawMap(int segments) {
        mapLines = MapGenerator.generateMap();

        // Получаем максимальные размеры карты
        double mapWidth = 0;// MapGenerator.MAP_LENGTH * MapGenerator.SEGMENT_LENGTH; // MAP_LENGTH * SEGMENT_LENGTH
        double mapHeight = 0; //MapGenerator.MAX_HEIGHT;    // MAX_HEIGHT



        for(Line line : mapLines) {
            if(Math.max(line.getStartX(), line.getEndX()) > mapWidth) {
                mapWidth = Math.max(line.getStartX(), line.getEndX());
            }
            if(Math.max(line.getStartY(), line.getEndY()) > mapHeight){
            mapHeight = Math.max(line.getStartY(), line.getEndY()) ;
            }
        }

        System.out.println("Map width: " + mapWidth + ", Map height: " + mapHeight);

        // Вычисляем коэффициенты масштабирования
        double scaleX = (double) width / mapWidth;
        System.out.println("scaleX: " + scaleX);
        if(scaleX < 1){
            System.out.println("Map must been cropped and ready to POV setup!");
        }
        double scaleY = (double) (height / 3) / mapHeight; // например, карта занимает нижнюю треть окна

        System.out.println("scaleY: " + scaleY);
        // Смещение по Y для размещения карты у нижнего края
        double offsetY = height - (mapHeight);
        System.out.println("offsetY: " + offsetY);

        for (Line line : mapLines) {
            // Масштабируем координаты
            line.setStartX(line.getStartX() * scaleX);
            line.setEndX(line.getEndX() * scaleX);
            line.setStartY(line.getStartY() * scaleY);
            line.setEndY(line.getEndY() * scaleY) ;
            line.setStrokeWidth(4);

            // Добавляем обработчик клика
            line.setOnMouseClicked(event -> {
                line.setStrokeWidth(3);
                System.out.println("Line clicked: #" + mapLines.indexOf(line) + ", startX=" + line.getStartX() + ", startY=" + line.getStartY() +
                        ", endX=" + line.getEndX() + ", endY=" + line.getEndY());
            });

            this.root.getChildren().add(line);
        }
    }

    /**
     * Обновление игрового поля каждый кадр
     */
    public void update(double deltaTime) {
        //showInfo("FPS: " + (int) (1 / deltaTime) + ", Delta Time: " + deltaTime);
        // player.rotate(1);
    }

    /**
     * Рисуе�� клеточки для работы с масштабом и относительными координатами
     */
    private void getGrids() {
        int cellHeight = (int) height / ROWS;

        System.out.println(height);
        for (int i = 0; i < width; i += cellHeight) { // x
            Line line = new Line();
            line.setStartX(i);
            line.setStartY(0);
            line.setEndX(i);
            line.setEndY(this.height);
            line.setStroke(GRID_COLOR);
            this.horizontalLines.add(line);
            this.grids.getChildren().add(line);
        }
        for (int j = 0; j < height; j += cellHeight) {
            Line line = new Line();
            line.setStartX(0);
            line.setStartY(j);
            line.setEndX(this.width);
            line.setEndY(j);
            line.setStroke(GRID_COLOR);
            this.verticalLines.add(line);
            this.grids.getChildren().add(line);
        }
        this.root.getChildren().add(grids); // отрисовка сетки
        //drawStereoVector(vec_start, vec_end);

    }

    public void showInfo(String text) {
        infoGC.clearRect(0, 0, infoCanvas.getWidth(), infoCanvas.getHeight());
        infoGC.setFill(Color.WHITE);
        infoGC.fillRect(0, 0, infoCanvas.getWidth(), infoCanvas.getHeight());
        infoGC.setFill(Color.BLACK);
        infoGC.fillText(text, 10, 25);
    }

    public void drawPlayerCentered(Player player){
        player.getPlayerModel().setLayoutX(width / 2 - player.getPlayerModel().getBoundsInParent().getWidth() / 2);
        player.getPlayerModel().setLayoutY(height / 2 - player.getPlayerModel().getBoundsInParent().getHeight() / 2);
        this.root.getChildren().add(player.getPlayerModel());
    }

    public void placePlayer(Player player){
        Line segment = getSegment(2); // Получаем первый сегмент карты
        if (segment != null) {
            double startX = segment.getStartX();
            double startY = segment.getStartY();
            double endX = segment.getEndX();
            double endY = segment.getEndY();
            double angle = Math.atan2(endY - startY, endX - startX) * 180 / Math.PI;
            player.getPlayerModel().setLayoutX(startX - player.getPlayerModel().getBoundsInParent().getWidth() / 2);
            player.getPlayerModel().setLayoutY(startY - player.getPlayerModel().getBoundsInParent().getHeight() / 2);
            player.getPlayerModel().setRotate(angle);
            this.root.getChildren().add(player.getPlayerModel());
        } else {
            System.out.println("Segment not found for placing the player.");
        }
    }

    public void drawLastSegmentStartLine() {
        if (mapLines == null || mapLines.isEmpty()) return;
        Line lastSegment = mapLines.get(mapLines.size() - 3);
        double x = lastSegment.getStartX();
        double y = lastSegment.getStartY();
        Line verticalLine = new Line(x, 0, x, height);
        verticalLine.setStroke(Color.RED);
        verticalLine.setStrokeWidth(2);
        this.root.getChildren().add(verticalLine);
    }

}
