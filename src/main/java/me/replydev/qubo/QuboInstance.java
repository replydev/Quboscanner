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
import lombok.extern.slf4j.Slf4j;
import org.replydev.mcping.PingOptions;

@Slf4j
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
    public final InputData inputData;

    @Getter
    private final AtomicInteger foundServers;

    @Getter
    private final AtomicInteger unfilteredFoundServers;

    private String ip; // current ip
    private int port; // current port
    private boolean stop;
    private long serverCount = 0;

    private ZonedDateTime start;

    public QuboInstance(InputData inputData) {
        this.inputData = inputData;
        this.foundServers = new AtomicInteger();
        this.unfilteredFoundServers = new AtomicInteger();
        stop = false;

        if (this.inputData.isDebugMode()) {
            log.info("Debug mode enabled");
        }
        if (this.inputData.getPortrange().size() < 1500) {
            log.info("Skipping the initial ping due to the few ports inserted");
            this.inputData.setPing(false);
        }
    }

    public void run() {
        start = ZonedDateTime.now();
        try {
            if (!checkServersExecutor()) {
                log.error("Something has gone wrong in thread termination awaiting...");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ZonedDateTime end = ZonedDateTime.now();
        if (inputData.isOutput()) log.info(getScanTime(start, end));
    }

    private boolean checkServersExecutor() throws InterruptedException, NumberFormatException {
        ExecutorService checkService = Executors.newVirtualThreadPerTaskExecutor();
        log.info("Checking Servers...");

        while (inputData.getIpList().hasNext()) {
            ip = inputData.getIpList().getNext();
            try {
                InetAddress address = InetAddress.getByName(ip);
                if (inputData.isSkipCommonPorts() && isLikelyBroadcast(address)) {
                    continue;
                }
            } catch (UnknownHostException ignored) {}

            while (inputData.getPortrange().hasNext()) {
                if (stop) {
                    checkService.shutdown();
                    return checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                }

                port = inputData.getPortrange().get();
                if (isCommonPort(port)) {
                    inputData.getPortrange().next();
                    continue;
                }

                PingOptions pingOptions = PingOptions
                    .builder()
                    .hostname(ip)
                    .port(port)
                    .timeout(inputData.getTimeout())
                    .build();

                Check pingJob = Check
                    .builder()
                    .pingOptions(pingOptions)
                    .foundServers(foundServers)
                    .unfilteredFoundServers(unfilteredFoundServers)
                    .count(inputData.getCount())
                    .filename(inputData.getFilename())
                    .filterMotd(inputData.getMotd())
                    .filterVersion(inputData.getVersion())
                    .minPlayer(inputData.getMinPlayer())
                    .build();

                checkService.execute(pingJob);
                inputData.getPortrange().next();
                serverCount++;
            }
            inputData.getPortrange().reload();
        }
        checkService.shutdown();
        return checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public String getCurrent() {
        return (
            "Current ip: " +
            ip +
            ":" +
            port +
            " - (" +
            String.format("%.2f", getPercentage()) +
            "%)"
        );
    }

    public void stop() {
        this.stop = true;
    }

    public String getFilename() {
        return this.inputData.getFilename();
    }

    private boolean isCommonPort(int port) {
        return !inputData.isSkipCommonPorts() || COMMON_PORTS.contains(port);
    }

    public double getPercentage() {
        // 15 : 15000 = x : 100
        double max = inputData.getIpList().getCount() * inputData.getPortrange().size();
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
