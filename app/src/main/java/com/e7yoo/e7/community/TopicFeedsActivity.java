package com.e7yoo.e7.community;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.beans.Topic;

/**
 * Created by andy on 2017/10/11.
 */

public class TopicFeedsActivity extends BaseActivity implements View.OnClickListener {
    private FragmentManager manager;
    private Topic mTopic;
    protected ImageView mPostIv;

    @Override
    protected String initTitle() {
        return getString(R.string.circle_topic);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_topic_detail;
    }

    @Override
    protected void initView() {
        mPostIv = (ImageView) findViewById(R.id.circle_post);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("Topic")) {
            mTopic = getIntent().getParcelableExtra("Topic");
        }
        if(mTopic == null || mTopic.id == null) {
            TastyToastUtil.toast(this, R.string.circle_topic_not_exist);
            finish();
            return;
        }
        setTitleTv(mTopic.name.replace("#", ""));
        manager = getSupportFragmentManager();
        TopicFeedsFragment fragment = TopicFeedsFragment.newInstance();
        Bundle args = fragment.getArguments();
        if(args == null) {
            args = new Bundle();
        }
        args.putParcelable("Topic", mTopic);
        fragment.setArguments(args);
        replaceFragment(fragment);
    }

    @Override
    protected void initViewListener() {
        mPostIv.setOnClickListener(this);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.topic_feeds_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_post:
                ActivityUtil.toPostOrLogin(this, mTopic);
                break;
        }
    }
}
