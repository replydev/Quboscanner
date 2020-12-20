package me.replydev.utils;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import me.replydev.qubo.Info;

public class Log {
    public static void logln(String s){
        log(s + "\n");
    }
    static void log(String s){
        if(Info.gui) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ITALIAN);
        LocalTime time = LocalTime.now();
        String f = formatter.format(time);
        System.out.print("[" + f + "] - " + s);
    }

    public static void log_to_file(String s,String filename){
        try {
            System.err.println("An issue as been reported on log file");
            FileUtils.doAppend(s,filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
