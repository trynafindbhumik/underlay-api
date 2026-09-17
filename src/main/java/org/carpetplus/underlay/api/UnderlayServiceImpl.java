package org.carpetplus.underlay.api;

import org.carpetplus.underlay.api.event.UnderlayCreateEvent;
import org.carpetplus.underlay.api.event.UnderlayRemoveEvent;
import org.carpetplus.underlay.chunk.ChunkKey;
import org.carpetplus.underlay.chunk.ChunkUnderlayCache;
import org.carpetplus.underlay.entity.UnderlayEntityManager;
import org.carpetplus.underlay.scheduler.UnderlayTaskScheduler;
import org.carpetplus.underlay.storage.UnderlayDao;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Production implementation of the UnderlayService API.
 */
public class UnderlayServiceImpl implements UnderlayService {

    private final UnderlayDao dao;
    private final ChunkUnderlayCache cache;
    private final UnderlayEntityManager entityManager;
    private final UnderlayTaskScheduler scheduler;
    private final Logger logger;

    public UnderlayServiceImpl(
            @NotNull UnderlayDao dao,
            @NotNull ChunkUnderlayCache cache,
            @NotNull UnderlayEntityManager entityManager,
            @NotNull UnderlayTaskScheduler scheduler,
            @NotNull Logger logger
    ) {
        this.dao = Objects.requireNonNull(dao, "dao cannot be null");
        this.cache = Objects.requireNonNull(cache, "cache cannot be null");
        this.entityManager = Objects.requireNonNull(entityManager, "entityManager cannot be null");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler cannot be null");
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
    }

    @Override
    @NotNull
    public CompletableFuture<Underlay> createUnderlay(
            @NotNull Location location,
            @NotNull Material baseMaterial,
            @NotNull String baseBlockDataString,
            @NotNull Material overlayMaterial,
            @NotNull String overlayBlockDataString
    ) {
        CompletableFuture<Underlay> future = new CompletableFuture<>();
        World world = Objects.requireNonNull(location.getWorld(), "location world cannot be null");

        BlockData baseData = Bukkit.createBlockData(baseBlockDataString);
        BlockData overlayData = Bukkit.createBlockData(overlayBlockDataString);

        Underlay underlay = Underlay.create(location, baseMaterial, baseData, overlayMaterial, overlayData);

        // Fire Bukkit Event synchronously
        scheduler.runSync(() -> {
            UnderlayCreateEvent createEvent = new UnderlayCreateEvent(underlay, null);
            Bukkit.getPluginManager().callEvent(createEvent);
            if (createEvent.isCancelled()) {
                future.completeExceptionally(new IllegalStateException("Underlay creation was cancelled by event listener"));
                return;
            }

            // Persist to database asynchronously
            scheduler.runAsync(() -> {
                try {
                    dao.insert(underlay);
                    scheduler.runSync(() -> {
                        cache.cacheUnderlay(location, underlay);
                        entityManager.spawnEntities(underlay, world);
                        future.complete(underlay);
                    });
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Failed to insert underlay record into SQLite", e);
                    future.completeExceptionally(e);
                }
            });
        });

        return future;
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> removeUnderlay(@NotNull UUID id) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        scheduler.runAsync(() -> {
            try {
                Optional<Underlay> opt = dao.findById(id);
                if (opt.isEmpty()) {
                    future.complete(false);
                    return;
                }

                Underlay underlay = opt.get();
                scheduler.runSync(() -> {
                    UnderlayRemoveEvent removeEvent = new UnderlayRemoveEvent(
                            underlay, UnderlayRemoveEvent.RemoveReason.PLUGIN, null
                    );
                    Bukkit.getPluginManager().callEvent(removeEvent);
                    if (removeEvent.isCancelled()) {
                        future.complete(false);
                        return;
                    }

                    scheduler.runAsync(() -> {
                        try {
                            boolean deleted = dao.delete(id);
                            scheduler.runSync(() -> {
                                Location loc = underlay.toLocation(Bukkit.getServer());
                                if (loc != null && loc.getWorld() != null) {
                                    cache.removeUnderlay(loc, id);
                                    entityManager.despawnEntities(id, loc.getWorld(), loc);
                                }
                                future.complete(deleted);
                            });
                        } catch (SQLException e) {
                            logger.log(Level.SEVERE, "Failed to delete underlay record from SQLite", e);
                            future.completeExceptionally(e);
                        }
                    });
                });
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to find underlay for removal", e);
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    @NotNull
    public Optional<Underlay> getUnderlayAt(@NotNull Location location) {
        return cache.getUnderlayAt(location);
    }

    @Override
    @NotNull
    public Optional<Underlay> getUnderlayById(@NotNull UUID id) {
        return cache.getUnderlayById(id);
    }

    @Override
    @NotNull
    public Collection<Underlay> getUnderlaysInChunk(@NotNull World world, int chunkX, int chunkZ) {
        ChunkKey chunkKey = new ChunkKey(world.getUID(), chunkX, chunkZ);
        return cache.getUnderlaysInChunk(chunkKey);
    }
}
