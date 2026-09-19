package org.carpetplus.underlay.chunk;

import org.carpetplus.underlay.api.Underlay;
import org.carpetplus.underlay.api.event.UnderlayLoadEvent;
import org.carpetplus.underlay.api.event.UnderlayUnloadEvent;
import org.carpetplus.underlay.entity.UnderlayEntityManager;
import org.carpetplus.underlay.scheduler.UnderlayTaskScheduler;
import org.carpetplus.underlay.storage.UnderlayDao;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Paper event listener managing entity spawning and despawning on chunk load/unload events.
 */
public class ChunkLifecycleListener implements Listener {

    private final UnderlayDao dao;
    private final ChunkUnderlayCache cache;
    private final UnderlayEntityManager entityManager;
    private final UnderlayTaskScheduler scheduler;
    private final Logger logger;

    public ChunkLifecycleListener(
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        ChunkKey chunkKey = ChunkKey.of(chunk);

        // Fetch chunk underlays asynchronously from SQLite database
        scheduler.runAsync(() -> {
            try {
                List<Underlay> underlays = dao.findByChunk(world.getUID(), chunk.getX(), chunk.getZ());
                if (underlays.isEmpty()) {
                    return;
                }

                // Dispatch entity spawning back onto main/region thread
                scheduler.runSync(() -> {
                    if (!world.isChunkLoaded(chunk.getX(), chunk.getZ())) {
                        return;
                    }
                    for (Underlay underlay : underlays) {
                        Location loc = underlay.toLocation(Bukkit.getServer());
                        if (loc != null) {
                            cache.cacheUnderlay(loc, underlay);
                            entityManager.spawnEntities(underlay, world);
                            Bukkit.getPluginManager().callEvent(new UnderlayLoadEvent(underlay));
                        }
                    }
                });
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to load chunk underlays for " + chunkKey, e);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        ChunkKey chunkKey = ChunkKey.of(chunk);

        for (Underlay underlay : cache.getUnderlaysInChunk(chunkKey)) {
            Location loc = underlay.toLocation(Bukkit.getServer());
            if (loc != null) {
                entityManager.despawnEntities(underlay.getId(), world, loc);
                Bukkit.getPluginManager().callEvent(new UnderlayUnloadEvent(underlay));
            }
        }
        cache.unloadChunk(chunkKey);
    }
}
