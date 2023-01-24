package me.replydev.utils;

import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class IpList implements Iterator<String>, Iterable<String> {

    private static final Pattern IP_RANGE_PATTERN = Pattern.compile(
        "^(?:(?:\\d{1,3}|\\*|\\d{1,3}-\\d{1,3})\\.){3}(?:\\d{1,3}|\\*|\\d{1,3}-\\d{1,3})$"
    );
    private final long start;
    private final long end;
    private long index;

    public IpList(String ipRange) {
        if (!validRange(ipRange)) {
            throw new IllegalArgumentException(ipRange + " is not a valid ip address");
        }
        IPAddressSeqRange range = new IPAddressString(ipRange).getSequentialRange();
        String ipStart = range.getLower().toString();
        String ipEnd = range.getUpper().toString();
        this.start = hostnameToLong(ipStart);
        this.end = hostnameToLong(ipEnd);
        index = this.start;
    }

    public static boolean validRange(String ip) {
        return IP_RANGE_PATTERN.matcher(ip).matches();
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

    @Override
    public boolean hasNext() {
        return index <= end;
    }

    @Override
    public String next() {
        String data = longToIpv4(index);
        index++;
        return data;
    }

    public long getCount() {
        return end - start;
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }
}
