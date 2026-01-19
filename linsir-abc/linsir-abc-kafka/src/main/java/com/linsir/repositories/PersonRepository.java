package com.linsir.repositories;

import com.linsir.entity.Person;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends ListCrudRepository<Person, Integer> {

}
