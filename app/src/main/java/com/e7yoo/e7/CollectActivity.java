package com.e7yoo.e7;

import android.support.v4.view.ViewPager;

import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.fragment.BaseFragment;
import com.e7yoo.e7.fragment.CollectListFragment;

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
        mViewPager = (ViewPager) findViewById(R.id.collect_viewpager);
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
