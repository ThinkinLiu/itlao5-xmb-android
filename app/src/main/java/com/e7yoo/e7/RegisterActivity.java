package com.e7yoo.e7;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.e7yoo.e7.model.User;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.OsUtil;
import com.e7yoo.e7.util.RandomUtil;
import com.e7yoo.e7.util.TastyToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
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
        if(E7App.auth && !TextUtils.isEmpty(name)) {
            register(name + RandomUtil.N, RandomUtil.P);
            return R.string.register;
        }
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            return R.string.register_error_empty;
        }
        if(!pwd.equals(pwdTwo)) {
            return R.string.register_pwd_equals_error;
        }
        if(pwd.length() < 0 && pwd.length() > 18) {
            return R.string.register_pwd_length_error;
        }
        //if(!name.contains("@") || !name.contains(".")) {
        if(!matchEmail(name)) {
            return R.string.register_name_error;
        }
        return 0;
    }

    private Matcher matcher;
    private boolean matchEmail(String email) {
        if(matcher == null) {
            Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
            matcher = p.matcher(email);
        }
        return matcher.matches();
    }

    private void register(String name, String pwd) {
        showProgress(R.string.register_ing);
        User user = new User();
        user.setUsername(name);
        user.setNickname(name);
        user.setPassword(OsUtil.toMD5(pwd));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("p", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user.setExtra(jsonObject.toString());
        addSubscription(user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e == null) {
                    TastyToastUtil.toast(RegisterActivity.this, R.string.welcome, user.getNickname());
                    finish(true);
                } else {
                    if(e.getErrorCode() == 202) {
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                    } else {
                        TastyToastUtil.toast(RegisterActivity.this, R.string.register_failed);
                    }
                }
            }
        }));
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

    private void finish(boolean register) {
        if(register) {
            EventBusUtil.post(Constant.EVENT_BUS_CIRCLE_REGISTER);
        }
        finish();
    }
}
