package com.example.qanewsservice.autotests.generation;

import com.github.javafaker.Faker;
import java.util.Locale;
import java.util.UUID;

public final class NewsDataGenerator {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private NewsDataGenerator() {
    }

    public static String generateTitle() {
        return "TEST News " + shortUuid() + " " + clean(FAKER.book().title(), 60);
    }

    public static String generatePreviewText() {
        return "Preview " + shortUuid() + ": " + clean(FAKER.lorem().sentence(8), 220);
    }

    public static String generateFullText() {
        return "Full text " + shortUuid() + ". " + clean(FAKER.lorem().paragraph(3), 900);
    }

    public static String generateAuthor() {
        return "QA Author " + shortUuid();
    }

    public static String generateString(int length) {
        return "A".repeat(length);
    }

    private static String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static String clean(String value, int maxLength) {
        String cleaned = value.replaceAll("[^a-zA-Z0-9 .,;:!?'-]", " ").replaceAll("\\s+", " ").trim();
        if (cleaned.length() <= maxLength) {
            return cleaned;
        }
        return cleaned.substring(0, maxLength);
    }
}
