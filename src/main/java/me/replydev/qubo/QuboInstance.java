package me.replydev.qubo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import me.replydev.utils.IpList;
import me.replydev.utils.PortList;
import org.replydev.mcping.PingOptions;

public class QuboInstance {

    private static final Set<Integer> COMMON_PORTS = Set.of(
        25,
        80,
        443,
        20,
        21,
        22,
        23,
        143,
        3306,
        3389,
        53,
        67,
        68,
        110
    );
    public final CommandLineArgs commandLineArgs;

    @Getter
    private final AtomicInteger foundServers;

    @Getter
    private final AtomicInteger unfilteredFoundServers;

    private String currentIp;
    private int currentPort;
    private boolean stop;
    private long serverCount = 0;

    private ZonedDateTime start;

    public QuboInstance(CommandLineArgs commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
        this.foundServers = new AtomicInteger();
        this.unfilteredFoundServers = new AtomicInteger();
        stop = false;
    }

    public void run() {
        start = ZonedDateTime.now();
        try {
            if (!checkServersExecutor()) {
                System.err.println("Something has gone wrong in thread termination awaiting...");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ZonedDateTime end = ZonedDateTime.now();
        System.out.println(getScanTime(start, end));
    }

    private boolean checkServersExecutor() throws InterruptedException, NumberFormatException {
        ExecutorService checkService = Executors.newVirtualThreadPerTaskExecutor();
        System.out.println("Checking Servers...");

        IpList ipList = commandLineArgs.getIpList();
        for (String ip : ipList) {
            currentIp = ip;
            try {
                InetAddress address = InetAddress.getByName(ip);
                if (commandLineArgs.isSkipCommon() && isLikelyBroadcast(address)) {
                    continue;
                }
            } catch (UnknownHostException ignored) {}

            PortList portRange = commandLineArgs.getPortRange();
            for (int port : portRange) {
                currentPort = port;
                if (stop) {
                    checkService.shutdown();
                    return checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                }

                if (isCommonPort(port)) {
                    continue;
                }

                PingOptions pingOptions = PingOptions
                    .builder()
                    .hostname(ip)
                    .port(port)
                    .timeout(commandLineArgs.getTimeout())
                    .build();

                Check pingJob = Check
                    .builder()
                    .pingOptions(pingOptions)
                    .foundServers(foundServers)
                    .unfilteredFoundServers(unfilteredFoundServers)
                    .count(commandLineArgs.getCount())
                    .filterMotd(commandLineArgs.getMotd())
                    .filterVersion(commandLineArgs.getVersion())
                    .minPlayer(commandLineArgs.getMinPlayer())
                    .build();

                checkService.execute(pingJob);
                serverCount++;
            }
            portRange.reload();
        }
        checkService.shutdown();
        return checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public String getCurrent() {
        return (
            "Current ip: " +
                    currentIp +
            ":" +
                    currentPort +
            " - (" +
            String.format("%.2f", getPercentage()) +
            "%)"
        );
    }

    public void stop() {
        this.stop = true;
    }

    private boolean isCommonPort(int port) {
        return !commandLineArgs.isSkipCommon() || COMMON_PORTS.contains(port);
    }

    public double getPercentage() {
        // 15 : 15000 = x : 100
        double max = commandLineArgs.getIpList().getCount() * commandLineArgs.getPortRange().size();
        return serverCount * 100 / max;
    }

    private boolean isLikelyBroadcast(InetAddress address) {
        byte[] bytes = address.getAddress();
        return bytes[bytes.length - 1] == 0 || bytes[bytes.length - 1] == (byte) 0xFF;
    }

    public ZonedDateTime getStartTime() {
        return this.start;
    }

    public String getScanTime(ZonedDateTime start) {
        return getScanTime(start, ZonedDateTime.now());
    }

    public String getScanTime(ZonedDateTime start, ZonedDateTime end) {
        ZonedDateTime tempDateTime = ZonedDateTime.from(start);

        long years = tempDateTime.until(end, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(end, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(end, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(end, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(end, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(end, ChronoUnit.SECONDS);

        StringBuilder builder = new StringBuilder();
        if (seconds != 0) builder.append(seconds).append(" seconds");
        if (minutes != 0) builder.insert(0, minutes + " minutes, ");
        if (hours != 0) builder.insert(0, minutes + " hours, ");
        if (days != 0) builder.insert(0, minutes + " days, ");
        if (months != 0) builder.insert(0, minutes + " months, ");
        if (years != 0) builder.insert(0, minutes + " years, ");
        builder.insert(0, "Scan time: ");
        return builder.toString();
    }
}
