package org.carpetplus.underlay.storage;

import org.carpetplus.underlay.api.Underlay;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for carpet_plus_underlays table.
 */
public class UnderlayDao {

    private final DatabaseManager databaseManager;

    public UnderlayDao(@NotNull DatabaseManager databaseManager) {
        this.databaseManager = Objects.requireNonNull(databaseManager, "databaseManager cannot be null");
    }

    public void insert(@NotNull Underlay underlay) throws SQLException {
        String sql = """
            INSERT INTO carpet_plus_underlays (
                id, world_id, x, y, z, chunk_x, chunk_z,
                base_material, base_block_data,
                overlay_material, overlay_block_data, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, underlay.getId().toString());
            stmt.setString(2, underlay.getWorldId().toString());
            stmt.setInt(3, underlay.getX());
            stmt.setInt(4, underlay.getY());
            stmt.setInt(5, underlay.getZ());
            stmt.setInt(6, underlay.getChunkX());
            stmt.setInt(7, underlay.getChunkZ());
            stmt.setString(8, underlay.getBaseMaterial().name());
            stmt.setString(9, underlay.getBaseBlockDataString());
            stmt.setString(10, underlay.getOverlayMaterial().name());
            stmt.setString(11, underlay.getOverlayBlockDataString());
            stmt.setLong(12, underlay.getCreatedAt());

            stmt.executeUpdate();
        }
    }

    public boolean delete(@NotNull UUID id) throws SQLException {
        String sql = "DELETE FROM carpet_plus_underlays WHERE id = ?;";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            return stmt.executeUpdate() > 0;
        }
    }

    @NotNull
    public Optional<Underlay> findById(@NotNull UUID id) throws SQLException {
        String sql = "SELECT * FROM carpet_plus_underlays WHERE id = ?;";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUnderlay(rs));
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    public Optional<Underlay> findByLocation(@NotNull UUID worldId, int x, int y, int z) throws SQLException {
        String sql = "SELECT * FROM carpet_plus_underlays WHERE world_id = ? AND x = ? AND y = ? AND z = ?;";
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, worldId.toString());
            stmt.setInt(2, x);
            stmt.setInt(3, y);
            stmt.setInt(4, z);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUnderlay(rs));
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    public List<Underlay> findByChunk(@NotNull UUID worldId, int chunkX, int chunkZ) throws SQLException {
        String sql = "SELECT * FROM carpet_plus_underlays WHERE world_id = ? AND chunk_x = ? AND chunk_z = ?;";
        List<Underlay> result = new ArrayList<>();
        Connection conn = databaseManager.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, worldId.toString());
            stmt.setInt(2, chunkX);
            stmt.setInt(3, chunkZ);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToUnderlay(rs));
                }
            }
        }
        return result;
    }

    @NotNull
    public List<Underlay> findAll() throws SQLException {
        String sql = "SELECT * FROM carpet_plus_underlays;";
        List<Underlay> result = new ArrayList<>();
        Connection conn = databaseManager.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSetToUnderlay(rs));
            }
        }
        return result;
    }

    private Underlay mapResultSetToUnderlay(ResultSet rs) throws SQLException {
        return new Underlay(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("world_id")),
                rs.getInt("x"),
                rs.getInt("y"),
                rs.getInt("z"),
                Material.valueOf(rs.getString("base_material")),
                rs.getString("base_block_data"),
                Material.valueOf(rs.getString("overlay_material")),
                rs.getString("overlay_block_data"),
                rs.getLong("created_at")
        );
    }
}
