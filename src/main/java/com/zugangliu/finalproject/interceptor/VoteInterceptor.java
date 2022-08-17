package com.zugangliu.finalproject.interceptor;

import com.zugangliu.finalproject.bean.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

@Slf4j
public class VoteInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Topic topicInDetail = (Topic)session.getAttribute("topicInDetail");
        Timestamp voteStartTime = topicInDetail.getVoteStartTime();
        Timestamp voteEndTime = topicInDetail.getVoteEndTime();
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        if(currTime.before(voteStartTime)){
            // vote does not start yet
            request.setAttribute("msg", "Vote does not started yet! Cannot vote now!");
            log.info("Vote does not started yet! Cannot vote now!");
            // back to the topic detail page
            request.getRequestDispatcher("/topicDetails/"+topicInDetail.getId()).forward(request, response);
            return false;
        }else if(currTime.after(voteStartTime) && currTime.before(voteEndTime)){
            // vote is going on
            request.setAttribute("msg", "");
            log.info("vote is going on");
            return true;
        }else if(currTime.after(voteEndTime)){
            // vote ended
            request.setAttribute("msg", "Vote has ended! Cannot vote now!");
            log.info("Vote has ended! Cannot vote now!");
            request.getRequestDispatcher("/topicDetails/"+topicInDetail.getId()).forward(request, response);
            return false;
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


}
