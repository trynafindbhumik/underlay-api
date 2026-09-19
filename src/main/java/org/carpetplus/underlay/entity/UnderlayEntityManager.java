package org.carpetplus.underlay.entity;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages spawning, despawning, tagging, and lookup of BlockDisplay and Interaction entities for virtual underlays.
 */
public class UnderlayEntityManager {

    private final UnderlayPDCKeys pdcKeys;

    public UnderlayEntityManager(@NotNull UnderlayPDCKeys pdcKeys) {
        this.pdcKeys = Objects.requireNonNull(pdcKeys, "pdcKeys cannot be null");
    }

    /**
     * Spawns both the BlockDisplay (rendering the top block) and the Interaction entity (trigger for underlay).
     */
    public boolean spawnEntities(@NotNull Underlay underlay, @NotNull World world) {
        Location loc = new Location(world, underlay.getX(), underlay.getY(), underlay.getZ());
        Location centerLoc = loc.clone().add(0.5, 0.0, 0.5);

        // Despawn any pre-existing entities at this location first
        despawnEntities(underlay.getId(), world, loc);

        try {
            BlockData blockData = Bukkit.createBlockData(underlay.getOverlayBlockDataString());

            // 1. Spawn BlockDisplay entity
            BlockDisplay display = world.spawn(loc, BlockDisplay.class, entity -> {
                entity.setBlock(blockData);
                entity.setPersistent(true);

                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                pdc.set(pdcKeys.getUnderlayIdKey(), PersistentDataType.STRING, underlay.getId().toString());
                pdc.set(pdcKeys.getEntityRoleKey(), PersistentDataType.STRING, UnderlayPDCKeys.ROLE_BLOCK_DISPLAY);
            });

            // 2. Spawn Interaction entity
            Interaction interaction = world.spawn(loc, Interaction.class, entity -> {
                entity.setInteractionHeight(1.0f);
                entity.setInteractionWidth(1.0f);
                entity.setResponsive(true);
                entity.setPersistent(true);

                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                pdc.set(pdcKeys.getUnderlayIdKey(), PersistentDataType.STRING, underlay.getId().toString());
                pdc.set(pdcKeys.getEntityRoleKey(), PersistentDataType.STRING, UnderlayPDCKeys.ROLE_INTERACTION);
            });

            return display.isValid() && interaction.isValid();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Despawns all entities associated with an underlay ID in a given world and location.
     */
    public void despawnEntities(@NotNull UUID underlayId, @NotNull World world, @NotNull Location location) {
        String idString = underlayId.toString();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            return;
        }

        Entity[] entities = world.getChunkAt(chunkX, chunkZ).getEntities();
        for (Entity entity : entities) {
            if (entity instanceof BlockDisplay || entity instanceof Interaction) {
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                String taggedId = pdc.get(pdcKeys.getUnderlayIdKey(), PersistentDataType.STRING);
                if (idString.equals(taggedId)) {
                    entity.remove();
                }
            }
        }
    }

    /**
     * Resolves the underlay UUID stored on an entity if present.
     */
    @Nullable
    public UUID getUnderlayIdFromEntity(@NotNull Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String idStr = pdc.get(pdcKeys.getUnderlayIdKey(), PersistentDataType.STRING);
        if (idStr == null) {
            return null;
        }
        try {
            return UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns true if the entity is an underlay Interaction entity.
     */
    public boolean isUnderlayInteractionEntity(@NotNull Entity entity) {
        if (!(entity instanceof Interaction)) {
            return false;
        }
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String role = pdc.get(pdcKeys.getEntityRoleKey(), PersistentDataType.STRING);
        return UnderlayPDCKeys.ROLE_INTERACTION.equals(role);
    }
}
