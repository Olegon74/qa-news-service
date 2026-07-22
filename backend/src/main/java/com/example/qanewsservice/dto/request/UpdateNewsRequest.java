package com.example.qanewsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNewsRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 300)
    private String previewText;

    @NotBlank
    @Size(max = 5000)
    private String fullText;

    @NotBlank
    @Size(max = 100)
    private String author;
}
