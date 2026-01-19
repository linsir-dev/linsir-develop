package com.linsir.test;

import com.linsir.components.NotifyMsgEvent;
import com.linsir.components.PersonSaveEvent;
import com.linsir.entity.Comment;
import com.linsir.service.CommentService;
import com.linsir.service.EventPublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
public class EventPublisherServiceImplTest {

    @Autowired
    private EventPublisherService eventPublisherService;


    @Autowired
    private CommentService commentService;


    @Test
    public void personSaveEventPublishTest() {
        PersonSaveEvent personSaveEvent = new PersonSaveEvent("save",1,"xxx");
        eventPublisherService.personSaveEventPublish(personSaveEvent);
    }


    @Test
    @Transactional
    public void notifyMsgEventPublishTest()
    {
        Comment comment = new Comment();
        comment.setId(12);
        comment.setAuthor("yuxl");
        comment.setArticleId(11);
        LocalDateTime currentDateTime = LocalDateTime.now();
        comment.setDate(currentDateTime.toString());
        comment.setContent("这个太棒了");
        commentService.saveComment(comment);
        NotifyMsgEvent notifyMsgEvent = new NotifyMsgEvent("REPLY Comment","REPLY",comment);
        eventPublisherService.notifyMsgEventPublish(notifyMsgEvent);
    }
}
