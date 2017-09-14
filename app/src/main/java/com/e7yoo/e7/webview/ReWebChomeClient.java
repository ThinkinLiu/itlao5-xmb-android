package com.e7yoo.e7.webview;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.e7yoo.e7.util.ProgressDialogEx;

public class ReWebChomeClient extends WebChromeClient {

	private OpenFileChooserCallBack mOpenFileChooserCallBack;
	private ProgressDialogEx mProgressDialog;

	public ReWebChomeClient(OpenFileChooserCallBack openFileChooserCallBack, ProgressDialogEx progressDialog) {
		mOpenFileChooserCallBack = openFileChooserCallBack;
		mProgressDialog = progressDialog;
	}

	// For Android 3.0+
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
		mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
	}

	// For Android < 3.0
	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		openFileChooser(uploadMsg, "");
	}

	// For Android > 4.1.1
	public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                String acceptType, String capture) {
		openFileChooser(uploadMsg, acceptType);
	}

	public interface OpenFileChooserCallBack {
		void openFileChooserCallBack(ValueCallback<Uri> uploadMsg,
                                     String acceptType);
		// void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg);
	}

	// // For Android > 5.0
	// public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]>
	// uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
	// mOpenFileChooserCallBack.openFileChooserImplForAndroid5(uploadMsg);
	// return true;
	// }
	
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		if(newProgress >= 100) {
			if(mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		}
		super.onProgressChanged(view, newProgress);
	}
	
	@Override
	public void onReceivedTitle(WebView view, String title) {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		super.onReceivedTitle(view, title);
	}
}
