package com.e7yoo.e7;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;

import cn.jpush.android.api.JPushInterface;

public class SettingsActivity extends BaseActivity implements OnCheckedChangeListener, OnClickListener {
    private View feedbackLayout;
    private ToggleButton disturbBtn;
    private ToggleButton ttsBtn;
    private TextView feedbackUnreadTv;

    @Override
    protected String initTitle() {
        return getString(R.string.mine_set);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        disturbBtn = (ToggleButton) findViewById(R.id.settings_tb_disturb);
        ttsBtn = (ToggleButton) findViewById(R.id.settings_tb_tts);
        feedbackLayout = findViewById(R.id.settings_feedback_layout);
        feedbackUnreadTv = (TextView) findViewById(R.id.settings_feedback_unread);
    }

    @Override
    protected void initSettings() {
        initPushDisturb();
        initTtsSet();
        initFeedBackCount();
    }

    @Override
    protected void initViewListener() {
        disturbBtn.setOnCheckedChangeListener(this);
        ttsBtn.setOnCheckedChangeListener(this);
        feedbackLayout.setOnClickListener(this);
    }

    private void initPushDisturb() {
        int disturb = PreferenceUtil.getInt(Constant.PREFERENCE_REPLY_PUSH_DISTURB, 0);
        if(disturb == 1) {
            disturbBtn.setChecked(true);
        } else {
            disturbBtn.setChecked(false);
        }
    }

    private void initFeedBackCount() {
        /*FeedbackAPI.getFeedbackUnreadCount(new IUnreadCountCallback() {
            @Override
            public void onSuccess(final int arg0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(feedbackUnreadTv != null) {
                            if (arg0 <= 0) {
                                feedbackUnreadTv.setText("");
                            } else {
                                feedbackUnreadTv.setText(arg0 + "");
                            }
                        }
                    }
                });
            }
            @Override
            public void onError(int arg0, String arg1) {
            }
        });*/
    }

    private void initTtsSet() {
        int ttsType = PreferenceUtil.getInt(Constant.PREFERENCE_REPLY_TTS_TYPE, 0);
        switch (ttsType) {
            case 0:
                ttsBtn.setChecked(false);
                break;
            case 1:
                ttsBtn.setChecked(true);
                break;
            case 2:
                ttsBtn.setChecked(true);
                break;
            case 3:
                ttsBtn.setChecked(true);
                break;
            default:
                ttsBtn.setChecked(false);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_feedback_layout:
                // 1.0
                // FeedbackAPI.openFeedbackActivity(this);
                // 2.0
                // FeedbackAPI.openFeedbackActivity();
                feedbackUnreadTv.setText("");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.settings_tb_disturb:
                if (isChecked) {
                    JPushInterface.setSilenceTime(this, 0, 0, 23, 59);
                    PreferenceUtil.commitInt(Constant.PREFERENCE_REPLY_PUSH_DISTURB, 1);
                } else {
                    JPushInterface.setSilenceTime(this, 22, 30, 6, 30);
                    PreferenceUtil.commitInt(Constant.PREFERENCE_REPLY_PUSH_DISTURB, 0);
                }
                break;
            case R.id.settings_tb_tts:
                if (isChecked) {
                    PreferenceUtil.commitInt(Constant.PREFERENCE_REPLY_TTS_TYPE, 1);
                } else {
                    PreferenceUtil.commitInt(Constant.PREFERENCE_REPLY_TTS_TYPE, 0);
                }
                break;
        }
    }

}
