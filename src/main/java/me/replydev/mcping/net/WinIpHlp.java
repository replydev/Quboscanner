package me.replydev.mcping.net;


import java.net.InetAddress;

class WinIpHlp {
    public static WinIpHlpDll.IpAddrByVal toIpAddr(InetAddress address) {
        WinIpHlpDll.IpAddrByVal addr = new WinIpHlpDll.IpAddrByVal();
        addr.bytes = address.getAddress();
        return addr;
    }

    public static WinIpHlpDll.Ip6SockAddrByRef toIp6Addr(InetAddress address) {
        WinIpHlpDll.Ip6SockAddrByRef addr = new WinIpHlpDll.Ip6SockAddrByRef();
        addr.bytes = address.getAddress();
        return addr;
    }
}
