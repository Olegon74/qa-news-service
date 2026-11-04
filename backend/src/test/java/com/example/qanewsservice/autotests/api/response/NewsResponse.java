package com.example.qanewsservice.autotests.api.response;

import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsResponse {

    private Long id;
    private String title;
    private String previewText;
    private String fullText;
    private String author;
    private NewsStatus status;
    private String createdAt;
    private String updatedAt;
    private String publishedAt;
    private String archivedAt;
}
