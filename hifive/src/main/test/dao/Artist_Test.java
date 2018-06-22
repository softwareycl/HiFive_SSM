package dao;

import java.util.List;
import org.junit.Test;


import org.springframework.beans.factory.annotation.Autowired;

import base.BaseTest;

import com.musicweb.dao.ArtistDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Artist;
import com.musicweb.domain.Song;
public class Artist_Test extends BaseTest{
	@Autowired
	ArtistDao artistmapper;

//	@Test
	public void test_insert() {
		try {
			Artist artist=new Artist("张震岳","Z","/image/singer/张震岳.jpg",2,"中国","这里是张震岳",0,1,"台北","Singer",null,"思念是一种病");
			artistmapper.insert(artist);
			System.out.println("test_insert succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_delete() {
		try {
			artistmapper.delete(107);
			System.out.println("test_delete succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_update() {
		try {
			Artist artist=new Artist(108,"张震岳","Z","/image/singer/张震岳.jpg",2,"中国","这里是张震岳",0,1,"台北","歌手",null,"思念是一种病");
			artistmapper.update(artist);
			System.out.println("test_update succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_updateImage() {
		try {
			artistmapper.updateImage(108,"/image/singer/张震岳_new.jpg");
			System.out.println("test_updateImage succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_updatePlayCount() {
		try {
			artistmapper.updatePlayCount(108,100000);
			System.out.println("test_updatePlayCount succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_select() {
		try {
			artistmapper.select(1);
			System.out.println("test_select succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectByName() {
		try {
			List<Artist> artists=artistmapper.selectByName("张",0,5);
			System.out.println("test_select succeed");
			for(Artist s : artists) {
				System.out.println(s.getName());
				System.out.println(s.getGender());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectCountByName() {
		try {
			int count=artistmapper.selectCountByName("张");
			System.out.println("test_selectCountByName succeed");
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_selectByCategory() {
		try {
			List<Artist> artists =	artistmapper.selectByCategory("Z", 0, 1, 0, 10);
			System.out.println("test_selectByCategory succeed");
			for(Artist s : artists) {
				System.out.println(s.getName());
				System.out.println(s.getBirthplace());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectCountByCatagory() {
		try {
			int count = artistmapper.selectCountByCategory("Z", 0, 1);
			System.out.println("test_select succeed");
			System.out.println(count);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectAllSongs() {
		try {
			List<Song> l = artistmapper.selectAllSongs(1);
			System.out.println("test_selectAllSongs succeed");
			for(Song s : l) {
				System.out.println(s.getName());
				System.out.println(s.getAlbumName());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_selectAllAlbums() {
		try {
			List<Album> l = artistmapper.selectAllAlbums(1);
			System.out.println("test_selectAllSongs succeed");
			for(Album s : l) {
				System.out.println(s.getName());
				System.out.println(s.getArtistName());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
