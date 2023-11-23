package me.replydev.utils;

import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Represents a range of IP addresses defined by a start and end IP.
 */
public class IpList implements Iterable<String> {

    private static final Pattern IP_RANGE_PATTERN = Pattern.compile(
        "^(?:(?:\\d{1,3}|\\*|\\d{1,3}-\\d{1,3})\\.){3}(?:\\d{1,3}|\\*|\\d{1,3}-\\d{1,3})$"
    );

    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private final long startIpLong;
    private final long endIpLong;

    /**
     * Constructs an IpList from a given IP range string.
     * @param ipRange A string representing the IP range.
     */
    public IpList(String ipRange) {
        if (!validRange(ipRange)) {
            throw new IllegalArgumentException("The IP range " + ipRange + " is not valid.");
        }
        IPAddressSeqRange range = new IPAddressString(ipRange).getSequentialRange();
        if (range == null) {
            throw new IllegalArgumentException("The IP range " + ipRange + " cannot be resolved to a valid range.");
        }

        this.startIpLong = ipToLong(range.getLower().toString());
        this.endIpLong = ipToLong(range.getUpper().toString());
    }

    /**
     * Checks if the IP range string is valid.
     * @param ip A string representing the IP range.
     * @return A boolean indicating if the IP range is valid.
     */
    private static boolean validRange(String ip) {
        return ip != null && IP_RANGE_PATTERN.matcher(ip).matches();
    }

    /**
     * Converts an IP address string to its long representation.
     * @param ip A string representation of an IP address.
     * @return A long representing the IP address.
     */
    private static long ipToLong(String ip) {
        long result = 0;
        if (!Character.isDigit(ip.charAt(0))) return -1;
        int[] octets = ipv4ToIntArray(ip);
        if (octets.length == 0) {
            return -1;
        }
        for (int i = 0; i < octets.length; ++i) {
            result += ((long) (Math.max(octets[i], 0))) << 8 * (3 - i);
        }
        return result;
    }

    /**
     * Converts an IPv4 address string to an array of integers.
     * @param ip A string representation of an IPv4 address.
     * @return An array of integers representing the IPv4 address.
     */
    private static int[] ipv4ToIntArray(String ip) {
        int[] octets = { -1, -1, -1, -1 };
        int index = 0;
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");
        if (tokenizer.countTokens() > 4) {
            return EMPTY_INT_ARRAY;
        }
        while (tokenizer.hasMoreTokens()) {
            try {
                octets[index++] = Integer.parseInt(tokenizer.nextToken()) & 0xFF;
            } catch (NumberFormatException e) {
                return EMPTY_INT_ARRAY;
            }
        }
        return octets;
    }

    /**
     * Converts a long representation of an IP address to a string.
     *
     * @param ipLong A long representing the IP address.
     * @return A string representation of the IP address.
     */
    private static String longToIpv4(long ipLong) {
        StringBuilder ipBuilder = new StringBuilder();
        for (int i = 0, shift = 24; i < 4; i++, shift -= 8) {
            long value = (ipLong >> shift) & 0xff;
            ipBuilder.append(value);
            if (i != 3) {
                ipBuilder.append('.');
            }
        }
        return ipBuilder.toString();
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private long currentIndex = startIpLong;

            @Override
            public boolean hasNext() {
                return currentIndex <= endIpLong;
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more IP addresses in range.");
                }
                return longToIpv4(currentIndex++);
            }
        };
    }
}
