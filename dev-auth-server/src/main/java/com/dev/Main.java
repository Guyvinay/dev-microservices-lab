package com.dev;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>();
        System.out.println(map.put(1, 3));
        System.out.println(map.put(1, 2));
    }
}