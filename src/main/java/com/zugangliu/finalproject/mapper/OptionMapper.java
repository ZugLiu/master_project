package com.zugangliu.finalproject.mapper;

import com.zugangliu.finalproject.bean.Option;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OptionMapper {
    public int insert(Option option);
    public List<Option> getOptionsByTopicId(int topicId);

    public int updateVoteCount(@Param("optionId") int optionId, @Param("change") int voteCountChange);
}
