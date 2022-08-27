package com.zugangliu.finalproject.service;

import com.zugangliu.finalproject.bean.Option;
import com.zugangliu.finalproject.bean.Vote;
import com.zugangliu.finalproject.mapper.OptionMapper;
import com.zugangliu.finalproject.mapper.VoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionServiceImpl implements OptionService{
    @Autowired
    OptionMapper optionMapper;
    @Autowired
    VoteMapper voteMapper;

    @Override
    public int insert(Option option) {
        return optionMapper.insert(option);
    }

    @Override
    public List<Option> getOptionByTopicId(int topicId) {
        return optionMapper.getOptionsByTopicId(topicId);
    }

    /**
     * return 0: update failed
     * return 1: update success, voteCount++
     * return -1: update success, voteCount--
     *
     * @param voterId
     * @param optionId
     * @param voteCountChange
     * @return
     */
    @Override
    public String updateOptionVoteCount(int voterId, int optionId, int voteCountChange, int optionBelongTo) {
        Vote vote = voteMapper.getVote(voterId, optionId);

        if(voteCountChange==1){
            if(vote == null){
                voteMapper.insertVote(voterId, optionId, optionBelongTo);
                optionMapper.updateVoteCount(optionId, 1);
                return "Vote successfully!";
            }else if(vote!=null){
                // the user has already voted to the option, vote failed
                return "You have already voted to this option. Vote rejected!";
            }
        }else if(voteCountChange==-1){
            if(vote== null){
                return "You haven't voted to this option before. Cancel rejected!";
            }else if(vote!= null){
                voteMapper.deleteVote(voterId, optionId);
                optionMapper.updateVoteCount(optionId, -1);
                return "Vote canceled successfully!";
            }
        }
        return "unknown error!";
    }

    /**
     * get all the votes to the option
     * @param optionId
     * @return
     */
    @Override
    public List<Vote> getOptionsVote(int optionId) {
        return voteMapper.getVoteByOptionId(optionId);
    }
}
