package com.zugangliu.finalproject.controller;

import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Controller
public class ImgController {

    @Autowired
    UserMapper userMapper;

    @GetMapping(value = "/img/{imgName}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] getCommImg(@PathVariable String imgName) throws IOException {

        //注意！！！
        //注意！！！
        //注意！！！
        // filePath中一定不能有空格，不然的话会报“系统找不到指定路径”！！！
        String filePath = ClassUtils.getDefaultClassLoader().getResource("static/img/upload/comm_header_img").getPath()+File.separator + imgName;

        // 使用输入流
        FileInputStream fileInputStream = new FileInputStream(filePath);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes, 0, fileInputStream.available());
        fileInputStream.close();
        return bytes;
    }

    @GetMapping(value = "/avatar/{userId}",
    produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] getUserAvatar(@PathVariable("userId") String id) throws IOException {
        User user = userMapper.selectById(id);
        String avatar = user.getAvatar();
        String filePath = ClassUtils.getDefaultClassLoader().getResource("static/").getPath() + File.separator + avatar;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes, 0, fileInputStream.available());
        fileInputStream.close();
        return bytes;
    }
}
