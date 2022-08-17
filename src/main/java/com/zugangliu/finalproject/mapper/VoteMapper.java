package com.zugangliu.finalproject.mapper;

import com.zugangliu.finalproject.bean.Vote;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoteMapper {
    int insertVote(@Param("userId") int userId, @Param("optionId") int optionId, @Param("optionBelongTo") int optionBelongTo);

     Integer getVote(@Param("userId") int userId, @Param("optionId") int optionId);

     Integer deleteVote(@Param("userId") int userId, @Param("optionId") int optionId);

     List<Vote> getVoteByOptionBelongTo(@Param("optionBelongTo") int optionBelongTo);

     List<Vote> getVoteByOptionId(@Param("optionId")int optionId);
}
