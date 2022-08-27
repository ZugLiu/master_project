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

	/**
	 * One's username cannot be the same as other user's
	 */
	@Test
	void testUserNameRepetitive() {
		String input = "zug888";
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("user_name", input);
		User one = userService.getOne(wrapper);
		System.out.println(one);
	}

	/**
	 * Test to get all topics from database.
	 * This is a joined-table query.
	 */
	@Test
	void testGetAllTopics(){
		List<Topic> allTopics = topicMapper.getAllTopics();
		allTopics.forEach(System.out::println);
	}

	/**
	 * Test whether the number of comments of a topic can be retrieved from database correctly
	 */
	@Test
	void testGetNumOfCommentsOfTopic(){
		int commentNumOfTopic = topicMapper.getCommentNumOfTopic(3);
		log.info(String.valueOf(commentNumOfTopic));
	}

	/**
	 * Test whether the pagination can work as intended,
	 * and see what information is included in PageInfo
	 */
	@Test
	void testPagination(){
		PageHelper.startPage(1, 2);
		List<Topic> allTopics = topicMapper.getAllTopics();
		PageInfo<Topic> topicPageInfo = new PageInfo<>(allTopics, 2);
		log.info(topicPageInfo.toString());
	}

	/**
	 * Know what the project root path is,
	 * in order to save user uploaded files
	 * @throws FileNotFoundException
	 */
	@Test
	void testGetProjectRootPath() throws FileNotFoundException {
		String path = ClassUtils.getDefaultClassLoader().getResource("static/img/upload/comm_header_img").getPath();
		log.info("comm header img path: "+path);
		String path1 = ResourceUtils.getURL("classpath:").getPath();
		log.info(path1);
		String property = System.getProperty("user.dir");
		log.info(property);

	}

	/**
	 * This is a joined-table query
	 */
	@Test
	void testGetCommListByUserId(){
		List<Community> communityListByUserId = communityMapper.getCommunityListByUserId(14);
		log.info(communityListByUserId.toString());
	}

	/**
	 * Test insert a comment
	 */
	@Test
	void testInsertComment(){
		Comment comment = new Comment();
		comment.setCreator("1");
		comment.setContent("good");
		comment.setToTopic(false);
		comment.setToCommentId(1);
		commentMapper.insertComment(comment);
	}

	/**
	 * This is a joined-table query.
	 */
	@Test
	void testGetCommentThreadByTopicId(){
		List<Comment> commentListByTopicId = commentService.getCommentListByTopicId(1);
		log.info(commentListByTopicId.toString());
	}
}
