package com.linsir.service;

import java.util.List;
import java.util.Map;

public interface CommentLikeService {

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否点赞成功
     */
    boolean likeComment(long commentId, long userId);

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否取消点赞成功
     */
    boolean unlikeComment(long commentId, long userId);

    /**
     * 获取评论的点赞数
     * @param commentId 评论ID
     * @return 点赞数
     */
    long getCommentLikeCount(long commentId);

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasLikedComment(long commentId, long userId);

    /**
     * 获取文章的评论列表
     * @param articleId 文章ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    List<Map<String, Object>> getArticleComments(long articleId, int page, int size);

    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @param page 页码
     * @param size 每页大小
     * @return 回复列表
     */
    List<Map<String, Object>> getCommentReplies(long commentId, int page, int size);

    /**
     * 缓存热门评论
     * @param articleId 文章ID
     * @param limit 限制数量
     * @return 热门评论列表
     */
    List<Map<String, Object>> getHotComments(long articleId, int limit);

    /**
     * 增加评论的浏览量
     * @param commentId 评论ID
     * @return 最新浏览量
     */
    long incrementCommentViewCount(long commentId);

    /**
     * 获取评论的浏览量
     * @param commentId 评论ID
     * @return 浏览量
     */
    long getCommentViewCount(long commentId);
}
