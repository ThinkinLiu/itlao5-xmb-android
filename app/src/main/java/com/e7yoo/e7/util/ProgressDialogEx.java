package com.e7yoo.e7.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

import com.e7yoo.e7.R;

public class ProgressDialogEx implements DialogInterface {
    private boolean isAutoDismiss;
    private int duration;

    private OnCancelListener2 mCancelListener;

    private Handler mHandler;
    private ProgressDialog progressDialog;
    
    public static final int DIALOG_AUTO_DISMISS = 15000;

    public interface OnCancelListener2 extends OnCancelListener {

        /**
         * call this method when the dialog is canceled by the user
         */
        void onCancel(DialogInterface dialog);

        void onAutoCancel(DialogInterface dialog);
    }

    public ProgressDialogEx(Context context) {
        init(context);
    }

    private void init(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        mHandler = new MyHandler(this);
    }

    public boolean isShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    public static class MyHandler extends Handler {

        private ProgressDialogEx dialog;

        public MyHandler(ProgressDialogEx dialog) {
            this.dialog = dialog;
        }

        @Override
        public void handleMessage(Message msg) {
            if (dialog.isShowing()) {
                try{
                    dialog.dismiss();
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (dialog.getOnCancelListener2() != null) {
                    dialog.getOnCancelListener2().onAutoCancel(dialog);
                }
            }
        }

    }

    public void setTitle(String title) {
        progressDialog.setTitle(title);
    }

    public void dismiss() {
        progressDialog.dismiss();
    }

    public void cancel() {
        progressDialog.cancel();
    }

    public void setMessage(String message) {
        progressDialog.setMessage(message);
    }
    
    public void setCancelOnTouchOutside(boolean param){
    	progressDialog.setCanceledOnTouchOutside(param);
    }

    private static String mMessage;
    public static ProgressDialogEx show(Context context, String title, String message,
                                        boolean isAutoDismiss, int duration, OnCancelListener2 listener) {
        ProgressDialogEx dialog = new ProgressDialogEx(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        mMessage = message;
        dialog.setAutoDismiss(isAutoDismiss);
        dialog.setOnAutoCancelListener(listener);
        dialog.setDuration(duration);
        dialog.show();
        return dialog;
    }

    public void show() {
        if (isAutoDismiss()) {
            mHandler.sendEmptyMessageDelayed(0, getDuration());
        }
        try {
            progressDialog.show();
            Window window = progressDialog.getWindow();
            window.setContentView(R.layout.dialog_progress);
            try {
                if(mMessage != null) {
                    ((TextView) window.findViewById(R.id.message)).setText(mMessage);
                }
    		} catch (Exception e) {
    		}
		} catch (Exception e) {
		}
    }

    /**
     * @return the isAutoDismiss
     */
    public boolean isAutoDismiss() {
        return isAutoDismiss;
    }

    /**
     * @param isAutoDismiss
     *            the isAutoDismiss to set
     */
    public void setAutoDismiss(boolean isAutoDismiss) {
        this.isAutoDismiss = isAutoDismiss;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     *            the mCancelListener to set
     * @param mCancelListener
     */
    public void setOnAutoCancelListener(OnCancelListener2 listener) {
        this.mCancelListener = listener;
        progressDialog.setOnCancelListener(listener);
    }

    OnCancelListener2 getOnCancelListener2() {
        return mCancelListener;
    }

}
