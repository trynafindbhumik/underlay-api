package org.carpetplus.underlay.storage;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Embedded database migration runner that tracks schema versions in underlay_schema_history table.
 */
public class SchemaMigrator {

    private final Connection connection;
    private final Logger logger;

    public SchemaMigrator(@NotNull Connection connection, @NotNull Logger logger) {
        this.connection = Objects.requireNonNull(connection, "connection cannot be null");
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
    }

    public void migrate() throws SQLException {
        ensureSchemaHistoryTable();

        int currentVersion = getCurrentVersion();
        logger.info("Current SQLite database schema version: " + currentVersion);

        if (currentVersion < 1) {
            applyV1Schema();
            recordVersion(1, "V1__create_underlays_table");
        }
    }

    private void ensureSchemaHistoryTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS underlay_schema_history (
                installed_rank INTEGER PRIMARY KEY AUTOINCREMENT,
                version INTEGER NOT NULL,
                description VARCHAR(200) NOT NULL,
                installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private int getCurrentVersion() throws SQLException {
        String sql = "SELECT MAX(version) FROM underlay_schema_history;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private void recordVersion(int version, String description) throws SQLException {
        String sql = "INSERT INTO underlay_schema_history (version, description) VALUES (?, ?);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, version);
            stmt.setString(2, description);
            stmt.executeUpdate();
        }
    }

    private void applyV1Schema() throws SQLException {
        logger.info("Applying migration V1__create_underlays_table...");
        String sql = """
            CREATE TABLE IF NOT EXISTS carpet_plus_underlays (
                id VARCHAR(36) PRIMARY KEY,
                world_id VARCHAR(36) NOT NULL,
                x INTEGER NOT NULL,
                y INTEGER NOT NULL,
                z INTEGER NOT NULL,
                chunk_x INTEGER NOT NULL,
                chunk_z INTEGER NOT NULL,
                base_material VARCHAR(64) NOT NULL,
                base_block_data TEXT NOT NULL,
                overlay_material VARCHAR(64) NOT NULL,
                overlay_block_data TEXT NOT NULL,
                created_at BIGINT NOT NULL
            );

            CREATE INDEX IF NOT EXISTS idx_underlays_chunk ON carpet_plus_underlays(world_id, chunk_x, chunk_z);
            CREATE UNIQUE INDEX IF NOT EXISTS idx_underlays_location ON carpet_plus_underlays(world_id, x, y, z);
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
