package com.dev.playg.functionalInterface;

public class FunctionalInterfaceImpl {

    public static void main(String[] args) {
        CustomFunctionalInterface functionalInterface = () -> "Vinay Kr. Singh";

        System.out.println(functionalInterface.apply());
    }
}
