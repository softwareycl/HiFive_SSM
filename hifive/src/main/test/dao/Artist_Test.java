package dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import base.BaseTest;

import com.musicweb.dao.ArtistDao;
import com.musicweb.domain.Artist;
public class Artist_Test extends BaseTest{
	@Autowired
	ArtistDao artistmapper;

//	@Test
	public void test_selectByCategory() {
		try {
			artistmapper.selectByCategory("Z", 1, 1, 5, 10);
			System.out.println("test_selectByCategory succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_select() {
		try {
			artistmapper.select(1);
			System.out.println("test_select succeed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
