package com.e7yoo.e7;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.e7yoo.e7.adapter.PushMsgRefreshRecyclerAdapter;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.fragment.BaseFragment;
import com.e7yoo.e7.fragment.CollectListFragment;
import com.e7yoo.e7.fragment.JokeListFragment;
import com.e7yoo.e7.model.JokeType;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.sql.DbThreadPool;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;

import java.util.ArrayList;

public class CollectActivity extends BaseActivity {
    private final ArrayList<BaseFragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected String initTitle() {
        return getString(R.string.mine_collect);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initView() {
        mViewPager = (ViewPager) findViewById(R.id.circle_viewpager);
    }

    @Override
    protected void initSettings() {
        fragments.add(CollectListFragment.newInstance());
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    protected void initViewListener() {
    }

}
