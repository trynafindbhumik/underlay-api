package org.carpetplus.underlay.api;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UnderlayTest {

    @Test
    void testUnderlayCreationAndGetters() {
        UUID id = UUID.randomUUID();
        UUID worldId = UUID.randomUUID();
        Underlay underlay = new Underlay(
                id, worldId, 100, 64, -200,
                Material.WHITE_CARPET, "minecraft:white_carpet",
                Material.CHEST, "minecraft:chest[facing=north]",
                1000L
        );

        assertEquals(id, underlay.getId());
        assertEquals(worldId, underlay.getWorldId());
        assertEquals(100, underlay.getX());
        assertEquals(64, underlay.getY());
        assertEquals(-200, underlay.getZ());
        assertEquals(6, underlay.getChunkX());
        assertEquals(-13, underlay.getChunkZ());
        assertEquals(Material.WHITE_CARPET, underlay.getBaseMaterial());
        assertEquals(Material.CHEST, underlay.getOverlayMaterial());
        assertEquals(1000L, underlay.getCreatedAt());
    }

    @Test
    void testEquality() {
        UUID id = UUID.randomUUID();
        UUID worldId = UUID.randomUUID();
        Underlay u1 = new Underlay(id, worldId, 10, 64, 20, Material.WHITE_CARPET, "data", Material.CHEST, "data", 100L);
        Underlay u2 = new Underlay(id, worldId, 10, 64, 20, Material.WHITE_CARPET, "data", Material.CHEST, "data", 100L);

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
