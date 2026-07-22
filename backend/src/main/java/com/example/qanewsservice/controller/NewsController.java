package com.example.qanewsservice.controller;

import com.example.qanewsservice.dto.request.CreateNewsRequest;
import com.example.qanewsservice.dto.request.UpdateNewsRequest;
import com.example.qanewsservice.dto.response.NewsResponse;
import com.example.qanewsservice.model.NewsStatus;
import com.example.qanewsservice.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "News management API")
@SecurityRequirement(name = "bearerAuth")
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    @Operation(summary = "Create news")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "News created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody CreateNewsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.createNews(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get news by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "News returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "News not found")
    })
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @GetMapping
    @Operation(summary = "Get all news")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "News list returned"),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<NewsResponse>> getAllNews(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(newsService.getAllNews(parseStatus(status)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update draft or published news")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "News updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<NewsResponse> updateNews(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNewsRequest request) {
        return ResponseEntity.ok(newsService.updateNews(id, request));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish draft news")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "News published"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<NewsResponse> publishNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.publishNews(id));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive published news")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "News archived"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<NewsResponse> archiveNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.archiveNews(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete draft or archived news")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "News deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    private NewsStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return NewsStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid news status: " + status);
        }
    }
}
