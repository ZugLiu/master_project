package com.zugangliu.finalproject.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    private int id;
    private String title;
    private String content;
    private User author;
    private Timestamp publishTime;
    private Timestamp voteStartTime;
    private Timestamp voteEndTime;
    //private int belongTo; no need since there is attr community


    private Community community; //store info of comm it belongs to in order to display correspond comm info to front end
    private int numOfComments;
    private List<Option> Options;
}
