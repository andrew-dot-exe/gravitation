package com.andrewexe.Engine;

public class InputHandler {

    // Здесь можно реализовать обработку ввода с клавиатуры, мыши или других устройств.
    // Например, можно использовать JavaFX для обработки событий нажатия клавиш.

    public void handleKeyPress(String key) {
        // Логика обработки нажатия клавиши
        System.out.println("Key pressed: " + key);
    }

    public void handleKeyRelease(String key) {
        // Логика обработки отпускания клавиши
        System.out.println("Key released: " + key);
    }

    public void handleMouseClick(int x, int y) {
        // Логика обработки клика мышью
        System.out.println("Mouse clicked at: (" + x + ", " + y + ")");
    }


}
