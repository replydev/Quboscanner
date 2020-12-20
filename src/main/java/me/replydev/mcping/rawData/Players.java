package me.replydev.mcping.rawData;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Players {
    @SerializedName("max")
    private int max;
    @SerializedName("online")
    private int online;
    @SerializedName("sample")
    private List<Player> sample;

    public int getMax() {
        return this.max;
    }

    public int getOnline() {
        return this.online;
    }

    public List<Player> getSample() {
        return this.sample;
    }
}
