package com.zugangliu.finalproject.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.zugangliu.finalproject.bean.Community;
import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.service.CommunityService;
import com.zugangliu.finalproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    Producer kaptchaProducer;

    @Autowired
    CommunityService communityService;

    /**
     * 用户登录
     * @param user
     * @param session
     * @param model
     * @return
     */
    @PostMapping("/login")
    public String login(User user,
                        @RequestParam(name = "verifyCodeInput") String verifyCodeInput,
                        HttpSession session, Model model)
    {
        String userName = user.getUserName();
        String password = user.getPassword();
        String sessionKey = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if(!sessionKey.equals(verifyCodeInput)){
            model.addAttribute("msg", "verify code is not correct!");
            return "login_signup";
        }

        if(StringUtils.hasLength(userName) && StringUtils.hasLength(password)){
            User ret = userService.getUserByNameAndPw(userName, password);
            if(ret != null){
                session.setAttribute("user", ret);
                model.addAttribute("user", user);
                return "redirect:/index";
            }else{
                model.addAttribute("msg","User Name or Password is wrong!");
                return "login_signup";
            }
        }
        model.addAttribute("msg", "User Name or Password is wrong!");
        return "login_signup";
    }

    /**
     * 用户注册
     * @param params
     * @param model
     * @param avatar
     * @return
     */
    @PostMapping("/signup")
    @ResponseBody
    public String signup(@RequestParam Map<String, Object> params, Model model, @RequestParam(value = "avatarImg", required = false)MultipartFile avatar) throws IOException {
        User user = new User();
        user.setUserName((String) params.get("userName"));
        user.setEmail((String) params.get("email"));
        user.setPassword((String) params.get("password"));

        //检测username是否已被注册
        String username = user.getUserName();
        if(userService.isUserNameRegistered(username)){
            // 如果username已经被注册，则无法完成注册，返回login_signup界面。给出提示信息。
            model.addAttribute("msg", "This username has been used by others!");
            return "500;-1";
        }

        //保存avatar至jar包同级目录下的/upload/user_avatar
        if(!avatar.isEmpty()){
            String originalFilename = avatar.getOriginalFilename();
            // get the directory of .jar
            ApplicationHome home = new ApplicationHome(getClass());
            File jarF = home.getSource();
            // create a fold '/upload/user_avatar/' to save uploaded avatar
            String dirPath = jarF.getParentFile().toString() + "/upload/user_avatar/";
            System.out.println("user avatar saved to: " + dirPath);

            File filePath = new File(dirPath);
            if(!filePath.exists()){
                filePath.mkdirs();
            }

            /* do not save images or other files to project directory
            String staticPath = ClassUtils.getDefaultClassLoader().getResource("static/img/upload").getPath();
            String imgPath = "user_avatar" + File.separator + originalFilename;
            String savePath = staticPath + File.separator + imgPath;

            log.info("user avatar saved to " + savePath);

            File saveFile = new File(savePath);
            if(!saveFile.exists()){
                saveFile.mkdirs();
            }*/

            avatar.transferTo(new File(dirPath + originalFilename));
            user.setAvatar(originalFilename);
        }

        if(userService.save(user)){
            //保存
            log.info(user.toString());
            log.info("signup successfully!");
            model.addAttribute("msg", "注册成功！");

            return "200;";
        }
        model.addAttribute("msg", "注册不成功！");
        return "500;-2";
    }

    /**
     * 生成验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/kaptcha")
    public void getKaptchaImg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //生成验证码
        String capText = kaptchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        //向客户端写出
        BufferedImage bi = kaptchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    /**
     * 展示用户加入的communities
     * @return
     */
    @GetMapping("/myComm")
    public String showMyComm(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        List<Community> communityListByUserId = communityService.getCommunityListByUserId(userId);
        model.addAttribute("listOfMyComm", communityListByUserId);
        return "my_comm";
    }
}
