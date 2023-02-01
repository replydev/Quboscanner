package me.replydev.qubo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import me.replydev.utils.SearchFilter;
import org.replydev.mcping.MCPinger;
import org.replydev.mcping.PingOptions;
import org.replydev.mcping.model.ServerResponse;

@Builder
@Slf4j
public class PingRunnable implements Runnable {

    private final PingOptions pingOptions;
    private final SearchFilter filter;
    private final int count;
    private final CommandLineArgs commandLineArgs;
    private final AtomicInteger foundServers;
    private final AtomicInteger unfilteredFoundServers;

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
            ") - (" +
            serverResponse.getDescription().getText() +
            ')'
        );
    }

    /**
     *  If checks placed in order of commonality of argument
     *
     * @author Swofty#0001
     */
    private boolean isFiltered(ServerResponse serverResponse) {
        if (serverResponse.getPlayers().getOnline() < filter.getMinimumPlayers()) {
            return true;
        }

        if (serverResponse.getDescription().getText().contains(filter.getMotd())) {
            return true;
        }

        return serverResponse.getVersion().getName().contains(filter.getVersion());
    }
}
