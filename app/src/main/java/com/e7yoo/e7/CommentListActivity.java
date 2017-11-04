package com.e7yoo.e7;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.e7yoo.e7.adapter.CommentListRefreshRecyclerAdapter;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedCommentResponse;

import java.util.List;

public class CommentListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CommentListRefreshRecyclerAdapter mRvAdapter;
    private List<FeedItem> mDatas;

    @Override
    protected String initTitle() {
        return getString(R.string.comment_msg);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_comment_list;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_list_rv);
    }

    @Override
    protected void initSettings() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(getDivider());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new CommentListRefreshRecyclerAdapter(this);
        mRvAdapter.setOnItemClickListener(mOnItemClickListener);
        mRvAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mRvAdapter.refreshData(mDatas);
        mRecyclerView.setAdapter(mRvAdapter);
        loadFriendMsg(true);
    }

    protected RecyclerViewDivider getDivider() {
        return new RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL,
                getResources().getDimensionPixelOffset(R.dimen.space_1dp),
                ContextCompat.getColor(this, R.color.backgroud),
                true,
                getResources().getDimensionPixelOffset(R.dimen.space_3x));
    }

    @Override
    protected void initViewListener() {
        initLoadMoreListener();
    }

    RecyclerAdapter.OnItemClickListener mOnItemClickListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            final FeedItem comment = (FeedItem) mRvAdapter.getItem(position);
            if(comment == null || comment.sourceFeed == null) {
                return;
            }
            ActivityUtil.toFeedDetail(CommentListActivity.this, comment.sourceFeed, comment);
        }
    };

    private String mNextPageUrl = null;
    private void loadFriendMsg(boolean refresh) {
        mRvAdapter.setFooter(CommentListRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        if(refresh || mNextPageUrl == null) {
            E7App.getCommunitySdk().fetchReceivedComments(0, mFirstSimpleFetchListener);
        } else {
            E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, FeedCommentResponse.class, mFetchListener);
        }
    }

    private Listeners.SimpleFetchListener<FeedCommentResponse> mFirstSimpleFetchListener = new Listeners.SimpleFetchListener<FeedCommentResponse>() {
        @Override
        public void onComplete(FeedCommentResponse feedCommentResponse) {
            mRvAdapter.setFooter(CommentListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            if(feedCommentResponse.errCode == ErrorCode.NO_ERROR && feedCommentResponse.result != null
                    && feedCommentResponse.result.size() > 0) {
                mNextPageUrl = feedCommentResponse.nextPageUrl;
                mDatas = feedCommentResponse.result;
                mRvAdapter.refreshData(feedCommentResponse.result);
            }
        }
    };

    private Listeners.FetchListener<FeedCommentResponse> mFetchListener = new Listeners.FetchListener<FeedCommentResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedCommentResponse feedCommentResponse) {
            if(feedCommentResponse.errCode == ErrorCode.NO_ERROR && feedCommentResponse.result != null
                    && feedCommentResponse.result.size() > 0) {
                mNextPageUrl = feedCommentResponse.nextPageUrl;
                mDatas = feedCommentResponse.result;
                mRvAdapter.addItemBottom(feedCommentResponse.result);
                mRvAdapter.setFooter(CommentListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(CommentListRefreshRecyclerAdapter.FooterType.END, R.string.loading_no_more, false);
            }
        }
    };

    RecyclerAdapter.OnItemLongClickListener mOnItemLongClickListener = new RecyclerAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(View view, int position) {
            return false;
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
                    loadFriendMsg(false);
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
                        && mRvAdapter.getFooter() != CommentListRefreshRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != CommentListRefreshRecyclerAdapter.FooterType.NO_MORE;
            }
        });

    }
}
