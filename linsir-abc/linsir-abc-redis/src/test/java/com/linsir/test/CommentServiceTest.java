package com.linsir.test;

import com.linsir.entity.Comment;
import com.linsir.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private  CommentService commentService;



    @Test
    public void saveCommentTest() {
        Comment comment = new Comment();
        comment.setId(21);
        comment.setArticleId(28);
        comment.setAuthor("yux");
        comment.setContent("xxxxxxxxxxxxxxxx");
        LocalDateTime currentDateTime = LocalDateTime.now();
        comment.setDate(currentDateTime.toString());
        commentService.saveComment(comment);
    }

    @Test
    public void findCommentTest() {
        commentService.getComment(23);
    }
}
