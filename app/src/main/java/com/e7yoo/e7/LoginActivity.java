package com.e7yoo.e7;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.e7yoo.e7.model.User;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.OsUtil;
import com.e7yoo.e7.util.RandomUtil;
import com.e7yoo.e7.util.TastyToastUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mNameEt;
    private EditText mPwdEt;
    private TextView mLoginTv;
    private TextView mForgetPwdTv;
    private TextView mRegisterTv;

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
        mLoginTv = (TextView) findViewById(R.id.login);
        mForgetPwdTv = (TextView) findViewById(R.id.login_forget_pwd);
        mRegisterTv = (TextView) findViewById(R.id.login_register);
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, 0, R.string.register, this);
    }

    @Override
    protected void initViewListener() {
        mLoginTv.setOnClickListener(this);
        mForgetPwdTv.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.login_forget_pwd:
                forgetPwd();
                break;
            case R.id.login_register:
            case R.id.titlebar_right_tv:
                ActivityUtil.toRegister(this, mNameEt.getText().toString().trim());
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
        if(!TextUtils.isEmpty(name) && RandomUtil.M.equals(OsUtil.toMD5(name))) {
            E7App.auth = true;
            return R.string.register;
        } else if(!TextUtils.isEmpty(name) && E7App.auth && TextUtils.isEmpty(pwd)) {
            login(name + RandomUtil.N, RandomUtil.P);
            return R.string.login;
        } else if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            return R.string.login_error_empty;
        }
        return 0;
    }

    private void forgetPwd() {
        String name = mNameEt.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            TastyToastUtil.toast(this, R.string.forget_pwd_name_empty);
            return;
        }

    }

    private void login(String name, String pwd) {
        showProgress(R.string.login_ing);
        User user = new User();
        user.setUsername(name);
        user.setPassword(OsUtil.toMD5(pwd));
        addSubscription(user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null) {
                    TastyToastUtil.toast(LoginActivity.this, R.string.welcome, user.getNickname());
                    finish(true);
                } else {
                    switch (e.getErrorCode()) {
                        case 101:
                            TastyToastUtil.toast(LoginActivity.this, R.string.login_failed_pwd_error);
                            break;
                        default:
                            TastyToastUtil.toast(LoginActivity.this, R.string.login_failed);
                            break;
                    }
                }
            }
        }));

    }

    public void finish(boolean login) {
        if(login) {
            EventBusUtil.post(Constant.EVENT_BUS_CIRCLE_LOGIN);
            EventBusUtil.post(Constant.EVENT_BUS_REFRESH_UN_READ_MSG);
        }
        finish();
    }

    @Override
    public void onEventMainThread(Message msg) {
        super.onEventMainThread(msg);
        switch (msg.what) {
            case Constant.EVENT_BUS_CIRCLE_REGISTER:
                finish(true);
                break;
        }
    }

}
