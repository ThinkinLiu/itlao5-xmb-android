package com.e7yoo.e7.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.OsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * 简单的多线程下载（不支持断点,无线程池）作用于启动页广告图下载
 * 
 * @Title MulThreadDownload
 * @Description 简单的多线程下载
 * @author 刘生健
 * @createTime 2016-4-26 下午03:21:06
 * @modifyBy
 * @modifyTime
 * @modifyRemark
 */
public class MulThreadDownload {
	/** 下载的URL */
	private URL downloadUrl;
	/** 本地文件 */
	private File localFile;
	/** 每个线程下载的数据长度 */
	private int block;

	/**
	 * 
	 * 多线程文件下载(根据网络状况自动分配线程数)
	 * 
	 * @param path
	 *            下载地址
	 * @param locDirPath
	 *            本地目录
	 * @param locFileName
	 *            本地图片名(传null则会根据OSUtil.toMD5(path)命名)
	 * @param downloadCallBack
	 *            回调
	 * @param fileTypes
	 *            指定文件类型（可选）
	 * @throws Exception
	 */
	public void download(Context context, String path, String locDirPath,
                         String locFileName, DownloadCallBack downloadCallBack,
                         String... fileTypes) throws Exception {
		int threadCount = 3;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info == null || !info.isConnectedOrConnecting()) {
				threadCount = 3;
				return;
			}
			switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI:
			case ConnectivityManager.TYPE_WIMAX:
			case ConnectivityManager.TYPE_ETHERNET:
				threadCount = 4;
				break;
			case ConnectivityManager.TYPE_MOBILE:
				switch (info.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_LTE: // 4G
				case TelephonyManager.NETWORK_TYPE_HSPAP:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
					threadCount = 3;
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
					threadCount = 2;
					break;
				case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
				case TelephonyManager.NETWORK_TYPE_EDGE:
					threadCount = 1;
					break;
				default:
					threadCount = 3;
				}
				break;
			default:
				threadCount = 3;
			}
		} catch (Exception e) {
			threadCount = 3;
		}
		download(path, locDirPath, locFileName, threadCount, downloadCallBack,
				fileTypes);
	}

	/**
	 * 
	 * 多线程文件下载
	 * 
	 * @param path
	 *            下载地址
	 * @param locDirPath
	 *            本地目录
	 * @param locFileName
	 *            本地图片名(传null则会根据OSUtil.toMD5(path)命名)
	 * @param threadCount
	 *            线程数
	 * @param downloadCallBack
	 *            回调
	 * @param fileTypes
	 *            指定文件类型（可选）
	 * @throws Exception
	 */
	public void download(final String path, final String locDirPath,
                         final String locFileName, final int threadCount,
                         final DownloadCallBack downloadCallBack, final String... fileTypes) {
		System.out.println("线程" + "主线程开始=" + path);
		new Thread() {
			public void run() {
				try {
					downloadThread(path, locDirPath, locFileName, threadCount,
							downloadCallBack, fileTypes);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("线程" + "子线程开始=" + e);
					downloadCallBack.error();
				}
			};
		}.start();
	}

	/**
	 * 
	 * 多线程文件下载
	 * 
	 * @param path
	 *            下载地址
	 * @param locDirPath
	 *            本地目录
	 * @param locFileName
	 *            本地文件名(传null则会根据OSUtil.toMD5(path)命名)
	 * @param threadCount
	 *            线程数
	 * @param downloadCallBack
	 *            回调
	 * @param fileTypes
	 *            指定文件类型
	 * @throws Exception
	 */
	private void downloadThread(String path, String locDirPath,
                                String locFileName, final int threadCount,
                                final DownloadCallBack downloadCallBack, String... fileTypes)
			throws Exception {
		downloadUrl = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) downloadUrl
				.openConnection();
		// 设置 GET 请求方式
		conn.setRequestMethod("GET");
		// 设置响应超时
		conn.setConnectTimeout(5 * 1000);
		// 获取下载文件大小
		final int len = conn.getContentLength();
		System.out.println("线程" + "len=" + len);
		if (len <= 0) {
			// 未获取到正确的文件大小
			downloadCallBack.contentLengthError();
			return;
		}
		File dir = new File(locDirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		/* 获取本地文件完整路径 */
		final String locPath = getLocPath(path, locDirPath, locFileName,
				fileTypes);
		/* 创建本地目标文件，并设置其大小为准备下载文件的总大小 */
		localFile = new File(locPath + ".tmp");
		RandomAccessFile accessFile = new RandomAccessFile(localFile, "rwd");
		accessFile.setLength(len);
		accessFile.close();
		/* 计算每条线程要下载的数据大小 */
		block = len % threadCount == 0 ? len / threadCount : len / threadCount
				+ 1;
		downloadCallBack.totalSize(len);
		/* 启动线程下载文件 */
		for (int i = 0; i < threadCount; i++) {
			int threadId = i;
			long start = i * block;
			long end = start + block - 1;
			if (end >= len) {
				end = len - 1;
			}
			System.out.println("线程" + threadId + "开始");
			new DownloadThread(threadId, start, end, new Callback() {
				@Override
				public void complete(int threadId) {
					completeNum++;
					System.out.println("线程" + threadId + "完成" + completeNum
							+ ":" + threadCount);
					if (completeNum == threadCount) {
						// 全部下载完成
						localFile.renameTo(new File(locPath));
						downloadCallBack.complete(locPath);
					}
				}

				@Override
				public void progress(int threadId, long size) {
					downloadSize += size;
					downloadCallBack.progress(len, downloadSize);
				}

				@Override
				public void error(int threadId) {
					System.out.println("线程" + threadId + "error");
					downloadCallBack.error();
				}
			}).start();
		}
	}

	public interface DownloadCallBack {
		/** 异常，结束下载，ContentLength<=0 */
		public void contentLengthError();

		public void totalSize(long total);

		public void complete(String path);

		public void progress(long total, long size);

		public void error();
	}

	int completeNum = 0;
	long downloadSize = 0;

	private String getLocPath(String path, String locDirPath,
                              String locFileName, String... fileTypes) {
		String locPath;
		// 获取文件名
		if (CommonUtil.isEmptyTrim(locFileName)) {
			locFileName = OsUtil.toMD5(path);
		}
		// 文件类型
		String fileType;
		if (fileTypes != null && fileTypes.length > 0) {
			fileType = fileTypes[0];
		} else {
			// 获取path中的文件格式后缀
			String[] s = path.split("\\.");
			fileType = s[s.length - 1];
		}
		if (fileType != null && fileType.trim().length() > 0) {
			// 如果path带有文件后缀，则使用其作为后缀
			locPath = locDirPath + File.separator + locFileName + "."
					+ fileType;
		} else {
			locPath = locDirPath + File.separator + locFileName;
		}
		return locPath;
	}

	/**
	 * 内部类： 文件下载线程类
	 */
	private final class DownloadThread extends Thread {
		/* 线程 id */
		private int threadId;
		/* 线程下载结果回调 */
		private Callback callback;
		/* 开始下载的位置 */
		private long startPosition;
		/* 结束下载的位置 */
		private long endPosition;

		/**
		 * 新建一个下载线程
		 * 
		 * @param threadId
		 *            线程 id
		 */
		public DownloadThread(int threadId, long start, long end,
				Callback callback) {
			this.threadId = threadId;
			this.callback = callback;
			startPosition = start;
			endPosition = end;
		}

		@Override
		public void run() {
			RandomAccessFile accessFile = null;
			try {
				/* 设置从本地文件的什么位置开始写入数据 ,"rwd" 表示对文件具有读写删权限 */
				accessFile = new RandomAccessFile(localFile, "rwd");
				accessFile.seek(startPosition);
				HttpURLConnection conn = (HttpURLConnection) downloadUrl
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(5 * 1000);
				/* 为 HTTP 设置 Range 属性，可以指定服务器返回数据的范围 */
				conn.setRequestProperty("Range", "bytes=" + startPosition + "-"
						+ endPosition);
				/* 将数据写往本地文件 */
				long size = writeTo(accessFile, conn);
				if (size >= endPosition - startPosition) {
					callback.complete(threadId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (accessFile != null) {
						accessFile.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		/**
		 * 将下载数据写往本地文件
		 */
		private long writeTo(RandomAccessFile accessFile, HttpURLConnection conn) {
			InputStream is = null;
			long size = 0;
			try {
				is = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = is.read(buffer)) != -1) {
					accessFile.write(buffer, 0, len);
					size += len;
					callback.progress(threadId, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return size;
		}
	}

	interface Callback {
		public void complete(int threadId);

		public void progress(int threadId, long size);

		public void error(int threadId);
	}
}