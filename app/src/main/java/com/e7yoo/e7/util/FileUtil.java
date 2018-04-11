package com.e7yoo.e7.util;

import android.content.Context;

import com.e7yoo.e7.E7App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


	public static boolean makeDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			return file.mkdirs();
		} else {
			return true;
		}
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

	public static String getFilePath(Context context, String fileName) {
		try {
			File file = context.getFileStreamPath(fileName);
			if (file.exists()) {
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 将需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
	 *
	 * @param isCover 是否覆盖已存在的目标文件
	 * @param source
	 * @param dest
	 */
	public static boolean copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
		boolean result = false;
		File file = new File(dest);
		if (isCover || (!isCover && !file.exists())) {
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				if(file.exists()) {
					file.mkdirs();
				}
				is = E7App.mApp.getResources().getAssets().open(source);
				String path = dest;
				fos = new FileOutputStream(path);
				byte[] buffer = new byte[1024];
				int size = 0;
				while ((size = is.read(buffer, 0, 1024)) >= 0) {
					fos.write(buffer, 0, size);
				}
				result = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
