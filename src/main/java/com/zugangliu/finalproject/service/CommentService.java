package com.zugangliu.finalproject.service;

import com.zugangliu.finalproject.bean.Comment;

import java.util.List;

public interface CommentService {
    public void insertComment(Comment comment);
    List<Comment> getCommentListByTopicId(int topicId);
}
