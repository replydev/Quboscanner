package me.replydev.qubo;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.replydev.mcping.MCPinger;
import org.replydev.mcping.PingOptions;
import org.replydev.mcping.model.ServerResponse;

/**
 * The PingRunnable class is designed to perform a ping operation on a server
 * multiple times, as defined by the count, and update the found server counts.
 * @author ReplyDev, Swofty
 */
@Builder
@Slf4j
public class PingRunnable implements Runnable {

    private final PingOptions pingOptions;
    private final SearchFilter filter;
    private final int count;
    private final CommandLineArgs commandLineArgs;
    private final AtomicInteger foundServers;
    private final AtomicInteger unfilteredFoundServers;

    /**
     * Executes the ping operation the specified number of times.
     */
    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            try {
                MCPinger mcPinger = MCPinger.builder().pingOptions(pingOptions).build();
                ServerResponse serverResponse = mcPinger.fetchData();
                processServerResponse(serverResponse);
            } catch (IOException ignored) {
                // Connection has failed, no need to log.
            }
        }
    }

    /**
     * Processes the server response and updates server counts accordingly.
     * @param serverResponse The response from the server.
     */
    private void processServerResponse(ServerResponse serverResponse) {
        unfilteredFoundServers.incrementAndGet();
        if (!isFiltered(serverResponse)) {
            foundServers.incrementAndGet();
            log.info(buildEntry(serverResponse));
        }
    }

    /**
     * Builds a log entry string for a server response.
     * @param serverResponse The server response to build the log entry for.
     * @return A string representation of the log entry.
     */
    private String buildEntry(ServerResponse serverResponse) {
        return String.format("%s:%d -> (%s) - (%d/%d) - (%s)",
                pingOptions.getHostname(),
                pingOptions.getPort(),
                serverResponse.getVersion().getName(),
                serverResponse.getPlayers().getOnline(),
                serverResponse.getPlayers().getMax(),
                serverResponse.getDescription().getText());
    }

    /**
     * Checks if the server response meets the filter criteria.
     * @param serverResponse The server response to check.
     * @return True if the response does not meet the criteria (is filtered out), false otherwise.
     */
    private boolean isFiltered(ServerResponse serverResponse) {
        if (filter.getMinimumPlayers() > 0 && serverResponse.getPlayers().getOnline() < filter.getMinimumPlayers()) {
            return true;
        }

        String motdFilter = Optional.ofNullable(filter.getMotd()).orElse("");
        if (!motdFilter.isEmpty() && !serverResponse.getDescription().getText().contains(motdFilter)) {
            return true;
        }

        String versionFilter = Optional.ofNullable(filter.getVersion()).orElse("");
        return !versionFilter.isEmpty() && !serverResponse.getVersion().getName().contains(versionFilter);
    }
}
