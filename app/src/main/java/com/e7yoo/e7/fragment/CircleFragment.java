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
import android.widget.TextView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.ViewPagerAdapter;

import java.util.ArrayList;

public class CircleFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTopicTv, mHotTv, mPlateTv;
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

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
            mHotTv = mRootView.findViewById(R.id.circle_hot);
            mTopicTv = mRootView.findViewById(R.id.circle_topic);
            mPlateTv = mRootView.findViewById(R.id.circle_plate);
            mViewPager = mRootView.findViewById(R.id.circle_viewpager);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(mViewPagerAdapter == null) {
            fragments.add(TopicFragment.newInstance());
            fragments.add(TopicFragment.newInstance());
            fragments.add(TopicFragment.newInstance());
            mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
        }
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        setSelectedTv(mTopicTv);
                        break;
                    case 2:
                        setSelectedTv(mPlateTv);
                        break;
                    case 0:
                    default:
                        setSelectedTv(mHotTv);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mHotTv.setOnClickListener(this);
        mTopicTv.setOnClickListener(this);
        mPlateTv.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
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
            case R.id.circle_hot:
                setSelectedTv(mHotTv);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.circle_topic:
                setSelectedTv(mTopicTv);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.circle_plate:
                setSelectedTv(mPlateTv);
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    private void setSelectedTv(TextView view) {
        mHotTv.setSelected(view == mHotTv ? true : false);
        mTopicTv.setSelected(view == mTopicTv ? true : false);
        mPlateTv.setSelected(view == mPlateTv ? true : false);
    }
}
