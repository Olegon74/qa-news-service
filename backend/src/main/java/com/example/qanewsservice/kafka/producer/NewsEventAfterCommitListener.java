package com.example.qanewsservice.kafka.producer;

import com.example.qanewsservice.kafka.dto.NewsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NewsEventAfterCommitListener {

    private final NewsEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNewsEvent(NewsEvent event) {
        switch (event.eventType()) {
            case "NEWS_PUBLISHED" -> producer.publishNewsPublishedEvent(event);
            case "NEWS_ARCHIVED" -> producer.publishNewsArchivedEvent(event);
            default -> throw new IllegalArgumentException("Unsupported news event: " + event.eventType());
        }
    }
}
