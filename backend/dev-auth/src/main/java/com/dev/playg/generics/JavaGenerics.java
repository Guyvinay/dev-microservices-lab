package com.dev.playg.generics;

import java.util.ArrayList;
import java.util.List;

public class JavaGenerics {

    public static void main(String[] args) {
        // Covariance (? extends T)
        List<? extends Number> numbers = new ArrayList<Integer>();
        Number num = numbers.get(0); // can read as Number
        // numbers.add(10);          //  compile error

        // Contravariance (? super T)

        List<? super Integer> integers = new ArrayList<Number>();
        integers.add(10);           // can add Integer
        Object obj = integers.get(0); // but read is only Object

    }
}
