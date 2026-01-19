package com.linsir.service;

import com.linsir.entity.Person;

import java.util.List;

public interface PersonService {

    List<Person> findAll();

    Person findById(int id);
}
