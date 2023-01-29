package me.replydev.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class IpListTest {

    @Test
    @DisplayName("Test valid ip range")
    void testIpRange() {
        String range = "164.132.0-1.*";
        IpList ipList = new IpList(range);
        int firstRangeCount = 0;
        int secondRangeCount = 0;
        for (String ip : ipList) {
            String expected = String.format("164.132.%d.%d", firstRangeCount, secondRangeCount);
            assertEquals(expected, ip);
            if (++secondRangeCount > 255) {
                secondRangeCount = 0;
                firstRangeCount = 1;
            }
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "164.132.200", "256.255.255.255", "1.1.1.f", "test" })
    @DisplayName("Test invalid ip range")
    void testInvalidIpRange(String range) {
        assertThrows(IllegalArgumentException.class, () -> new IpList(range));
    }
}
