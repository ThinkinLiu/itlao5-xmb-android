package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.model.JokeType;

import java.util.ArrayList;

public class CircleFragment extends BaseFragment implements View.OnClickListener {

    private TextView mJokeTv, mPicTv;
    private final ArrayList<BaseFragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    public CircleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {
        for(BaseFragment fragment : fragments) {
            fragment.onEventMainThread(msg);
        }
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
            mJokeTv = mRootView.findViewById(R.id.circle_top_joke);
            mPicTv = mRootView.findViewById(R.id.circle_top_pic);
            mViewPager = mRootView.findViewById(R.id.circle_viewpager);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(mViewPagerAdapter == null) {
            fragments.add(JokeListFragment.newInstance().setJokeType(JokeType.JOKE));
            fragments.add(JokeListFragment.newInstance().setJokeType(JokeType.PIC));
            mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
            setSelectedTv(mJokeTv);
        }
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mJokeTv.setOnClickListener(this);
        mPicTv.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isFirstShow = true;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser && isFirstShow && mViewPager != null) {
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
            case R.id.circle_top_joke:
                setSelectedTv(mJokeTv);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.circle_top_pic:
                setSelectedTv(mPicTv);
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    private void setSelectedTv(TextView view) {
        mJokeTv.setSelected(view == mJokeTv ? true : false);
        mPicTv.setSelected(view == mPicTv ? true : false);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    setSelectedTv(mJokeTv);
                    break;
                case 1:
                    setSelectedTv(mPicTv);
                    break;
                default:
                    setSelectedTv(mJokeTv);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
