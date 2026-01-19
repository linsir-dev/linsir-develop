package com.linsir.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Article {

    @Id
    private Long id;



}
