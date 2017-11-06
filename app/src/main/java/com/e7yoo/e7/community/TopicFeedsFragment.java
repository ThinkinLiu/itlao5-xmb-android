package com.e7yoo.e7.community;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7yoo.e7.E7App;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
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
        if(isRefresh || nextPageUrl == null) {
            E7App.getCommunitySdk().fetchTopicFeed(mTopic.id, mRefreshFetchListener);
        } else {
            E7App.getCommunitySdk().fetchNextPageData(nextPageUrl, FeedsResponse.class, mFetchListener);
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
