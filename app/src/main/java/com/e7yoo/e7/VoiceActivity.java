package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.e7yoo.e7.util.Constant;

import static com.e7yoo.e7.util.Constant.INTENT_SHOW_UNKNOW_SEX;

public class VoiceActivity extends BaseActivity implements View.OnClickListener {
    private RadioGroup mRadioGroup;
    private RadioButton mFemaleRadioBtn;
    private RadioButton mMaleRadioBtn;
    private RadioButton mMale1RadioBtn;
    private RadioButton mMale2RadioBtn;
    private RadioButton mChildrenRadioBtn;

    @Override
    protected String initTitle() {
        return "";
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_voice;
    }

    @Override
    protected void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.voice_radio_group);
        mFemaleRadioBtn = (RadioButton) findViewById(R.id.voice_radio_btn_female);
        mMaleRadioBtn = (RadioButton) findViewById(R.id.voice_radio_btn_male);
        mMale1RadioBtn = (RadioButton) findViewById(R.id.voice_radio_btn_male1);
        mMale2RadioBtn = (RadioButton) findViewById(R.id.voice_radio_btn_male2);
        mChildrenRadioBtn = (RadioButton) findViewById(R.id.voice_radio_btn_children);
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
        if(getIntent() != null) {
            int titleResId = getIntent().getIntExtra(Constant.INTENT_TITLE_RES_ID, 0);
            if(titleResId > 0) {
                setTitleTv(titleResId);
            }
            initVoice(getIntent().getIntExtra(Constant.INTENT_VOICE, 4));
        }
    }

    @Override
    protected void initViewListener() {
    }

    /**
     * 0女声， 1男声 2特别男声 3情感男声 4童声
     * @param sex
     */
    private void initVoice(int sex) {
        mFemaleRadioBtn.setChecked(false);
        mMaleRadioBtn.setChecked(false);
        mMale1RadioBtn.setChecked(false);
        mMale1RadioBtn.setChecked(false);
        mChildrenRadioBtn.setChecked(false);
        switch (sex) {
            case 0:
                mFemaleRadioBtn.setChecked(true);
                break;
            case 1:
                mMaleRadioBtn.setChecked(true);
                break;
            case 2:
                mMale1RadioBtn.setChecked(true);
                break;
            case 3:
                mMale2RadioBtn.setChecked(true);
                break;
            case 4:
            default:
                mChildrenRadioBtn.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_right_tv:
                int id = mRadioGroup.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.voice_radio_btn_female:
                        finish(0);
                        break;
                    case R.id.voice_radio_btn_male:
                        finish(1);
                        break;
                    case R.id.voice_radio_btn_male1:
                        finish(2);
                        break;
                    case R.id.voice_radio_btn_male2:
                        finish(3);
                        break;
                    case R.id.voice_radio_btn_children:
                        finish(4);
                    default:
                        break;
                }
                break;
        }
    }

    private void finish(int voiceId) {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_INT, voiceId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
