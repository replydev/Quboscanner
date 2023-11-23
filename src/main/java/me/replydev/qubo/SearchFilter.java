package me.replydev.qubo;

import lombok.Builder;
import lombok.Value;

/**
 * @author Swofty
 */
@Value
@Builder
public class SearchFilter {

    int minimumPlayers;
    String version;
    String motd;
}
