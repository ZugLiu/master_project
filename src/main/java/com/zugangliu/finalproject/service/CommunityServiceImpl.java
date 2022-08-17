package com.zugangliu.finalproject.service;

import com.zugangliu.finalproject.bean.Community;
import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.mapper.CommunityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    CommunityMapper communityMapper;
    private static final int pageSize = 8;

    @Override
    public void createComm(Community community) {
        communityMapper.createCommunity(community);
    }

    @Override
    public List<Community> getCommunityList() {
        return communityMapper.getCommunityList();
    }

    @Override
    public List<Community> getAllComms() {
        List<Community> allComms = communityMapper.getAllComms();
        return allComms;
    }

    @Override
    public List<Community> getCommunityListByUserId(Integer userId) {
        return communityMapper.getCommunityListByUserId(userId);
    }

    @Override
    public Community getCommunityById(Integer commId) {
        return communityMapper.getCommunityById(commId);
    }

    @Override
    public int joinComm(User user, Integer commId) {
        if(communityMapper.getUserComm(user, commId) == null){
            communityMapper.joinComm(user, commId);
            communityMapper.updateCommPopulation(1, commId);
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public int leaveComm(User user, Integer commId) {
        if(communityMapper.getUserComm(user, commId)== null){
            return 0;
        }else{
            communityMapper.leaveComm(user, commId);
            communityMapper.updateCommPopulation(-1, commId);
            return 1;
        }
    }
}
