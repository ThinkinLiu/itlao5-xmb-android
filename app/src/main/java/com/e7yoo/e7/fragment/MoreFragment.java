package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7yoo.e7.GameListActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.app.history.TodayHisActivity;
import com.e7yoo.e7.app.light.FlashLightActivity;
import com.e7yoo.e7.app.news.NewsActivity;
import com.e7yoo.e7.util.ActivityUtil;

public class MoreFragment extends BaseFragment implements View.OnClickListener {
    private View newsLayout, historyLayout, gameLayout, lightLayout, findPhoneLayout;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {

    }

    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_more, container, false);
            newsLayout = mRootView.findViewById(R.id.more_news_layout);
            historyLayout = mRootView.findViewById(R.id.more_history_layout);
            gameLayout = mRootView.findViewById(R.id.more_game_layout);
            lightLayout = mRootView.findViewById(R.id.more_light_layout);
            findPhoneLayout = mRootView.findViewById(R.id.more_findphone_layout);
            newsLayout.setOnClickListener(this);
            historyLayout.setOnClickListener(this);
            gameLayout.setOnClickListener(this);
            lightLayout.setOnClickListener(this);
            findPhoneLayout.setOnClickListener(this);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
            case R.id.more_news_layout:
                ActivityUtil.toActivity(getActivity(), NewsActivity.class);
                break;
            case R.id.more_history_layout:
                ActivityUtil.toActivity(getActivity(), TodayHisActivity.class);
                break;
            case R.id.more_game_layout:
                ActivityUtil.toActivity(getActivity(), GameListActivity.class);
                break;
            case R.id.more_light_layout:
                ActivityUtil.toActivity(getActivity(), FlashLightActivity.class);
                break;
            case R.id.more_findphone_layout:
                break;
        }
    }
}
