package com.example.qanewsservice.autotests.api.provider;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.api.requests.NewsRequestBuilder;
import com.example.qanewsservice.autotests.generation.NewsDataGenerator;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class NewsValidationProvider {

    private NewsValidationProvider() {
    }

    public static Stream<Arguments> invalidNewsRequests() {
        NewsRequest emptyTitle = NewsRequestBuilder.buildRequiredNewsRequest();
        emptyTitle.setTitle("");

        NewsRequest emptyPreview = NewsRequestBuilder.buildRequiredNewsRequest();
        emptyPreview.setPreviewText("");

        NewsRequest emptyFullText = NewsRequestBuilder.buildRequiredNewsRequest();
        emptyFullText.setFullText("");

        NewsRequest emptyAuthor = NewsRequestBuilder.buildRequiredNewsRequest();
        emptyAuthor.setAuthor("");

        NewsRequest tooLongTitle = NewsRequestBuilder.buildRequiredNewsRequest();
        tooLongTitle.setTitle(NewsDataGenerator.generateString(151));

        NewsRequest tooLongPreview = NewsRequestBuilder.buildRequiredNewsRequest();
        tooLongPreview.setPreviewText(NewsDataGenerator.generateString(301));

        NewsRequest tooLongFullText = NewsRequestBuilder.buildRequiredNewsRequest();
        tooLongFullText.setFullText(NewsDataGenerator.generateString(5001));

        NewsRequest tooLongAuthor = NewsRequestBuilder.buildRequiredNewsRequest();
        tooLongAuthor.setAuthor(NewsDataGenerator.generateString(101));

        return Stream.of(
                arguments("пустой заголовок", emptyTitle, "title"),
                arguments("пустой previewText", emptyPreview, "previewText"),
                arguments("пустой fullText", emptyFullText, "fullText"),
                arguments("пустой автор", emptyAuthor, "author"),
                arguments("слишком длинный заголовок", tooLongTitle, "title"),
                arguments("слишком длинный previewText", tooLongPreview, "previewText"),
                arguments("слишком длинный fullText", tooLongFullText, "fullText"),
                arguments("слишком длинный автор", tooLongAuthor, "author")
        );
    }
}
