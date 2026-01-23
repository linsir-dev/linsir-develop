package com.linsir.controller;

import com.linsir.service.CommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis/comment")
public class CommentLikeController {

    @Autowired
    private CommentLikeService commentLikeService;

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/like")
    public boolean likeComment(
            @RequestParam long commentId,
            @RequestParam long userId) {
        return commentLikeService.likeComment(commentId, userId);
    }

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/unlike")
    public boolean unlikeComment(
            @RequestParam long commentId,
            @RequestParam long userId) {
        return commentLikeService.unlikeComment(commentId, userId);
    }

    /**
     * 获取评论的点赞数
     * @param commentId 评论ID
     * @return 点赞数
     */
    @GetMapping("/like-count")
    public long getCommentLikeCount(@RequestParam long commentId) {
        return commentLikeService.getCommentLikeCount(commentId);
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    @GetMapping("/has-liked")
    public boolean hasLikedComment(
            @RequestParam long commentId,
            @RequestParam long userId) {
        return commentLikeService.hasLikedComment(commentId, userId);
    }

    /**
     * 获取文章的评论列表
     * @param articleId 文章ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    @GetMapping("/article")
    public List<Map<String, Object>> getArticleComments(
            @RequestParam long articleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentLikeService.getArticleComments(articleId, page, size);
    }

    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @param page 页码
     * @param size 每页大小
     * @return 回复列表
     */
    @GetMapping("/replies")
    public List<Map<String, Object>> getCommentReplies(
            @RequestParam long commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentLikeService.getCommentReplies(commentId, page, size);
    }

    /**
     * 获取热门评论
     * @param articleId 文章ID
     * @param limit 限制数量
     * @return 热门评论列表
     */
    @GetMapping("/hot")
    public List<Map<String, Object>> getHotComments(
            @RequestParam long articleId,
            @RequestParam(defaultValue = "5") int limit) {
        return commentLikeService.getHotComments(articleId, limit);
    }

    /**
     * 增加评论的浏览量
     * @param commentId 评论ID
     * @return 最新浏览量
     */
    @PostMapping("/view")
    public long incrementCommentViewCount(@RequestParam long commentId) {
        return commentLikeService.incrementCommentViewCount(commentId);
    }

    /**
     * 获取评论的浏览量
     * @param commentId 评论ID
     * @return 浏览量
     */
    @GetMapping("/view-count")
    public long getCommentViewCount(@RequestParam long commentId) {
        return commentLikeService.getCommentViewCount(commentId);
    }
}
