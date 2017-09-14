package com.e7yoo.e7.fragment;


import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e7yoo.e7.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    protected View mRootView;


    public BaseFragment() {
        // Required empty public constructor
    }

    public abstract void onEventMainThread(Message msg);

}
