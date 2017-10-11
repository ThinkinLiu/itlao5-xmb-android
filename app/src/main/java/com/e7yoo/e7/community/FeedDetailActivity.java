package com.e7yoo.e7.community;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.CommentAPI;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.CommentResponse;
import com.umeng.comm.core.nets.responses.FeedCommentResponse;
import com.umeng.comm.core.nets.responses.FeedItemResponse;

import java.util.List;

/**
 * Created by andy on 2017/10/11.
 */

public class FeedDetailActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private FeedDetailRecyclerAdapter mRvAdapter;
    private FeedItem mFeedItem;
    private List<Comment> mComments;
    private String mNextPageUrl;
    private CommunitySDK communitySDK;

    @Override
    protected String initTitle() {
        return getString(R.string.circle_feed_detail);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_feed_detail;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.feed_detail_rv);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("FeedItem")) {
            mFeedItem = getIntent().getParcelableExtra("FeedItem");
        }
        if(mFeedItem == null || mFeedItem.id == null) {
            TastyToastUtil.toast(this, R.id.circle_feed_not_exist);
            return;
        }
        mRvAdapter = new FeedDetailRecyclerAdapter(this);
        mRvAdapter.addItemBottom(mFeedItem);
        mRvAdapter.addItemBottom(mFeedItem.comments);
        mRecyclerView.setAdapter(mRvAdapter);
        communitySDK = CommunityFactory.getCommSDK(this);
        communitySDK.fetchFeedWithId(mFeedItem.id, mFetchListener);
        communitySDK.fetchFeedComments(mFeedItem.id, mSimpleFetchListener);
    }

    @Override
    protected void initViewListener() {

    }

    private Listeners.FetchListener<FeedItemResponse> mFetchListener = new Listeners.FetchListener<FeedItemResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedItemResponse feedItemResponse) {
            if(feedItemResponse != null && feedItemResponse.result != null) {
                mNextPageUrl = feedItemResponse.nextPageUrl;
                if(mFeedItem == null) {
                    mRvAdapter.refreshFeedItem(feedItemResponse.result);
                } else {
                    mRvAdapter.addItemBottom(feedItemResponse.result);
                }
                mFeedItem = feedItemResponse.result;
            }
        }
    };

    private Listeners.SimpleFetchListener<CommentResponse> mSimpleFetchListener = new Listeners.SimpleFetchListener<CommentResponse>() {
        @Override
        public void onComplete(CommentResponse commentResponse) {
            if(commentResponse != null && commentResponse.result != null) {
                mNextPageUrl = commentResponse.nextPageUrl;
                mComments = commentResponse.result;
                mRvAdapter.refreshComments(commentResponse.result);
            }
        }
    };

    private Listeners.FetchListener mCommentFetchListener = new Listeners.FetchListener<CommentResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(CommentResponse commentResponse) {
            if(commentResponse != null && commentResponse.result != null) {
                mNextPageUrl = commentResponse.nextPageUrl;
                mComments = commentResponse.result;
                mRvAdapter.addItemBottom(commentResponse.result);
            }
        }
    };

    private void initLoadMoreListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (isNeedLoadMore(newState)) {
                    mRvAdapter.setFooter(FeedItemRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    communitySDK.fetchNextPageData(mNextPageUrl, CommentResponse.class, mCommentFetchListener);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }

            private boolean isNeedLoadMore(int newState) {
                return !isFinishing() && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRvAdapter.getItemCount()
                        && mRvAdapter.getFooter() != FeedItemRefreshRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != FeedItemRefreshRecyclerAdapter.FooterType.NO_MORE;
            }
        });

    }


}
