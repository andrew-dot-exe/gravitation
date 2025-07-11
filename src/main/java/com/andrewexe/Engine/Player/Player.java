package com.andrewexe.Engine.Player;

import com.andrewexe.Engine.Physics.RigidBody;
import com.andrewexe.Engine.GameObject;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

public class Player extends RigidBody implements GameObject {

    Group playerModel;

    public Player(Group playerModel, double mass) {
        super(mass);
        this.playerModel = playerModel;
    }

    @Override
    public void update() {

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



}
