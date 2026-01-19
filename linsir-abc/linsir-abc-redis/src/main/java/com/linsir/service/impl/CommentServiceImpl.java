package com.linsir.service.impl;

import com.linsir.entity.Comment;
import com.linsir.repository.CommentRepostitory;
import com.linsir.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepostitory commentRepostitory;

    @Override
    public void saveComment(Comment comment) {
        commentRepostitory.save(comment);
    }


    @Cacheable(key = "#id",value = "comment")
    @Override
    public Comment getComment(int id) {
        return commentRepostitory.findById(id).get();
    }
}
