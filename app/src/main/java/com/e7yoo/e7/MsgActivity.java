package com.e7yoo.e7;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.e7yoo.e7.adapter.MsgRRecyclerAdapter;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.beans.MessageSession;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.MessageSessionResponse;

import java.util.List;

public class MsgActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private MsgRRecyclerAdapter mRvAdapter;
    private List<MessageSession> mMessageSessions;

    @Override
    protected String initTitle() {
        return getString(R.string.mine_msg);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_msg;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.msg_rv);
    }

    @Override
    protected void initSettings() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(getDivider());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new MsgRRecyclerAdapter(this);
        mRvAdapter.setOnItemClickListener(mOnItemClickListener);
        mRvAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mRvAdapter.refreshData(mMessageSessions);
        mRecyclerView.setAdapter(mRvAdapter);

        mRvAdapter.setFooter(MsgRRecyclerAdapter.FooterType.HINT, R.string.loading_up_load_more, false);
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
        loadFriendMsg(true);
    }

    RecyclerAdapter.OnItemClickListener mOnItemClickListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            final MessageSession msg = mRvAdapter.getItem(position);
            if(msg == null) {
                return;
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
                        && mRvAdapter.getFooter() != MsgRRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != MsgRRecyclerAdapter.FooterType.NO_MORE;
            }
        });

    }

    private String mNextPageUrl = null;
    private void loadFriendMsg(boolean refresh) {
        mRvAdapter.setFooter(MsgRRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        if(refresh || mNextPageUrl == null) {
            E7App.getCommunitySdk().fetchSessionList(mFirstSimpleFetchListener);
        } else {
            E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, MessageSessionResponse.class, mFetchListener);
        }
    }

    private Listeners.SimpleFetchListener<MessageSessionResponse> mFirstSimpleFetchListener = new Listeners.SimpleFetchListener<MessageSessionResponse>() {
        @Override
        public void onComplete(MessageSessionResponse messageSessionResponse) {
            mRvAdapter.setFooter(MsgRRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            if(messageSessionResponse.errCode == ErrorCode.NO_ERROR && messageSessionResponse.result != null
                    && messageSessionResponse.result.size() > 0) {
                mNextPageUrl = messageSessionResponse.nextPageUrl;
                mMessageSessions = messageSessionResponse.result;
                mRvAdapter.refreshData(messageSessionResponse.result);
            }
        }
    };

    private Listeners.FetchListener<MessageSessionResponse> mFetchListener = new Listeners.FetchListener<MessageSessionResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(MessageSessionResponse messageSessionResponse) {
            if(messageSessionResponse.errCode == ErrorCode.NO_ERROR && messageSessionResponse.result != null
                    && messageSessionResponse.result.size() > 0) {
                mNextPageUrl = messageSessionResponse.nextPageUrl;
                mMessageSessions = messageSessionResponse.result;
                mRvAdapter.addItemBottom(messageSessionResponse.result);
                mRvAdapter.setFooter(MsgRRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(MsgRRecyclerAdapter.FooterType.END, R.string.loading_no_more, false);
            }
        }
    };
}
