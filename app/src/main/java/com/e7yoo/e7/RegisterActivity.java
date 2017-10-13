package com.e7yoo.e7;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.ProgressDialogEx;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;

import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    CommUser mCommUser;
    private EditText mNameEt;
    private EditText mPwdEt;
    private EditText mPwdTwoEt;
    private TextView mRegisterTv;

    @Override
    protected String initTitle() {
        return getString(R.string.register);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        mNameEt = (EditText) findViewById(R.id.register_name);
        mPwdEt = (EditText) findViewById(R.id.register_pwd);
        mPwdTwoEt = (EditText) findViewById(R.id.register_pwd_two);
        mRegisterTv = (TextView) findViewById(R.id.register);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("name")) {
            mNameEt.setText(getIntent().getStringExtra("name"));
        }
    }

    @Override
    protected void initViewListener() {
        mRegisterTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                register();
                break;
        }
    }

    private void register() {
        String name = mNameEt.getText().toString().trim();
        String pwd = mPwdEt.getText().toString().trim();
        String pwdTwo = mPwdTwoEt.getText().toString().trim();
        int error = match(name, pwd, pwdTwo);
        if(error == 0) {
            register(name, pwd);
        } else {
            TastyToastUtil.toast(this, error);
        }
    }

    private int match(String name, String pwd, String pwdTwo) {
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            return R.string.register_error_empty;
        }
        if(!pwd.equals(pwdTwo)) {
            return R.string.register_pwd_equals_error;
        }
        if(pwd.length() < 0 && pwd.length() > 18) {
            return R.string.register_pwd_length_error;
        }
        if(!name.contains("@") || !name.contains(".")) {
            return R.string.register_name_error;
        }
        return 0;
    }

    private void register(String name, String pwd) {
        showProgress();
        CommUser user = new CommUser();
        user.id = name;
        user.name = getResources().getString(R.string.circle_name_default) + getName();
        // CommunitySDKImpl.getInstance().loginToWsq(this, user, loginListener, pwd);
        E7App.getCommunitySdk().registerByWsq(user, pwd, loginFetchListener);
    }

    private String getName() {
        String mills = String.valueOf(System.currentTimeMillis() / 1000);
        int count = 0;
        for(int i = 0; i < mills.length(); i++) {
            count += mills.charAt(i) - 48;
        }
        return "" + count + Calendar.getInstance().get(Calendar.MINUTE) + Calendar.getInstance().get(Calendar.SECOND)
                + mills.charAt(6)
                + mills.charAt(4);
    }

    ProgressDialogEx progressDialogEx;
    private void showProgress(){
        progressDialogEx = ProgressDialogEx.show(RegisterActivity.this, "", getString(R.string.register_ing), true, 30 * 1000, new ProgressDialogEx.OnCancelListener2() {
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
            progressDialogEx = null;
        }
    }

    private LoginListener loginListener = new LoginListener() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(int i, CommUser commUser) {
            dismissProgress();
            if (commUser != null && !TextUtils.isEmpty(commUser.id) && i == ErrorCode.NO_ERROR) {
                mCommUser = commUser;
                CommonUtils.saveLoginUserInfo(RegisterActivity.this, mCommUser);
                finish(true);
            } else {
                switch (i) {
                    case ErrorCode.ERR_CODE_USER_DELETED:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                        break;
                    case ErrorCode.ERR_CODE_USER_NAME_LENGTH_ERROR:
                    case ErrorCode.SENSITIVE_ERR_CODE:
                    case ErrorCode.ERR_CODE_USER_NAME_ILLEGAL_CHAR:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_user_name_illegal_char);
                        break;
                    case ErrorCode.ERR_CODE_DEVICE_FORBIDDEN:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_device_forbiddened);
                        break;
                    case ErrorCode.ERROR_CLOSE_COMMUNITY:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_close_community);
                        CommonUtils.cleanCurrentUserCache(ResFinder.getApplicationContext());
                        break;
                    case ErrorCode.ERR_CODE_USER_NAME_DUPLICATE:
                    case ErrorCode.ERR_CODE_USER_HAVED:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_user_haved);
                        break;
                    case 10034:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_illegal_uid);
                        break;
                    default:
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                        break;
                }
            }
        }
    };

    private Listeners.FetchListener<LoginResponse> loginFetchListener = new Listeners.FetchListener<LoginResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(LoginResponse loginResponse) {
            dismissProgress();
            CommUser loginedUser = loginResponse.result;
            switch (loginResponse.errCode) {
                case ErrorCode.NO_ERROR:
                    if(loginResponse.result != null || !TextUtils.isEmpty(loginedUser.id)) {
                        mCommUser = loginedUser;
                        CommonUtils.saveLoginUserInfo(RegisterActivity.this, mCommUser);
                        finish(true);
                    } else {
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                    }
                    break;
                case ErrorCode.ERR_CODE_USER_DELETED:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                    break;
                case ErrorCode.ERR_CODE_USER_NAME_LENGTH_ERROR:
                case ErrorCode.SENSITIVE_ERR_CODE:
                case ErrorCode.ERR_CODE_USER_NAME_ILLEGAL_CHAR:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_user_name_illegal_char);
                    break;
                case ErrorCode.ERR_CODE_DEVICE_FORBIDDEN:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_device_forbiddened);
                    break;
                case ErrorCode.ERROR_CLOSE_COMMUNITY:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_close_community);
                    CommonUtils.cleanCurrentUserCache(ResFinder.getApplicationContext());
                    break;
                case ErrorCode.ERR_CODE_USER_NAME_DUPLICATE:
                case ErrorCode.ERR_CODE_USER_HAVED:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_user_haved);
                    break;
                case 10034:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_illegal_uid);
                    break;
                default:
                    TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                    break;
            }
        }
    };

    private void finish(boolean register) {
        EventBusUtil.post(Constant.EVENT_BUS_CIRCLE_REGISTER);
        finish();
    }
}
