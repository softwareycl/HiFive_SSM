package dao;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


import org.springframework.beans.factory.annotation.Autowired;

import base.BaseTest;

import com.musicweb.dao.ArtistDao;
import com.musicweb.dao.SongDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
import com.musicweb.util.RedisUtil;
public class Song_Test extends BaseTest{
	@Autowired
	SongDao songmapper;
	@Resource
	private RedisUtil redisUtil;
//	@Test
	public void test_insert() {
		try {
			Song song=new Song("安静",111,6,0,"/lyrics/周杰伦/范特西/安静.txt","/image/album/周杰伦/范特西.jpg","普通话",5,null,"/music/周杰伦/范特西/安静.mp3",2);
			songmapper.insert(song);
			System.out.println("test_insert succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	@Test
	public void test_updateImage() {
		try {
			songmapper.updateImage(150,"/image/album/周杰伦/范特西_new.jpg");
			System.out.println("test_updateImage succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_updateLyricsPath() {
		try {
			songmapper.updateLyricsPath(150,"/lyrics/周杰伦/范特西/安静_new.txt");
			System.out.println("test_updateLyricsPath succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_updateFilePath() {
		try {
			songmapper.updateFilePath(150,"/music/周杰伦/床边故事/安静_new.mp3");
			System.out.println("test_updateFilePath succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
//	@Test
	public void test_updatePlayCount() {
		try {
			songmapper.updatePlayCount(150,100000);
			System.out.println("test_updatePlayCount succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_update() {
		try {
			Song song=new Song(150,"安静_new",111,6,0,"/lyrics/周杰伦/范特西/安静.txt","/image/album/周杰伦/范特西.jpg","普通话",5,null,"/music/周杰伦/范特西/安静.mp3",3);
			songmapper.update(song);
			System.out.println("test_update succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_delete() {
		try {
			songmapper.delete(150);
			System.out.println("test_delete succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectById() {
		try {
			Song song=songmapper.selectById(1);
			System.out.println("test_select succeed");
			System.out.println(song.getName());
			System.out.println(song.getArtistName());	
			System.out.println(song.getRegion());
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectByName() {
		try {
			List<Song> songs=songmapper.selectByName("爱",0,5);
			System.out.println("test_selectByName succeed");
			for(Song s : songs) {
				System.out.println(s.getName());
				System.out.println(s.getArtistName());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectCountByName() {
		try {
			int count=songmapper.selectCountByName("爱");
			System.out.println("test_selectCountByName succeed");
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_selectLatest() {
		try {
			List<Song> songs =songmapper.selectLatest(1,7);
			System.out.println("test_selectLatest succeed");
			for(Song s : songs) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectHittest() {
		try {
			List<Song> songs =songmapper.selectHittest(10);
			System.out.println("test_selectHittest succeed");
			for(Song s : songs) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectRankByRegion() {
		try {
			List<Song> songs =songmapper.selectRankByRegion(1,10);
			System.out.println("selectRankByRegion succeed");
			for(Song s : songs) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}