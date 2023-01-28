package me.replydev.utils;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@UtilityClass
public class Keyboard {

    private final BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in)
    );

    // TODO Add @Sneaky Throws when it became supported in Java 19
    public String s() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
