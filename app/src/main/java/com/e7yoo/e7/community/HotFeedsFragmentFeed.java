package com.e7yoo.e7.community;

import android.os.Message;

import com.e7yoo.e7.E7App;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */

public class HotFeedsFragmentFeed extends FeedListFragment {

    public static HotFeedsFragmentFeed newInstance() {
        HotFeedsFragmentFeed fragment = new HotFeedsFragmentFeed();
        return fragment;
    }

    @Override
    protected void loadDataFromNet(boolean isRefresh, String nextPageUrl) {
        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(E7App.mApp);
        if(isRefresh || nextPageUrl == null) {
            // listener-监听器 ranktime-热度时间（1，3，7，30） start-从第几个index开始
            mCommSDK.fetchHotestFeeds(mRefreshFetchListener, 30, 0);
        } else {
            mCommSDK.fetchNextPageData(nextPageUrl, FeedsResponse.class, mFetchListener);
        }
    }

    @Override
    protected void loadDataFromDb() {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        mDatabaseAPI.getFeedDBAPI().loadHotFeeds(30, new Listeners.SimpleFetchListener<List<FeedItem>>() {
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
        mDatabaseAPI.getFeedDBAPI().saveHotFeedToDB(30, feedItems);
    }

    @Override
    public void onEventMainThread(Message msg) {

    }
}
