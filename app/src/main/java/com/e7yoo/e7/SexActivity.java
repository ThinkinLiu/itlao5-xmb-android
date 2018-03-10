package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.e7yoo.e7.util.Constant;

import static com.e7yoo.e7.util.Constant.INTENT_SHOW_UNKNOW_SEX;

public class SexActivity extends BaseActivity implements View.OnClickListener {
    private RadioGroup mRadioGroup;
    private RadioButton mMaleRadioBtn;
    private RadioButton mFemaleRadioBtn;
    private RadioButton mUnknowRadioBtn;

    @Override
    protected String initTitle() {
        return "";
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_sex;
    }

    @Override
    protected void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.sex_radio_group);
        mMaleRadioBtn = (RadioButton) findViewById(R.id.sex_radio_btn_male);
        mFemaleRadioBtn = (RadioButton) findViewById(R.id.sex_radio_btn_female);
        mUnknowRadioBtn = (RadioButton) findViewById(R.id.sex_radio_btn_unknow);
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
        if(getIntent() != null) {
            int titleResId = getIntent().getIntExtra(Constant.INTENT_TITLE_RES_ID, 0);
            if(titleResId > 0) {
                setTitleTv(titleResId);
            }
            if(getIntent().getBooleanExtra(INTENT_SHOW_UNKNOW_SEX, false)) {
                mUnknowRadioBtn.setVisibility(View.VISIBLE);
            } else {
                mUnknowRadioBtn.setVisibility(View.GONE);
            }
            initSex(getIntent().getIntExtra(Constant.INTENT_SEX, 0));
        }
    }

    @Override
    protected void initViewListener() {
        mMaleRadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        mFemaleRadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        mUnknowRadioBtn.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateDrawable(buttonView, isChecked);
        }
    };

    /**
     * 0保密， 1男 2女
     * @param sex
     */
    private void initSex(int sex) {
        setChecked(mMaleRadioBtn, false);
        setChecked(mFemaleRadioBtn, false);
        setChecked(mUnknowRadioBtn, false);
        switch (sex) {
            case 1:
                setChecked(mMaleRadioBtn, true);
                break;
            case 2:
                setChecked(mFemaleRadioBtn, true);
                break;
            case 0:
            default:
                if(mUnknowRadioBtn.getVisibility() == View.VISIBLE) {
                    setChecked(mUnknowRadioBtn, true);
                } else {
                    setChecked(mMaleRadioBtn, true);
                }
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
                    case R.id.sex_radio_btn_male:
                        finish(1);
                        break;
                    case R.id.sex_radio_btn_female:
                        finish(2);
                        break;
                    case R.id.sex_radio_btn_unknow:
                        finish(0);
                    default:
                        break;
                }
                break;
        }
    }

    private void finish(int sexId) {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_INT, sexId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
