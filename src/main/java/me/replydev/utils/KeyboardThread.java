package me.replydev.utils;

import me.replydev.qubo.CLI;

public class KeyboardThread implements Runnable {

    @Override
    public void run() {
        while (true) {
            String s = Keyboard.s();
            if (s == null) continue;
            s = s.toLowerCase();
            switch (s) {
                case "help":
                    System.out.println("Commands: \n" +
                            "status - show current ip\n" +
                            "threads - show thread in execution\n" +
                            "skip - skip current scan and start the next one\n" +
                            "exit - exit the program");
                    break;
                case "status":
                    Log.logln(CLI.getQuboInstance().getCurrent());
                    break;
                case "threads":
                    Log.logln("Current threads: " + CLI.getQuboInstance().getThreads());
                    break;
                case "skip":
                    Log.logln("Skipping \"" + CLI.getQuboInstance().getFilename() + "\"");
                    CLI.getQuboInstance().stop();
                    break;
                case "exit":
                    if (CLI.getQuboInstance().getStartTime() != null)
                        System.out.println(CLI.getQuboInstance().getScanTime(CLI.getQuboInstance().getStartTime()));
                    Log.logln("Bye");
                    System.exit(0);
                    break;
                case "":
                    break;
                default:
                    Log.logln("Command \"" + s + "\" not found, digit help to get all commands");
            }
        }
    }
}
