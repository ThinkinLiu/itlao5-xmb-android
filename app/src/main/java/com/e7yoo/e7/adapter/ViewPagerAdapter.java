package com.e7yoo.e7.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.e7yoo.e7.fragment.BaseFragment;
import com.e7yoo.e7.fragment.CircleFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/25.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BaseFragment> mFragmentList;
    private FragmentManager mFm;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> fragmentList) {
        super(fm);
        mFm = fm;
        mFragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    @Override
    public BaseFragment getItem(int position) {
        return getCount() == 0 ? CircleFragment.newInstance() : mFragmentList.get(position % getCount());
    }

}
