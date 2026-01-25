package com.linsir.es.controller;

import com.linsir.es.entity.Article;
import com.linsir.es.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 文章控制器类，以API接口形式演示ElasticSearch操作
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    /**
     * 创建文章
     */
    @PostMapping("/create")
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article createdArticle = articleService.createArticle(article);
        return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    }
    
    /**
     * 根据ID查询文章
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable(name = "id") String id) {
        Optional<Article> article = articleService.getArticleById(id);
        return article.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * 查询所有文章
     */
    @GetMapping("/all")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }
    
    /**
     * 根据标题查询文章
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Article>> getArticlesByTitle(@PathVariable String title) {
        List<Article> articles = articleService.getArticlesByTitle(title);
        return ResponseEntity.ok(articles);
    }
    
    /**
     * 根据作者查询文章
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Article>> getArticlesByAuthor(@PathVariable String author) {
        List<Article> articles = articleService.getArticlesByAuthor(author);
        return ResponseEntity.ok(articles);
    }
    
    /**
     * 根据浏览量范围查询文章
     */
    @GetMapping("/views-range")
    public ResponseEntity<List<Article>> getArticlesByViewsRange(
            @RequestParam Integer minViews,
            @RequestParam Integer maxViews) {
        List<Article> articles = articleService.getArticlesByViewsRange(minViews, maxViews);
        return ResponseEntity.ok(articles);
    }
    
    /**
     * 全文搜索文章
     */
    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(@RequestParam String keyword) {
        List<Article> articles = articleService.searchArticles(keyword);
        return ResponseEntity.ok(articles);
    }
    
    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody Article article) {
        Article updatedArticle = articleService.updateArticle(id, article);
        if (updatedArticle != null) {
            return ResponseEntity.ok(updatedArticle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
