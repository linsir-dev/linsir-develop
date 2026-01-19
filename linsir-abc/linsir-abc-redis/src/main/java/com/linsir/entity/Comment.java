package com.linsir.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Comment implements Serializable {

    @Id
    private int id;

    private String content;

    private int articleId;

    private String author;

    private String date;


}
