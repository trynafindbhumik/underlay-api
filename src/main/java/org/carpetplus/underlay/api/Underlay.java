package org.carpetplus.underlay.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable representation of a server-side virtual underlay.
 */
public final class Underlay {

    private final UUID id;
    private final UUID worldId;
    private final int x;
    private final int y;
    private final int z;
    private final Material baseMaterial;
    private final String baseBlockDataString;
    private final Material overlayMaterial;
    private final String overlayBlockDataString;
    private final long createdAt;

    public Underlay(
            @NotNull UUID id,
            @NotNull UUID worldId,
            int x,
            int y,
            int z,
            @NotNull Material baseMaterial,
            @NotNull String baseBlockDataString,
            @NotNull Material overlayMaterial,
            @NotNull String overlayBlockDataString,
            long createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.worldId = Objects.requireNonNull(worldId, "worldId cannot be null");
        this.x = x;
        this.y = y;
        this.z = z;
        this.baseMaterial = Objects.requireNonNull(baseMaterial, "baseMaterial cannot be null");
        this.baseBlockDataString = Objects.requireNonNull(baseBlockDataString, "baseBlockDataString cannot be null");
        this.overlayMaterial = Objects.requireNonNull(overlayMaterial, "overlayMaterial cannot be null");
        this.overlayBlockDataString = Objects.requireNonNull(overlayBlockDataString, "overlayBlockDataString cannot be null");
        this.createdAt = createdAt;
    }

    public static Underlay create(
            @NotNull Location location,
            @NotNull Material baseMaterial,
            @NotNull BlockData baseBlockData,
            @NotNull Material overlayMaterial,
            @NotNull BlockData overlayBlockData
    ) {
        World world = Objects.requireNonNull(location.getWorld(), "Location world cannot be null");
        return new Underlay(
                UUID.randomUUID(),
                world.getUID(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                baseMaterial,
                baseBlockData.getAsString(),
                overlayMaterial,
                overlayBlockData.getAsString(),
                System.currentTimeMillis()
        );
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public UUID getWorldId() {
        return worldId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getChunkX() {
        return x >> 4;
    }

    public int getChunkZ() {
        return z >> 4;
    }

    @NotNull
    public Material getBaseMaterial() {
        return baseMaterial;
    }

    @NotNull
    public String getBaseBlockDataString() {
        return baseBlockDataString;
    }

    @NotNull
    public Material getOverlayMaterial() {
        return overlayMaterial;
    }

    @NotNull
    public String getOverlayBlockDataString() {
        return overlayBlockDataString;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public Location toLocation(@NotNull org.bukkit.Server server) {
        World world = server.getWorld(worldId);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Underlay underlay = (Underlay) o;
        return x == underlay.x && y == underlay.y && z == underlay.z &&
                id.equals(underlay.id) && worldId.equals(underlay.worldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, worldId, x, y, z);
    }

    @Override
    public String toString() {
        return "Underlay{" +
                "id=" + id +
                ", worldId=" + worldId +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", base=" + baseMaterial +
                ", overlay=" + overlayMaterial +
                '}';
    }
}
