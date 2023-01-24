package me.replydev.qubo;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.replydev.utils.KeyboardThread;

public class CLI {

    private static QuboInstance quboInstance;

    public static QuboInstance getQuboInstance() {
        return quboInstance;
    }

    static void init(String[] args) throws IOException {
        printLogo();
        checkEncodingParameter();
        createOutputDir();
        launchKeyboardThread();
        standardRun(args);

        System.out.println(
            "Scan terminated - " +
            quboInstance.getFoundServers().get() +
            " (" +
            quboInstance.getUnfilteredFoundServers().get() +
            " in total)"
        );
        System.exit(0);
    }

    private static void checkEncodingParameter() {
        if (!isUTF8Mode()) {
            System.out.println("The scanner isn't running in UTF-8 mode!");
            System.out.println(
                "Put \"-Dfile.encoding=UTF-8\" in JVM args in order to run the program correctly!"
            );
            System.exit(-1);
        }
    }

    private static void launchKeyboardThread() {
        ExecutorService inputService = Executors.newSingleThreadExecutor();
        inputService.execute(new KeyboardThread());
    }

    private static void createOutputDir() throws IOException {
        Path outputsDir = Paths.get("outputs");
        if (!Files.isDirectory(outputsDir)) {
            Files.createDirectory(outputsDir);
        }
    }

    private static void printLogo() {
        System.out.println(
                """
                           ____        _           _____                                \s
                          / __ \\      | |         / ____|                               \s
                         | |  | |_   _| |__   ___| (___   ___ __ _ _ __  _ __   ___ _ __\s
                         | |  | | | | | '_ \\ / _ \\\\___ \\ / __/ _` | '_ \\| '_ \\ / _ \\ '__|
                         | |__| | |_| | |_) | (_) |___) | (_| (_| | | | | | | |  __/ |  \s
                          \\___\\_\\\\__,_|_.__/ \\___/_____/ \\___\\__,_|_| |_|_| |_|\\___|_|  \s
                                                                                        \
                        """
        );
        System.out.println(
            "By @replydev on Telegram\nVersion " + Info.version + " " + Info.otherVersionInfo
        );
    }

    private static void standardRun(String[] args) {
        CommandLineArgs commandLineArgs = new CommandLineArgs(args);
        quboInstance = new QuboInstance(commandLineArgs);
        try {
            quboInstance.run();
        } catch (NumberFormatException e) {
            commandLineArgs.showHelpAndExit();
        }
    }

    private static boolean isUTF8Mode() {
        List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        return arguments.contains("-Dfile.encoding=UTF-8");
    }
}
