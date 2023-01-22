package me.replydev.utils;

import lombok.extern.slf4j.Slf4j;
import me.replydev.qubo.CLI;

@Slf4j
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
                            threads - show thread in execution
                            skip - skip current scan and start the next one
                            exit - exit the program"""
                    );
                    break;
                case "status":
                    log.info(CLI.getQuboInstance().getCurrent());
                    break;
                case "skip":
                    log.info("Skipping \"" + CLI.getQuboInstance().getFilename() + "\"");
                    CLI.getQuboInstance().stop();
                    break;
                case "exit":
                    if (CLI.getQuboInstance().getStartTime() != null) System.out.println(
                        CLI.getQuboInstance().getScanTime(CLI.getQuboInstance().getStartTime())
                    );
                    log.info("Bye");
                    System.exit(0);
                    break;
                case "":
                    break;
                default:
                    log.info("Command \"" + s + "\" not found, digit help to get all commands");
            }
        }
    }
}
