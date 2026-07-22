package com.example.qanewsservice.service.impl;

import com.example.qanewsservice.dto.request.CreateNewsRequest;
import com.example.qanewsservice.dto.request.UpdateNewsRequest;
import com.example.qanewsservice.dto.response.NewsResponse;
import com.example.qanewsservice.exception.InvalidNewsStatusException;
import com.example.qanewsservice.exception.NewsNotFoundException;
import com.example.qanewsservice.mapper.NewsMapper;
import com.example.qanewsservice.model.News;
import com.example.qanewsservice.model.NewsStatus;
import com.example.qanewsservice.repository.NewsRepository;
import com.example.qanewsservice.service.NewsService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    @Transactional
    public NewsResponse createNews(CreateNewsRequest request) {
        News news = newsRepository.save(newsMapper.toEntity(request));
        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponse getNewsById(Long id) {
        return newsMapper.toResponse(findNews(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> getAllNews(NewsStatus status) {
        List<News> news = status == null ? newsRepository.findAll() : newsRepository.findByStatus(status);
        return news.stream()
                .map(newsMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NewsResponse updateNews(Long id, UpdateNewsRequest request) {
        News news = findNews(id);
        if (news.getStatus() == NewsStatus.ARCHIVED) {
            throw new InvalidNewsStatusException("Archived news cannot be updated");
        }

        news.setTitle(request.getTitle());
        news.setPreviewText(request.getPreviewText());
        news.setFullText(request.getFullText());
        news.setAuthor(request.getAuthor());
        return newsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public NewsResponse publishNews(Long id) {
        News news = findNews(id);
        if (news.getStatus() != NewsStatus.DRAFT) {
            throw new InvalidNewsStatusException("Only draft news can be published");
        }

        news.setStatus(NewsStatus.PUBLISHED);
        news.setPublishedAt(LocalDateTime.now());
        return newsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public NewsResponse archiveNews(Long id) {
        News news = findNews(id);
        if (news.getStatus() != NewsStatus.PUBLISHED) {
            throw new InvalidNewsStatusException("Only published news can be archived");
        }

        news.setStatus(NewsStatus.ARCHIVED);
        news.setArchivedAt(LocalDateTime.now());
        return newsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        News news = findNews(id);
        if (news.getStatus() == NewsStatus.PUBLISHED) {
            throw new InvalidNewsStatusException("Published news cannot be deleted");
        }
        newsRepository.delete(news);
    }

    private News findNews(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new NewsNotFoundException(id));
    }
}
