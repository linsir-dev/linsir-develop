package com.linsir.test;

import com.linsir.entity.Person;
import com.linsir.service.CacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CacheTest {

    @Autowired
    private CacheService cacheService;


    @Test
    public void addTest()
    {
        Person person = new Person();
        person.setId(1);
        person.setName("ui");
        person.setAge(18);
        cacheService.addPerson(person);
    }

    @Test
    public void findPersonBycCacheTest() {
        Person person = cacheService.findById(String.valueOf(1));
        System.out.println(person);
        Assertions.assertNotNull(person);
    }

    @Test
    public void upDataPersonTest()
    {
        Person person = new Person();
        person.setId(1);
        person.setName("uixiaqqqq");
        person.setAge(13);
        cacheService.updatePerson(person);
    }


    @Test
    public void delPersonTest()
    {
        cacheService.deletePerson(String.valueOf(1));
    }

}
