package service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.dao.UserDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;
import com.musicweb.service.SongService;
import com.musicweb.service.UserService;
import com.musicweb.util.RedisUtil;

/**
 * 
 * @author likexin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml",
"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-redis.xml"})
//@Rollback(false)
//@Transactional(transactionManager = "transactionManager")
public class UserServiceTest {
	
	@Resource
	private UserService userService;
	@Resource
	private AlbumDao albumDao;
	@Resource
	private ArtistDao artistDao;
	@Resource
	private SongDao songDao;
	@Resource
	private UserDao userDao;
	
	@Resource
	private RedisUtil redisUtil;

	/**
	 * 测试注册
	 */
	@Test
	public void registerTest() {
		User user = new User();
		user.setId("test");
		user.setName("name");
		user.setPwd("000000");
		user.setGender(1);
		user.setImage("...");
		user.setSecurityAnswer("hahaha");
		user.setSecurityQuestion(1);
		user.setType(2);
		//System.out.println(((User)redisUtil.hget("user", "test")).getId());
		System.out.println(userService.register(user));
		//System.out.println(redisUtil.hget("user", "test"));
		System.out.println(((User)redisUtil.hget("user", "test")).getId());
	}
	
	/**
	 * 测试用户是否存在
	 */
	@Test 
	public void checkUserExistedTest() {
		System.out.println(userService.checkUserExisted("test3"));
	}
	
	/**
	 * 测试登录功能
	 */
	@Test
	public void loginTest() {
		User user = new User();
		user.setId("test");
		user.setPwd("000000");
		System.out.println(userService.login(user));
		
	}
	
	/**
	 * 测试获取用户的密保问题
	 */
	@Test
	public void  getSecurityQuestionTest() {
		System.out.println(userService.getSecurityQuestion("test"));
	}
	
	/**
	 * 测试检验密保问题
	 */
	@Test
	public void checkSecurityAnswerTest() {
		System.out.println(userService.checkSecurityAnswer("test","hahaha"));
	}
	
	/**
	 * 测试重设密码
	 */
	@Test
	public void resetPassword() {
		System.out.println(userService.resetPassword("test", "000000"));
	}
	
	/**
	 * 测试添加喜欢的歌曲
	 */
	@Test
	public void addLikedSongtest() {
		userService.addLikeSong("test", 3);
		userService.addLikeSong("test", 2);
	}
	
	/**
	 * 测试获取喜欢的歌曲列表
	 */
	@Test
	public void getLikeSongListTest() {
		List<Song> songList = userService.getLikedSongs("test");
		for(Song song: songList) {
			System.out.println(song.getId());
		}
	}
	
	/**
	 * 测试移除喜欢的歌曲
	 */
	@Test
	public void removeLikeSongTest() {
		userService.removeLikeSong("test", 1);
		List<Song> songList = userService.getLikedSongs("test");
		for(Song song: songList) {
			System.out.println(song.getId());
		}
	}
}
