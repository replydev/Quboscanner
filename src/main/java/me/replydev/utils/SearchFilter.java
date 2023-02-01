package me.replydev.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Swofty#0001
 */
@Getter
@Setter
public class SearchFilter {
	private int minimumPlayers = -1;
	private String version = null;
	private String motd = null;
}
