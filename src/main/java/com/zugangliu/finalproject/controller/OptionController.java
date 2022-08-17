package com.zugangliu.finalproject.controller;

import com.zugangliu.finalproject.bean.Option;
import com.zugangliu.finalproject.bean.Topic;
import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.mapper.VoteMapper;
import com.zugangliu.finalproject.service.OptionService;
import com.zugangliu.finalproject.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class OptionController {

    @Autowired
    TopicService topicService;

    @Autowired
    OptionService optionService;
    @Autowired
    VoteMapper voteMapper;

    @PutMapping("/option")
    @ResponseBody
    public String vote(@RequestParam Map<String, Object> params, HttpSession session) {
        // check whether current time is in vote time span
        String topicId = (String) params.get("optionBelongTo");
        Topic topicById = topicService.getTopicById(Integer.parseInt(topicId));
        Timestamp voteStartTime = topicById.getVoteStartTime();
        Timestamp voteEndTime = topicById.getVoteEndTime();
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        if (currTime.before(voteStartTime)) {
            // vote does not start yet
            log.info("Vote does not started yet! Cannot vote now!");
            // back to the topic detail page
            return "Vote does not started yet! Cannot vote now!";
        } else if (currTime.after(voteStartTime) && currTime.before(voteEndTime)) {
            // vote is going on
            log.info("vote is going on");
        } else if (currTime.after(voteEndTime)) {
            // vote ended
            log.info("Vote has ended! Cannot vote now!");
            return "Vote has ended! Cannot vote now!";
        }

        // time span is OK, start voting or cancel vote
        log.info(params.toString());
        Integer optionId = Integer.parseInt((String) params.get("optionId"));
        Integer voterId = Integer.parseInt((String) params.get("voterId"));
        Integer voteCountChange = Integer.parseInt((String) params.get("voteCountChange"));
        Integer optionBelongTo = Integer.parseInt((String) params.get("optionBelongTo"));

        String updateMsg = optionService.updateOptionVoteCount(voterId, optionId, voteCountChange, optionBelongTo);

        return updateMsg;
    }


    @GetMapping("/getUserVotedOption")
    @ResponseBody
    public List<Option> getUserVotedOptions(HttpSession session) {
        Topic topicInDetail = (Topic) session.getAttribute("topicInDetail");
        List<Option> options = topicInDetail.getOptions();
        User currUser = (User) session.getAttribute("user");
        List<Option> userVotedOptions = new ArrayList<>();
        for (Option o : options) {
            int oid = o.getId();
            int uid = currUser.getId();
            Integer num = voteMapper.getVote(uid, oid);
            if (num != null) {
                // the user voted for this option
                userVotedOptions.add(o);
            }
        }

        return userVotedOptions;
    }

    /**
     * 用于index和community页面，直接在topic excerpt下方显示用户已投的选项
     * @param session
     * @param options
     * @return
     */
    @PostMapping("/getUserVotedOptionFrontend")
    @ResponseBody
    public List<Option> getUserVotedOptionsFrontend(HttpSession session, @RequestBody List<Option> options){
        User user = (User) session.getAttribute("user");
        List<Option> userVotedOptions = new ArrayList<>();
        for(Option o: options){
            int oid = o.getId();
            int uid = user.getId();
            Integer num = voteMapper.getVote(uid, oid);
            if (num != null) {
                // the user voted for this option
                userVotedOptions.add(o);
            }
        }

        return userVotedOptions;
    }

    @GetMapping("/getOptionsByTopic/{id}")
    @ResponseBody
    public List<Option> getOptionsByTopic(@PathVariable("id")Integer id){
        List<Option> optionByTopicId = optionService.getOptionByTopicId(id);
        return optionByTopicId;
    }

    @PostMapping("/createOptions")
    public String createOptions(HttpSession session, @RequestParam Map<String, Object> params){
        User user = (User) session.getAttribute("user");
        String topicId = (String) params.get("topicId");
        int tId = Integer.parseInt(topicId);
        int size = params.size();
        // 如果想在list的任意位置（位置在list长度内）插入元素，则必须传入一个数组才能生效
        List<Option> optionList = Arrays.asList(new Option[size/2]);
        for (Map.Entry<String, Object> e:
             params.entrySet()) {
            String key = e.getKey();
            // get type of option: optionTitle or optionContent
            String type = key.substring(0, key.length() - 1);
            // get No of option title/content
            int No = key.charAt(key.length()-1) - '0';
            if(type.equals("optionTitle")){
                if(optionList.get(No) == null){
                    Option option = new Option();
                    option.setTitle((String) e.getValue());
                    option.setCreator(user);
                    option.setBelongTo(tId);
                    optionList.set(No, option);
                }else{
                    optionList.get(No).setTitle((String) e.getValue());
                }
            }else if(type.equals("optionContent")){
                if(optionList.get(No) == null){
                    Option option = new Option();
                    option.setContent((String) e.getValue());
                    option.setCreator(user);
                    option.setBelongTo(tId);
                    optionList.set(No, option);
                }else{
                    optionList.get(No).setContent((String) e.getValue());
                }
            }
        }

        log.info(optionList.toString());

        log.info(params.toString());

        for (Option o :
                optionList) {
            optionService.insert(o);
        }
        return "redirect:/topicDetails/"+tId;
    }

}
