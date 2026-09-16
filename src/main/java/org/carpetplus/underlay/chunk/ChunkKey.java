package org.carpetplus.underlay.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable value object representing a unique chunk in a world.
 */
public final class ChunkKey {

    private final UUID worldId;
    private final int x;
    private final int z;

    public ChunkKey(@NotNull UUID worldId, int x, int z) {
        this.worldId = Objects.requireNonNull(worldId, "worldId cannot be null");
        this.x = x;
        this.z = z;
    }

    public static ChunkKey of(@NotNull Chunk chunk) {
        return new ChunkKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
    }

    public static ChunkKey of(@NotNull Location location) {
        Objects.requireNonNull(location.getWorld(), "location world cannot be null");
        return new ChunkKey(location.getWorld().getUID(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @NotNull
    public UUID getWorldId() {
        return worldId;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkKey chunkKey = (ChunkKey) o;
        return x == chunkKey.x && z == chunkKey.z && worldId.equals(chunkKey.worldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldId, x, z);
    }

    @Override
    public String toString() {
        return "ChunkKey{" +
                "world=" + worldId +
                ", x=" + x +
                ", z=" + z +
                '}';
    }
}
