package me.replydev.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PortListTest {

    @Test
    @DisplayName("Test port list")
    void portList() {
        String portRange = "25565-25577";
        PortList portList = new PortList(portRange);
        int portCount = 25565;

        for (Integer iteratedPort : portList) {
            assertEquals(portCount++, iteratedPort);
        }
    }

    @Test
    @DisplayName("Test single port")
    void testSinglePort() {
        String portString = "25565";
        PortList portList = new PortList(portString);
        int loop = 0;
        for (Integer iteratedPort : portList) {
            assertEquals(25565, iteratedPort);
            loop++;
        }
        // Assert that we iterated only one time as we inserted only one port
        assertEquals(1, loop);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "test", "-1-65535", "10-65536", "1-0", "f-f", "65536" })
    @DisplayName("Test invalid port list")
    void portList_invalid(String portRange) {
        assertThrows(IllegalArgumentException.class, () -> new PortList(portRange));
    }
}
