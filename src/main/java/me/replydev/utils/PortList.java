package me.replydev.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PortList implements Iterable<Integer> {

    private static final Pattern PORT_RANGE_PATTERN = Pattern.compile(
        "^(0|[1-9]\\d{0,4})(?:-([1-9]\\d{0,4}))?$"
    );

    private final int initialPort;
    private final Optional<Integer> finalPort;

    public PortList(String portString) {
        if (portString == null) {
            throw new IllegalArgumentException("Invalid null port range");
        }

        Matcher match = PORT_RANGE_PATTERN.matcher(portString);
        if (!match.find()) {
            throw new IllegalArgumentException("Invalid port range: " + portString);
        }
        initialPort = Integer.parseInt(match.group(1));
        finalPort = Optional.ofNullable(match.group(2)).map(Integer::parseInt);
        finalPort.ifPresent(f -> {
            if (initialPort > f) {
                throw new IllegalArgumentException(
                    String.format(
                        "Initial range is major than final range: %d > %d",
                        initialPort,
                        f
                    )
                );
            }
        });

        if (initialPort < 0 || initialPort > 65535 || finalPort.orElse(-1) > 65535) {
            throw new IllegalArgumentException("Invalid port range: " + portString);
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int currentPort = initialPort;

            @Override
            public boolean hasNext() {
                return finalPort
                    .map(integer -> currentPort <= integer)
                    .orElseGet(() -> currentPort == initialPort);
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
