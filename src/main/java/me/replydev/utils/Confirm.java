package me.replydev.utils;

public class Confirm {

    public static boolean getConfirm(String text) {
        return Keyboard.s(text + " (y/n): ").equalsIgnoreCase("y");
    }
}
