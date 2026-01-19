package com.linsir.repository;

import com.linsir.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User,Integer> {


    @Query(nativeQuery = true, value = "select * from user u where u.username =?1")
    User getUserByUsername(String username);


}
