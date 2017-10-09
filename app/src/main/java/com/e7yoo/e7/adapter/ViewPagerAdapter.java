package com.e7yoo.e7.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.e7yoo.e7.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/25.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList;
    private FragmentManager mFm;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
        super(fm);
        mFm = fm;
        mFragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    @Override
    public Fragment getItem(int position) {
        return getCount() == 0 ? new Fragment() : mFragmentList.get(position % getCount());
    }

}
