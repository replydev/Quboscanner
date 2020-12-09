package me.replydev.utils;

public class SystemSpecs {
    public String getOperatingSystem() {
        // System.out.println("Using System Property: " + os);
        return System.getProperty("os.name");
    }
}
