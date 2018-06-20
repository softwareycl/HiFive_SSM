package dao;

import org.springframework.beans.factory.annotation.Autowired;

import base.BaseTest;

import com.musicweb.dao.AlbumDao;
import com.musicweb.domain.Album;
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
			albummapper.select(1);
			System.out.println("test_select succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
