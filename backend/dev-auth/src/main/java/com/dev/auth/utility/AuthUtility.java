package com.dev.auth.utility;

import java.util.Random;

public class AuthUtility {

    public static long generateRandomNumber(int digits) {
        if (digits < 1 || digits > 18) {
            throw new IllegalArgumentException("Digits must be between 1 and 18 (long max limit)");
        }

        long min = (long) Math.pow(10, digits - 1);
        long max = (long) Math.pow(10, digits) - 1;

        Random random = new Random();
        return min + ((long) (random.nextDouble() * (max - min)));
    }

}
