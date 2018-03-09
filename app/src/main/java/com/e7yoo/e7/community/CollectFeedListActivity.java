package com.e7yoo.e7.community;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;

/**
 * Created by andy on 2017/10/11.
 */

public class CollectFeedListActivity extends BaseActivity {
    private FragmentManager manager;
    private CollectFeedsFragment fragment;

    @Override
    protected String initTitle() {
        return getString(R.string.title_circle_collect);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_collect_feed_list;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initSettings() {
        manager = getSupportFragmentManager();
        fragment = CollectFeedsFragment.newInstance();
        /*Bundle args = fragment.getArguments();
        if(args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);*/
        replaceFragment(fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fragment != null) {
            fragment.firstLoadData();
        }
    }

    @Override
    protected void initViewListener() {

    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.collect_feed_list_layout, fragment);
        transaction.commit();
    }
}
