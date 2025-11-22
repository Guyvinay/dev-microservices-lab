package com.dev.play.multithreading.demo;

// ===================================================================
// Example 2: Creating a thread using Runnable
// Recommended over extending Thread because Runnable is more flexible
// ===================================================================
public class MyTask implements Runnable {
    @Override
    public void run() {
        // This is the logic that runs inside the worker thread
        System.out.println("Inside MyTask class run method");
    }
}
