package com.linsir.es.service.impl;

import com.linsir.es.entity.Article;
import com.linsir.es.repository.ArticleRepository;
import com.linsir.es.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文章服务实现类，实现业务逻辑方法
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    
    /**
     * 创建文章
     */
    @Override
    public Article createArticle(Article article) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        article.setCreatedAt(now);
        article.setUpdatedAt(now);
        return articleRepository.save(article);
    }
    
    /**
     * 根据ID查询文章
     */
    @Override
    public Optional<Article> getArticleById(String id) {
        return articleRepository.findById(id);
    }
    
    /**
     * 查询所有文章
     */
    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = new java.util.ArrayList<>();
        articleRepository.findAll().forEach(articles::add);
        return articles;
    }
    
    /**
     * 根据标题查询文章
     */
    @Override
    public List<Article> getArticlesByTitle(String title) {
        return articleRepository.findByTitle(title);
    }
    
    /**
     * 根据作者查询文章
     */
    @Override
    public List<Article> getArticlesByAuthor(String author) {
        return articleRepository.findByAuthor(author);
    }
    
    /**
     * 根据浏览量范围查询文章
     */
    @Override
    public List<Article> getArticlesByViewsRange(Integer minViews, Integer maxViews) {
        return articleRepository.findByViewsBetween(minViews, maxViews);
    }
    
    /**
     * 更新文章
     */
    @Override
    public Article updateArticle(String id, Article article) {
        Optional<Article> existingArticle = articleRepository.findById(id);
        if (existingArticle.isPresent()) {
            Article updatedArticle = existingArticle.get();
            updatedArticle.setTitle(article.getTitle());
            updatedArticle.setContent(article.getContent());
            updatedArticle.setAuthor(article.getAuthor());
            updatedArticle.setPublishDate(article.getPublishDate());
            updatedArticle.setViews(article.getViews());
            updatedArticle.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return articleRepository.save(updatedArticle);
        }
        return null;
    }
    
    /**
     * 删除文章
     */
    @Override
    public void deleteArticle(String id) {
        articleRepository.deleteById(id);
    }
    
    /**
     * 全文搜索文章
     */
    @Override
    public List<Article> searchArticles(String keyword) {
        Criteria criteria = new Criteria("title").matches(keyword)
                .or(new Criteria("content").matches(keyword));
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<Article> searchHits = elasticsearchOperations.search(query, Article.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
