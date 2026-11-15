package com.example.qanewsservice.kafka.dto;

import com.example.qanewsservice.model.NewsStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewsEvent(
        UUID eventId,
        String eventType,
        Long newsId,
        String title,
        NewsStatus status,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant publishedAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant archivedAt) {
}
