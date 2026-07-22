package com.example.qanewsservice.service;

import com.example.qanewsservice.dto.request.CreateNewsRequest;
import com.example.qanewsservice.dto.request.UpdateNewsRequest;
import com.example.qanewsservice.dto.response.NewsResponse;
import com.example.qanewsservice.model.NewsStatus;
import java.util.List;

public interface NewsService {

    NewsResponse createNews(CreateNewsRequest request);

    NewsResponse getNewsById(Long id);

    List<NewsResponse> getAllNews(NewsStatus status);

    NewsResponse updateNews(Long id, UpdateNewsRequest request);

    NewsResponse publishNews(Long id);

    NewsResponse archiveNews(Long id);

    void deleteNews(Long id);
}
