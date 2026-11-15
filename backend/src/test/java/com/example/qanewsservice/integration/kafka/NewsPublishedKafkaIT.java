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
@Feature("Kafka news publication")
@Tag("KAFKA")
class NewsPublishedKafkaIT extends AbstractKafkaIntegrationTest {

    @Test
    void successfulPublicationEmitsEvent() {
        JsonNode draft = createDraft();
        long id = draft.path("id").asLong();
        try (KafkaTestConsumer consumer = consumer(publishedTopic)) {
            JsonNode published = parse(transition(id, "publish", adminToken).then().statusCode(200).extract().response());
            KafkaTestConsumer.ObservedEvent event = consumer.awaitEvent(id, positiveTimeout);
            assertThat(published.path("status").asText()).isEqualTo("PUBLISHED");
            assertThat(event.payload().path("eventType").asText()).isEqualTo("NEWS_PUBLISHED");
            assertThat(event.payload().path("newsId").asLong()).isEqualTo(id);
            assertThat(event.payload().path("status").asText()).isEqualTo("PUBLISHED");
        }
    }

    @Test
    void eventPayloadHasCurrentTitleUuidAndUtcTimestamps() {
        JsonNode draft = createDraft();
        long id = draft.path("id").asLong();
        try (KafkaTestConsumer consumer = consumer(publishedTopic)) {
            transition(id, "publish", adminToken).then().statusCode(200);
            JsonNode payload = consumer.awaitEvent(id, positiveTimeout).payload();
            assertThat(UUID.fromString(payload.path("eventId").asText())).isNotNull();
            assertThat(payload.path("title").asText()).isEqualTo(draft.path("title").asText());
            assertThat(payload.path("status").asText()).isEqualTo("PUBLISHED");
            assertThat(payload.path("publishedAt").asText()).endsWith("Z");
            assertThat(payload.path("occurredAt").asText()).endsWith("Z");
            assertThat(Instant.parse(payload.path("publishedAt").asText())).isNotNull();
            assertThat(Instant.parse(payload.path("occurredAt").asText())).isNotNull();
            assertThat(payload.has("archivedAt")).isFalse();
        }
    }

    @Test
    void kafkaRecordKeyIsNewsId() {
        long id = createDraft().path("id").asLong();
        try (KafkaTestConsumer consumer = consumer(publishedTopic)) {
            transition(id, "publish", adminToken).then().statusCode(200);
            assertThat(consumer.awaitEvent(id, positiveTimeout).key()).isEqualTo(Long.toString(id));
        }
    }

    @Test
    void databaseAndKafkaAgreeOnPublication() {
        long id = createDraft().path("id").asLong();
        try (KafkaTestConsumer consumer = consumer(publishedTopic)) {
            transition(id, "publish", adminToken).then().statusCode(200);
            JsonNode event = consumer.awaitEvent(id, positiveTimeout).payload();
            var row = jdbc.queryForMap("select status, published_at from news where id = ?", id);
            assertThat(row.get("status")).isEqualTo("PUBLISHED");
            assertThat(row.get("published_at")).isNotNull();
            Instant stored = ((java.sql.Timestamp) row.get("published_at"))
                    .toLocalDateTime().toInstant(ZoneOffset.UTC);
            assertThat(Instant.parse(event.path("publishedAt").asText())).isEqualTo(stored);
            assertThat(event.path("eventType").asText()).isEqualTo("NEWS_PUBLISHED");
            assertThat(event.path("newsId").asLong()).isEqualTo(id);
        }
    }
}
