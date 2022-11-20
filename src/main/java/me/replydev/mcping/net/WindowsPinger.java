package me.replydev.mcping.net;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Arrays;

import static java.lang.Thread.currentThread;
import static me.replydev.mcping.net.WinIpHlp.toIp6Addr;
import static me.replydev.mcping.net.WinIpHlp.toIpAddr;
import static me.replydev.mcping.net.WinIpHlpDll.dll;

class WindowsPinger {
    private final int timeout;
    private final WinIpHlpDll.Ip6SockAddrByRef anyIp6SourceAddr = new WinIpHlpDll.Ip6SockAddrByRef();

    public WindowsPinger(int timeout) {
        this.timeout = timeout;
    }

    public static void main(String[] args) throws IOException {
        //System.out.println(new WindowsPinger(5000).ping(InetAddress.getByName("::1"), 1));
        long now = System.currentTimeMillis();
        System.out.println(new TCPPinger(5000).ping(InetAddress.getByName("164.132.200.78"), 1));
        System.out.println(System.currentTimeMillis() - now);
        now = System.currentTimeMillis();
        System.out.println(new WindowsPinger(5000).ping(InetAddress.getByName("164.132.200.78"), 1));
        System.out.println(System.currentTimeMillis() - now);
    }

    public boolean ping(InetAddress address, int count) throws IOException {
        if (address instanceof Inet6Address)
            return ping6(address, count);
        else
            return ping4(address, count);
    }

    private boolean ping4(InetAddress address, int count) throws IOException {
        Pointer handle = dll.IcmpCreateFile();
        if (handle == null) throw new IOException("Unable to create Windows native ICMP handle");

        int sendDataSize = 32;
        int replyDataSize = sendDataSize + (new WinIpHlpDll.IcmpEchoReply().size()) + 10;
        Pointer sendData = new Memory(sendDataSize);
        sendData.clear(sendDataSize);
        Pointer replyData = new Memory(replyDataSize);

        //PingResult result = new PingResult(a, count);
        try {
            WinIpHlpDll.IpAddrByVal ipaddr = toIpAddr(address);
            for (int i = 1; i <= count && !currentThread().isInterrupted(); i++) {
                int numReplies = dll.IcmpSendEcho(handle, ipaddr, sendData, (short) sendDataSize, null, replyData, replyDataSize, timeout);
                WinIpHlpDll.IcmpEchoReply echoReply = new WinIpHlpDll.IcmpEchoReply(replyData);
                if (numReplies > 0 && echoReply.status == 0 && Arrays.equals(echoReply.address.bytes, ipaddr.bytes)) {
                    return true;
                }
            }
        } finally {
            dll.IcmpCloseHandle(handle);
        }
        return false;
    }

    private boolean ping6(InetAddress address, int count) throws IOException {
        Pointer handle = dll.Icmp6CreateFile();
        if (handle == null) throw new IOException("Unable to create Windows native ICMP6 handle");

        int sendDataSize = 32;
        int replyDataSize = sendDataSize + (new WinIpHlpDll.Icmp6EchoReply().size()) + 10;
        Pointer sendData = new Memory(sendDataSize);
        sendData.clear(sendDataSize);
        Pointer replyData = new Memory(replyDataSize);

        //PingResult result = new PingResult(subject.getAddress(), count);
        try {
            WinIpHlpDll.Ip6SockAddrByRef ipaddr = toIp6Addr(address);
            for (int i = 1; i <= count && !currentThread().isInterrupted(); i++) {
                int numReplies = dll.Icmp6SendEcho2(handle, null, null, null, anyIp6SourceAddr, toIp6Addr(address),
                        sendData, (short) sendDataSize, null, replyData, replyDataSize, timeout);
                WinIpHlpDll.Icmp6EchoReply echoReply = new WinIpHlpDll.Icmp6EchoReply(replyData);
                if (numReplies > 0 && echoReply.status == 0 && Arrays.equals(echoReply.addressBytes, ipaddr.bytes)) {
                    //result.addReply(echoReply.roundTripTime);
                    dll.IcmpCloseHandle(handle);
                    return true;
                }
            }
        } finally {
            dll.IcmpCloseHandle(handle);
        }

        return false;
    }
}