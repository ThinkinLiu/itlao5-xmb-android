package com.e7yoo.e7.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtil {
	/**
	 * 保存数据
	 * 
	 * @return 是否保存成功
	 */
	public static synchronized boolean saveFile(Context context, String fileName, Object obj) {
		boolean result = false;
		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(obj);
			result = true;
			os.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取文件 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static synchronized Object readFile(Context context, String fileName) {
		Object obj = null;
		try {
			File file = context.getFileStreamPath(fileName);
			if (!file.exists()) {
				return null;
			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			obj = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static synchronized boolean isFileExists(Context context, String fileName) {
		try {
			File file = context.getFileStreamPath(fileName);
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
