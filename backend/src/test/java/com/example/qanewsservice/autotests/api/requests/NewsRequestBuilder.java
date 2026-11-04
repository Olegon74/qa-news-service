package com.example.qanewsservice.autotests.api.requests;

import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.generation.NewsDataGenerator;

public final class NewsRequestBuilder {

    private NewsRequestBuilder() {
    }

    public static NewsRequest buildRequiredNewsRequest() {
        return NewsRequest.builder()
                .title(NewsDataGenerator.generateTitle())
                .previewText(NewsDataGenerator.generatePreviewText())
                .fullText(NewsDataGenerator.generateFullText())
                .author(NewsDataGenerator.generateAuthor())
                .build();
    }

    public static NewsRequest buildUpdatedNewsRequest() {
        return NewsRequest.builder()
                .title("Updated " + NewsDataGenerator.generateTitle())
                .previewText("Updated " + NewsDataGenerator.generatePreviewText())
                .fullText("Updated " + NewsDataGenerator.generateFullText())
                .author("Updated " + NewsDataGenerator.generateAuthor())
                .build();
    }

    public static NewsRequest copy(NewsRequest request) {
        return NewsRequest.builder()
                .title(request.getTitle())
                .previewText(request.getPreviewText())
                .fullText(request.getFullText())
                .author(request.getAuthor())
                .build();
    }
}
