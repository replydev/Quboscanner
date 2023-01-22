package me.replydev.qubo;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            CLI.init(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
