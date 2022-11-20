package me.replydev.mcping.data;

import me.replydev.mcping.rawData.Players;
import me.replydev.mcping.rawData.Version;

public class FinalResponse extends MCResponse {

    private final String description;

    public FinalResponse(Players players, Version version, String favicon, String description) {
        this.description = Input.stripMinecraft(description);
        this.favicon = favicon;
        this.players = players;
        this.version = version;
    }

    public Players getPlayers() {
        if (players == null) {
            return new Players();
        }
        return players;
    }

    public Version getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getFavIcon() {
        return favicon;
    }
}
