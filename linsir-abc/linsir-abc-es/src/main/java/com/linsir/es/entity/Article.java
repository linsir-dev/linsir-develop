package com.linsir.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 文章实体类，映射到ElasticSearch的articles索引
 */
@Data
@Document(indexName = "articles")
public class Article {
    @Id
    private String id;
    
    @Field(type = FieldType.Text)
    private String title;
    
    @Field(type = FieldType.Text)
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String author;
    
    @Field(type = FieldType.Date)
    private LocalDateTime publishDate;
    
    @Field(type = FieldType.Integer)
    private Integer views;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
}
