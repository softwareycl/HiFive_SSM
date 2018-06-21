package com.musicweb.util;

import java.io.File;

public class FileUtil {
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static void deleteFile(File file) {
		if (!file.exists() || !file.isFile()) {
			System.out.println("File does not exist or is not a file");
			return;
		}
		file.delete();
	}
	
	public static void deleteFolder(File file) {
		if (!file.exists() || !file.isDirectory()) {
			System.out.println("File does not exist or is not a folder");
			return;
		}
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				// 使用递归
				deleteFolder(f);
			} else {
				f.delete();
			}
		}
		file.delete();
	}
	
	public static Boolean mkdir(File dir) {
		return dir.mkdirs();
	}
}
