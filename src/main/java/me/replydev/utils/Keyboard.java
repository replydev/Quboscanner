package me.replydev.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Keyboard {

    private static final BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in)
    );

    // TODO Add @Sneaky Throws when it became supported in Java 19
    public static String s() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
