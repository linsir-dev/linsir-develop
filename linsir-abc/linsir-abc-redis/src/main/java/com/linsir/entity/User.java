package com.linsir.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class User implements Serializable {

    @Id
    private int id;

    private String username;

    private String password;

}
