package com.dev.library.play.patterns.factory;

// Concrete implementation of Shape
public class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a Circle");
    }
}