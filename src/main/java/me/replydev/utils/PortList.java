package me.replydev.utils;

import java.util.Iterator;

public final class PortList implements Iterator<Integer>, Cloneable {

    private int[] portRangeStart;
    private int[] portRangeEnd;

    private int rangeCountMinus1;
    private int rangeIndex;
    private int currentPort;

    private boolean hasNext;

    private String[] portRanges;


    public PortList(String portString) throws NumberFormatException {
        if (portString != null && (portString = portString.trim()).length() > 0) {
            portRanges = portString.split("[\\s\t\n\r,.;]+");

            // initialize storage
            portRangeStart = new int[portRanges.length + 1];    // +1 for optimization of 'next' method, prevents ArrayIndexOutOfBoundsException
            portRangeEnd = new int[portRanges.length];

            // parse ints
            for (int i = 0; i < portRanges.length; i++) {
                String range = portRanges[i];
                int dashPos = range.indexOf('-') + 1;
                int endPort = Integer.parseInt(range.substring(dashPos));
                portRangeEnd[i] = endPort;
                portRangeStart[i] = dashPos == 0 ? endPort : Integer.parseInt(range.substring(0, dashPos - 1));
                if (endPort <= 0 || endPort >= 65536) {
                    throw new NumberFormatException(endPort + " port is out of range");
                }
            }
            reload();
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    public Integer next() {
        int returnPort = currentPort++;

        if (currentPort > portRangeEnd[rangeIndex]) {
            hasNext = rangeIndex < rangeCountMinus1;
            rangeIndex++;
            currentPort = portRangeStart[rangeIndex];
        }

        return returnPort;
    }

    public Integer get() {
        return currentPort;
    }

    public int size() {
        int size = 0;
        if (portRangeStart != null) {
            for (int i = 0; i <= rangeCountMinus1; i++) {
                size += portRangeEnd[i] - portRangeStart[i] + 1;
            }
        }
        return size;
    }

    public PortList copy() {
        try {
            return (PortList) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void reload() {
        currentPort = portRangeStart[0];
        rangeCountMinus1 = portRanges.length - 1;
        rangeIndex = 0;
        hasNext = rangeCountMinus1 >= 0;
    }

}