package me.replydev.mcping.rawData;

import com.google.gson.annotations.SerializedName;

public class Version {
    @SerializedName("name")
    private String name;
    @SerializedName("protocol")
    private int protocol = Integer.MIN_VALUE; // Don't use -1 as this has special meaning

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setName(String a){
        name = a;
    }

    public String getName() {
        return this.name;
    }

    public int getProtocol() {
        return protocol;
    }
}
