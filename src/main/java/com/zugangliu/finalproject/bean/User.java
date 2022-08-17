package com.zugangliu.finalproject.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User {
    @TableId(type = AUTO)
    private int id;
    private String userName;
    private String password;
    private String email;
    private String avatar;
}
