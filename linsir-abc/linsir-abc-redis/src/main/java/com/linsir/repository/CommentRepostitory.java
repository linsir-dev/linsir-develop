package com.linsir.repository;

import com.linsir.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepostitory  extends CrudRepository<Comment, Integer> {
}
