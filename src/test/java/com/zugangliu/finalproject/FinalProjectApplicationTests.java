package com.zugangliu.finalproject;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zugangliu.finalproject.bean.Comment;
import com.zugangliu.finalproject.bean.Community;
import com.zugangliu.finalproject.bean.Topic;
import com.zugangliu.finalproject.bean.User;
import com.zugangliu.finalproject.mapper.CommentMapper;
import com.zugangliu.finalproject.mapper.CommunityMapper;
import com.zugangliu.finalproject.mapper.TopicMapper;
import com.zugangliu.finalproject.mapper.VoteMapper;
import com.zugangliu.finalproject.service.CommentService;
import com.zugangliu.finalproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.List;

@Slf4j
@SpringBootTest
class FinalProjectApplicationTests {
	@Autowired
	UserService userService;

	@Autowired
	CommentService commentService;

	@Autowired
	TopicMapper topicMapper;

	@Autowired
	CommunityMapper communityMapper;

	@Autowired
	VoteMapper voteMapper;

	@Autowired
	CommentMapper commentMapper;

	@Test
	void contextLoads() {
		String input = "zug888";
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("user_name", input);
		User one = userService.getOne(wrapper);
		System.out.println(one);
	}

	@Test
	void testGetAllTopics(){
		List<Topic> allTopics = topicMapper.getAllTopics();
		allTopics.forEach(System.out::println);
	}

	@Test
	void testGetNumOfCommentsOfTopic(){
		int commentNumOfTopic = topicMapper.getCommentNumOfTopic(3);
		log.info(String.valueOf(commentNumOfTopic));
	}

	@Test
	void testPagination(){
		PageHelper.startPage(1, 2);
		List<Topic> allTopics = topicMapper.getAllTopics();
		PageInfo<Topic> topicPageInfo = new PageInfo<>(allTopics, 2);
		log.info(topicPageInfo.toString());
	}

	@Test
	void testGetProjectRootPath() throws FileNotFoundException {
		String path = ClassUtils.getDefaultClassLoader().getResource("static/img/upload/comm_header_img").getPath();
		log.info("comm header img path: "+path);
		String path1 = ResourceUtils.getURL("classpath:").getPath();
		log.info(path1);
		String property = System.getProperty("user.dir");
		log.info(property);

	}

	@Test
	void testGetCommListByUserId(){
		List<Community> communityListByUserId = communityMapper.getCommunityListByUserId(14);
		log.info(communityListByUserId.toString());
	}

//	@Test
//	void testVoteCRUD(){
//		Integer get = voteMapper.getVote(1, 3);
//		log.info(String.valueOf(get));
//
//		Integer insert = voteMapper.insertVote(2, 5);
//		log.info(String.valueOf(insert));
//
//		Integer delete = voteMapper.deleteVote(3, 6);
//		log.info(String.valueOf(delete));
//	}
	@Test
	void testInsertComment(){
		Comment comment = new Comment();
		comment.setCreator("1");
		comment.setContent("good");
		comment.setToTopic(false);
		comment.setToCommentId(1);
		commentMapper.insertComment(comment);
	}

	@Test
	void testGetCommentThread(){
		List<Comment> commentListByTopicId = commentService.getCommentListByTopicId(1);
		log.info(commentListByTopicId.toString());
	}
}
