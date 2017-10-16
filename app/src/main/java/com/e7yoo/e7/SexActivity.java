package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.RobotUtil;
import com.e7yoo.e7.util.TastyToastUtil;

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
    }

    /**
     * 0保密， 1男 2女
     * @param sex
     */
    private void initSex(int sex) {
        mMaleRadioBtn.setChecked(false);
        mFemaleRadioBtn.setChecked(false);
        mUnknowRadioBtn.setChecked(false);
        switch (sex) {
            case 1:
                mMaleRadioBtn.setChecked(true);
                break;
            case 2:
                mFemaleRadioBtn.setChecked(true);
                break;
            case 0:
            default:
                if(mUnknowRadioBtn.getVisibility() == View.VISIBLE) {
                    mUnknowRadioBtn.setChecked(true);
                } else {
                    mMaleRadioBtn.setChecked(true);
                }
                break;
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
