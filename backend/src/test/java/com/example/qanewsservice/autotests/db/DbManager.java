package com.example.qanewsservice.autotests.db;

import com.example.qanewsservice.autotests.config.ConfigService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DbManager {

    private static HikariDataSource dataSource;

    private DbManager() {
    }

    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(ConfigService.getRequired("db.jdbc_url"));
        hikariConfig.setUsername(ConfigService.getRequired("db.username"));
        hikariConfig.setPassword(ConfigService.getRequired("db.password"));

        String maxPoolSize = ConfigService.get("db.pool.maxSize");
        String minIdle = ConfigService.get("db.pool.minIdle");
        String idleTimeoutMs = ConfigService.get("db.pool.idleTimeoutMs");
        String connectionTimeoutMs = ConfigService.get("db.pool.connTimeoutMs");
        if (!isBlank(maxPoolSize)) {
            hikariConfig.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
        }
        if (!isBlank(minIdle)) {
            hikariConfig.setMinimumIdle(Integer.parseInt(minIdle));
        }
        if (!isBlank(idleTimeoutMs)) {
            hikariConfig.setIdleTimeout(Long.parseLong(idleTimeoutMs));
        }
        if (!isBlank(connectionTimeoutMs)) {
            hikariConfig.setConnectionTimeout(Long.parseLong(connectionTimeoutMs));
        }

        String schema = ConfigService.get("db.schema");
        if (!isBlank(schema)) {
            hikariConfig.setSchema(schema);
        }

        String applicationName = ConfigService.get("db.applicationName");
        if (!isBlank(applicationName)) {
            hikariConfig.addDataSourceProperty("ApplicationName", applicationName);
        }

        dataSource = new HikariDataSource(hikariConfig);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DbManager не инициализирован");
        }
        return dataSource.getConnection();
    }

    public static boolean isInitialized() {
        return dataSource != null;
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
        dataSource = null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
