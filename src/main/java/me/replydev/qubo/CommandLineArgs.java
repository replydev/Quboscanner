package me.replydev.qubo;

import me.replydev.utils.IpList;
import me.replydev.utils.PortList;
import org.apache.commons.cli.*;

public class CommandLineArgs {

    private final Options options;
    private IpList ipList;
    private PortList portRange;
    private CommandLine cmd;

    public CommandLineArgs(String[] command) throws NumberFormatException {
        options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, command);
            parseIpRange();
            parsePortRange();
        } catch (ParseException e) {
            showHelpAndExit();
        }
    }

    private void parsePortRange() throws ParseException {
        String portRange = cmd.getOptionValue("p");
        try {
            this.portRange = new PortList(portRange);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }

    private void parseIpRange() throws ParseException {
        String ipRange = cmd.getOptionValue("i");
        try {
            ipList = new IpList(ipRange);
        } catch (RuntimeException e) {
            throw new ParseException(e.getMessage());
        }
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

    public boolean isSkipCommon() {
        return !cmd.hasOption("all");
    }

    public int getCount() {
        return Integer.parseInt(cmd.getOptionValue("c", "1"));
    }

    public IpList getIpList() {
        return ipList;
    }

    public PortList getPortRange() {
        return portRange;
    }

    public int getTimeout() {
        return Integer.parseInt(cmd.getOptionValue("t"));
    }

    public String getMotd() {
        return cmd.getOptionValue("m", "");
    }

    public String getVersion() {
        return cmd.getOptionValue("v", "");
    }

    public int getMinPlayer() {
        return Integer.parseInt(cmd.getOptionValue("o", "-1"));
    }
}
