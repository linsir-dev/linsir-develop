package com.linsir.service.impl;

import com.linsir.entity.Person;
import com.linsir.repositories.PersonRepository;
import com.linsir.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;


    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Person findById(int id) {
        return null;
    }
}
