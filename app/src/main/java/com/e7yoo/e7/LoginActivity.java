package com.e7yoo.e7;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.ProgressDialogEx;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.LoginResponse;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    CommUser commUser;
    private EditText mNameEt;
    private EditText mPwdEt;
    private TextView mLoginTv;

    @Override
    protected String initTitle() {
        return getString(R.string.login);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mNameEt = (EditText) findViewById(R.id.login_name);
        mPwdEt = (EditText) findViewById(R.id.login_pwd);
        mLoginTv = (TextView) findViewById(R.id.login_tv);
    }

    @Override
    protected void initSettings() {
        mLoginTv.setOnClickListener(this);
    }

    @Override
    protected void initViewListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_tv:
                login();
                break;
        }
    }

    private void login() {
        String name = mNameEt.getText().toString().trim();
        String pwd = mPwdEt.getText().toString().trim();
        int error = match(name, pwd);
        if(error == 0) {
            login(name, pwd);
        } else {
            TastyToastUtil.toast(this, error);
        }
    }

    private int match(String name, String pwd) {
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            return R.string.login_error_empty;
        }
        return 0;
    }

    private void login(String name, String pwd) {
        showProgress();
        CommUser user = new CommUser();
        user.id = name;
        CommunitySDK communitySDK = CommunityFactory.getCommSDK(this);
        communitySDK.loginByWsq(user, pwd, loginListener);
    }

    ProgressDialogEx progressDialogEx;
    private void showProgress(){
        progressDialogEx = ProgressDialogEx.show(LoginActivity.this, "", getString(R.string.login_ing), true, 30 * 1000, new ProgressDialogEx.OnCancelListener2() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
            @Override
            public void onAutoCancel(DialogInterface dialog) {
            }
        });
    }

    private void dismissProgress() {
        if(!isFinishing() && progressDialogEx != null && progressDialogEx.isShowing()) {
            progressDialogEx.dismiss();
        }
    }

    private Listeners.FetchListener<LoginResponse> loginListener = new Listeners.FetchListener<LoginResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(LoginResponse loginResponse) {
            dismissProgress();
            if(loginResponse != null && loginResponse.result != null && loginResponse.errCode == ErrorCode.NO_ERROR) {
                commUser = loginResponse.result;
                finish(commUser);
            } else {
                TastyToastUtil.toast(LoginActivity.this, R.string.login_failed);
            }
        }
    };

    private void finish(CommUser commUser) {
        Intent intent = new Intent();
        intent.putExtra("CommUser", commUser);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
