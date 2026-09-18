package org.carpetplus.underlay.chunk;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChunkKeyTest {

    @Test
    void testChunkKeyEqualsAndHashCode() {
        UUID worldId = UUID.randomUUID();
        ChunkKey ck1 = new ChunkKey(worldId, 10, -5);
        ChunkKey ck2 = new ChunkKey(worldId, 10, -5);
        ChunkKey ck3 = new ChunkKey(worldId, 11, -5);

        assertEquals(ck1, ck2);
        assertEquals(ck1.hashCode(), ck2.hashCode());
        assertNotEquals(ck1, ck3);
    }
}
