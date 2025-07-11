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
    private final int MIN_WINDOW_HEIGHT = 720;


    private Scene scene;
    private Renderer renderer;
    private Player player;


    private void addListeners() {
        scene.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            System.out.println("Window close requested");
            // Здесь можно добавить логику сохранения состояния или очистки ресурсов
        });
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    System.out.println("W pressed");
                    break;
                case A:
                    player.getPlayerModel().setTranslateX(
                            player.getPlayerModel().getTranslateX() - 10
                    );
                    break;
                case S:
                    break;
                case D:
                    player.getPlayerModel().setTranslateX(
                            player.getPlayerModel().getTranslateX() + 10
                    );
                    break;
                default:
                    break;
            }
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W:
                    System.out.println("W released");
                    break;
                case A:
                    System.out.println("A released");
                    break;
                case S:
                    System.out.println("S released");
                    break;
                case D:
                    System.out.println("D released");
                    break;
                default:
                    break;
            }
        });
        //        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
//            render.onScreenResize();
//        });
//        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
//            render.onScreenResize();
//        });
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
                Double deltaTime = getFps(now);
                if (deltaTime == null) return;
//                player.rotate(1);
                renderer.update(deltaTime);
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
                    //System.out.printf("Avg FPS: %.1f%n", avgFps);
                    frames = 0;
                    elapsedTime = 0;
                }
                return deltaTime;
            }
        }.start();
    }

    public static void main(String[] args) {
        launch();
    }

}