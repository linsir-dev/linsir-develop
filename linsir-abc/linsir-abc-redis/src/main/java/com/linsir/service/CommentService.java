package com.linsir.service;

import com.linsir.entity.Comment;

public interface CommentService {

    void saveComment(Comment comment);

    Comment getComment(int id);
}
