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
			List<Album> albums =albummapper.selectByCategory(1, 1, 10, 5);
			System.out.println("test_selectByCategory succeed");
			for(Album s : albums) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
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
	
//	@Test
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
	
//	@Test
	public void test_selectCountByCatagory() {
		try {
			int count = albummapper.selectCountByCategory(2, 1);
			System.out.println("test_select succeed");
			System.out.println(count);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_insert() {
		try {
			Album album=new Album("范特西",null,2,1,"rien","/image/album/周杰伦/范特西.jpg",0,6);
			albummapper.insert(album);
			System.out.println("test_insert succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_update() {
		try {
			Album album=new Album(111,"范特西",null,2,1,"Fantasy","/image/album/周杰伦/范特西.jpg",0,6);
			albummapper.update(album);
			System.out.println("test_update succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_updateImage() {
		try {
			albummapper.updateImage(6,"/image/album/周杰伦/范特西_new.jpg");
			System.out.println("test_updateImage succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_updatePlayCount() {
		try {
			albummapper.updatePlayCount(6,10);
			System.out.println("test_updatePlayCount succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_selectByName() {
		try {
			List<Album> albums =albummapper.selectByName("YOU", 0,5);
			System.out.println("test_selectByName succeed");
			for(Album s : albums) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
//	@Test
	public void test_selectCountByName() {
		try {
			int count=albummapper.selectCountByName("YOU");
			System.out.println("test_selectCountByName succeed");
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_selectLatest() {
		try {
			List<Album> albums =albummapper.selectLatest(1,5);
			System.out.println("test_selectLatest succeed");
			for(Album s : albums) {
				System.out.println(s.getName());
				System.out.println(s.getStyle());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test_selectSongCount() {
		try {
			int count =albummapper.selectSongCount(5);
			System.out.println("test_selectSongCount succeed");
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void test_selectImage() {
		try {
			String Image =albummapper.selectImage(5);
			System.out.println("test_selectImage succeed");
			System.out.println(Image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
