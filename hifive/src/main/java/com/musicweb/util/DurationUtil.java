/**
 * 
 */
package com.musicweb.util;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import com.musicweb.service.impl.SongServiceImpl;

/**
 * 获得歌曲音频文件时长工具类
 * @author brian
 *
 */
public class DurationUtil {
	
	/**
	 * 获取指定音频文件的时长
	 * @param filePath：音频文件的绝对路径
	 * @return：表示时长的字符串，失败则返回null
	 */
	public static String computeDuration(String filePath) {
		File file = new File(filePath);
		try {
			MP3File f = (MP3File)AudioFileIO.read(file);
			MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
			System.out.println(audioHeader.getTrackLength());
			int len = audioHeader.getTrackLength();//以秒为单位
			//时长格式为03: 23
			int minute = len/60;
			int second = len%60;
			String format = "%02d";
			String minutes = String.format(format, minute);
			String seconds = String.format(format, second);
			String duration = minutes + ": " + seconds;
			return duration;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(DurationUtil.computeDuration("/home/brian/eclipse/eclipse-workspace/MusicWeb/src/main/webapp/WEB-INF/music/Alan Walker/Faded/Faded.mp3"));
	}

}
