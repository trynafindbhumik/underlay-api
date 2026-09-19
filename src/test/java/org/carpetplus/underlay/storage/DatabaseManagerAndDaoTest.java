package org.carpetplus.underlay.storage;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerAndDaoTest {

    @TempDir
    Path tempDir;

    private DatabaseManager dbManager;
    private UnderlayDao dao;

    @BeforeEach
    void setUp() throws SQLException {
        File dbFile = tempDir.resolve("test_underlays.db").toFile();
        dbManager = new DatabaseManager(dbFile, Logger.getLogger("TestLogger"));
        dbManager.initialize();
        dao = new UnderlayDao(dbManager);
    }

    @AfterEach
    void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @Test
    void testInsertAndFindById() throws SQLException {
        UUID id = UUID.randomUUID();
        UUID worldId = UUID.randomUUID();
        Underlay underlay = new Underlay(
                id, worldId, 32, 64, 48,
                Material.WHITE_CARPET, "minecraft:white_carpet",
                Material.CHEST, "minecraft:chest[facing=south]",
                System.currentTimeMillis()
        );

        dao.insert(underlay);

        Optional<Underlay> retrieved = dao.findById(id);
        assertTrue(retrieved.isPresent());
        assertEquals(underlay, retrieved.get());
        assertEquals(Material.WHITE_CARPET, retrieved.get().getBaseMaterial());
        assertEquals(Material.CHEST, retrieved.get().getOverlayMaterial());
    }

    @Test
    void testFindByChunk() throws SQLException {
        UUID worldId = UUID.randomUUID();
        Underlay u1 = new Underlay(
                UUID.randomUUID(), worldId, 16, 64, 32, // chunk 1, 2
                Material.RED_CARPET, "minecraft:red_carpet",
                Material.CHEST, "minecraft:chest",
                System.currentTimeMillis()
        );
        Underlay u2 = new Underlay(
                UUID.randomUUID(), worldId, 20, 64, 35, // chunk 1, 2
                Material.BLUE_CARPET, "minecraft:blue_carpet",
                Material.CHEST, "minecraft:chest",
                System.currentTimeMillis()
        );
        Underlay u3 = new Underlay(
                UUID.randomUUID(), worldId, 100, 64, 100, // chunk 6, 6
                Material.MOSS_CARPET, "minecraft:moss_carpet",
                Material.CHEST, "minecraft:chest",
                System.currentTimeMillis()
        );

        dao.insert(u1);
        dao.insert(u2);
        dao.insert(u3);

        List<Underlay> chunkList = dao.findByChunk(worldId, 1, 2);
        assertEquals(2, chunkList.size());
        assertTrue(chunkList.contains(u1));
        assertTrue(chunkList.contains(u2));
        assertFalse(chunkList.contains(u3));
    }

    @Test
    void testDelete() throws SQLException {
        UUID id = UUID.randomUUID();
        UUID worldId = UUID.randomUUID();
        Underlay underlay = new Underlay(
                id, worldId, 0, 64, 0,
                Material.WHITE_CARPET, "minecraft:white_carpet",
                Material.CHEST, "minecraft:chest",
                System.currentTimeMillis()
        );

        dao.insert(underlay);
        assertTrue(dao.findById(id).isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);
        assertFalse(dao.findById(id).isPresent());
    }
}
