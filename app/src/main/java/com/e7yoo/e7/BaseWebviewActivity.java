package com.e7yoo.e7;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;

import com.e7yoo.e7.download.MulThreadDownload;
import com.e7yoo.e7.download.MulThreadDownload.DownloadCallBack;
import com.e7yoo.e7.model.TextSet;
import com.e7yoo.e7.util.PopupWindowUtil;
import com.e7yoo.e7.util.ProgressDialogEx;
import com.e7yoo.e7.webview.ImageUtil;
import com.e7yoo.e7.webview.ReWebChomeClient;
import com.e7yoo.e7.webview.ReWebViewClient;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;

public abstract class BaseWebviewActivity extends BaseActivity implements ReWebChomeClient.OpenFileChooserCallBack {

	protected static final int REQUEST_CODE_PICK_IMAGE = 0;
	protected WebView mWebView;
	protected Intent mSourceIntent;
	protected ValueCallback<Uri> mUploadMsg;
	protected ProgressDialogEx mProgressDialog;

	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();
			mWebView.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// 延迟三秒，保证zoomButton消失
					// 解决Receiver not registered:
					// android.widget.ZoomButtonsController
					if (mWebView != null) {
						try {
							mWebView.destroy();
							mWebView = null;
						} catch (Exception e) {
						}
					}
				}
			}, 3000);
			// new Timer().schedule(new TimerTask() {
			// @Override
			// public void run() {
			// // 寤惰繜涓夌锛屼繚璇亃oomButton娑堝け
			// // 瑙ｅ喅Receiver not registered:
			// // android.widget.ZoomButtonsController$1@354d07b7
			// try {
			// runOnUiThread(new Runnable() {
			// public void run() {
			// if (mWebView != null) {
			// try {
			// mWebView.destroy();
			// mWebView = null;
			// } catch (Exception e) {
			// }
			// }
			// }
			// });
			// } catch (Exception e) {
			// }
			// }
			// }, 3000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	public void initWebView() {
		// mProgressDialog = ProgressDialogEx.show(BaseWebviewActivity.this,
		// "loading", getString(R.string.loading), true,
		// ProgressDialogEx.DIALOG_AUTO_DISMISS, new OnCancelListener2() {
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// }
		//
		// @Override
		// public void onAutoCancel(DialogInterface dialog) {
		// }
		// });
		mWebView.setInitialScale(80);

		mWebView.setScrollbarFadingEnabled(true);

		mWebView.setWebViewClient(new ReWebViewClient());
		mWebView.setWebChromeClient(new ReWebChomeClient(this, mProgressDialog));

		// 璁惧畾缂栫爜鏍煎紡
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");

		WebSettings settings = mWebView.getSettings();
		// settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		settings.setBuiltInZoomControls(false);
		settings.setSupportZoom(false);
		int screenDensity = getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.FAR;
			break;
		}

		System.out.println("zoomDensity = = " + zoomDensity);

		settings.setDefaultZoom(zoomDensity);
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setAllowFileAccess(true);// 设置允许访问文件数据
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);

		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);

		fixDirPath();

		// mWebView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// HitTestResult htr = mWebView.getHitTestResult();
		// if (htr.getType() == WebView.HitTestResult.IMAGE_TYPE) {//判断被点击的类型为图片
		// // 大图
		// }
		// }
		// });
		//

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_PICK_IMAGE/*
														 * || requestCode ==
														 * FILECHOOSER_RESULTCODE_FOR_ANDROID_5
														 */) {
				if (mUploadMsg != null) {
					mUploadMsg.onReceiveValue(null);
					mUploadMsg = null;
				}
			}
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_PICK_IMAGE: {
			try {
				if (mUploadMsg == null) {
					return;
				}
				String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
				if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
					// System.out.println("您没有选择图片或图片不存在");
					break;
				}
				Uri uri = Uri.fromFile(new File(sourcePath));
				mUploadMsg.onReceiveValue(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		/*
		 * case FILECHOOSER_RESULTCODE_FOR_ANDROID_5:{ if (null ==
		 * mUploadMessageForAndroid5) return; Uri result = (data == null ||
		 * resultCode != RESULT_OK) ? null: data.getData(); if (result != null)
		 * { mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result}); } else
		 * { mUploadMessageForAndroid5.onReceiveValue(new Uri[]{}); }
		 * mUploadMessageForAndroid5 = null; }
		 */
		}
	}

	@Override
	public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
		mUploadMsg = uploadMsg;
		mSourceIntent = ImageUtil.choosePicture();
		startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
	}

	private void fixDirPath() {
		String path = ImageUtil.getDirPath();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	protected void addOnLongClickListener() {
		mWebView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// 长按事件监听（注意：需要实现LongClickCallBack接口并传入对象）
				try {
					HitTestResult htr = mWebView.getHitTestResult();// 获取所点击的内容
					if (htr.getType() == HitTestResult.IMAGE_TYPE) {// 判断被点击的类型为图片
						final String urlStr = htr.getExtra();
						// String name = urlStr.substring(urlStr.lastIndexOf(File.separator) + 1);
						// if (name.startsWith("image")) {
						// 与web端约定以img开头的图片才能进行保存，其它图片不能保存
						TextSet setItem1 = new TextSet(R.string.save_to_phone, false, new OnClickListener() {
							@Override
							public void onClick(View v) {
								// 先读取webview缓存文件
								boolean isSaved = false;
								/*
								 * File file = new File(getCacheDir() +
								 * "/webviewCache/10d8d5cd"); if (file != null
								 * && file.isFile()) { try { String result =
								 * PictureUtil .copyFile(
								 * BaseWebViewActivity.this, file, null); if
								 * (CommonUtil .isEmptyTrimString(result)) { //
								 * 为空，表示复制失败 } else { isSaved = true;
								 * Toast.makeText( BaseWebViewActivity.this,
								 * R.string.save_success, Toast.LENGTH_SHORT)
								 * .show(); } } catch (Exception e) { if
								 * (Constant.DEBUG) { e.printStackTrace(); } } }
								 */
								if (!isSaved) { // 复制webview缓存图片失败，则从网络下载
									downloadImg(urlStr);
								}
							}
						});
						PopupWindowUtil.showPopWindow(BaseWebviewActivity.this, mWebView, R.string.save_pictrue_hint,
								setItem1, null, true);
						return false;
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
	}

	private void downloadImg(String urlStr) {
		new MulThreadDownload().download(urlStr, "", "Coomix_" + System.currentTimeMillis(), 1, new DownloadCallBack() {
			@Override
			public void totalSize(long total) {
			}

			@Override
			public void progress(long total, long size) {
			}

			@Override
			public void error() {
			}

			@Override
			public void contentLengthError() {
			}

			@Override
			public void complete(String path) {
				Uri uri = Uri.fromFile(new File(path));
				if (path != null) {
					Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					intent.setData(uri);
					sendBroadcast(intent);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
		    			TastyToast.makeText(BaseWebviewActivity.this, getString(R.string.save_success), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
					}
				});
			}
		});
	}

	// public ValueCallback<Uri[]> mUploadMessageForAndroid5;
	// public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
	// @Override
	// public void openFileChooserImplForAndroid5(ValueCallback<Uri[]>
	// uploadMsg) {
	// mUploadMessageForAndroid5 = uploadMsg;
	// Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
	// contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
	// contentSelectionIntent.setType("image/*");
	//
	// Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
	// chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
	// chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
	//
	// startActivityForResult(chooserIntent,
	// FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
	// }
}
