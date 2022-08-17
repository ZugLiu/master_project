package com.zugangliu.finalproject.service;

import com.github.pagehelper.PageInfo;
import com.zugangliu.finalproject.bean.Topic;

import java.util.List;

public interface TopicService{

    public List<Topic> getTopicList();
    public PageInfo<Topic> getTopicListByPN(int pageNum);
    public PageInfo<Topic> getTopicByCommId(Integer commId, Integer pageNum);

    public int insert(Topic topic);

    public Topic getTopicById(int id);
}
