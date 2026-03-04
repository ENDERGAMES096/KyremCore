package com.kyrem.core.util;

import java.util.concurrent.ThreadLocalRandom;

public class CustomMath {

    public static boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= num / 2; i++)
            if ((num % i) == 0) return false;
        return true;
    }

    public static boolean isDouble(String txt) {
        if (txt == null) return false;
        try {
            Double.parseDouble(txt);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
