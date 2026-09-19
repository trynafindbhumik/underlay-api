package org.carpetplus.underlay.storage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages SQLite JDBC database connections and PRAGMA settings for optimal performance.
 */
public class DatabaseManager {

    private final File databaseFile;
    private final Logger logger;
    private Connection connection;

    public DatabaseManager(@NotNull File databaseFile, @NotNull Logger logger) {
        this.databaseFile = Objects.requireNonNull(databaseFile, "databaseFile cannot be null");
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
    }

    public synchronized void initialize() throws SQLException {
        File parentDir = databaseFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found on classpath", e);
        }

        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        this.connection = DriverManager.getConnection(url);

        // Apply performance PRAGMAs
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA synchronous = NORMAL;");
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA busy_timeout = 5000;");
        }

        logger.info("Initialized SQLite database connection: " + databaseFile.getName());

        // Run schema migrations
        SchemaMigrator migrator = new SchemaMigrator(connection, logger);
        migrator.migrate();
    }

    @NotNull
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            this.connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing SQLite database connection", e);
            }
        }
    }
}
