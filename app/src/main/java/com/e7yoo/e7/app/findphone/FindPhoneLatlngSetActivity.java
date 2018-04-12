package com.e7yoo.e7.app.findphone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.TastyToastUtil;

import java.util.ArrayList;

public class FindPhoneLatlngSetActivity extends BaseActivity implements OnClickListener {
    private EditText et;

    @Override
    protected String initTitle() {
        return getString(R.string.more_findphone);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_find_phone_latlng_set;
    }

    @Override
    protected void initView() {
        et = (EditText) findViewById(R.id.input_content_et);
    }

    @Override
    protected void initSettings() {
        initPermission();
        String textLatlng = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG, null);
        if (textLatlng != null) {
            et.setHint(textLatlng);
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
                if (text.length() < 6 || text.length() > 20) {
                    TastyToastUtil.toast(this, R.string.toast_set_find_phone_latlng_length);
                    return;
                }
                String voicePwd = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT, null);
                if (voicePwd != null && voicePwd.equals(text)) {
                    TastyToastUtil.toast(this, R.string.toast_set_find_phone_latlng_error);
                    return;
                }
                PreferenceUtil.commitString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG, text);
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


    String PERMISSIONS[] = {Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            /*该权限无法弹出框口进行提醒*/Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (permissions == null || grantResults == null || permissions.length != grantResults.length) {
                    return;
                }
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if(permission != null && grantResults[i] != PackageManager.PERMISSION_GRANTED
                            && !permission.equals(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
                        CheckPermissionUtil.AskForPermission(FindPhoneLatlngSetActivity.this, R.string.dialog_latlng_hint_title, R.string.dialog_latlng_hint);
                        return;
                    }
                }
                break;
        }
    }
}
