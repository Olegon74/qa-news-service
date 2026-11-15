package com.example.qanewsservice.integration.kafka.support;

import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public final class KafkaTestConsumer implements AutoCloseable {

    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper objectMapper;
    private final Duration pollInterval;

    public KafkaTestConsumer(String bootstrapServers, String topic, ObjectMapper objectMapper,
                             Duration assignmentTimeout, Duration pollInterval) {
        this.objectMapper = objectMapper;
        this.pollInterval = pollInterval;
        this.consumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "kafka-it-" + UUID.randomUUID(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false));
        try {
            consumer.subscribe(List.of(topic));
            await().atMost(assignmentTimeout).pollInterval(pollInterval)
                    .until(() -> {
                        consumer.poll(pollInterval);
                        return !consumer.assignment().isEmpty();
                    });
            consumer.seekToEnd(consumer.assignment());
            for (var partition : consumer.assignment()) {
                consumer.position(partition);
            }
            Allure.step("Consumer assigned before action: " + topic);
        } catch (RuntimeException failure) {
            consumer.close();
            throw failure;
        }
    }

    public ObservedEvent awaitEvent(long newsId, Duration timeout) {
        AtomicReference<ObservedEvent> found = new AtomicReference<>();
        await().atMost(timeout).pollInterval(pollInterval)
                .until(() -> {
                    find(newsId).ifPresent(found::set);
                    return found.get() != null;
                });
        return found.get();
    }

    public void assertNoEvent(long newsId, Duration observationWindow) {
        Allure.step("Observe no Kafka event for newsId=" + newsId + " during " + observationWindow);
        Instant deadline = Instant.now().plus(observationWindow);
        while (Instant.now().isBefore(deadline)) {
            Duration remaining = Duration.between(Instant.now(), deadline);
            if (remaining.isNegative() || remaining.isZero()) {
                break;
            }
            Duration wait = remaining.compareTo(pollInterval) < 0 ? remaining : pollInterval;
            Optional<ObservedEvent> unexpected = find(newsId, wait);
            if (unexpected.isPresent()) {
                throw new AssertionError("Unexpected Kafka event for newsId=" + newsId
                        + ", eventId=" + unexpected.get().payload().path("eventId").asText());
            }
        }
    }

    private Optional<ObservedEvent> find(long newsId) {
        return find(newsId, pollInterval);
    }

    private Optional<ObservedEvent> find(long newsId, Duration wait) {
        for (ConsumerRecord<String, String> record : consumer.poll(wait)) {
            try {
                JsonNode payload = objectMapper.readTree(record.value());
                if (payload.path("newsId").asLong(-1) == newsId) {
                    return Optional.of(new ObservedEvent(record.key(), payload));
                }
            } catch (Exception failure) {
                throw new AssertionError("Invalid Kafka JSON for record key=" + record.key(), failure);
            }
        }
        return Optional.empty();
    }

    @Override
    public void close() {
        consumer.close();
    }

    public record ObservedEvent(String key, JsonNode payload) {
    }
}
