package com.linsir.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Bank {

    @Id
    private int id;

    private int goodsId;

    private int goodsNum;
}
