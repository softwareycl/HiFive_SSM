package dao;

import org.springframework.beans.factory.annotation.Autowired;

import base.BaseTest;

import java.util.List;

import org.junit.Test;

import com.musicweb.dao.AlbumDao;
import com.musicweb.domain.Album;
import com.musicweb.domain.Song;
public class Album_Test extends BaseTest{
	@Autowired
	AlbumDao albummapper;
	
//	@Test
	public void test_selectByCategory() {
		try {
			albummapper.selectByCategory(1, 1, 10, 5);
			System.out.println("test_selectByCategory succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void test_select() {
		try {
			Album album = albummapper.select(6);
			System.out.println("test_select succeed");
			System.out.println(album.getArtistName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_selectAllSongs() {
		try {
			List<Song> l = albummapper.selectAllSongs(1);
			System.out.println("test_select succeed");
			for(Song s : l) {
				System.out.println(s.getName());
				System.out.println(s.getAlbumName());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
