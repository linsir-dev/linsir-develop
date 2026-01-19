package com.linsir.service;

import com.linsir.entity.Person;


public interface CacheService {

    void addPerson(Person person);

    Person findById(String id);

    Person updatePerson(Person person);

    void deletePerson(String id);

}
