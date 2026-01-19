package com.linsir.controller;


import com.linsir.entity.Person;
import com.linsir.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/person/")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("list")
    public List<Person> list()
    {
        return personService.findAll();
    }
}
