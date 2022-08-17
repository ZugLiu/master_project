package com.zugangliu.finalproject.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {
    private int id;
    private String title;
    private String content;
    private int votes;
    private User creator;
    private Timestamp publishTime;
    private int belongTo;

}
