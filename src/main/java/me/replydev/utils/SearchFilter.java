package me.replydev.utils;

import lombok.Builder;
import lombok.Value;

/**
 * @author Swofty#0001
 */
@Value
@Builder
public class SearchFilter {

    int minimumPlayers;
    String version;
    String motd;
}
