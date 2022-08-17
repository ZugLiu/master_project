package com.zugangliu.finalproject.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zugangliu.finalproject.bean.Option;
import com.zugangliu.finalproject.bean.Topic;
import com.zugangliu.finalproject.mapper.OptionMapper;
import com.zugangliu.finalproject.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService{

    @Autowired
    TopicMapper topicMapper;
    @Autowired
    OptionMapper optionMapper;

    private static final int pageSize = 8; // 8 topics in every page
    private static final int navigatePages = 5;

    @Override
    public List<Topic> getTopicList() {
        return topicMapper.getAllTopics();
    }

    @Override
    public PageInfo<Topic> getTopicListByPN(int pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Topic> allTopics = topicMapper.getAllTopics();
        PageInfo<Topic> topicPageInfo = new PageInfo<>(allTopics, navigatePages);
        return topicPageInfo;
    }

    @Override
    public PageInfo<Topic> getTopicByCommId(Integer commId, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Topic> topics = topicMapper.getTopicsByCommId(commId);
        PageInfo<Topic> topicPageInfo = new PageInfo<>(topics, navigatePages);
        return topicPageInfo;
    }

    @Override
    public int insert(Topic topic) {
        return topicMapper.insert(topic);
    }

    @Override
    public Topic getTopicById(int id) {
        Topic topic = topicMapper.getTopicById(id);
        List<Option> optionsByTopicId = optionMapper.getOptionsByTopicId(id);
        topic.setOptions(optionsByTopicId);
        return topic;
    }
}
