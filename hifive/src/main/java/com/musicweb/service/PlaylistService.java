package com.musicweb.service;

import java.util.List;

import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;

/**
 * PlaylistService
 * @author likexin
 * @Date 2018.6.21
 * PlaylistService完成有关歌单模块的业务逻辑实现
 * 接受PlaylistController的调用，通过对Dao层各类方法的调用，完成业务逻辑
 * 操作完成后，将操作结果返回给PlaylistController
 * Service层针对业务数据增加各类缓存操作
 */
public interface PlaylistService {
	
	/**
	 * 获取歌单信息
	 * @param id 歌单ID
	 * @return 返回歌单对象
	 */
	Playlist getInfo(int id);

	/**
	 * 创建歌单
	 * @param userId 用户ID
	 * @param playlist 歌单信息
	 * @return 返回新建的歌单ID
	 */
	int create(String userId,Playlist playlist);

	/**
	 * 设置歌单图片
	 * @param d 歌单ID
	 * @param image 上传的图片在服务器的路径
	 * @return 设置歌单图片状态
	 */
	boolean setImage(int id, String image);

	/**
	 * 修改歌单信息
	 * @param Playlist 存有修改的歌单信息的歌单
	 * @return 修改歌单信息状态
	 */
	boolean modifyInfo(Playlist playlist);

	/**
	 * 删除歌单
	 * @param id 歌单ID
	 * @return 删除歌单的状态
	 */
	boolean remove(int id);

	/**
	 * 获取歌单的歌曲列表
	 * @param playlistId 歌单ID
	 * @return 歌单的歌曲列表
	 */
	List<Song> getSongList(int playlistId);

	/**
	 * 为歌单新增歌曲
	 * @param songId 新增的歌曲ID
	 * @param playlistId 歌单ID
	 * @return 新增歌曲状态
	 */
	boolean addSong(int playlistId, int songId);

	/**
	 * 从歌单中删除歌曲
	 * @param playlistId 歌单ID
	 * @param songId 移除的歌单ID
	 * @return 移除歌曲状态
	 */
	boolean removeSong(int playlistId, int songId);

	/**
	 * 向歌单从添加另一歌单所有歌曲
	 * @param fromId 被复制的歌单ID
	 * @param toId 新增歌曲的歌单ID
	 * @return 向歌单从添加另一歌单所有歌曲状态
	 */
	boolean addPlaylistToPlaylist(int fromId, int toId);

	/**
	 * 向歌单中添加专辑
	 * @param albumId 专辑ID
	 * @param playlistId 歌单ID
	 * @return 向歌单添加专辑状态
	 */
	boolean addAlbumToPlaylist(int albumId, int playlistId);
	
	/**
	 * 显示用户的歌单列表
	 * @param userId 获取用户ID
	 * @return 歌单列表
	 */
	List<Playlist> getPlaylistList(String userId);
	
	/**
	 * 验证用户是否拥有对应歌单
	 * @param userId 用户ID
	 * @param playlistId 歌单ID
	 * @return 是否拥有的状态
	 */
	Boolean checkPossession(String userId, int playlistId);
	
}
