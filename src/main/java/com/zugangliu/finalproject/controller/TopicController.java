package com.zugangliu.finalproject.controller;

import com.github.pagehelper.PageInfo;
import com.zugangliu.finalproject.bean.*;
import com.zugangliu.finalproject.service.CommentService;
import com.zugangliu.finalproject.service.OptionService;
import com.zugangliu.finalproject.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class TopicController {

    @Autowired
    TopicService topicService;

    @Autowired
    OptionService optionService;

    @Autowired
    CommentService commentService;

    @GetMapping("/allTopics")
    @ResponseBody
    public List<Topic> getAllTopics(){
        return topicService.getTopicList();
    }

    @GetMapping(value = {"/topics/{pn}", "/topics"})
    @ResponseBody
    public PageInfo<Topic> getTopicsByPN(@PathVariable(value = "pn", required = false) Integer pageNum){
        if( pageNum == null||pageNum == 0 ){
            pageNum = 1;
        }
        PageInfo<Topic> topicList = topicService.getTopicListByPN(pageNum);
        return topicList;
    }


    @GetMapping("/getServerTime")
    @ResponseBody
    public String getServerTime(){
        return "this is server time";
    }

    /**
     * 获取当前comm的某一页topics
     * @param commId
     * @param session
     * @return
     */
    @GetMapping("/topics/{commId}/{pn}")
    @ResponseBody
    public PageInfo<Topic> getTopicsByCommId(@PathVariable("commId") Integer commId, @PathVariable("pn")Integer pageNum, HttpSession session){
        PageInfo<Topic> topicPageInfo = topicService.getTopicByCommId(commId, pageNum);
        return topicPageInfo;
    }

    @PostMapping("/topics")
    public String createTopic(@RequestParam Map<String, Object> params, HttpSession session) throws ParseException {
        Topic topic = new Topic();
        //log.info(params.toString());

        //get commId, title, content
        String commId = (String)params.get("commId");
        String content = (String)params.get("content");
        String title = (String) params.get("title");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // get vote start date
        /*String voteStartsIn = (String) params.get("voteStartsIn");
        Map<String, Integer> voteStartsInTime = getTime(params, "voteStartsIn", voteStartsIn);
        Calendar voteStartDate = Calendar.getInstance();
        voteStartDate.add(Calendar.DAY_OF_MONTH, voteStartsInTime.get("d"));
        voteStartDate.add(Calendar.HOUR, voteStartsInTime.get("h"));
        voteStartDate.add(Calendar.MINUTE, voteStartsInTime.get("m"));
        voteStartDate.add(Calendar.SECOND, voteStartsInTime.get("s"));
        topic.setVoteStartTime(new Timestamp(voteStartDate.getTimeInMillis()));*/
        String startDate = (String) params.get("startDate");
        Date start = simpleDateFormat.parse(startDate);
        long s = start.getTime();
        topic.setVoteStartTime(new Timestamp(s));


        // get vote ends date
        /*String votingDuration = (String) params.get("votingDuration");
        Map<String, Integer> votingDurationTime = getTime(params, "votingDuration", votingDuration);
        voteStartDate.add(Calendar.DAY_OF_MONTH, votingDurationTime.get("d"));
        voteStartDate.add(Calendar.HOUR, votingDurationTime.get("h"));
        voteStartDate.add(Calendar.MINUTE, votingDurationTime.get("m"));
        voteStartDate.add(Calendar.SECOND, votingDurationTime.get("s"));
        topic.setVoteEndTime(new Timestamp(voteStartDate.getTimeInMillis()));*/
        String endDate = (String) params.get("endDate");
        Date end = simpleDateFormat.parse(endDate);
        long e = end.getTime();
        topic.setVoteEndTime(new Timestamp(e));

        // get author
        User user = (User)session.getAttribute("user");

        topic.setTitle(title);
        topic.setContent(content);
        topic.setAuthor(user);
        topic.setCommunity(new Community(Integer.parseInt(commId)));

        topicService.insert(topic);
        log.info(String.valueOf(topic.getId()));

        // get options and its title
        Map<String, List<String>> optionMap = new HashMap<>();
        optionMap.put("optionTitle", new ArrayList<>());
        optionMap.put("optionContent", new ArrayList<>());
        for(String key:params.keySet()){
            // first, split type and number
            int No = key.charAt(key.length()-1) - '0'; // get the last char, which is the number of option
            String type = key.substring(0, key.length()-1);

            if(type.equals("option")){
                String optionContent = (String)params.get(key);
                optionMap.get("optionContent").add(optionContent);
            }
            else if(type.equals("optionTitle")){
                String optionTitle = (String) params.get(key);
                optionMap.get("optionTitle").add(optionTitle);
            }
        }

        for(int i = 0; i< optionMap.get("optionTitle").size(); i++){
            String optionTitle = optionMap.get("optionTitle").get(i);
            String optionContent = optionMap.get("optionContent").get(i);
            Option option = new Option();
            option.setTitle(optionTitle);
            option.setContent(optionContent);
            option.setCreator(user);
            option.setBelongTo(topic.getId());
            optionService.insert(option);
        }

        //log.info(optionMap.toString());
        return "redirect:/community/"+commId;
    }

    private Map<String, Integer> getTime(Map<String, Object> params, String phase, String timeSpan) {
        Map<String, Integer> ret = new HashMap<>();

        //根据不同的阶段来确定日，时，分，秒的键
        String dayKey = null;
        String hourKey = null;
        String minKey= null;
        String secKey= null;
        if(phase.equals("voteStartsIn")){
            dayKey = "startInDays";
            hourKey = "startInHours";
            minKey = "startInMinutes";
            secKey = "startInSeconds";
        }else if(phase.equals("votingDuration")){
            dayKey = "durationDays";
            hourKey = "durationHours";
            minKey = "durationMinutes";
            secKey="durationSeconds";
        }

        if(timeSpan.equals("on")){
            // get input strings
            String stringDays = (String) params.get(dayKey);
            String stringHours = (String) params.get(hourKey);
            String stringMinutes = (String) params.get(minKey);
            String stringSeconds = (String) params.get(secKey);

            //convert all "" to 0
            if(stringDays.equals("")){
                stringDays="0";
            }
            if(stringHours.equals("")){
                stringHours="0";
            }
            if(stringMinutes.equals("")){
                stringMinutes="0";
            }
            if(stringSeconds.equals("")){
                stringSeconds="0";
            }
            // convert string to Integer
            Integer d = Integer.parseInt(stringDays);
            Integer h  = Integer.parseInt(stringHours);
            Integer m = Integer.parseInt(stringMinutes);
            Integer s = Integer.parseInt(stringSeconds);

            ret.put("d", d);
            ret.put("h", h);
            ret.put("m", m);
            ret.put("s", s);

        }
        // the user chose a pre-set preparation time
        else{
            String[] s = timeSpan.split(" ");
            Integer num = Integer.parseInt(s[0]);
            String unit = s[1];
            if(unit.equals("days")){
                ret.put("d", num);
                ret.put("h", 0);
                ret.put("m", 0);
                ret.put("s", 0);
            }else if(unit.equals("hours")){
                ret.put("d", 0);
                ret.put("h", num);
                ret.put("m", 0);
                ret.put("s", 0);
            }
        }

        return ret;
    }

    /**
     * 获取topic的所有内容：topic + 评论
     * @param id
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/topicDetails/{id}")
    public String getTopicDetails(@PathVariable("id") int id, Model model, HttpSession session){
        Topic topicById = topicService.getTopicById(id);
        model.addAttribute("topicInDetail", topicById);
        session.setAttribute("topicInDetail", topicById);

        // get all comment threads to this topic
        List<Comment> commentThread = commentService.getCommentListByTopicId(topicById.getId());
        session.setAttribute("commentThread", commentThread);

        return "topic";
    }

    @PostMapping("/search")
    public String search(@RequestParam Map<String, Object> params, Model model){
        String searchWord = (String)params.get("searchWord");
        model.addAttribute("searchWord", searchWord);
        return "search_result";
    }

}
