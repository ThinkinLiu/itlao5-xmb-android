package com.e7yoo.e7.community;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.CommentResponse;
import com.umeng.comm.core.nets.responses.FeedItemResponse;
import com.umeng.comm.core.nets.responses.PostCommentResponse;
import com.umeng.comm.core.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/10/11.
 */

public class FeedDetailActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private FeedDetailRecyclerAdapter mRvAdapter;
    private FeedItem mFeedItem;
    private List<Comment> mComments;
    private String mNextPageUrl;

    private EditText mReplyEt;
    private ImageView mReplyIv;

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
        mReplyEt = (EditText) findViewById(R.id.feed_detail_input_edit);
        mReplyIv = (ImageView) findViewById(R.id.feed_detail_input_send);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("FeedItem")) {
            mFeedItem = getIntent().getParcelableExtra("FeedItem");
        }
        if(mFeedItem == null || mFeedItem.id == null) {
            TastyToastUtil.toast(this, R.string.circle_feed_not_exist);
            finish();
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(getDivider());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new FeedDetailRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRvAdapter);

        mRvAdapter.refreshData(mFeedItem, mFeedItem.comments);

        E7App.getCommunitySdk().fetchFeedWithId(mFeedItem.id, mFetchListener);
        mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        E7App.getCommunitySdk().fetchFeedComments(mFeedItem.id, mSimpleFetchListener);
    }

    private RecyclerViewDivider getDivider() {
        RecyclerViewDivider divider = new RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL,
                0,
                ContextCompat.getColor(this, R.color.backgroud),
                true,
                getResources().getDimensionPixelOffset(R.dimen.space_3x),
                true,
                getResources().getDimensionPixelOffset(R.dimen.item_robot_divider));
        return divider;
    }

    @Override
    protected void initViewListener() {
        initLoadMoreListener();
        mReplyIv.setOnClickListener(this);
    }

    private Listeners.FetchListener<FeedItemResponse> mFetchListener = new Listeners.FetchListener<FeedItemResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedItemResponse feedItemResponse) {
            if(feedItemResponse != null && feedItemResponse.result != null && TextUtils.isEmpty(feedItemResponse.result.id)) {
                mRvAdapter.refreshFeedItem(feedItemResponse.result);
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
                if(commentResponse.result.size() > 0) {
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
                } else {
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more_comment, false);
                }
            } else {
                mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more_comment, false);
            }
        }
    };

    private Listeners.FetchListener mCommentFetchListener = new Listeners.FetchListener<CommentResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(CommentResponse commentResponse) {
            if(commentResponse != null && commentResponse.result != null && commentResponse.result.size() > 0) {
                mNextPageUrl = commentResponse.nextPageUrl;
                mComments = commentResponse.result;
                mRvAdapter.addItemBottom(commentResponse.result);
                mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more_comment, false);
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
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    if(mNextPageUrl == null) {
                        E7App.getCommunitySdk().fetchFeedComments(mFeedItem.id, mSimpleFetchListener);
                    } else {
                        E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, CommentResponse.class, mCommentFetchListener);
                    }
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
                        && mRvAdapter.getFooter() != FeedDetailRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != FeedDetailRecyclerAdapter.FooterType.NO_MORE;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_detail_input_send:
                String text = mReplyEt.getText().toString().trim();
                if(text.length() == 0) {
                    TastyToastUtil.toast(this, R.string.circle_feed_detail_reply_empty);
                } else {
                    if(Net.isNetWorkConnected(this)) {
                        if(CommonUtils.isLogin(this)) {
                            reply(text);
                        } else {
                            ActivityUtil.toLogin(this);
                        }
                    } else {
                        TastyToastUtil.toast(this, R.string.net_no);
                    }
                }
                break;
        }
    }

    private void reply(String text) {
        showProgress(R.string.feed_detail_reply_ing);
        Comment comment = new Comment();
        // comment.replyCommentId = null;
        // comment.replyUser = null;
        comment.creator = CommConfig.getConfig().loginedUser;
        comment.feedId = mFeedItem.id;
        comment.text = text;
        // comment.imageUrls = new ArrayList<>();
        E7App.getCommunitySdk().postCommentforResult(comment, new Listeners.FetchListener<PostCommentResponse>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(PostCommentResponse postCommentResponse) {
                if(postCommentResponse.errCode == ErrorCode.NO_ERROR && postCommentResponse.getComment() != null && !TextUtils.isEmpty(postCommentResponse.getComment().id)) {
                    mRvAdapter.addComment(postCommentResponse.getComment());
                } else {
                    TastyToastUtil.toast(FeedDetailActivity.this, R.string.feed_detail_reply_failed);
                }
                dismissProgress();
            }
        });
    }
}
