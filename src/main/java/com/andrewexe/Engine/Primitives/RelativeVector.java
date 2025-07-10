package com.andrewexe.Engine.Primitives;

import com.andrewexe.Engine.Coordinates.RelativePoint;
import javafx.scene.shape.Line;

public class RelativeVector extends Line {
    // Представляет собой враппер над Line с относительными координатами

    public RelativeVector(RelativePoint start, RelativePoint end) {
        super();
        this.setStartX(start.getAbsX());
        this.setStartY(start.getAbsY());
        this.setEndX(end.getAbsX());
        this.setEndY(end.getAbsY());
    }
}
