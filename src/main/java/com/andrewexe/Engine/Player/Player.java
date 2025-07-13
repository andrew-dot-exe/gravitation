package com.andrewexe.Engine.Player;

import com.andrewexe.Engine.Physics.RigidBody;
import com.andrewexe.Engine.GameObject;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import com.andrewexe.Engine.Renderer;
import javafx.scene.shape.Line;

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

    private double velocityY = 0; // Вертикальная скорость для гравитации
    private double velocityX = 0; // Горизонтальная скорость для инерции
    private final double friction = 0.98; // Коэффициент трения (затухания)

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
        // Управление ускорением (газ/тормоз)
        if (isMoving) {
            // Ускорение по оси X
            velocityX += xAxisAcceleration * maxSpeed * deltaTime;
            // Ограничение максимальной скорости
            if (velocityX > maxSpeed) velocityX = maxSpeed;
            if (velocityX < -maxSpeed) velocityX = -maxSpeed;
        } else {
            // Затухание скорости (инерция)
            velocityX *= friction;
            if (Math.abs(velocityX) < 0.01) velocityX = 0;
        }
        // Перемещение по X с учётом инерции
        playerModel.setTranslateX(playerModel.getTranslateX() + velocityX);
        // Вертикальное движение и гравитация
        //moveWithSlopeAndCollision(renderer, deltaTime);
    }

    private void applyGravity(double deltaTime) {
        // Здесь можно добавить логику для применения гравитации к игроку.
        // Например, можно уменьшать y-координату игрока на значение, пропорциональное времени кадра.
        double gravityForce = gravity * deltaTime;
        if (isFreeFalling) {
            playerModel.setTranslateY(playerModel.getTranslateY() + gravityForce);
        }
        //System.out.println(getBottomEdgeY());
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

    public void vectorMove(double step){
        incrementXAxisAcceleration(step);

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
        //System.out.println(boundsInScene);
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

    public Shape getPlayerLeftWheel() {
        return (Shape) playerModel.getChildren().get(0);
    }
    public Shape getPlayerRightWheel() {
        return (Shape) playerModel.getChildren().get(1);
    }

    public boolean setMoving(boolean isMoving) {
        this.isMoving = isMoving;
        // Если отпустили газ/тормоз — ускорение сбрасываем, но скорость сохраняется
        if (!isMoving) xAxisAcceleration = 0;
        return this.isMoving;
    }

    public void moveWithSlopeAndCollision(Renderer renderer, double deltaTime) {
        Shape leftWheel = getPlayerLeftWheel();
        Shape rightWheel = getPlayerRightWheel();
        double leftWheelCenterX = leftWheel.localToScene(leftWheel.getBoundsInLocal()).getCenterX();
        double rightWheelCenterX = rightWheel.localToScene(rightWheel.getBoundsInLocal()).getCenterX();
        double leftWheelRadius = leftWheel.getBoundsInLocal().getWidth() / 2.0;
        double rightWheelRadius = rightWheel.getBoundsInLocal().getWidth() / 2.0;
        Line leftLine = null, rightLine = null;
        double leftMinDist = Double.MAX_VALUE, rightMinDist = Double.MAX_VALUE;
        for (Line line : renderer.getMapLines()) {
            double x1 = line.getStartX();
            double x2 = line.getEndX();
            if (leftWheelCenterX >= Math.min(x1, x2) && leftWheelCenterX <= Math.max(x1, x2)) {
                double dist = Math.abs(leftWheelCenterX - x1);
                if (dist < leftMinDist) {
                    leftMinDist = dist;
                    leftLine = line;
                }
            }
            if (rightWheelCenterX >= Math.min(x1, x2) && rightWheelCenterX <= Math.max(x1, x2)) {
                double dist = Math.abs(rightWheelCenterX - x1);
                if (dist < rightMinDist) {
                    rightMinDist = dist;
                    rightLine = line;
                }
            }
        }
        boolean onGround = false;
        double newY = playerModel.getTranslateY();
        double angleDeg = 0;
        if (leftLine != null && rightLine != null) {
            double lx1 = leftLine.getStartX(), ly1 = leftLine.getStartY();
            double lx2 = leftLine.getEndX(), ly2 = leftLine.getEndY();
            double rx1 = rightLine.getStartX(), ry1 = rightLine.getStartY();
            double rx2 = rightLine.getEndX(), ry2 = rightLine.getEndY();
            double leftGroundY = ly1 + (ly2 - ly1) * (leftWheelCenterX - lx1) / (lx2 - lx1);
            double rightGroundY = ry1 + (ry2 - ry1) * (rightWheelCenterX - rx1) / (rx2 - rx1);
            double leftOffset = leftGroundY - (leftWheel.localToScene(leftWheel.getBoundsInLocal()).getMaxY());
            double rightOffset = rightGroundY - (rightWheel.localToScene(rightWheel.getBoundsInLocal()).getMaxY());
            newY += Math.min(leftOffset, rightOffset);
            angleDeg = Math.toDegrees(Math.atan2(rightGroundY - leftGroundY, rightWheelCenterX - leftWheelCenterX));
            onGround = true;
        } else if (leftLine != null) {
            double lx1 = leftLine.getStartX(), ly1 = leftLine.getStartY();
            double lx2 = leftLine.getEndX(), ly2 = leftLine.getEndY();
            double leftGroundY = ly1 + (ly2 - ly1) * (leftWheelCenterX - lx1) / (lx2 - lx1);
            double leftOffset = leftGroundY - (leftWheel.localToScene(leftWheel.getBoundsInLocal()).getMaxY());
            newY += leftOffset;
            angleDeg = Math.toDegrees(Math.atan2(ly2 - ly1, lx2 - lx1));
            onGround = true;
        } else if (rightLine != null) {
            double rx1 = rightLine.getStartX(), ry1 = rightLine.getStartY();
            double rx2 = rightLine.getEndX(), ry2 = rightLine.getEndY();
            double rightGroundY = ry1 + (ry2 - ry1) * (rightWheelCenterX - rx1) / (rx2 - rx1);
            double rightOffset = rightGroundY - (rightWheel.localToScene(rightWheel.getBoundsInLocal()).getMaxY());
            newY += rightOffset;
            angleDeg = Math.toDegrees(Math.atan2(ry2 - ry1, rx2 - rx1));
            onGround = true;
        }
        if (onGround) {
            if (isFreeFalling) velocityY = 0;
            playerModel.setTranslateY(newY);
            playerModel.setRotate(angleDeg);
            isFreeFalling = false;
            return;
        }
        isFreeFalling = true;
        velocityY += gravity * deltaTime;
        playerModel.setTranslateY(playerModel.getTranslateY() + velocityY * deltaTime);
    }

}
