package controller;

import java.util.Random;

public class NameGenerator {

    private static Random RAND = new Random();

    private static String[] names = new String[] {
            "Alice",
            "Bob"
    };

    public static String newName() {
        return names[RAND.nextInt(names.length)];
    }
}
