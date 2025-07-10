package com.andrewexe.Engine.Physics;

public interface PhysicsInteractable {

    // Интерфейс, который определяет взаимодействие с физическими объектами.
    // Он может быть реализован классами, которые хотят взаимодействовать с физическими телами.
    // Например, игроки, NPC и т.д.

    void applyForce(double forceX, double forceY); // Применить силу к объекту
    void applyImpulse(double impulseX, double impulseY); // Применить импульс к объекту
}
