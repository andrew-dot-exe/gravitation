package com.andrewexe.Engine.Physics;

public interface PhysicsInteractable {


    void applyForce(double forceX, double forceY); // Применить силу к объекту
    void applyImpulse(double impulseX, double impulseY); // Применить импульс к объекту
}
