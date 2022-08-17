package com.zugangliu.finalproject.mapper;

import com.zugangliu.finalproject.bean.Community;
import com.zugangliu.finalproject.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommunityMapper{
    void createCommunity(@Param("comm") Community community);

    /**
     * get top 10 largest comm
     * @return
     */
    public List<Community> getCommunityList();
    List<Community> getAllComms();
    public List<Community> getCommunityListByUserId(@Param("userId") Integer userId);
    public Community getCommunityById(@Param("commId") Integer commId);
    int joinComm(@Param("user") User user, @Param("commId") Integer commId);
    int leaveComm(@Param("user")User user, @Param("commId") Integer commId);
    void updateCommPopulation(@Param("p") int variation, @Param("commId") int commId);
    Integer getUserComm(@Param("user") User user, @Param("commId") Integer commId);
}
