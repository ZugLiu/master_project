package com.zugangliu.finalproject.controller;

import com.zugangliu.finalproject.bean.Comment;
import com.zugangliu.finalproject.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class CommentController {
    @Autowired
    CommentService commentService;

    /**
     * 插入对topic的评论
     * 返回topic页面
     * @param comment
     * @return
     */
    @PostMapping("/make_comments")
    public String makeComment(Comment comment){
        commentService.insertComment(comment);
        return "redirect:/topicDetails/"+comment.getToTopicId();
    }

    @GetMapping("/show_comments")
    public String showComments(){

        return null;
    }

}
