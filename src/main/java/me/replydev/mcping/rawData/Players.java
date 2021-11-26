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

    public void setMax(int max) {
        this.max = max;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public void setSample(List<Player> sample) {
        this.sample = sample;
    }

    public List<Player> getSample() {
        return this.sample == null ? List.of() : this.sample;
    }
}
