package com.dev.multithreading;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;
import java.util.stream.IntStream;

@Getter
@Setter
public class SynchronizedMethods {

    private int sum = 0;

    public void calculate() {
        System.out.println("synchronisedCalculate called: "+getSum());
        setSum(getSum() + 1);
    }
    public synchronized void synchronisedCalculate() {
        System.out.println("synchronisedCalculate called: "+getSum());
        setSum(getSum() + 1);
    }

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.range(0,10).forEach((e)->{
            System.out.println("ele: "+e);
        });
        Future<String> future = executorService.submit(() -> "Hello World");
        try {
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }
}
