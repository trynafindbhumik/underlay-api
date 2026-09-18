package org.carpetplus.underlay.api;

import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UnderlayRegistryTest {

    private UnderlayRegistry registry;

    @BeforeEach
    void setUp() {
        registry = UnderlayRegistry.getInstance();
        registry.clear();
    }

    @Test
    void testRegisterAndValidateCombination() {
        Set<Material> carpets = Set.of(Material.WHITE_CARPET, Material.RED_CARPET, Material.MOSS_CARPET);
        UnderlayDefinition carpetDef = new UnderlayDefinition(
                "carpets",
                carpets,
                mat -> mat == Material.CHEST || mat == Material.TRAPPED_CHEST || mat.name().endsWith("_FENCE")
        );

        registry.registerDefinition(carpetDef);

        assertTrue(registry.isValidCombination(Material.WHITE_CARPET, Material.CHEST));
        assertTrue(registry.isValidCombination(Material.MOSS_CARPET, Material.OAK_FENCE));
        assertFalse(registry.isValidCombination(Material.DIRT, Material.CHEST));
        assertFalse(registry.isValidCombination(Material.WHITE_CARPET, Material.STONE));
    }
}
