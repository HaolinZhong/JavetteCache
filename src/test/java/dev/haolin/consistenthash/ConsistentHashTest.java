package dev.haolin.consistenthash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashTest {

    @Test
    void testHash() {
        ConsistentHash consistentHash = new ConsistentHash(Integer::parseInt, 3);
        consistentHash.addNodes("2", "5", "8");

        // v node hash val (node name): 2(2), 5(5), 8(8), 12(2), 15(5), 18(8), 22(2), 25(5), 28(8)

        assertEquals("2", consistentHash.findNode("2"));
        assertEquals("2", consistentHash.findNode("11"));
        assertEquals("5", consistentHash.findNode("24"));
        assertEquals("8", consistentHash.findNode("7"));
        assertEquals("2", consistentHash.findNode("29"));
    }
}
