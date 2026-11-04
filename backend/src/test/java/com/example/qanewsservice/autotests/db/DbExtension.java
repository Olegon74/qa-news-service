package com.example.qanewsservice.autotests.db;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DbExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Object DB_LIFECYCLE_LOCK = new Object();
    private static int activeContexts;

    private boolean contextRegistered;

    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println(">>> DbExtension.beforeAll()");
        synchronized (DB_LIFECYCLE_LOCK) {
            try {
                DbManager.init();
                activeContexts++;
                contextRegistered = true;
                System.out.println("Инициализировано подключение к БД");
            } catch (Exception e) {
                contextRegistered = false;
                System.out.println("Ошибка инициализации БД: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Не удалось инициализировать подключение к БД для тестов", e);
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        synchronized (DB_LIFECYCLE_LOCK) {
            if (!contextRegistered) {
                System.out.println("Отключение БД пропущено: соединение не инициализировано");
                return;
            }

            activeContexts--;
            contextRegistered = false;

            if (activeContexts <= 0) {
                activeContexts = 0;
                DbManager.shutdown();
                System.out.println("БД отключена");
            }
        }
    }
}
