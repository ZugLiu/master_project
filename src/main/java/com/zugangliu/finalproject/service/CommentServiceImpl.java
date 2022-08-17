package com.zugangliu.finalproject.service;

import com.zugangliu.finalproject.bean.Comment;
import com.zugangliu.finalproject.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    CommentMapper commentMapper;

    @Override
    public void insertComment(Comment comment) {
        commentMapper.insertComment(comment);
    }

    @Override
    public List<Comment> getCommentListByTopicId(int topicId) {
        List<Comment> commentsByTopicId = commentMapper.getCommentsByTopicId(topicId);
        for (Comment comment : commentsByTopicId) {
            getReplies(comment);
        }

        return commentsByTopicId;
    }

    /**
     * 递归调用此方法，获取每个comment的reply，以及reply的reply，以及reply的reply的reply……
     * @param comment
     */
    private void getReplies(Comment comment){
        comment.setThread(commentMapper.getReplyToComment(comment.getId()));

        List<Comment> thread = comment.getThread();
        if(thread.size() == 0){
            return;
        }

        for(Comment r: thread){
            getReplies(r);
        }
    }
}
