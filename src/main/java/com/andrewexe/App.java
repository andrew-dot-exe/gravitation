package com.andrewexe;

import com.andrewexe.Engine.Player.Player;
import com.andrewexe.Engine.Renderer;
import com.andrewexe.Game.PlayerModel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Random;


/**
 * JavaFX App
 */
public class App extends Application {
    private final int MIN_WINDOW_WIDTH = 1280;
    private final int MIN_WINDOW_HEIGHT = 800;


    private Scene scene;
    private Renderer renderer;
    private Player player;

    private double deltaTime;


    private void addListeners() {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case A:
                    player.incrementXAxisAcceleration(-0.1);
                    break;
                case D:
                    player.incrementXAxisAcceleration(0.1);
                    break;
                case SPACE:
                    player.handbrake();
                    break;
                default:
                    break;
            }
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case A:
                    player.setMoving(false);
                    break;
                case D:
                    player.setMoving(false);
                    break;

                default:
                    break;
            }
        });
    }

    @Override
    public void start(Stage stage) {

        renderer = new Renderer(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        scene = new Scene(renderer.getRoot(), MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        player = new Player(PlayerModel.getPlayerModel(), 1.0);
        renderer.placePlayer(player);
        addListeners();
        addGameCycle();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addGameCycle() {
        new AnimationTimer() {
            private long lastTime = 0;
            private int frames = 0;
            private double elapsedTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                }
                deltaTime = (now - lastTime) / 1_000_000_000.0;
                player.update(deltaTime);
                player.moveWithSlopeAndCollision(renderer, deltaTime);
                checkBounds();
                renderer.update(deltaTime);
                if(renderer.isRestartingState()){
                    restartGame();
                }
            }

            private void checkBounds() {
                double playerX = player.getPlayerModel().getTranslateX();
                double playerY = player.getBottomEdgeY();
                double width = renderer.getRoot().getWidth();
                double height = renderer.getRoot().getHeight();

                if (playerX < 0) {
                    player.getPlayerModel().setTranslateX(0);
                } else if (playerX + player.getPlayerModel().getBoundsInParent().getWidth() > width) {
                    player.getPlayerModel().setTranslateX(width - player.getPlayerModel().getBoundsInParent().getWidth());
                }
                // intersects with map
                if(renderer.isPlayerIntersectingWithMap()){
                    player.setFreeFalling(false);
                }
                if(playerY > height) {
                    player.setFreeFalling(false); // останавливаем свободное падение
                    player.setOffsetBottomEdgeY(height - playerY);
                } else if (playerY < 0) {
                    player.getPlayerModel().setTranslateY(0);
                }
            }

            private Double getFps(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return null;
                }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                double fps = 1.0 / deltaTime;
                frames++;
                elapsedTime += deltaTime;
                if (elapsedTime >= 1.0) {
                    double avgFps = frames / elapsedTime;
                    System.out.printf("Avg FPS: %.1f%n", avgFps);
                    frames = 0;
                    elapsedTime = 0;
                }
                return deltaTime;
            }
        }.start();
    }

    private void restartGame() {
        renderer.restart();
        player = new Player(PlayerModel.getPlayerModel(), 1.0);
        renderer.placePlayer(player);
    }

    public static void main(String[] args) {
        launch();
    }

}