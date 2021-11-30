package me.replydev.mcping;

public class PingOptions {

    private String hostname;
    private int port;
    private int timeout;
    private int protocolVersion = -1;

    public PingOptions setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public PingOptions setPort(int port) {
        this.port = port;
        return this;
    }

    public PingOptions setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    String getHostname() {
        return this.hostname;
    }

    int getPort() {
        return this.port;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocol) {
        this.protocolVersion = protocol;
    }
}
