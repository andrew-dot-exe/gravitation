package com.andrewexe.Engine.Player;

import com.andrewexe.Engine.Physics.RigidBody;
import com.andrewexe.Engine.GameObject;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

public class Player extends RigidBody implements GameObject {

    Group playerModel;

    public double getAxisAcceleration() {
        return xAxisAcceleration;
    }

    public double getBrakeAcceleration() {
        return xBrakeAcceleration;
    }

    public double getHandbrakeAcceleration() {
        return xHandbrakeAcceleration;
    }



    private final double gravity = 9;

    private double xAxisAcceleration = 0;
    private double xBrakeAcceleration = 0.009;
    private double xHandbrakeAcceleration = 0.2;
    private double xForwardSensitivity = 0.6;
    private double xBackwardSensitivity = 0.4;
    private double maxSpeed = 2;
    private double speed = 0;
    private boolean isMoving = false;

    public void setFreeFalling(boolean freeFalling) {
        isFreeFalling = freeFalling;
    }

    private boolean isFreeFalling = true;

    public Player(Group playerModel, double mass) {
        super(mass);
        this.playerModel = playerModel;
    }

    @Override
    public void update() {
        // Этот метод будет вызываться каждый кадр для обновления состояния игрока.
        // Здесь можно добавить логику для обработки ввода пользователя, движения и т.д.
    }

    public void update(double deltaTime) {
        if (!isMoving) { // isMoving — булева переменная, true если кнопка движения нажата
            brakeAcceleration(deltaTime); // deltaTime — время между кадрами
        }
        move(xAxisAcceleration * maxSpeed, 0);
        applyGravity(deltaTime); // Применяем гравитацию к игроку
    }

    private void applyGravity(double deltaTime) {
        // Здесь можно добавить логику для применения гравитации к игроку.
        // Например, можно уменьшать y-координату игрока на значение, пропорциональное времени кадра.
        double gravityForce = gravity * deltaTime;
        if (isFreeFalling) {
            playerModel.setTranslateY(playerModel.getTranslateY() + gravityForce);
        }
        System.out.println(getBottomEdgeY());
        //playerModel.setTranslateY(playerModel.getTranslateY() + gravityForce);
    }

    public void incrementXAxisAcceleration(double step) {
        isMoving = true;
        if(xAxisAcceleration > 1) {
            this.xAxisAcceleration = 1;
        } else if (xAxisAcceleration < -1) {
            this.xAxisAcceleration = -1;
        } else {
            this.xAxisAcceleration += step;
        }
    }

    public void brakeAcceleration(double deltaTime) {
        if (xAxisAcceleration > 0) {
            xAxisAcceleration -= xBrakeAcceleration * deltaTime;
            if (xAxisAcceleration < 0) {
                xAxisAcceleration = 0; // Останавливаем, если значение стало меньше нуля
            }
        } else if (xAxisAcceleration < 0) {
            xAxisAcceleration += xBrakeAcceleration * deltaTime;
            if (xAxisAcceleration > 0) {
                xAxisAcceleration = 0; // Останавливаем, если значение стало больше нуля
            }
        }

        // Полностью останавливаем, если ускорение близко к нулю
        if (Math.abs(xAxisAcceleration) < 0.001) {
            xAxisAcceleration = 0;
        }
    }

    public void handbrake(){
        if (xAxisAcceleration > 0) {
            xAxisAcceleration -= xHandbrakeAcceleration;
        } else if (xAxisAcceleration < 0) {
            xAxisAcceleration += xHandbrakeAcceleration;
        }

        // Stop completely if acceleration is close to zero
        if (Math.abs(xAxisAcceleration) < 0.06) {
            xAxisAcceleration = 0;
        }
    }

    public double getBottomEdgeY() {
        Bounds boundsInScene = playerModel.localToScene(playerModel.getBoundsInLocal());
        System.out.println(boundsInScene);
        return boundsInScene.getMaxY();
    }

    public void setOffsetBottomEdgeY(double offsetY) {
        Bounds boundsInScene = playerModel.localToScene(playerModel.getBoundsInLocal());
        playerModel.setTranslateY(playerModel.getTranslateY() + offsetY);
    }

    public void move(double x, double y) {
        playerModel.setTranslateX(playerModel.getTranslateX() + x);
        playerModel.setTranslateY(playerModel.getTranslateY() + y);
    }

    public void rotate(double angle) {
        playerModel.setRotate(playerModel.getRotate() + angle);
    }

    public void translate(double x, double y) {
        playerModel.setTranslateX(playerModel.getTranslateX() + x);
        playerModel.setTranslateY(playerModel.getTranslateY() + y);
    }

    public void scale(double factor) {
        playerModel.setScaleX(playerModel.getScaleX() * factor);
        playerModel.setScaleY(playerModel.getScaleY() * factor);
    }

    public void rotateWheel(){

    }

    public Node getLeftWheel() {
        return playerModel.getChildren().get(0);
    }

    public Group getPlayerModel() {
        return playerModel;
    }

    public boolean setMoving(boolean isMoving) {
        this.isMoving = isMoving;
        return this.isMoving;
    }

}
