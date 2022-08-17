package com.zugangliu.finalproject.service;

import com.zugangliu.finalproject.bean.Option;
import com.zugangliu.finalproject.bean.Vote;

import java.util.List;

public interface OptionService {
    public int insert(Option option);

    public List<Option> getOptionByTopicId(int topicId);

    public String updateOptionVoteCount(int voterId, int optionId, int voteCountChange, int optionBelongTo);

    public List<Vote> getOptionsVote(int optionId);
}
