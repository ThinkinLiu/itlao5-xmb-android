package com.e7yoo.e7.app.findphone;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ServiceUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.UmengUtil;
import com.e7yoo.e7.util.WpEventManagerUtil;

public class FindPhoneActivity extends BaseActivity implements OnCheckedChangeListener, OnClickListener {
    private ToggleButton findPhoneSmsBtn;
    private View findPhoneSmsLayout;
    private TextView findPhoneSmsTv;
    private TextView findPhoneSmsTv2;
    private ToggleButton findPhoneLatlngBtn;
    private View findPhoneLatlngLayout;
    private TextView findPhoneLatlngTv;
    private TextView findPhoneLatlngTv2;
    private ToggleButton findPhoneVoiceBtn;
    private View findPhoneVoiceLayout;
    private TextView findPhoneVoiceTv;
    private TextView findPhoneVoiceTv2;
    private View findPhoneShare;

    @Override
    protected String initTitle() {
        return getString(R.string.more_findphone);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_find_phone;
    }

    @Override
    protected void initView() {
        findPhoneSmsBtn = (ToggleButton) findViewById(R.id.tb_find_phone_sms);
        findPhoneSmsLayout = findViewById(R.id.find_phone_sms);
        findPhoneSmsTv = (TextView) findViewById(R.id.find_phone_sms_hint);
        findPhoneSmsTv2 = (TextView) findViewById(R.id.find_phone_sms_text);
        findPhoneLatlngBtn = (ToggleButton) findViewById(R.id.tb_find_phone_latlng);
        findPhoneLatlngLayout = findViewById(R.id.find_phone_latlng);
        findPhoneLatlngTv = (TextView) findViewById(R.id.find_phone_hint_latlng);
        findPhoneLatlngTv2 = (TextView) findViewById(R.id.find_phone_text_latlng);
        findPhoneVoiceBtn = (ToggleButton) findViewById(R.id.tb_find_phone_voice);
        findPhoneVoiceLayout = findViewById(R.id.find_phone_voice);
        findPhoneVoiceTv = (TextView) findViewById(R.id.find_phone_hint_voice);
        findPhoneVoiceTv2 = (TextView) findViewById(R.id.find_phone_text_voice);
        findPhoneShare = findViewById(R.id.share_findphone);
    }

    @Override
    protected void initSettings() {
        boolean sms = PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE, 0) == 1;
        initLayout(findPhoneSmsBtn, findPhoneSmsLayout, sms);
        boolean latlng = PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG, 0) == 1;
        initLayout(findPhoneLatlngBtn, findPhoneLatlngLayout, latlng);
        boolean voice = PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 0) == 1;
        initLayout(findPhoneVoiceBtn, findPhoneVoiceLayout, voice);
    }

    private void initLayout(ToggleButton btn, View layout, boolean checked) {
        btn.setChecked(checked);
        layout.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initViewListener() {
        findPhoneSmsBtn.setOnCheckedChangeListener(this);
        findPhoneSmsLayout.setOnClickListener(this);
        findPhoneLatlngBtn.setOnCheckedChangeListener(this);
        findPhoneLatlngLayout.setOnClickListener(this);
        findPhoneVoiceBtn.setOnCheckedChangeListener(this);
        findPhoneVoiceLayout.setOnClickListener(this);
        findPhoneShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_phone_sms:
                toFindPhoneActivity(0);
                break;
            case R.id.find_phone_latlng:
                toFindPhoneActivity(1);
                break;
            case R.id.find_phone_voice:
                toFindPhoneActivity(2);
                break;
            case R.id.share_findphone:
                ShareDialogUtil.show(this, null, "你还在为找不到手机发愁？", "丢三落四不用怕，【小萌伴】帮我找手机，【小萌伴】语音找手机功能全新改版，可以自定义语音唤醒词了", null);
                UmengUtil.onEvent(UmengUtil.FP_TO_SHARE);
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param toActivity FindPhoneLatlngSetActivity 1;      FindPhoneSetActivity 0
     */
    private void toFindPhoneActivity(int toActivity) {
        if(toActivity == 2) {
            startActivityForResult(new Intent(this, FindPhoneVoiceSetActivity.class), 111);
        } else if(toActivity == 1) {
            startActivityForResult(new Intent(this, FindPhoneLatlngSetActivity.class), 111);
        } else {
            startActivityForResult(new Intent(this, FindPhoneSmsSetActivity.class), 111);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.tb_find_phone_sms:
                if (isChecked) {
                    if (PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT, null) == null) {
                        toFindPhoneActivity(0);
                    }
                    PreferenceUtil.commitInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE, 1);

                    ServiceUtil.startE7Service(this, null, null, new String[]{Constant.PREFERENCE_WAKEUP_KEYWORD}, new int[]{1}, null, null, null, null);
                    findPhoneSmsLayout.setVisibility(View.VISIBLE);
                    UmengUtil.onEvent(UmengUtil.FP_SMS_1);
                } else {
                    PreferenceUtil.commitInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE, 0);

                    ServiceUtil.startE7Service(this, null, null, new String[]{Constant.PREFERENCE_WAKEUP_KEYWORD}, new int[]{0}, null, null, null, null);

                    findPhoneSmsLayout.setVisibility(View.GONE);
                    UmengUtil.onEvent(UmengUtil.FP_SMS_0);
                }
                break;
            case R.id.tb_find_phone_latlng:
                if (isChecked) {
                    if (PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG, null) == null) {
                        toFindPhoneActivity(1);
                    }
                    PreferenceUtil.commitInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG, 1);
                    ServiceUtil.startE7Service(this, null, null, new String[]{Constant.PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG}, new int[]{1}, null, null, null, null);
                    findPhoneLatlngLayout.setVisibility(View.VISIBLE);
                    UmengUtil.onEvent(UmengUtil.FP_LATLNG_1);
                } else {
                    PreferenceUtil.commitInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG, 0);
                    ServiceUtil.startE7Service(this, null, null, new String[]{Constant.PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG}, new int[]{0}, null, null, null, null);
                    findPhoneLatlngLayout.setVisibility(View.GONE);
                    UmengUtil.onEvent(UmengUtil.FP_LATLNG_0);
                }
                break;
            case R.id.tb_find_phone_voice:
                if (isChecked) {
                    CheckPermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, REQUEST_TO_BD_VOICE,
                            R.string.dialog_voice_hint_title, R.string.dialog_voice_hint);
                    PreferenceUtil.commitInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 1);
                    ServiceUtil.startE7Service(this, null, null, new String[]{Constant.PREFERENCE_OPEN_VOICE_FINDPHONE}, new int[]{1}, null, null, null, null);

                    findPhoneVoiceLayout.setVisibility(View.VISIBLE);
                    UmengUtil.onEvent(UmengUtil.FP_VOICE_1);
                } else {
                    PreferenceUtil.commitInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 0);
                    ServiceUtil.startE7Service(this, null, null, new String[]{Constant.PREFERENCE_OPEN_VOICE_FINDPHONE}, new int[]{0}, null, null, null, null);

                    findPhoneVoiceLayout.setVisibility(View.GONE);
                    UmengUtil.onEvent(UmengUtil.FP_VOICE_0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String text = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT, null);
        if (text == null) {
            findPhoneSmsTv.setText(R.string.findphone_sms_set);
        } else {
            findPhoneSmsTv.setText(R.string.findphone_sms_update);
            if (text.length() > 5) {
                findPhoneSmsTv2.setText(text.substring(0, 5) + "...");
            }
        }
        String textLatlng = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG, null);
        if (textLatlng == null) {
            findPhoneLatlngTv.setText(R.string.findphone_latlng_set);
        } else {
            findPhoneLatlngTv.setText(R.string.findphone_latlng_update);
            if (textLatlng.length() > 5) {
                findPhoneLatlngTv2.setText(textLatlng.substring(0, 5) + "...");
            }
        }
        String textVoice = PreferenceUtil.getString(Constant.PREFERENCE_WAKEUP_KEYWORD, null);
        if (textVoice == null) {
            findPhoneVoiceTv.setText(R.string.findphone_voice_update);
            findPhoneVoiceTv2.setText(WpEventManagerUtil.KEYWORDS[8]);
        } else {
            findPhoneVoiceTv.setText(R.string.findphone_voice_update);
            findPhoneVoiceTv2.setText(textVoice);
        }
    }
}
