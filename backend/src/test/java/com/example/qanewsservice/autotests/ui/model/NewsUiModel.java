package com.example.qanewsservice.autotests.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsUiModel {

    private String title;
    private String previewText;
    private String fullText;
    private String author;
}
