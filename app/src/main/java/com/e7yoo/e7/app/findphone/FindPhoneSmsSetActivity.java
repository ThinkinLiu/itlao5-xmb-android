package com.e7yoo.e7.app.findphone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.TastyToastUtil;

public class FindPhoneSmsSetActivity extends BaseActivity implements OnClickListener {
    private EditText et;

    @Override
    protected String initTitle() {
        return getString(R.string.more_findphone);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_find_phone_sms_set;
    }

    @Override
    protected void initView() {
        et = (EditText) findViewById(R.id.input_content_et);
    }

    @Override
    protected void initSettings() {
        CheckPermissionUtil.checkPermission(this, Manifest.permission.RECEIVE_SMS, REQUEST_TO_SMS,
                R.string.dialog_sms_hint_title, R.string.dialog_sms_hint);
        String text = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT, null);
        if(text != null) {
            et.setHint(text);
        }
    }

    @Override
    protected void initViewListener() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_right_tv:
                String text = et.getText().toString().trim();
                if(text.length() < 6 || text.length() > 20) {
                    TastyToastUtil.toast(this, R.string.toast_set_find_phone_sms_length);
                    return;
                }
                String latlngPwd = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG, null);
                if(latlngPwd != null && latlngPwd.equals(text)) {
                    TastyToastUtil.toast(this, R.string.toast_set_find_phone_sms_error);
                    return;
                }
                PreferenceUtil.commitString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT, text);
                finish(text);
                break;
            default:
                break;
        }
    }

    private void finish(String text) {
        Intent intent = new Intent();
        intent.putExtra("text", text);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
