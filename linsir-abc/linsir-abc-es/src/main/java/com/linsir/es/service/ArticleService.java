package com.linsir.es.service;

import com.linsir.es.entity.Article;

import java.util.List;
import java.util.Optional;

/**
 * 文章服务接口，定义业务逻辑方法
 */
public interface ArticleService {
    
    /**
     * 创建文章
     */
    Article createArticle(Article article);
    
    /**
     * 根据ID查询文章
     */
    Optional<Article> getArticleById(String id);
    
    /**
     * 查询所有文章
     */
    List<Article> getAllArticles();
    
    /**
     * 根据标题查询文章
     */
    List<Article> getArticlesByTitle(String title);
    
    /**
     * 根据作者查询文章
     */
    List<Article> getArticlesByAuthor(String author);
    
    /**
     * 根据浏览量范围查询文章
     */
    List<Article> getArticlesByViewsRange(Integer minViews, Integer maxViews);
    
    /**
     * 更新文章
     */
    Article updateArticle(String id, Article article);
    
    /**
     * 删除文章
     */
    void deleteArticle(String id);
    
    /**
     * 全文搜索文章
     */
    List<Article> searchArticles(String keyword);
}
