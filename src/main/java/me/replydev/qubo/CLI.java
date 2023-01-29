package me.replydev.qubo;

import java.lang.management.ManagementFactory;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CLI {

    private QuboInstance quboInstance;

    void init(String[] args) {
        printLogo();
        checkEncodingParameter();
        standardRun(args);

        log.info(
            "Scan terminated - " +
            quboInstance.getFoundServers().get() +
            " (" +
            quboInstance.getUnfilteredFoundServers().get() +
            " in total)"
        );
        System.exit(0);
    }

    private void checkEncodingParameter() {
        if (!isUTF8Mode()) {
            log.info("The scanner isn't running in UTF-8 mode!");
            log.info(
                "Put \"-Dfile.encoding=UTF-8\" in JVM args in order to run the program correctly!"
            );
            System.exit(-1);
        }
    }

    private void printLogo() {
        log.info(
            String.format(
                """
                                   ____        _           _____                                \s
                                  / __ \\      | |         / ____|                               \s
                                 | |  | |_   _| |__   ___| (___   ___ __ _ _ __  _ __   ___ _ __\s
                                 | |  | | | | | '_ \\ / _ \\\\___ \\ / __/ _` | '_ \\| '_ \\ / _ \\ '__|
                                 | |__| | |_| | |_) | (_) |___) | (_| (_| | | | | | | |  __/ |  \s
                                  \\___\\_\\\\__,_|_.__/ \\___/_____/ \\___\\__,_|_| |_|_| |_|\\___|_|  \s
                                                                                                                   
                                    By @replydev on Telegram
                                    Version %s
                                """,
                Info.VERSION
            )
        );
    }

    private void standardRun(String[] args) {
        CommandLineArgs commandLineArgs = new CommandLineArgs(args);
        quboInstance = new QuboInstance(commandLineArgs);
        try {
            quboInstance.run();
        } catch (NumberFormatException e) {
            commandLineArgs.showHelpAndExit();
        }
    }

    private boolean isUTF8Mode() {
        List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        return arguments.contains("-Dfile.encoding=UTF-8");
    }
}
