package me.replydev.mcping.net;

import me.replydev.utils.SystemSpecs;

import java.io.IOException;
import java.net.InetAddress;

public class SimplePing {

    private final InetAddress address;
    private final int timeout;

    public SimplePing(InetAddress address, int timeout) {
        this.address = address;
        this.timeout = timeout;
    }

    public boolean isAlive() {
        SystemSpecs specs = new SystemSpecs();
        if (specs.getOperatingSystem().contains("windows")) {
            WindowsPinger pinger = new WindowsPinger(timeout);
            boolean response;
            try {
                response = pinger.ping(address, 2);
                return response;
            } catch (IOException e) {
                return true;
            }
        } else {
            TCPPinger pinger = new TCPPinger(timeout);
            boolean response;

            response = pinger.ping(address, 2);
            return response;
        }
    }
}
