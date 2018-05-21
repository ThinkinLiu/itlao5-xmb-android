package com.e7yoo.e7.fragment;


import android.content.DialogInterface;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.util.ProgressDialogEx;
import com.umeng.analytics.MobclickAgent;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    protected View mRootView;


    public BaseFragment() {
        // Required empty public constructor
    }

    public abstract void onEventMainThread(Message msg);

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    protected ProgressDialogEx progressDialogEx;
    protected void showProgress(int loading){
        showProgress(loading, 30 * 1000);
    }
    protected void showProgress(int loading, int time){
        if(isDetached()) {
           return;
        }
        dismissProgress();
        progressDialogEx = ProgressDialogEx.show(getActivity(), "", getString(loading), true, time, new ProgressDialogEx.OnCancelListener2() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
            @Override
            public void onAutoCancel(DialogInterface dialog) {
            }
        });
    }

    protected void dismissProgress() {
        try {
            if(progressDialogEx != null && progressDialogEx.isShowing()) {
                progressDialogEx.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        progressDialogEx = null;
    }

    @Override
    public void onDetach() {
        dismissProgress();
        super.onDetach();
    }
}
