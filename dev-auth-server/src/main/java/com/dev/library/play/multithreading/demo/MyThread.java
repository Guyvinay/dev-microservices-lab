package com.dev.library.play.multithreading.demo;
// ===================================================================
// Example 1: Creating a thread by extending the Thread class
// Not preferred in real applications but good for understanding basics
// ===================================================================
public class MyThread extends Thread {

    @Override
    public void run() {
        // This code runs inside a separate thread
        System.out.println("Running in MyThread run method");
    }
}
