package com.linsir.es.repository;

import com.linsir.es.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 文章Repository接口，用于ElasticSearch数据访问
 */
public interface ArticleRepository extends ElasticsearchRepository<Article, String> {
    
    /**
     * 根据标题查询文章
     */
    List<Article> findByTitle(String title);
    
    /**
     * 根据作者查询文章
     */
    List<Article> findByAuthor(String author);
    
    /**
     * 根据浏览量范围查询文章
     */
    List<Article> findByViewsBetween(Integer minViews, Integer maxViews);
}
