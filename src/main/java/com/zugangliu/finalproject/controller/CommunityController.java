package com.zugangliu.finalproject.controller;

import com.github.pagehelper.PageInfo;
import com.zugangliu.finalproject.bean.Community;
import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class CommunityController {

    @Autowired
    CommunityService communityService;

    @GetMapping("/community")
    @ResponseBody
    public List<Community> getTop10LargestCommunities(){
        return communityService.getCommunityList();
    }

    /**
     * 此handler返回所有comm的id, c_name, c_about, c_creator
     * @return
     */
    @GetMapping("/allComm")
    @ResponseBody
    public List<Community> getAllComms(){
        return communityService.getAllComms();
    }

    @GetMapping("/createComm")
    public String getCreateCommPage(){
        return "create_comm";
    }

    /** @deprecated
     * 目前这个handler只是处理用户上传的图片，而且只是把图片随便放到了一个位置
     * 22/07/22
     * @param commHeader
     * @return
     * @throws IOException
     */
/*    @PostMapping("/createComm")
    public String createComm(@RequestPart("croppedImg")MultipartFile commHeader) throws IOException {
        log.info(commHeader.getName(), commHeader.getSize());
        if(!commHeader.isEmpty()){
            String originalFilename = commHeader.getOriginalFilename();
            commHeader.transferTo(new File("D:\\"+originalFilename));
        }
        return "create_comm";
    }*/

    @PostMapping("/createComm")
    @ResponseBody
    public String createComm(@RequestParam Map<String, Object> params,
                             @RequestPart("cHeaderImg") MultipartFile cHeaderImg,
                             Model model,
                             HttpSession session
    ) throws IOException {
        log.info(params.toString());

        Community community = new Community();
        community.setCName((String) params.get("cName"));
        community.setCCreator((String) params.get("cCreator"));
        community.setCAbout((String) params.get("cAbout"));
        community.setCRules((String) params.get("cRules"));

        // save comm header image
        if(!cHeaderImg.isEmpty()){
            String originalFilename = cHeaderImg.getOriginalFilename();

            // get directory of .jar
            ApplicationHome h = new ApplicationHome(getClass());
            File jarF = h.getSource();

            // create a fold '/upload/comm_header/' to save uploaded images
            String dirPath = jarF.getParentFile().toString() + "/upload/comm_header/";
            System.out.println("directory of jar: "+jarF.toString());
            System.out.println("directory of uploaded images" + dirPath);

            File file = new File(dirPath);
            if(!file.exists()){
                file.mkdirs();
            }

            // write image to hard disc
            cHeaderImg.transferTo(new File(dirPath + originalFilename));


            /*String staticPath = ClassUtils.getDefaultClassLoader().getResource("static/img/upload").getPath();
            String imgPath = "comm_header_img" + File.separator + originalFilename;
            String savePath = staticPath + File.separator + imgPath;


            File saveFile = new File(savePath);
            if(!saveFile.exists()){
                saveFile.mkdirs();
            }
            cHeaderImg.transferTo(saveFile);
            log.info("comm header img saved to " + savePath);*/

            community.setCHeaderImg(originalFilename);
        }

        communityService.createComm(community);
        log.info(Integer.toString(community.getId()));

        // after create a comm, the creator directly join the comm
        User user = (User) session.getAttribute("user");
        communityService.joinComm(user, community.getId());

        // return a success code to frontend; plus the community id
        return "200;"+community.getId();
    }

    /**
     * 先获得community的信息
     * 再forward给/getTopicByCommunityId
     * @param commId
     * @param model
     * @return
     */
    @GetMapping("/community/{id}")
    public String getCommById(@PathVariable("id")Integer commId, Model model){
        Community community = communityService.getCommunityById(commId);
        model.addAttribute("comm", community);
        return "community";
    }

    @GetMapping("/joinComm/{id}")
    @ResponseBody
    public String joinAComm(HttpSession session, @PathVariable("id") Integer commId){
        User user = (User) session.getAttribute("user");
        int ret = communityService.joinComm(user, commId);
        if(ret==1){
            return "join successfully";
        }else if(ret==0){
            return "join failed";
        }

        return "unknown error";
    }

    @GetMapping("/leaveComm/{id}")
    @ResponseBody
    public String leaveAComm(HttpSession session, @PathVariable("id") Integer commId){
        User user = (User) session.getAttribute("user");
        int ret = communityService.leaveComm(user, commId);
        if(ret == 1){
            return "leave successfully";
        }else if(ret == 0){
            return "leave failed";
        }

        return "unknown error";
    }
}
