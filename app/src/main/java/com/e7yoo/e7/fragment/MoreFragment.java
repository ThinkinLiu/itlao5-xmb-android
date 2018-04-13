package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e7yoo.e7.GameListActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.app.findphone.FindPhoneActivity;
import com.e7yoo.e7.app.history.TodayHisActivity;
import com.e7yoo.e7.app.light.FlashLightActivity;
import com.e7yoo.e7.app.news.NewsActivity;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;

public class MoreFragment extends BaseFragment implements View.OnClickListener {
    private View newsLayout, historyLayout, gameLayout, lightLayout, findPhoneLayout, taobaoLayout;
    private View newsLine;

    private TextView newsTitleTv, gamesTitleTv, appsTitleTv;
    private View newsRootLayout, gamesRootLayout, appsRootLayout;

    private ImageView findPhoneNew;

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
            newsLine = mRootView.findViewById(R.id.more_news_line);
            newsLayout = mRootView.findViewById(R.id.more_news_layout);
            historyLayout = mRootView.findViewById(R.id.more_history_layout);
            gameLayout = mRootView.findViewById(R.id.more_game_layout);
            lightLayout = mRootView.findViewById(R.id.more_light_layout);
            findPhoneLayout = mRootView.findViewById(R.id.more_findphone_layout);
            taobaoLayout = mRootView.findViewById(R.id.more_taobao_layout);
            newsTitleTv = mRootView.findViewById(R.id.news_title_tv);
            gamesTitleTv = mRootView.findViewById(R.id.games_title_tv);
            appsTitleTv = mRootView.findViewById(R.id.apps_title_tv);
            newsRootLayout = mRootView.findViewById(R.id.news_root_layout);
            gamesRootLayout = mRootView.findViewById(R.id.games_root_layout);
            appsRootLayout = mRootView.findViewById(R.id.apps_root_layout);
            findPhoneNew = mRootView.findViewById(R.id.more_findphone_new);
            newsLayout.setOnClickListener(this);
            historyLayout.setOnClickListener(this);
            gameLayout.setOnClickListener(this);
            lightLayout.setOnClickListener(this);
            findPhoneLayout.setOnClickListener(this);
            taobaoLayout.setOnClickListener(this);
        }
        if(CommonUtil.isChannel(getActivity(), "bd", "91", "hiapk")) {
            /*newsLine.setVisibility(View.GONE);
            newsLayout.setVisibility(View.GONE);*/
            newsTitleTv.setVisibility(View.GONE);
            newsRootLayout.setVisibility(View.GONE);
        } else {
            /*newsLine.setVisibility(View.VISIBLE);
            newsLayout.setVisibility(View.VISIBLE);*/
            newsTitleTv.setVisibility(View.VISIBLE);
            newsRootLayout.setVisibility(View.VISIBLE);
        }

        boolean showNew = PreferenceUtil.getBoolean(Constant.PREFERENCE_MORE_POINT_NEW, true);
        if(showNew) {
            findPhoneNew.setVisibility(View.VISIBLE);
        } else {
            findPhoneNew.setVisibility(View.GONE);
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
                ActivityUtil.toActivity(getActivity(), FindPhoneActivity.class);
                if(findPhoneNew.getVisibility() == View.VISIBLE) {
                    PreferenceUtil.commitBoolean(Constant.PREFERENCE_MORE_POINT_NEW, false);
                    ((MainActivity) getActivity()).showMoreNew(false);
                    findPhoneNew.setVisibility(View.GONE);
                }
                break;
            case R.id.more_taobao_layout:
                break;
        }
    }
}
