package me.replydev.qubo;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import me.replydev.utils.IpList;
import me.replydev.utils.PortList;
import org.apache.commons.cli.*;

/**
 * This class is responsible for parsing the command line arguments.
 * @author ReplyDev, Swofty
 */
@Getter
public class CommandLineArgs {

    private final Options options;
    private final IpList ipList;
    private final PortList portRange;
    private final boolean skipCommon;
    private final int timeout;
    private final SearchFilter searchFilter;
    private final int count;

    @Setter
    private CommandLine cmd;

    /**
     * Constructor for CommandLineArgs.
     * @param command The array of command line arguments to be parsed.
     * @throws NumberFormatException If parsing of numeric values fails.
     */
    public CommandLineArgs(String[] command) throws NumberFormatException {
        options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, command);
        } catch (ParseException e) {
            showHelpAndExit();
        }
        ipList = new IpList(cmd.getOptionValue("i"));
        portRange = new PortList(cmd.getOptionValue("p"));
        skipCommon = !cmd.hasOption("all");
        timeout = Integer.parseInt(cmd.getOptionValue("t", "1000"));

        searchFilter =
            SearchFilter
                .builder()
                .version(cmd.getOptionValue("v", null))
                .motd(cmd.getOptionValue("m", null))
                .minimumPlayers(Integer.parseInt(cmd.getOptionValue("o", "0")))
                .build();

        count = Integer.parseInt(cmd.getOptionValue("c", "1"));
    }

    /**
     * Builds the command line options.
     * @see Options
     * @return Options The command line options.
     */
    private static Options buildOptions() {
        Options options = new Options();

        options.addRequiredOption("i", "iprange", true, "The IP range to scan");
        options.addRequiredOption("p", "portrange", true, "The range of ports to scan");
        options.addOption("t", "timeout", true, "TCP connection timeout");
        options.addOption("c", "pingcount", true, "Number of ping retries");
        options.addOption("a", "all", false, "Force to scan broadcast IPs and common ports");
        options.addOption("v", "filterversion", true, "Show only hits with given version");
        options.addOption("m", "filtermotd", true, "Show only hits with given motd");
        options.addOption("o", "minonline", true, "Show only hits with at least <arg> players online");

        return options;
    }

    /**
     * Prints help information using the command line options and exits the program
     * with exit code -1.
     */
    public void showHelpAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Usage: -i <iprange> -p <portrange> -t <timeout> [-c <pingcount>] [...]", options);
        System.exit(-1);
    }
}
