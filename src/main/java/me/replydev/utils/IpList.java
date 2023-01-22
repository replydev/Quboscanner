package me.replydev.utils;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class IpList {

    private static final Pattern PATTERN = Pattern.compile(
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
    );
    private final long start;
    private final long end;
    private long index;

    public IpList(String start, String end) {
        if (isNotIp(start)) throw new IllegalArgumentException(start + " is not a valid ip!");
        if (isNotIp(end)) throw new IllegalArgumentException(end + " is not a valid ip!");

        this.start = hostnameToLong(start);
        this.end = hostnameToLong(end);
        index = this.start;
    }

    public static boolean isNotIp(String ip) {
        return !PATTERN.matcher(ip).matches();
    }

    private static long hostnameToLong(String host) {
        long ip = 0;
        if (!Character.isDigit(host.charAt(0))) return -1;
        int[] addr = ipv4ToIntArray(host);
        if (addr == null) return -1;
        for (int i = 0; i < addr.length; ++i) {
            ip += ((long) (Math.max(addr[i], 0))) << 8 * (3 - i);
        }
        return ip;
    }

    private static int[] ipv4ToIntArray(String host) {
        int[] address = { -1, -1, -1, -1 };
        int i = 0;
        StringTokenizer tokens = new StringTokenizer(host, ".");
        if (tokens.countTokens() > 4) return null;
        while (tokens.hasMoreTokens()) {
            try {
                address[i++] = Integer.parseInt(tokens.nextToken()) & 0xFF;
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
        return address;
    }

    private static String longToIpv4(long address) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, shift = 24; i < 4; i++, shift -= 8) {
            long value = (address >> shift) & 0xff;
            sb.append(value);
            if (i != 3) {
                sb.append('.');
            }
        }
        return sb.toString();
    }

    public boolean hasNext() {
        return index <= end;
    }

    public long getCount() {
        return end - start;
    }

    public String getNext() {
        String data = longToIpv4(index);
        index++;
        return data;
    }
}
