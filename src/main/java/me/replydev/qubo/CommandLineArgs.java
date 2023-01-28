package me.replydev.qubo;

import lombok.Value;
import lombok.experimental.NonFinal;
import me.replydev.utils.IpList;
import me.replydev.utils.PortList;
import org.apache.commons.cli.*;

@Value
public class CommandLineArgs {

    Options options;
    IpList ipList;
    PortList portRange;
    boolean skipCommon;
    int timeout;
    String filterVersion;
    String filterMotd;
    int minimumPlayers;
    int count;

    @NonFinal
    CommandLine cmd;

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
        timeout = Integer.parseInt(cmd.getOptionValue("t"));
        filterVersion = cmd.getOptionValue("v", "");
        filterMotd = cmd.getOptionValue("m", "");
        minimumPlayers = Integer.parseInt(cmd.getOptionValue("o", "-1"));
        count = Integer.parseInt(cmd.getOptionValue("c", "1"));
    }

    private static Options buildOptions() {
        Option iprange = new Option("i", "iprange", true, "The IP range to scan");
        iprange.setRequired(true);

        Option portrange = new Option("p", "portrange", true, "The range of ports to scan");
        portrange.setRequired(true);

        Option timeout = new Option("t", "timeout", true, "TCP connection timeout");
        timeout.setRequired(true);

        Option count = new Option("c", "pingcount", true, "Number of ping retries");
        count.setRequired(false);

        Option all = new Option("a", false, "Force Qubo to scan broadcast IPs and common ports");
        all.setRequired(false);

        Option filterVersion = new Option(
            "v",
            "filterversion",
            true,
            "Show only hits with given version"
        );
        filterVersion.setRequired(false);

        Option filterMotd = new Option("m", "filtermotd", true, "Show only hits with given motd");
        filterMotd.setRequired(false);

        Option filterOn = new Option(
            "o",
            "minonline",
            true,
            "Show only hits with at least <arg> players online"
        );
        filterOn.setRequired(false);

        Options options = new Options();
        options.addOption(iprange);
        options.addOption(portrange);
        options.addOption(timeout);
        options.addOption(count);
        options.addOption(all);
        options.addOption(filterVersion);
        options.addOption(filterMotd);
        options.addOption(filterOn);

        return options;
    }

    public void showHelpAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("-range <arg> -ports <arg> -th <arg> -ti <arg>", options);
        System.exit(-1);
    }
}
