package me.replydev.qubo;

/**
 * The Main class is the entry point of Qubo.
 * It delegates the start of the application to the CLI class.
 * @see CLI
 */
public class Main {

    /**
     * The main method that is executed when the program is started.
     * It calls the init method of the CLI class, passing along any command line arguments.
     * @param args Command line arguments passed to the program.
     */
    public static void main(String[] args) {
        CLI.init(args);
    }
}