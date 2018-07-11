package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
import com.musicweb.domain.User;

/**
 * UserService
 * @author likexin
 * @Date 2018.6.23
 * UserService完成有关用户模块的业务逻辑实现
 * 接受UserController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给UserController
 * Service层针对业务数据增加各类缓存操作
 */
public interface UserService {
	/**
	 * 用户注册
	 * 
	 * @param user 用户注册信息
	 * @return 注册操作状态
	 */
	boolean register(User user);
	
	/**
	 * 激活用户
	 * 
	 * @param code 激活码
	 * @return true表示激活成功，false表示激活失败
	 */
	boolean active(String code);

	/**
	 * 用户登录
	 * 
	 * @param user 用户信息
	 * @return 登录状态
	 */
	int login(User user);

	/**
	 * 查看用户是否存在
	 * 
	 * @param id 用户ID
	 * @return 
	 */
	boolean checkUserExisted(String id);

	/**
	 * 获取用户的密保问题
	 * 
	 * @param id 用户ID
	 * @return 用户的密保问题
	 */
	int getSecurityQuestion(String id);

	/**
	 * 检验密保问题答案是否正确
	 * 
	 * @param id 用户id
	 * @param answer 用户输入的密保问题答案
	 * @return 匹配状态
	 */
	boolean checkSecurityAnswer(String id, String answer);

	/**
	 * 重置密码
	 * 
	 * @param id 用户id
	 * @param pwd 新设密码
	 * @return 操作状态
	 */
	boolean resetPassword(String id, String pwd);

	/**
	 * 获取用户信息
	 * 
	 * @param id 用户id
	 * @return 用户
	 */
	User getInfo(String id);

	/**
	 * 设置用户头像
	 * 
	 * @param id 用户id
	 * @param image 图片路径
	 * @return 操作状态
	 */
	boolean setImage(String id, String image);

	/**
	 * 用户修改个人信息
	 * 
	 * @param user
	 * @return 操作状态
	 */
	boolean modifyInfo(User user);

	/**
	 * 用户获取我的歌单
	 * 
	 * @param id 用户id
	 * @return 用户的歌单列表
	 */
	List<Playlist> getMyPlaylists(String id);

	/**
	 * 用户获取收藏的音乐
	 * 
	 * @param userId 用户id
	 * @return 用户喜欢的音乐列表
	 */
	List<Song> getLikedSongs(String userId);

	/**
	 * 用户获取收藏的专辑
	 * 
	 * @param userId 用户ID
	 * @return 用户收藏的专辑列表
	 */
	List<Album> getLikeAlbums(String userId);

	/**
	 * 添加喜欢的专辑
	 * 
	 * @param userId 用户id
	 * @param albumId 专辑id
	 * @return 操作状态
	 */
	boolean addLikeAlbum(String userId, int albumId);

	/**
	 * 添加喜欢的歌曲
	 * 
	 * @param userId 用户id
	 * @param songId 歌曲id
	 * @return 操作状态
	 */
	boolean addLikeSong(String userId, int songId);

	/**
	 * 移除喜欢的专辑
	 * 
	 * @param userId 用户id
	 * @param albumId 专辑id
	 * @return
	 */
	boolean removeLikeAlbum(String userId, int albumId);

	/**
	 * 删除喜欢的歌曲
	 * 
	 * @param userId 用户id
	 * @param songId 歌曲id
	 * @return 操作状态
	 */
	boolean removeLikeSong(String userId, int songId);
	
	/**
	 * 修改密码
	 * 
	 * @param id 用户id
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 * @return 操作状态
	 */
	boolean modifyPassword(String id, String oldPwd, String newPwd);
	
}
