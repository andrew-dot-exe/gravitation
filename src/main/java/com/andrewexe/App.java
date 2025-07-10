package com.andrewexe;

import com.andrewexe.Engine.Renderer;
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


    @Override
    public void start(Stage stage) {

        Renderer render = new Renderer(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        var scene = new Scene(render.getRoot(), MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
//        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
//            render.onScreenResize();
//        });
//        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
//            render.onScreenResize();
//        });

        new AnimationTimer() {
            private long lastTime = 0;
            private int frames = 0;
            private double elapsedTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
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
                render.update(deltaTime);
            }
        }.start();



        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}