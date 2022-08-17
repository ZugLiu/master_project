package com.zugangliu.finalproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zugangliu.finalproject.bean.User;

public interface UserService extends IService<User> {
    public User getUserByNameAndPw(String userName, String password);
    public boolean isUserNameRegistered(String userName);
}
