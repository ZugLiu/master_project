package com.zugangliu.finalproject.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    private int id;
    private int userId;
    private int optionId;
    private Timestamp voteTime;
    private int optionBelongTo;
}
