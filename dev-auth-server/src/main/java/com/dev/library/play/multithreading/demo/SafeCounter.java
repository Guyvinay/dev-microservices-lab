package com.dev.library.play.multithreading.demo;

public class SafeCounter {
    private int value = 0;

    public synchronized void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }
}
