package me.replydev.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a range of ports, potentially a single port or a range from an initial to a final port.
 */
public final class PortList implements Iterable<Integer> {

    private static final Pattern PORT_RANGE_PATTERN = Pattern.compile(
            "^(0|[1-9]\\d{0,4})(?:-([1-9]\\d{0,4}))?$"
    );

    private final int startPort;
    private final int endPort;

    /**
     * Constructs a PortList from a given port range string.
     * @param portRangeString A string representing the port range.
     */
    public PortList(String portRangeString) {
        if (portRangeString == null) {
            throw new IllegalArgumentException("Port range cannot be null.");
        }

        Matcher matcher = PORT_RANGE_PATTERN.matcher(portRangeString);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid port range format: " + portRangeString);
        }
        startPort = Integer.parseInt(matcher.group(1));
        endPort = Optional.ofNullable(matcher.group(2)).map(Integer::parseInt).orElse(startPort);

        if (startPort > endPort) {
            throw new IllegalArgumentException(
                    String.format(
                            "Initial range is major than final range: %d > %d",
                            startPort,
                            endPort
                    )
            );
        }

        if (startPort < 0 || startPort > 65535 || endPort > 65535) {
            throw new IllegalArgumentException("Port range must be between 0 and 65535: " + portRangeString);
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int currentPort = startPort;

            @Override
            public boolean hasNext() {
                return currentPort <= endPort;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more ports in the range.");
                }
                return currentPort++;
            }
        };
    }
}