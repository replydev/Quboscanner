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
import me.replydev.utils.IpList;
import me.replydev.utils.PortList;
import org.replydev.mcping.PingOptions;

/**
 * QuboInstance handles the scanning process for the application.
 * @author ReplyDev, Swofty
 */
@Slf4j
public class QuboInstance {

    private static final Set<Integer> STANDARDIZED_PORTS = Set.of(
            20,    // File Transfer Protocol (FTP) data transfer
            21,    // File Transfer Protocol (FTP) command control
            22,    // Secure Shell (SSH) protocol for secure logins, file transfers, and port forwarding
            23,    // Telnet protocol for unencrypted text communications
            25,    // Simple Mail Transfer Protocol (SMTP) for email routing between mail servers
            53,    // Domain Name System (DNS) service for translating domain names to IP addresses
            67,    // Bootstrap Protocol (BOOTP) Server; often used by DHCP
            68,    // Bootstrap Protocol (BOOTP) Client; often used by DHCP
            80,    // Hypertext Transfer Protocol (HTTP) used for unsecured web traffic
            110,   // Post Office Protocol (POP3) used by email clients to retrieve messages from a server
            143,   // Internet Message Access Protocol (IMAP) for email retrieval
            443,   // Hypertext Transfer Protocol Secure (HTTPS) for secure web traffic
            3306,  // Default port for the MySQL database management system
            3389,  // Remote Desktop Protocol (RDP) for Windows-based systems remote management
            6379   // Redis data structure store
    );
    private final CommandLineArgs commandLineArgs;

    @Getter
    private final AtomicInteger foundServers = new AtomicInteger();
    @Getter
    private final AtomicInteger unfilteredFoundServers = new AtomicInteger();

    /**
     * Constructs a QuboInstance with the given command line arguments.
     * @param commandLineArgs The command line arguments for the scan configuration.
     */
    public QuboInstance(CommandLineArgs commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
    }

    /**
     * Starts the scanning process.
     */
    public void run() {
        ZonedDateTime start = ZonedDateTime.now();
        try {
            if (!checkServersExecutor()) {
                log.error("Something went wrong awaiting thread termination.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted during execution.", e);
        }
        ZonedDateTime end = ZonedDateTime.now();
        log.info(calculateScanDuration(start, end));
    }

    private boolean checkServersExecutor() throws InterruptedException, NumberFormatException {
        try (ExecutorService checkService = Executors.newVirtualThreadPerTaskExecutor()) {
            log.info("Checking Servers...");

            IpList ipList = commandLineArgs.getIpList();
            for (String ip : ipList) {
                try {
                    InetAddress address = InetAddress.getByName(ip);
                    if (commandLineArgs.isSkipCommon() && isLikelyBroadcast(address)) {
                        continue;
                    }
                } catch (UnknownHostException ignored) {
                    // We can ignore this exception
                }

                PortList portRange = commandLineArgs.getPortRange();
                for (int port : portRange) {
                    if (commandLineArgs.isSkipCommon() && STANDARDIZED_PORTS.contains(port)) {
                        continue;
                    }

                    PingOptions pingOptions = PingOptions
                        .builder()
                        .hostname(ip)
                        .port(port)
                        .timeout(commandLineArgs.getTimeout())
                        .build();

                    PingRunnable pingJob = PingRunnable
                        .builder()
                        .pingOptions(pingOptions)
                        .foundServers(foundServers)
                        .unfilteredFoundServers(unfilteredFoundServers)
                        .count(commandLineArgs.getCount())
                        .filter(commandLineArgs.getSearchFilter())
                        .build();

                    checkService.execute(pingJob);
                }
            }
            checkService.shutdown();
            return checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }

    private static boolean isLikelyBroadcast(InetAddress address) {
        byte[] bytes = address.getAddress();
        return bytes[bytes.length - 1] == 0 || bytes[bytes.length - 1] == (byte) 0xFF;
    }

    /**
     * Calculates the duration of the scan.
     *
     * @param start The start time of the scan.
     * @param end   The end time of the scan.
     * @return A string representation of the scan duration.
     */
    private String calculateScanDuration(ZonedDateTime start, ZonedDateTime end) {
        long seconds = ChronoUnit.SECONDS.between(start, end);
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30; // Approximation
        long years = days / 365; // Approximation

        return String.format("Scan time: %d years, %d months, %d days, %d hours, %d minutes, %d seconds",
                years, months % 12, days % 30, hours % 24, minutes % 60, seconds % 60);
    }
}
