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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    /**
     * Класс, который отвечает за отрисовку уровня и игрока.
     */


    private final int VISIBLE_SEGMENTS = 10; // Количество видимых сегментов карты

    public static final int ROWS = 30;
    private int width;
    private int height;
    private final Color GRID_COLOR = Color.rgb(120, 200, 240);

    private ArrayList<Line> verticalLines = new ArrayList<>();
    private ArrayList<Line> horizontalLines = new ArrayList<>();
    private ArrayList<Line> mapLines;

    public boolean isRestartingState() {
        return restartingState;
    }

    public void setRestartingState(boolean restartingState) {
        this.restartingState = restartingState;
    }

    private boolean restartingState;

    public ArrayList<Line> getMapLines() {
        return mapLines;
    }

    private double scaleX;
    private double scaleY;

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    private List<Point2D> gridIntersections;


    // сетка нужна как помощник и как элемент стилизации
    private Group grids;
    private Player player;

    public Pane getRoot() {
        return root;
    }

    // JavaFX
    private final Pane root;

    private GameObject level;

    private Canvas infoCanvas;
    private GraphicsContext infoGC;

    private boolean finishShown = false;

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

        this.infoCanvas = new Canvas(400, 40);
        this.infoGC = infoCanvas.getGraphicsContext2D();
        this.root.getChildren().add(infoCanvas);
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
        scaleY = (double) (height / 3) / mapHeight;
        for (Line line : mapLines) {
            // Масштабируем координаты
            line.setStartX(line.getStartX() * scaleX);
            line.setEndX(line.getEndX() * scaleX);
            line.setStartY(line.getStartY() * scaleY);
            line.setEndY(line.getEndY() * scaleY) ;
            line.setStrokeWidth(4);
            this.root.getChildren().add(line);
        }
    }

    /**
     * Обновление игрового поля каждый кадр
     */
    public void update(double deltaTime) {
        showInfo("FPS: " + (int) (1 / deltaTime) + ", Delta Time: " + deltaTime);
        highlightLinesOnPlayerContact();
        checkFinishCrossed();
    }

    private void showDialog(String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.setOnHidden(e -> restartGame());
        alert.show();
    }

    private void restartGame() {
        finishShown = true;
        restartingState = true;
    }

    public void restart() {
        finishShown = false;
        restartingState = false;
        root.getChildren().clear();
        gridIntersections.clear();
        verticalLines.clear();
        horizontalLines.clear();
        mapLines.clear();
        getGrids();
        drawMap(VISIBLE_SEGMENTS);
        drawLastSegmentStartLine();
    }

    private void checkFinishCrossed() {
        if (finishShown || player == null || mapLines == null || mapLines.isEmpty()) return;
        Line finishLine = null;
        for (Node node : root.getChildren()) {
            if (node instanceof Line line && line.getStroke().equals(Color.RED) && line.getStartX() == line.getEndX()) {
                finishLine = line;
                break;
            }
        }
        if (finishLine == null) return;
        double leftWheelX = player.getPlayerLeftWheel().localToScene(player.getPlayerLeftWheel().getBoundsInLocal()).getCenterX();
        double rightWheelX = player.getPlayerRightWheel().localToScene(player.getPlayerRightWheel().getBoundsInLocal()).getCenterX();
        double finishX = finishLine.getStartX();
        if (leftWheelX >= finishX || rightWheelX >= finishX) {
            showDialog("Финиш!");
            finishShown = true;
        }
    }

    public void highlightLinesOnPlayerContact() {
        if (player == null || mapLines == null) return;
        for (Line line : mapLines) {
            boolean leftTouch = Shape.intersect(player.getPlayerLeftWheel(), line).getBoundsInLocal().getWidth() != -1;
            boolean rightTouch = Shape.intersect(player.getPlayerRightWheel(), line).getBoundsInLocal().getWidth() != -1;
            if (leftTouch || rightTouch) {
                line.setStroke(Color.RED);
            } else {
                line.setStroke(Color.BLACK);
            }
        }
    }

    public boolean isPlayerIntersectingWithMap() {
        if (player == null || mapLines == null) return false;
        for (Line line : mapLines) {
            if (Shape.intersect(player.getPlayerLeftWheel(), line).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean isRightWheelIntersectingWithMap() {
        if (player == null || mapLines == null) return false;
        for (Line line : mapLines) {
            if (Shape.intersect(player.getPlayerRightWheel(), line).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }



    private void getGrids() {
        int cellHeight = (int) height / ROWS;

        //System.out.println(height);
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
        this.root.getChildren().add(grids);

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
        this.player = player;
        Line segment = getSegment(2);
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

    public void checkIntersection(){

    }
}
