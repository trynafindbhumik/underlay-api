package org.carpetplus.underlay.chunk;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe spatial cache mapping ChunkKeys and Locations to active underlays.
 */
public class ChunkUnderlayCache {

    private final Map<ChunkKey, Map<Location, Underlay>> chunkCache = new ConcurrentHashMap<>();
    private final Map<UUID, Underlay> idCache = new ConcurrentHashMap<>();

    public void cacheUnderlay(@NotNull Location location, @NotNull Underlay underlay) {
        ChunkKey chunkKey = ChunkKey.of(location);
        chunkCache.computeIfAbsent(chunkKey, k -> new ConcurrentHashMap<>()).put(location, underlay);
        idCache.put(underlay.getId(), underlay);
    }

    public void removeUnderlay(@NotNull Location location, @NotNull UUID underlayId) {
        ChunkKey chunkKey = ChunkKey.of(location);
        Map<Location, Underlay> chunkMap = chunkCache.get(chunkKey);
        if (chunkMap != null) {
            chunkMap.remove(location);
            if (chunkMap.isEmpty()) {
                chunkCache.remove(chunkKey);
            }
        }
        idCache.remove(underlayId);
    }

    public void unloadChunk(@NotNull ChunkKey chunkKey) {
        Map<Location, Underlay> removed = chunkCache.remove(chunkKey);
        if (removed != null) {
            for (Underlay underlay : removed.values()) {
                idCache.remove(underlay.getId());
            }
        }
    }

    @NotNull
    public Optional<Underlay> getUnderlayAt(@NotNull Location location) {
        ChunkKey chunkKey = ChunkKey.of(location);
        Map<Location, Underlay> map = chunkCache.get(chunkKey);
        if (map == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(map.get(location));
    }

    @NotNull
    public Optional<Underlay> getUnderlayById(@NotNull UUID id) {
        return Optional.ofNullable(idCache.get(id));
    }

    @NotNull
    public Collection<Underlay> getUnderlaysInChunk(@NotNull ChunkKey chunkKey) {
        Map<Location, Underlay> map = chunkCache.get(chunkKey);
        if (map == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(map.values());
    }

    public boolean isChunkLoaded(@NotNull ChunkKey chunkKey) {
        return chunkCache.containsKey(chunkKey);
    }

    public void clear() {
        chunkCache.clear();
        idCache.clear();
    }
}
