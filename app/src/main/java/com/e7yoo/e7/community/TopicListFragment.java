package com.e7yoo.e7.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.view.View;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicResponse;

import java.util.List;

public class TopicListFragment extends ListFragment {

    public static TopicListFragment newInstance() {
        TopicListFragment fragment = new TopicListFragment();
        return fragment;
    }

    @Override
    protected ListRefreshRecyclerAdapter initAdapter() {
        return new TopicRefreshRecyclerAdapter(getActivity());
    }

    @Override
    protected void addListener() {
        mRvAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mRvAdapter.getItem(position) != null && mRvAdapter.getItem(position) instanceof Topic) {
                    if(getActivity() instanceof TopicListActivity) {
                        Intent intent = new Intent();
                        intent.putExtra("Topic", (Topic) mRvAdapter.getItem(position));
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
                        intent.putExtra("Topic", (Topic) mRvAdapter.getItem(position));
                        ActivityUtil.toActivity(getActivity(), intent);
                    }
                }
            }
        });
    }

    @Override
    protected void loadDataFromNet(boolean isRefresh, String nextPageUrl) {
        if(isRefresh || nextPageUrl == null) {
            E7App.getCommunitySdk().fetchTopics(mRefreshTopicListener);
        } else {
            E7App.getCommunitySdk().fetchNextPageData(nextPageUrl, TopicResponse.class, mFetchListener);
        }
    }

    @Override
    protected void loadDataFromDb() {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        mDatabaseAPI.getTopicDBAPI().loadTopicsFromDB(new Listeners.SimpleFetchListener<List<Topic>>() {
            @Override
            public void onComplete(List<Topic> topics) {
                refreshData(topics);
            }
        });
    }

    protected void saveDataToDb(List<Topic> topics) {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        // mDatabaseAPI.getFeedDBAPI().clearRecommendFeed();
        mDatabaseAPI.getTopicDBAPI().deleteAllTopics();
        mDatabaseAPI.getTopicDBAPI().saveTopicsToDB(topics);
    }

    protected void refreshData(List<Topic> topics) {
        if(mDatas == null) {
            mDatas = topics;
            mRvAdapter.refreshData(mDatas);
        }
    }

    protected Listeners.FetchListener mRefreshTopicListener = new Listeners.FetchListener<TopicResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(TopicResponse topicResponse) {
            mSRLayout.setRefreshing(false);
            mRvAdapter.setFooter(FeedItemRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            if(topicResponse != null && topicResponse.result != null) {
                mNextPageUrl = topicResponse.nextPageUrl;
                mDatas = topicResponse.result;
                mRvAdapter.refreshData(mDatas);
                saveDataToDb(mDatas);
            }
        }
    };

    protected Listeners.FetchListener mFetchListener = new Listeners.FetchListener<TopicResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(TopicResponse topicResponse) {
            mSRLayout.setRefreshing(false);
            if(topicResponse != null && topicResponse.result != null && topicResponse.result.size() != 0) {
                mNextPageUrl = topicResponse.nextPageUrl;
                mDatas = topicResponse.result;
                mRvAdapter.addItemBottom(mDatas);
                mRvAdapter.setFooter(TopicRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(TopicRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
            }
        }
    };

    @Override
    public void onEventMainThread(Message msg) {
    }
}
