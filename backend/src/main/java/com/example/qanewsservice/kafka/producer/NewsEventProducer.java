package com.example.qanewsservice.kafka.producer;

import com.example.qanewsservice.kafka.dto.NewsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsEventProducer {

    private final KafkaTemplate<String, NewsEvent> kafkaTemplate;

    @Value("${app.kafka.topics.published}")
    private String publishedTopic;

    @Value("${app.kafka.topics.archived}")
    private String archivedTopic;

    public void publishNewsPublishedEvent(NewsEvent event) {
        send(publishedTopic, event);
    }

    public void publishNewsArchivedEvent(NewsEvent event) {
        send(archivedTopic, event);
    }

    private void send(String topic, NewsEvent event) {
        try {
            kafkaTemplate.send(topic, event.newsId().toString(), event)
                    .whenComplete((result, failure) -> {
                        if (failure != null) {
                            log.error("Kafka delivery failed: newsId={}, eventId={}, eventType={}",
                                    event.newsId(), event.eventId(), event.eventType(), failure);
                        }
                    });
        } catch (RuntimeException failure) {
            log.error("Kafka send initiation failed: newsId={}, eventId={}, eventType={}",
                    event.newsId(), event.eventId(), event.eventType(), failure);
        }
    }
}
