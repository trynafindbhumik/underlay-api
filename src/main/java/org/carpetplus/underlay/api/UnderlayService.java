package org.carpetplus.underlay.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for querying, creating, and removing underlays asynchronously or synchronously.
 */
public interface UnderlayService {

    /**
     * Creates and registers a new virtual underlay at the given location asynchronously.
     *
     * @param location the location of the underlay
     * @param baseMaterial the base block material (e.g., CARPET)
     * @param baseBlockDataString the NBT/BlockData string of the base block
     * @param overlayMaterial the top block material (e.g., CHEST)
     * @param overlayBlockDataString the NBT/BlockData string of the top block
     * @return a future resolving to the newly created Underlay
     */
    @NotNull
    CompletableFuture<Underlay> createUnderlay(
            @NotNull Location location,
            @NotNull org.bukkit.Material baseMaterial,
            @NotNull String baseBlockDataString,
            @NotNull org.bukkit.Material overlayMaterial,
            @NotNull String overlayBlockDataString
    );

    /**
     * Removes an underlay by ID asynchronously, despawning entities and cleaning database records.
     *
     * @param id the UUID of the underlay
     * @return a future resolving to true if successfully removed
     */
    @NotNull
    CompletableFuture<Boolean> removeUnderlay(@NotNull UUID id);

    /**
     * Gets an active or cached underlay at the specific block location.
     *
     * @param location block location
     * @return Optional containing the underlay if present
     */
    @NotNull
    Optional<Underlay> getUnderlayAt(@NotNull Location location);

    /**
     * Gets an underlay by its unique UUID.
     *
     * @param id underlay UUID
     * @return Optional containing the underlay if present
     */
    @NotNull
    Optional<Underlay> getUnderlayById(@NotNull UUID id);

    /**
     * Gets all loaded underlays in a specific chunk.
     *
     * @param world world
     * @param chunkX chunk X
     * @param chunkZ chunk Z
     * @return collection of loaded underlays
     */
    @NotNull
    Collection<Underlay> getUnderlaysInChunk(@NotNull World world, int chunkX, int chunkZ);
}
