package com.zugangliu.finalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping({"/"})
    public String test(){
        return "login_signup";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    /*@RequestMapping("/c")
    public String community(){
        return "community";
    }*/

    @RequestMapping("/t")
    public String topic(){
        return "topic";
    }

    @RequestMapping("/about")
    public String about(){ return "about"; }

}
