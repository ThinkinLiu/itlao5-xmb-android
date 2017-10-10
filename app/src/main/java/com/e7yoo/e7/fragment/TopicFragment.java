package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.community.FeedItemRefreshRecyclerAdapter;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedItemResponse;
import com.umeng.comm.core.nets.responses.FeedsResponse;

import java.util.List;

public class TopicFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private List<FeedItem> mDatas;
    private FeedItemRefreshRecyclerAdapter mRvAdapter;
    private String mNextPageUrl;

    public TopicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {

    }

    public static TopicFragment newInstance() {
        TopicFragment fragment = new TopicFragment();
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
            mRootView = inflater.inflate(R.layout.fragment_topic, container, false);
            mRecyclerView = mRootView.findViewById(R.id.topic_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mRecyclerView.addItemDecoration(new RecyclerViewDivider(
                    getContext(), LinearLayoutManager.VERTICAL,
                    getResources().getDimensionPixelOffset(R.dimen.item_robot_divider),
                    ContextCompat.getColor(getContext(), R.color.backgroud)));
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(mRvAdapter == null) {
            mRvAdapter = new FeedItemRefreshRecyclerAdapter(getContext());
        }
        mRecyclerView.setAdapter(mRvAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isFirstShow = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirstShow) {
            loadMore();
            isFirstShow = false;
        }
    }

    private void loadMore() {
        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(E7App.mApp);
        if(mNextPageUrl == null) {
            mCommSDK.fetchRecommendedFeeds(mFetchListener);
        } else {
            mCommSDK.fetchNextPageData(mNextPageUrl, FeedItemResponse.class, mFetchListener);
        }
    }

    private Listeners.FetchListener mFetchListener = new Listeners.FetchListener<FeedsResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedsResponse feedsResponse) {
            if(feedsResponse != null && feedsResponse.result != null &&
                    (mNextPageUrl == null || mNextPageUrl.equals(feedsResponse.nextPageUrl))) {
                mNextPageUrl = feedsResponse.nextPageUrl;
                mDatas = feedsResponse.result;
                mRvAdapter.addItemBottom(mDatas);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
