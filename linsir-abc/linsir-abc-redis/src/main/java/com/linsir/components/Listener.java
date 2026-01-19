package com.linsir.components;

import com.linsir.constants.CountConstants;
import com.linsir.entity.Comment;
import com.linsir.service.impl.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class Listener {

    private final static Logger logger = LoggerFactory.getLogger(Listener.class);


    private final String COMMENT = "COMMENT";

    private final String REPLY = "REPLY";

    private final String DELETE_COMMENT ="DELETE_COMMENT";


    @Autowired
    private RedisClient redisClient;



    @EventListener(classes = {PersonSaveEvent.class})
    public void handleForPersonSaveEvent(PersonSaveEvent personSaveEvent)
    {
        logger.info("handleForPersonSaveEvent----------------"+personSaveEvent.getName());
    }

    @EventListener(NotifyMsgEvent.class)
    @Async
    public void notifyMsgListener(NotifyMsgEvent notifyMsgEvent)
    {
        Comment comment = (Comment) notifyMsgEvent.getObject();
        switch (notifyMsgEvent.getNotifyType()){
                case COMMENT:
                    logger.info("notifyMsgEvent:COMMENT comment 发布评论 -----------{}",comment.getContent());
                    break;
                case REPLY:
                    logger.info("notifyMsgEvent:REPLY  回复评论");
                    redisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO+comment.getArticleId(),CountConstants.COMMENT_COUNT,1);
                    break;
                //删除评论/回复
                case DELETE_COMMENT:
                    logger.info("notifyMsgEvent:DELETE_COMMENT");
                    redisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO+comment.getArticleId(),CountConstants.COMMENT_COUNT,-1);
                    break;
            default:
                logger.info("xxxxxxxxxxx");
        }
    }
}
