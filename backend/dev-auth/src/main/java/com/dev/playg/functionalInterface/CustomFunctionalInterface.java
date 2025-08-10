package com.dev.playg.functionalInterface;

@FunctionalInterface
public interface CustomFunctionalInterface {

    String apply();
    default void log() {
        System.out.println("default method call: log");
    }
}
