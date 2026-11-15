package com.example.qanewsservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class NewsTopicsConfig {

    @Bean
    NewTopic publishedTopic(@Value("${app.kafka.topics.published}") String name) {
        return TopicBuilder.name(name).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic archivedTopic(@Value("${app.kafka.topics.archived}") String name) {
        return TopicBuilder.name(name).partitions(1).replicas(1).build();
    }
}
