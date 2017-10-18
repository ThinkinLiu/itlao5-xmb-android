package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.community.FollowedFeedsFragment;
import com.e7yoo.e7.community.HotFeedsFragmentFeed;
import com.e7yoo.e7.community.RealtimeFeedsFragment;
import com.e7yoo.e7.community.RecomFeedsFragment;
import com.e7yoo.e7.community.TopicListFragment;
import com.e7yoo.e7.util.ActivityUtil;
import com.umeng.comm.core.utils.CommonUtils;

import java.util.ArrayList;

public class CircleFragment extends BaseFragment implements View.OnClickListener {

    private TextView mAllTv, mRecomTv, mHotTv, mFollowedTv, mPlateTv;
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    protected ImageView mPostIv;

    public CircleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {

    }

    public static CircleFragment newInstance() {
        CircleFragment fragment = new CircleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_circle, container, false);
            mAllTv = mRootView.findViewById(R.id.circle_all);
            mRecomTv = mRootView.findViewById(R.id.circle_recom);
            mHotTv = mRootView.findViewById(R.id.circle_hot);
            mFollowedTv = mRootView.findViewById(R.id.circle_followed);
            mPlateTv = mRootView.findViewById(R.id.circle_plate);
            mViewPager = mRootView.findViewById(R.id.circle_viewpager);
            mPostIv = mRootView.findViewById(R.id.circle_post);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(mViewPagerAdapter == null) {
            fragments.add(RealtimeFeedsFragment.newInstance());
            fragments.add(RecomFeedsFragment.newInstance());
            fragments.add(HotFeedsFragmentFeed.newInstance());
            fragments.add(FollowedFeedsFragment.newInstance());
            fragments.add(TopicListFragment.newInstance());
            mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
            setSelectedTv(mAllTv);
        }
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mAllTv.setOnClickListener(this);
        mRecomTv.setOnClickListener(this);
        mHotTv.setOnClickListener(this);
        mFollowedTv.setOnClickListener(this);
        mPlateTv.setOnClickListener(this);
        mPostIv.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isFirstShow = true;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser && isFirstShow) {
            isFirstShow = false;
            mViewPager.setAdapter(mViewPagerAdapter);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circle_all:
                setSelectedTv(mAllTv);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.circle_recom:
                setSelectedTv(mRecomTv);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.circle_hot:
                setSelectedTv(mHotTv);
                mViewPager.setCurrentItem(2);
                break;
            case R.id.circle_followed:
                setSelectedTv(mFollowedTv);
                mViewPager.setCurrentItem(3);
                break;
            case R.id.circle_plate:
                setSelectedTv(mPlateTv);
                mViewPager.setCurrentItem(4);
                break;
            case R.id.circle_post:
                if(getActivity() != null) {
                    ActivityUtil.toPostOrLogin(getActivity(), null);
                }
                break;
        }
    }


    public void setPostIvVisibility(int visibility) {
        if(mPostIv != null) {
            mPostIv.setVisibility(visibility);
        }
    }

    private void setSelectedTv(TextView view) {
        mAllTv.setSelected(view == mAllTv ? true : false);
        mRecomTv.setSelected(view == mRecomTv ? true : false);
        mHotTv.setSelected(view == mHotTv ? true : false);
        mFollowedTv.setSelected(view == mFollowedTv ? true : false);
        mPlateTv.setSelected(view == mPlateTv ? true : false);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 1:
                    setSelectedTv(mRecomTv);
                    break;
                case 2:
                    setSelectedTv(mHotTv);
                    break;
                case 3:
                    setSelectedTv(mFollowedTv);
                    break;
                case 4:
                    setSelectedTv(mPlateTv);
                    break;
                case 0:
                default:
                    setSelectedTv(mAllTv);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
