package me.replydev.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public final class PortList implements Iterable<Integer> {

    private static final Pattern PORT_RANGE_PATTERN = Pattern.compile(
        "^(0|[1-9]\\d{0,4})(?:-([1-9]\\d{0,4}))?$"
    );

    private final int initialPort;
    private final int finalPort;

    public PortList(@NonNull String portString) {
        Matcher match = PORT_RANGE_PATTERN.matcher(portString);

        if (!match.find()) {
            throw new IllegalArgumentException("Invalid port range: " + portString);
        }
        initialPort = Integer.parseInt(match.group(1));
        finalPort = match.groupCount() > 1 ? Integer.parseInt(match.group(2)) : -1;
    }

    public int size() {
        return finalPort - initialPort;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int currentPort = initialPort;

            @Override
            public boolean hasNext() {
                return currentPort <= finalPort;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements to extract");
                }
                return currentPort++;
            }
        };
    }
}
