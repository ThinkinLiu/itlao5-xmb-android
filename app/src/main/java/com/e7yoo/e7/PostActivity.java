package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.TastyToastUtil;

public class PostActivity extends BaseActivity implements View.OnClickListener {
    private EditText inputEt;
    private TextView hintTv;
    private int minLength;

    @Override
    protected String initTitle() {
        return getString(R.string.post);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_post;
    }

    @Override
    protected void initView() {
        inputEt = (EditText) findViewById(R.id.input_edit);
        hintTv = (TextView) findViewById(R.id.input_hint);
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
        if(getIntent() != null) {
            int titleResId = getIntent().getIntExtra(Constant.INTENT_TITLE_RES_ID, 0);
            if(titleResId > 0) {
                setTitleTv(titleResId);
            }
            int maxEms = getIntent().getIntExtra(Constant.INTENT_MAX_LENGTH, 20);
            if(maxEms > 0) {
                inputEt.setMaxEms(maxEms);
            }
            minLength = getIntent().getIntExtra(Constant.INTENT_MIN_LENGTH, 0);
            String value = getIntent().getStringExtra(Constant.INTENT_TEXT);
            if(value != null) {
                inputEt.setText(value);
            }
            String hint = getIntent().getStringExtra(Constant.INTENT_HINT);
            if(hint != null) {
                hintTv.setText(hint);
            }
        }

    }

    @Override
    protected void initViewListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_right_tv:
                String text = inputEt.getText().toString().trim();
                if(text.length() >= minLength) {
                    if(minLength > 0 && getString(R.string.mengmeng).equals(text)) {
                        TastyToastUtil.toast(this, R.string.input_error);
                    } else {
                        finish(text);
                    }
                } else {
                    TastyToastUtil.toast(this, R.string.input_is_empty);
                }
                break;
        }
    }

    private void finish(String text) {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_TEXT, text);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
