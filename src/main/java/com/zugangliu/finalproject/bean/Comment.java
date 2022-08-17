package com.zugangliu.finalproject.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int id;
    private String content;
    private Timestamp publishTime;
    private String creator;
    private String creatorName;
    private Boolean toTopic;
    private int toTopicId;
    private int toCommentId;
    private String toCommentCreatorName;

    private List<Comment> thread;
}
