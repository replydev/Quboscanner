package me.replydev.mcping.net;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import me.replydev.qubo.InputData;
import org.replydev.mcping.MCPinger;
import org.replydev.mcping.PingOptions;
import org.replydev.mcping.model.ServerResponse;

@Builder
@Slf4j
public class Check implements Runnable {

    private final PingOptions pingOptions;
    private final String filename;
    private final int count;
    private final InputData inputData;

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

            }
        }
    }

    private String buildEntry(ServerResponse serverResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(pingOptions.getHostname()).append(':').append(pingOptions.getPort());
        stringBuilder.append(" -> ");
        stringBuilder.append('(').append(serverResponse.getVersion().getName()).append(") - (");
        stringBuilder
            .append(serverResponse.getPlayers().getOnline())
            .append('/')
            .append(serverResponse.getPlayers().getMax()).append(')');
        return stringBuilder.toString();
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

        return filterMotd == null || (!filterMotd.isBlank() && serverResponse.getDescription().getText().contains(filterMotd));
    }
}
