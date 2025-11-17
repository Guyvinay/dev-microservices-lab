package com.dev.play.patterns.factory;

public class ShapeFactory {

/**
 * A pattern where the creation of objects is handled by a separate method (factory),
 * instead of directly using new everywhere.
 * It lets you create objects based on input or conditions without exposing the creation logic to the client.
 */
    public static Shape getShape(String shape) {
        if (shape == null) return null;
        return switch (shape.toLowerCase()) {
            case "circle" -> new Circle();
            case "square" -> new Square();
            case "rectangle" -> new Rectangle();
            default -> throw new IllegalArgumentException("Invalid shape type.");
        };
    }
}
