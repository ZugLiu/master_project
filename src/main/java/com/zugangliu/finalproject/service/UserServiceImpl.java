package com.zugangliu.finalproject.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Override
    public User getUserByNameAndPw(String userName, String password){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_name", userName);
        map.put("password", password);
        wrapper.allEq(map);
        User ret = this.getOne(wrapper, true);
        return ret;
    }

    @Override
    public boolean isUserNameRegistered(String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        User user = this.getOne(wrapper);
        if(user != null){
            return true;
        }
        return false;
    }
}
