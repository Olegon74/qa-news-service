package com.example.qanewsservice.autotests.db;

import com.example.qanewsservice.autotests.config.ConfigService;
import io.qameta.allure.Step;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public final class NewsCleanupHelper {

    private static final String DEFAULT_TABLE = "news";

    private NewsCleanupHelper() {
    }

    @Step("Очистить новости по id")
    public static int cleanupNewsByIds(Collection<Long> newsIds) throws SQLException {
        if (!DbManager.isInitialized() || newsIds == null || newsIds.isEmpty()) {
            return 0;
        }

        int deletedRows = 0;
        for (Long newsId : newsIds) {
            deletedRows += cleanupNewsById(newsId);
        }
        System.out.println("Очищено новостей после теста: " + deletedRows);
        return deletedRows;
    }

    @Step("Очистить новость по id={newsId}")
    public static int cleanupNewsById(Long newsId) throws SQLException {
        if (!DbManager.isInitialized() || newsId == null) {
            return 0;
        }

        String cleanupSql = "delete from " + newsTableName() + " where id = ?";
        try (Connection connection = DbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(cleanupSql)) {
            statement.setLong(1, newsId);
            int deletedRows = statement.executeUpdate();
            System.out.println("Очищено новостей с id=" + newsId + ": " + deletedRows);
            return deletedRows;
        }
    }

    @Step("Очистить новость по заголовку: {title}")
    public static int cleanupNewsByTitle(String title) throws SQLException {
        if (!DbManager.isInitialized() || isBlank(title)) {
            return 0;
        }

        String cleanupSql = "delete from " + newsTableName() + " where title = ?";
        try (Connection connection = DbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(cleanupSql)) {
            statement.setString(1, title);
            int deletedRows = statement.executeUpdate();
            System.out.println("Очищено новостей с title=\"" + title + "\": " + deletedRows);
            return deletedRows;
        }
    }

    private static String newsTableName() {
        String schema = ConfigService.get("db.schema");
        String table = ConfigService.get("db.news.table");
        if (isBlank(table)) {
            table = DEFAULT_TABLE;
        }
        if (isBlank(schema)) {
            return quoteIdentifier(table);
        }
        return quoteIdentifier(schema) + "." + quoteIdentifier(table);
    }

    private static String quoteIdentifier(String identifier) {
        if (!identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Некорректный SQL identifier: " + identifier);
        }
        return "\"" + identifier + "\"";
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
