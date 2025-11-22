package com.dev.play.multithreading;

import com.dev.play.multithreading.demo.Counter;
import com.dev.play.multithreading.demo.LockCounter;
import com.dev.play.multithreading.demo.MyTask;
import com.dev.play.multithreading.demo.MyThread;
import com.dev.play.multithreading.demo.SafeCounter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Creating a new thread instance
//        MyThread myThread = new MyThread();
        // start() creates a new OS-level thread and then calls run()
//        myThread.start();

        // Wrap the Runnable inside a Thread
//        Thread thread = new Thread(new MyTask());
        // Start execution in a separate thread
//        thread.start();;

// ===================================================================
// Example 3: Lambda expression for Runnable
// Most concise and used commonly in modern Java
// ===================================================================
//        Thread thread = new Thread(
//                ()-> {
//                    System.out.println("Inside lambda of thread");
//                }
//        );

//        thread.start();

// ===================================================================
// Example 4: Using join() to make the main thread wait until
// another thread completes its work
//        Thread worker = new Thread(()-> {
//            try {
//                System.out.println("Worker thread started.");
//                Thread.sleep(2000);
//                System.out.println("Worker thread finished.");
//            } catch (InterruptedException e) {
//                System.out.println("Thread interrupted!");
//            }
//        });

//        worker.start();

        // join() blocks the main thread until "worker" finishes
//        worker.join();
//        System.out.println("Main thread continues after worker completes.");


// ===================================================================
// Example 5: Race condition demo
// Multiple threads try to update the shared variable "value"
// Because the operation is not atomic, you get inconsistent results
// ===================================================================
/*
        Counter counter = new Counter();

        Thread countThread1 = new Thread(counter::incrementCounter);
        Thread countThread2 = new Thread(counter::incrementCounter);

        countThread1.start();
        countThread2.start();

        countThread1.join();
        countThread2.join();

        System.out.println("counter after join: " + counter.getCount());
*/

// ===================================================================
// Example 6: Making increment() thread-safe using synchronized keyword
// Only one thread can enter a synchronized method at a time
// ===================================================================
/*

        SafeCounter safeCounter = new SafeCounter();

        Thread safeCounter1 = new Thread(safeCounter::increment);
        Thread safeCounter2 = new Thread(safeCounter::increment);

        safeCounter1.start();
        safeCounter2.start();

        safeCounter1.join();
        safeCounter2.join();


        // Always prints 2 now
        System.out.println("Safe value = " + safeCounter.getValue());
*/


// ===================================================================
// Example 7: Using ReentrantLock for manual lock control
// Advantage: tryLock(), lockInterruptibly(), timed lock attempts
// ===================================================================
/*
        LockCounter lockCounter = new LockCounter();

        Thread lockThread1 = new Thread(lockCounter::increment);
        Thread lockThread2 = new Thread(lockCounter::increment);

        lockThread1.start();
        lockThread2.start();

        lockThread1.join();
        lockThread2.join();

        System.out.println("Reentrant lock counter value = " + lockCounter.getValue());
*/
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 6; i++) {
            int task = i;
            executorService.submit(()-> {
                System.out.println("Task " + task + ", thread name: " + Thread.currentThread().getName() );
            });
        }

        executorService.shutdown();
    }
}


