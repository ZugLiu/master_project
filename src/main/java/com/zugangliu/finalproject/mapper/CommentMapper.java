package com.zugangliu.finalproject.mapper;

import com.zugangliu.finalproject.bean.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    void insertComment(@Param("comment") Comment comment);
    void insertReply(@Param("comment") Comment comment);
    List<Comment> getCommentsByTopicId(@Param("topicId") int topicId);
    List<Comment> getReplyToComment(@Param("commentId") int cId);
}
