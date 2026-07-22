package com.example.qanewsservice.dto.response;

import com.example.qanewsservice.model.NewsStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsResponse {

    private Long id;
    private String title;
    private String previewText;
    private String fullText;
    private String author;
    private NewsStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime archivedAt;
}
