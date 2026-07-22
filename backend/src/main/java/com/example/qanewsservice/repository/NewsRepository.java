package com.example.qanewsservice.repository;

import com.example.qanewsservice.model.News;
import com.example.qanewsservice.model.NewsStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByStatus(NewsStatus status);
}
