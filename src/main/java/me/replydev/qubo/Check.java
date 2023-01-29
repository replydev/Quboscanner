package me.replydev.qubo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.replydev.mcping.MCPinger;
import org.replydev.mcping.PingOptions;
import org.replydev.mcping.model.ServerResponse;

@Builder
@Slf4j
public class Check implements Runnable {

    private final PingOptions pingOptions;
    private final int count;
    private final CommandLineArgs commandLineArgs;

    private final AtomicInteger foundServers;
    private final AtomicInteger unfilteredFoundServers;
    private final String filterVersion;
    private final String filterMotd;
    private final int minPlayer;

    public void run() {
        for (int i = 0; i < count; i++) {
            MCPinger mcPinger = MCPinger.builder().pingOptions(pingOptions).build();
            try {
                ServerResponse serverResponse = mcPinger.fetchData();
                unfilteredFoundServers.incrementAndGet();
                if (!isFiltered(serverResponse)) {
                    foundServers.incrementAndGet();
                    log.info(buildEntry(serverResponse));
                }
            } catch (IOException ignored) {
                // Connection has failed, no need to log
            }
        }
    }

    private String buildEntry(ServerResponse serverResponse) {
        return (
            pingOptions.getHostname() +
            ':' +
            pingOptions.getPort() +
            " -> " +
            '(' +
            serverResponse.getVersion().getName() +
            ") - (" +
            serverResponse.getPlayers().getOnline() +
            '/' +
            serverResponse.getPlayers().getMax() +
            ')'
        );
    }

    private boolean isFiltered(ServerResponse serverResponse) {
        if (
            filterVersion != null && !serverResponse.getVersion().getName().contains(filterVersion)
        ) {
            return false;
        }
        if (minPlayer > serverResponse.getPlayers().getOnline()) {
            return false;
        }

        return (
            filterMotd == null ||
            (
                !filterMotd.isBlank() &&
                serverResponse.getDescription().getText().contains(filterMotd)
            )
        );
    }
}
