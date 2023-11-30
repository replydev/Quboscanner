package me.replydev.qubo;

import java.lang.management.ManagementFactory;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The CLI class handles the command line interface functionality, including
 * initializing the application, printing the logo, and starting the standard
 * run process.
 * @author ReplyDev
 */
@Slf4j
@UtilityClass
public class CLI {

    private static final String VERSION = "1.1.0";

    private static final String LOGO_TEMPLATE = """
                                   ____        _           _____                                \s
                                  / __ \\      | |         / ____|                               \s
                                 | |  | |_   _| |__   ___| (___   ___ __ _ _ __  _ __   ___ _ __\s
                                 | |  | | | | | '_ \\ / _ \\\\___ \\ / __/ _` | '_ \\| '_ \\ / _ \\ '__|
                                 | |__| | |_| | |_) | (_) |___) | (_| (_| | | | | | | |  __/ |  \s
                                  \\___\\_\\\\__,_|_.__/ \\___/_____/ \\___\\__,_|_| |_|_| |_|\\___|_|  \s
                                                                                                                   
                                    By @replydev on Telegram
                                    Version %s
                                """;

    private QuboInstance quboInstance;

    /**
     * Initializes the application with given command line arguments.
     * @param args The command line arguments.
     */
    void init(String[] args) {
        printLogo();
        standardRun(args);

        log.info("Scan terminated - {} ({}) in total",
                quboInstance.getFoundServers().get(),
                quboInstance.getUnfilteredFoundServers().get()
        );
        System.exit(0);
    }

    /**
     * Prints the application logo with the current version.
     */
    private void printLogo() {
        log.info(LOGO_TEMPLATE.replace("%s", VERSION));
    }

    /**
     * Executes the standard application run process.
     * @param args The command line arguments.
     */
    private void standardRun(String[] args) {
        try {
            CommandLineArgs commandLineArgs = new CommandLineArgs(args);
            quboInstance = new QuboInstance(commandLineArgs);
            quboInstance.run();
        } catch (NumberFormatException e) {
            log.error("There was an error parsing the numbers.", e);
            new CommandLineArgs(args).showHelpAndExit();
        }
    }
}
