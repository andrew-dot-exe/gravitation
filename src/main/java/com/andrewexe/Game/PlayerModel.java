package com.andrewexe.Game;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PlayerModel {

    /**
     * Возвращает модель велосипеда с центром в (0,0), длиной 1.0 и высотой 0.3 (в относительных единицах).
     * Все размеры нормированы, чтобы не зависеть от cellSize.
     * Для анимации можно трансформировать всю группу (scale, rotate, translate).
     */
    public static Group getPlayerModel() {
        Group model = new Group();
        Circle left_wheel = new Circle();
        left_wheel.setFill(Color.RED);
        left_wheel.setCenterX(1);
        left_wheel.setCenterY(0);
        left_wheel.setRadius(10);
        Circle right_wheel = new Circle();
        right_wheel.setCenterX(30);
        right_wheel.setCenterY(0);
        right_wheel.setRadius(10);
        Rectangle body = new Rectangle();
        body.setX(-5);
        body.setY(-30);
        body.setWidth(40);
        body.setHeight(20);
        model.getChildren().addAll(left_wheel, right_wheel, body);

        return model;
    }
}
