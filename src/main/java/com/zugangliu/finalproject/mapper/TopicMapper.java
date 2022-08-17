package com.zugangliu.finalproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zugangliu.finalproject.bean.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * This mapper totally uses mybatis
 */
public interface TopicMapper{
    public List<Topic> getAllTopics();
    public int getCommentNumOfTopic(int topicId);
    public List<Topic> getTopicsByCommId(@Param("commId") Integer commId);

    public int insert(Topic topic);

    public Topic getTopicById(int id);
}
