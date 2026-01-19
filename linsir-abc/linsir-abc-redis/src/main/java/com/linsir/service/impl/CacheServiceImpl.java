package com.linsir.service.impl;

import com.linsir.entity.Person;
import com.linsir.repository.PersonRepository;
import com.linsir.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private PersonRepository personRepository;


    @Override
    public void addPerson(Person person) {
        personRepository.save(person);
    }

    @Cacheable(key = "#id",value = "person")
    @Override
    public Person findById(String id) {
        return personRepository.findById(Integer.parseInt(id)).get();
    }

    @CachePut(value = "person",key = "#person.id")
    @Override
    public Person updatePerson(Person person) {
        return personRepository.save(person);
    }

    @CacheEvict(value = "person",key = "#id")
    @Override
    public void deletePerson(String id) {
        personRepository.deleteById(Integer.parseInt(id));
    }

}
