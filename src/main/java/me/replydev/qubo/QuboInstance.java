package me.replydev.qubo;

import me.replydev.mcping.net.Check;
import me.replydev.mcping.net.SimplePing;
import me.replydev.utils.FileUtils;
import me.replydev.utils.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QuboInstance {

    public final InputData inputData;
    public final AtomicInteger currentThreads;
    private final int[] COMMON_PORTS = {25, 80, 443, 20, 21, 22, 23, 143, 3306, 3389, 53, 67, 68, 110};
    private String ip; // current ip
    private int port; // current port
    private boolean stop;
    private long serverCount = 0;

    private ZonedDateTime start;

    public QuboInstance(InputData inputData) {
        this.inputData = inputData;
        this.currentThreads = new AtomicInteger();
        stop = false;

        if (this.inputData.isDebugMode()) {
            Log.logln("Debug mode enabled");
        }
        if (this.inputData.getPortrange().size() < 1500) {
            Log.logln("Skipping the initial ping due to the few ports inserted");
            this.inputData.setPing(false);
        }
    }

    public void run() {
        start = ZonedDateTime.now();
        ///ZonedDateTime start = ZonedDateTime.now();
        if (inputData.isOutput()) {
            FileUtils.appendToFile("Scanner started on: " + start.format(DateTimeFormatter.RFC_1123_DATE_TIME), inputData.getFilename());
        }
        try {
            checkServersExecutor();
        } catch (InterruptedException e) {
            Log.log_to_file(e.toString(), "log.txt");
        }
        ZonedDateTime end = ZonedDateTime.now();
        if (inputData.isOutput())
            FileUtils.appendToFile(
                    "Scanner ended on: " + end.format(DateTimeFormatter.RFC_1123_DATE_TIME),
                    inputData.getFilename());
        Log.logln(getScanTime(start, end));

    }

    private void checkServersExecutor() throws InterruptedException, NumberFormatException {
        ExecutorService checkService = Executors.newFixedThreadPool(inputData.getThreads());
        Log.logln("Checking Servers...");

        while (inputData.getIpList().hasNext()) {
            ip = inputData.getIpList().getNext();
            try {
                InetAddress address = InetAddress.getByName(ip);
                if (inputData.isPing()) {
                    SimplePing simplePing = new SimplePing(address, inputData.getTimeout());
                    if (!simplePing.isAlive())
                        continue;
                }
                if (inputData.isSkipCommonPorts() && isLikelyBroadcast(address))
                    continue;
            } catch (UnknownHostException ignored) {
            }

            while (inputData.getPortrange().hasNext()) {
                if (stop) {
                    checkService.shutdown();
                    checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                    return;
                }

                port = inputData.getPortrange().get();
                if (isCommonPort(port)) {
                    inputData.getPortrange().next();
                    continue;
                }

                if (currentThreads.get() < inputData.getThreads()) {
                    currentThreads.incrementAndGet();
                    checkService.execute(
                            new Check(ip, port, inputData.getTimeout(), inputData.getFilename(), inputData.getCount(),
                                    this, inputData.getVersion(), inputData.getMotd(), inputData.getMinPlayer()));
                    inputData.getPortrange().next(); // va al successivo
                    serverCount++;
                }
            }
            inputData.getPortrange().reload();
        }
        checkService.shutdown();
        checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public String getCurrent() {
        return "Current ip: " + ip + ":" + port + " - (" + String.format("%.2f", getPercentage()) + "%)";
    }

    public int getThreads() {
        return currentThreads.get();
    }

    public void stop() {
        this.stop = true;
    }

    public String getFilename() {
        return this.inputData.getFilename();
    }

    private boolean isCommonPort(int port) {
        if (!inputData.isSkipCommonPorts()) {
            return false;
        }
        for (int i : COMMON_PORTS) {
            if (i == port) {
                return true;
            }
        }
        return false;
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
