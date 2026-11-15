package com.example.qanewsservice.integration.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.qanewsservice.integration.kafka.base.AbstractKafkaIntegrationTest;
import com.example.qanewsservice.integration.kafka.support.KafkaTestConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("QA News Service")
@Feature("Kafka news archival")
@Tag("KAFKA")
class NewsArchivedKafkaIT extends AbstractKafkaIntegrationTest {

    @Test
    void successfulArchivalEmitsEvent() {
        long id = createPublishedNews();
        try (KafkaTestConsumer consumer = consumer(archivedTopic)) {
            JsonNode archived = parse(transition(id, "archive", adminToken).then().statusCode(200).extract().response());
            KafkaTestConsumer.ObservedEvent event = consumer.awaitEvent(id, positiveTimeout);
            JsonNode payload = event.payload();
            assertThat(archived.path("status").asText()).isEqualTo("ARCHIVED");
            assertThat(event.key()).isEqualTo(Long.toString(id));
            assertThat(payload.path("eventType").asText()).isEqualTo("NEWS_ARCHIVED");
            assertThat(payload.path("newsId").asLong()).isEqualTo(id);
            assertThat(payload.path("status").asText()).isEqualTo("ARCHIVED");
            assertThat(payload.path("title").asText()).isEqualTo(archived.path("title").asText());
        }
    }

    @Test
    void archivePayloadHasUuidAndUtcTimestamps() {
        long id = createPublishedNews();
        try (KafkaTestConsumer consumer = consumer(archivedTopic)) {
            transition(id, "archive", adminToken).then().statusCode(200);
            JsonNode payload = consumer.awaitEvent(id, positiveTimeout).payload();
            assertThat(UUID.fromString(payload.path("eventId").asText())).isNotNull();
            assertThat(payload.path("eventType").asText()).isEqualTo("NEWS_ARCHIVED");
            assertThat(payload.path("status").asText()).isEqualTo("ARCHIVED");
            assertThat(payload.path("newsId").asLong()).isEqualTo(id);
            assertThat(payload.path("archivedAt").asText()).endsWith("Z");
            assertThat(payload.path("occurredAt").asText()).endsWith("Z");
            assertThat(Instant.parse(payload.path("archivedAt").asText())).isNotNull();
            assertThat(Instant.parse(payload.path("occurredAt").asText())).isNotNull();
            assertThat(payload.has("publishedAt")).isFalse();
        }
    }

    @Test
    void databaseAndKafkaAgreeOnArchival() {
        long id = createPublishedNews();
        try (KafkaTestConsumer consumer = consumer(archivedTopic)) {
            transition(id, "archive", adminToken).then().statusCode(200);
            JsonNode event = consumer.awaitEvent(id, positiveTimeout).payload();
            var row = jdbc.queryForMap("select status, archived_at from news where id = ?", id);
            assertThat(row.get("status")).isEqualTo("ARCHIVED");
            assertThat(row.get("archived_at")).isNotNull();
            Instant stored = ((java.sql.Timestamp) row.get("archived_at"))
                    .toLocalDateTime().toInstant(ZoneOffset.UTC);
            assertThat(Instant.parse(event.path("archivedAt").asText())).isEqualTo(stored);
            assertThat(event.path("newsId").asLong()).isEqualTo(id);
            assertThat(event.path("status").asText()).isEqualTo("ARCHIVED");
        }
    }

    private long createPublishedNews() {
        long id = createDraft().path("id").asLong();
        transition(id, "publish", adminToken).then().statusCode(200);
        return id;
    }
}
