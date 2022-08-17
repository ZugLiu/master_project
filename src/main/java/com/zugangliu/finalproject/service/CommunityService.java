package com.zugangliu.finalproject.service;

import com.zugangliu.finalproject.bean.Community;
import com.zugangliu.finalproject.bean.User;

import java.util.List;

public interface CommunityService {
    void createComm(Community community);

    List<Community> getCommunityList();

    List<Community> getAllComms();

    List<Community> getCommunityListByUserId(Integer userId);

    Community getCommunityById(Integer commId);

    int joinComm(User user, Integer commId);

    int leaveComm(User user, Integer commId);
}
