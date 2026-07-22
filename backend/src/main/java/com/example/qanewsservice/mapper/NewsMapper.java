package com.example.qanewsservice.mapper;

import com.example.qanewsservice.dto.request.CreateNewsRequest;
import com.example.qanewsservice.dto.response.NewsResponse;
import com.example.qanewsservice.model.News;
import com.example.qanewsservice.model.NewsStatus;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {

    public News toEntity(CreateNewsRequest request) {
        return News.builder()
                .title(request.getTitle())
                .previewText(request.getPreviewText())
                .fullText(request.getFullText())
                .author(request.getAuthor())
                .status(NewsStatus.DRAFT)
                .build();
    }

    public NewsResponse toResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .previewText(news.getPreviewText())
                .fullText(news.getFullText())
                .author(news.getAuthor())
                .status(news.getStatus())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .publishedAt(news.getPublishedAt())
                .archivedAt(news.getArchivedAt())
                .build();
    }
}
