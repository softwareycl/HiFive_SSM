package dao;

import org.springframework.beans.factory.annotation.Autowired;

import base.BaseTest;

import java.util.List;

import org.junit.Test;

import com.musicweb.dao.AlbumDao;
import com.musicweb.dao.PlaylistDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Playlist;
import com.musicweb.domain.Song;
public class Playlist_Test extends BaseTest{
	@Autowired
	PlaylistDao playlistmapper;
	
//	@Test
	public void test_insert() {
		try {
			Playlist pl=new Playlist("Jay Chou","rien","/image/playlist/201806220001.jpg");
			playlistmapper.insert("guozyunzhe.se@gmail.com",pl);
			System.out.println("test_insert succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	@Test
	public void test_insertSong() {
		try {
			playlistmapper.insertSong(2,150);
			System.out.println("test_insertSong succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_insertPlaylistToPlaylist() {
		try {
			playlistmapper.insertPlaylistToPlaylist(2,3);
			System.out.println("test_insertPlaylistToPlaylist succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_insertAlbumToPlaylist() {
		try {
			playlistmapper.insertAlbumToPlaylist(102,3);
			System.out.println("test_insertAlbumToPlaylist succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_delete() {
		try {
			playlistmapper.delete(4);
			System.out.println("test_delete succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_deleteSong() {
		try {
			playlistmapper.deleteSong(3,126);
			System.out.println("test_deleteSong succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
//	@Test
	public void test_deleteSongInAll() {
		try {
			playlistmapper.deleteSongInAll(2);
			System.out.println("test_deleteSongInAll succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_update() {
		try {
			Playlist pl=new Playlist(2,"Jay Chou","rien","/image/playlist/201806220001.jpg");
			playlistmapper.update(pl);
			System.out.println("test_update succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_updateImage() {
		try {
			playlistmapper.updateImage(2,"/image/playlist/201806220001_new.jpg");
			System.out.println("test_updateImage succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_select() {
		try {
			playlistmapper.select(2);
			System.out.println("test_select succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectSongCount() {
		try {
			int count=playlistmapper.selectSongCount(3);
			System.out.println("test_selectSongCount succeed");
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectAllSongs() {
		try {
			List<Song> songs =playlistmapper.selectAllSongs(3);
			System.out.println("test_selectAllSongs succeed");
			for(Song s : songs) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}