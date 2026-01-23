package com.linsir.service.impl;

import com.linsir.service.CommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private static final String LIKE_PREFIX = "comment:like:";
    private static final String USER_LIKED_PREFIX = "user:liked:";
    private static final String COMMENT_LIKE_COUNT_PREFIX = "comment:like:count:";
    private static final String COMMENT_VIEW_COUNT_PREFIX = "comment:view:count:";
    private static final String HOT_COMMENTS_PREFIX = "article:hot:comments:";
    private static final String ARTICLE_COMMENTS_PREFIX = "article:comments:";
    private static final String COMMENT_REPLIES_PREFIX = "comment:replies:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean likeComment(long commentId, long userId) {
        String likeKey = LIKE_PREFIX + commentId;
        String userLikedKey = USER_LIKED_PREFIX + userId;
        String likeCountKey = COMMENT_LIKE_COUNT_PREFIX + commentId;

        // 添加到点赞集合
        Long added = redisTemplate.opsForSet().add(likeKey, userId);
        if (added != null && added > 0) {
            // 用户点赞记录
            redisTemplate.opsForSet().add(userLikedKey, commentId);
            // 增加点赞数
            redisTemplate.opsForValue().increment(likeCountKey);
            // 设置过期时间（可选）
            redisTemplate.expire(likeKey, 30, TimeUnit.DAYS);
            redisTemplate.expire(userLikedKey, 30, TimeUnit.DAYS);
            redisTemplate.expire(likeCountKey, 30, TimeUnit.DAYS);
            return true;
        }
        return false;
    }

    @Override
    public boolean unlikeComment(long commentId, long userId) {
        String likeKey = LIKE_PREFIX + commentId;
        String userLikedKey = USER_LIKED_PREFIX + userId;
        String likeCountKey = COMMENT_LIKE_COUNT_PREFIX + commentId;

        // 从点赞集合中移除
        boolean removed = redisTemplate.opsForSet().remove(likeKey, userId) > 0;
        if (removed) {
            // 从用户点赞记录中移除
            redisTemplate.opsForSet().remove(userLikedKey, commentId);
            // 减少点赞数
            redisTemplate.opsForValue().decrement(likeCountKey);
            return true;
        }
        return false;
    }

    @Override
    public long getCommentLikeCount(long commentId) {
        String key = COMMENT_LIKE_COUNT_PREFIX + commentId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count.toString()) : 0;
    }

    @Override
    public boolean hasLikedComment(long commentId, long userId) {
        String key = LIKE_PREFIX + commentId;
        return redisTemplate.opsForSet().isMember(key, userId);
    }

    @Override
    public List<Map<String, Object>> getArticleComments(long articleId, int page, int size) {
        String key = ARTICLE_COMMENTS_PREFIX + articleId;
        int start = (page - 1) * size;
        int end = start + size - 1;

        // 从Redis获取评论ID列表
        List<Object> commentIds = redisTemplate.opsForList().range(key, start, end);
        List<Map<String, Object>> comments = new ArrayList<>();

        if (commentIds != null && !commentIds.isEmpty()) {
            for (Object commentIdObj : commentIds) {
                long commentId = Long.parseLong(commentIdObj.toString());
                Map<String, Object> comment = new HashMap<>();
                comment.put("id", commentId);
                comment.put("likeCount", getCommentLikeCount(commentId));
                comment.put("viewCount", getCommentViewCount(commentId));
                comments.add(comment);
            }
        }

        return comments;
    }

    @Override
    public List<Map<String, Object>> getCommentReplies(long commentId, int page, int size) {
        String key = COMMENT_REPLIES_PREFIX + commentId;
        int start = (page - 1) * size;
        int end = start + size - 1;

        // 从Redis获取回复ID列表
        List<Object> replyIds = redisTemplate.opsForList().range(key, start, end);
        List<Map<String, Object>> replies = new ArrayList<>();

        if (replyIds != null && !replyIds.isEmpty()) {
            for (Object replyIdObj : replyIds) {
                long replyId = Long.parseLong(replyIdObj.toString());
                Map<String, Object> reply = new HashMap<>();
                reply.put("id", replyId);
                reply.put("likeCount", getCommentLikeCount(replyId));
                replies.add(reply);
            }
        }

        return replies;
    }

    @Override
    public List<Map<String, Object>> getHotComments(long articleId, int limit) {
        String key = HOT_COMMENTS_PREFIX + articleId;

        // 从Redis获取热门评论
        Set<Object> commentIds = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        List<Map<String, Object>> hotComments = new ArrayList<>();

        if (commentIds != null && !commentIds.isEmpty()) {
            for (Object commentIdObj : commentIds) {
                long commentId = Long.parseLong(commentIdObj.toString());
                Map<String, Object> comment = new HashMap<>();
                comment.put("id", commentId);
                comment.put("likeCount", getCommentLikeCount(commentId));
                hotComments.add(comment);
            }
        }

        return hotComments;
    }

    @Override
    public long incrementCommentViewCount(long commentId) {
        String key = COMMENT_VIEW_COUNT_PREFIX + commentId;
        Long count = redisTemplate.opsForValue().increment(key);
        // 设置过期时间
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        return count != null ? count : 0;
    }

    @Override
    public long getCommentViewCount(long commentId) {
        String key = COMMENT_VIEW_COUNT_PREFIX + commentId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count.toString()) : 0;
    }

    /**
     * 添加评论到文章评论列表
     * @param articleId 文章ID
     * @param commentId 评论ID
     */
    public void addCommentToArticle(long articleId, long commentId) {
        String key = ARTICLE_COMMENTS_PREFIX + articleId;
        redisTemplate.opsForList().leftPush(key, commentId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    /**
     * 添加回复到评论回复列表
     * @param commentId 评论ID
     * @param replyId 回复ID
     */
    public void addReplyToComment(long commentId, long replyId) {
        String key = COMMENT_REPLIES_PREFIX + commentId;
        redisTemplate.opsForList().leftPush(key, replyId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    /**
     * 更新热门评论
     * @param articleId 文章ID
     * @param commentId 评论ID
     * @param score 分数（点赞数 + 浏览数）
     */
    public void updateHotComment(long articleId, long commentId, double score) {
        String key = HOT_COMMENTS_PREFIX + articleId;
        redisTemplate.opsForZSet().add(key, commentId, score);
        // 只保留前100条热门评论
        redisTemplate.opsForZSet().removeRange(key, 100, -1);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
}
