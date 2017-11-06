package com.e7yoo.e7.community;

import android.os.Message;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.util.Constant;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */

public class RealtimeFeedsFragment extends FeedListFragment {

    public static RealtimeFeedsFragment newInstance() {
        RealtimeFeedsFragment fragment = new RealtimeFeedsFragment();
        return fragment;
    }

    @Override
    protected void loadDataFromNet(boolean isRefresh, String nextPageUrl) {
        if(isRefresh || nextPageUrl == null) {
            E7App.getCommunitySdk().fetchRealTimeFeed(mRefreshFetchListener);
        } else {
            E7App.getCommunitySdk().fetchNextPageData(nextPageUrl, FeedsResponse.class, mFetchListener);
        }
    }

    @Override
    protected void loadDataFromDb() {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        mDatabaseAPI.getFeedDBAPI().loadRealTimeFeedsFromDB(false, new Listeners.SimpleFetchListener<List<FeedItem>>() {
            @Override
            public void onComplete(List<FeedItem> feedItems) {
                refreshData(feedItems);
            }
        });
    }

    @Override
    protected void saveDataToDb(List<FeedItem> feedItems) {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        // mDatabaseAPI.getFeedDBAPI().clearRecommendFeed();
        mDatabaseAPI.getFeedDBAPI().saveRealTimeFeedToDB(false, feedItems);
    }

    @Override
    public void onEventMainThread(Message msg) {
        super.onEventMainThread(msg);
        switch (msg.what) {
            case Constant.EVENT_BUS_POST_FEED_SUCCESS:
                if(msg.obj != null && msg.obj instanceof FeedItem) {
                    mRvAdapter.addItemTop(msg.obj);
                }
                break;
        }
    }

}
