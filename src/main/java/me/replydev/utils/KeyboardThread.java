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
                    System.out.println(
                        """
                                        Commands:
                                        status - show current ip
                                        skip - skip current scan and start the next one
                                        exit - exit the program
                                    """
                    );
                    break;
                case "status":
                    System.out.println(CLI.getQuboInstance().getCurrent());
                    break;
                case "skip":
                    System.out.println("Skipping");
                    CLI.getQuboInstance().stop();
                    break;
                case "exit":
                    if (CLI.getQuboInstance().getStartTime() != null) System.out.println(
                        CLI.getQuboInstance().getScanTime(CLI.getQuboInstance().getStartTime())
                    );
                    System.out.println("Bye");
                    System.exit(0);
                    break;
                case "":
                    break;
                default:
                    System.out.println(
                        "Command \"" + s + "\" not found, digit help to get all commands"
                    );
            }
        }
    }
}
