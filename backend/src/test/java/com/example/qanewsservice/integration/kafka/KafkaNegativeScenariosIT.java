package com.example.qanewsservice.integration.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.qanewsservice.integration.kafka.base.AbstractKafkaIntegrationTest;
import com.example.qanewsservice.integration.kafka.support.KafkaTestConsumer;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("QA News Service")
@Feature("Kafka negative scenarios")
@Tag("KAFKA")
class KafkaNegativeScenariosIT extends AbstractKafkaIntegrationTest {

    @Test
    void repeatedPublicationDoesNotEmitAnotherEvent() {
        long id = publishedNews();
        assertRejectedWithoutEvent(id, "publish", 409, publishedTopic, "PUBLISHED", adminToken);
    }

    @Test
    void archivedNewsCannotBeRepublished() {
        long id = archivedNews();
        assertRejectedWithoutEvent(id, "publish", 409, publishedTopic, "ARCHIVED", adminToken);
    }

    @Test
    void draftCannotBeArchived() {
        long id = createDraft().path("id").asLong();
        assertRejectedWithoutEvent(id, "archive", 409, archivedTopic, "DRAFT", adminToken);
    }

    @Test
    void repeatedArchivalDoesNotEmitAnotherEvent() {
        long id = archivedNews();
        assertRejectedWithoutEvent(id, "archive", 409, archivedTopic, "ARCHIVED", adminToken);
    }

    @Test
    void viewerCannotPublishAndEmitsNothing() {
        long id = createDraft().path("id").asLong();
        assertRejectedWithoutEvent(id, "publish", 403, publishedTopic, "DRAFT",
                login("viewer", "viewer123"));
    }

    @Test
    void missingTokenCannotPublishAndEmitsNothing() {
        long id = createDraft().path("id").asLong();
        assertRejectedWithoutEvent(id, "publish", 401, publishedTopic, "DRAFT", null);
    }

    @Test
    void nonexistentNewsEmitsNothing() {
        long id = Long.MAX_VALUE;
        try (KafkaTestConsumer consumer = consumer(publishedTopic)) {
            transition(id, "publish", adminToken).then().statusCode(404);
            consumer.assertNoEvent(id, negativeWindow);
        }
    }

    private void assertRejectedWithoutEvent(long id, String action, int expectedHttpStatus,
                                            String topic, String expectedNewsStatus, String token) {
        try (KafkaTestConsumer consumer = consumer(topic)) {
            transition(id, action, token).then().statusCode(expectedHttpStatus);
            assertThat(getNews(id).path("status").asText()).isEqualTo(expectedNewsStatus);
            consumer.assertNoEvent(id, negativeWindow);
        }
    }

    private long publishedNews() {
        long id = createDraft().path("id").asLong();
        transition(id, "publish", adminToken).then().statusCode(200);
        return id;
    }

    private long archivedNews() {
        long id = publishedNews();
        transition(id, "archive", adminToken).then().statusCode(200);
        return id;
    }
}
