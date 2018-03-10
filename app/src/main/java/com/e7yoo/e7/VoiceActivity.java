package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.e7yoo.e7.util.Constant;

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
        mFemaleRadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        mMaleRadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        mMale1RadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        mMale2RadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        mChildrenRadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateDrawable(buttonView, isChecked);
        }
    };

    /**
     * 0女声， 1男声 2特别男声 3情感男声 4童声
     * @param voice 声音类型
     */
    private void initVoice(int voice) {
        setChecked(mFemaleRadioBtn, false);
        setChecked(mMaleRadioBtn, false);
        setChecked(mMale1RadioBtn, false);
        setChecked(mMale2RadioBtn, false);
        setChecked(mChildrenRadioBtn, false);
        switch (voice) {
            case 0:
                setChecked(mFemaleRadioBtn, true);
                break;
            case 1:
                setChecked(mMaleRadioBtn, true);
                break;
            case 2:
                setChecked(mMale1RadioBtn, true);
                break;
            case 3:
                setChecked(mMale2RadioBtn, true);
                break;
            case 4:
            default:
                setChecked(mChildrenRadioBtn, true);
                break;
        }
    }

    private void setChecked(CompoundButton radioButton, boolean checked) {
        radioButton.setChecked(checked);
        updateDrawable(radioButton, checked);
    }

    private void updateDrawable(CompoundButton radioButton, boolean checked) {
        if(checked) {
            radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
        } else {
            radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_unselected, 0, 0, 0);
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
