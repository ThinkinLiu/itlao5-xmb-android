package com.e7yoo.e7;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.community.UserListFragment;
import com.e7yoo.e7.fragment.BaseFragment;

import java.util.ArrayList;

/**
 *
 */
public class FriendActivity extends BaseActivity implements View.OnClickListener {
    private TextView mRecommendedTv, mAttentionTv, mFansTv;
    /**
     * 关注，粉丝
     */
    private final ArrayList<BaseFragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_friend;
    }

    @Override
    protected String initTitle() {
        return getString(R.string.title_user_recommended);
    }

    @Override
    protected void initView() {
        mRecommendedTv = (TextView) findViewById(R.id.friend_top_recommended);
        mAttentionTv = (TextView) findViewById(R.id.friend_top_attention);
        mFansTv = (TextView) findViewById(R.id.friend_top_fans);
        mViewPager = (ViewPager) findViewById(R.id.friend_viewpager);
    }

    @Override
    protected void initSettings() {
        fragments.add(getUserListFragment(UserListFragment.FLAG_RECOMMENDED));
        fragments.add(getUserListFragment(UserListFragment.FLAG_ATTENTION));
        fragments.add(getUserListFragment(UserListFragment.FLAG_FANS));
        setSelectedTv(mRecommendedTv, R.string.title_user_recommended);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
    }

    public UserListFragment getUserListFragment(int flag) {
        UserListFragment userListFragment = UserListFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("flag", flag);
        userListFragment.setArguments(bundle);
        return userListFragment;
    }

    @Override
    protected void initViewListener() {
        mRecommendedTv.setOnClickListener(this);
        mAttentionTv.setOnClickListener(this);
        mFansTv.setOnClickListener(this);
        initViewPager();
    }

    /**
     */
    private void initViewPager() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        setSelectedTv(mAttentionTv, R.string.title_user_recommended);
                        break;
                    case 2:
                        setSelectedTv(mFansTv, R.string.title_user_attention);
                        break;
                    case 0:
                    default:
                        setSelectedTv(mRecommendedTv, R.string.title_user_fans);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_top_recommended:
                setSelectedTv(mAttentionTv, R.string.title_user_recommended);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.friend_top_attention:
                setSelectedTv(mAttentionTv, R.string.title_user_attention);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.friend_top_fans:
                setSelectedTv(mFansTv, R.string.title_user_fans);
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    private void setSelectedTv(TextView view, int titleResId) {
        mRecommendedTv.setSelected(view == mRecommendedTv ? true : false);
        mAttentionTv.setSelected(view == mAttentionTv ? true : false);
        mFansTv.setSelected(view == mFansTv ? true : false);
        setTitle(titleResId);
    }
}
