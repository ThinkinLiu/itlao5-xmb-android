package com.e7yoo.e7.community;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7yoo.e7.E7App;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */

public class TopicFeedsFragment extends FeedListFragment {
    private Topic mTopic;
    public static TopicFeedsFragment newInstance() {
        TopicFeedsFragment fragment = new TopicFeedsFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null && getArguments().containsKey("Topic")) {
            mTopic = getArguments().getParcelable("Topic");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstLoadData();
    }

    @Override
    protected void loadDataFromNet(boolean isRefresh, String nextPageUrl) {
        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(E7App.mApp);
        if(isRefresh || nextPageUrl == null) {
            mCommSDK.fetchTopicFeed(mTopic.id, mRefreshFetchListener);
        } else {
            mCommSDK.fetchNextPageData(nextPageUrl, FeedsResponse.class, mFetchListener);
        }
    }

    @Override
    protected void loadDataFromDb() {
    }

    @Override
    protected void saveDataToDb(List<FeedItem> feedItems) {
    }

    @Override
    public void onEventMainThread(Message msg) {

    }
}
