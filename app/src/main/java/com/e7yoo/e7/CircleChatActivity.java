package com.e7yoo.e7;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.e7yoo.e7.adapter.CircleMsgRefreshRecyclerAdapter;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.MessageChat;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.MessageChatListResponse;
import com.umeng.comm.core.nets.responses.MessageChatResponse;
import com.umeng.comm.core.nets.responses.MessageSessionResponse;

import java.util.List;

/**
 *
 */
public class CircleChatActivity extends BaseActivity implements View.OnClickListener {
    private SwipeRefreshLayout mHomeSRLayout;
    private RecyclerView mRecyclerView;
    private CircleMsgRefreshRecyclerAdapter mRvAdapter;
    private List<MessageChat> mDatas;
    private CommUser mTargetUser;
    private String mNextPageUrl;

    private EditText mEditText;
    private ImageView mSendIv;

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_cricle_chat;
    }

    @Override
    protected String initTitle() {
        return getString(R.string.title_circle_chat);
    }

    @Override
    protected void initView() {
        mHomeSRLayout = (SwipeRefreshLayout) findViewById(R.id.circle_chat_sr_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.circle_chat_rv);
        mEditText = (EditText) findViewById(R.id.circle_chat_edit);
        mSendIv = (ImageView) findViewById(R.id.circle_chat_send);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("targetCommUser")) {
            mTargetUser = getIntent().getParcelableExtra("targetCommUser");
        }
        if(mTargetUser == null) {
            finish();
            return;
        }
        setTitleTv(mTargetUser.name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new CircleMsgRefreshRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRvAdapter);
        mHomeSRLayout.setColorSchemeResources(R.color.titlebar_bg);
        loadMessageChatFromNet(true);
        mHomeSRLayout.setRefreshing(true);
    }

    @Override
    protected void initViewListener() {
        mHomeSRLayout.setOnRefreshListener(onRefreshListener);
        mSendIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_chat_send:
                toSend();
                break;
        }
    }

    private void toSend() {
        String text = mEditText.getText().toString().trim();
        if(text.length() > 0) {
            if(Net.isNetWorkConnected(this)) {
                E7App.getCommunitySdk().sendChatMessage(mTargetUser.id, text, chatFetchListener);
            } else {
                TastyToastUtil.toast(this, R.string.net_no);
            }
        } else {
            TastyToastUtil.toast(this, R.string.title_circle_chat_empty);
        }
    }

    private Listeners.SimpleFetchListener<MessageChatResponse> chatFetchListener = new Listeners.SimpleFetchListener<MessageChatResponse>() {
        @Override
        public void onComplete(MessageChatResponse messageChatResponse) {
            if(messageChatResponse.errCode == ErrorCode.NO_ERROR && !CommonUtil.isEmpty(messageChatResponse.result.id)) {
                mRvAdapter.addItemBottom(messageChatResponse.result);
            } else if(messageChatResponse.errCode == ErrorCode.ERR_CODE_USER_FORBIDDEN) {
                E7App.getCommunitySdk().createSession(mTargetUser.id, createFetchListener);
            } else {
                TastyToastUtil.toast(CircleChatActivity.this, R.string.circle_chat_send_failed);
            }
        }
    };

    private Listeners.SimpleFetchListener<MessageSessionResponse> createFetchListener = new Listeners.SimpleFetchListener<MessageSessionResponse>() {
        @Override
        public void onComplete(MessageSessionResponse messageSessionResponse) {
            if(messageSessionResponse.errCode == ErrorCode.NO_ERROR && messageSessionResponse.result.size() > 0) {
                toSend();
            } else {
                TastyToastUtil.toast(CircleChatActivity.this, R.string.circle_chat_send_failed);
            }
        }
    };

    private void scrollToEnd() {
        int position = 0;
        try {
            position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        int lastPosition = mRvAdapter.getLastPosition();
        if(lastPosition > 0) {
            if(lastPosition - position > 15) {
                // 相差15个item以上，则先直接跳转到相差12个item的位置，再进行动画滚动smoothScrollToPosition
                mRecyclerView.scrollToPosition(lastPosition - 12);
            }
            mRecyclerView.smoothScrollToPosition(lastPosition);
        }
    }


    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(loadMessageChatFromNet(false)){
                mHomeSRLayout.setRefreshing(true);
            }
        }
    };

    private boolean loadMessageChatFromNet(boolean refresh) {
        if(!Net.isNetWorkConnected(this)) {
            TastyToastUtil.toast(this, R.string.net_no);
            return false;
        }
        if(mNextPageUrl == null || refresh) {
            if(mTargetUser != null) {
                E7App.getCommunitySdk().fetchChatList(mTargetUser.id, firstFetchListener);
            } else {
                return false;
            }
        } else {
            E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, MessageChatListResponse.class, fetchListener);
        }
        return true;
    }

    private Listeners.SimpleFetchListener<MessageChatListResponse> firstFetchListener =  new Listeners.SimpleFetchListener<MessageChatListResponse>() {
        @Override
        public void onComplete(MessageChatListResponse messageChatListResponse) {
            if(messageChatListResponse.errCode == ErrorCode.NO_ERROR) {
                if(messageChatListResponse.result.size() > 0) {
                    mNextPageUrl = messageChatListResponse.nextPageUrl;
                    mDatas = messageChatListResponse.result;
                    mRvAdapter.refreshData(messageChatListResponse.result);
                    scrollToEnd();
                } else {
                    // TastyToastUtil.toast(CircleChatActivity.this, R.string.circle_chat_error_no);
                }
            } else {
                TastyToastUtil.toast(CircleChatActivity.this, R.string.circle_chat_error);
            }
            mHomeSRLayout.setRefreshing(false);
        }
    };

    private Listeners.FetchListener<MessageChatListResponse> fetchListener = new Listeners.FetchListener<MessageChatListResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(MessageChatListResponse messageChatListResponse) {
            if(messageChatListResponse.errCode == ErrorCode.NO_ERROR) {
                if(messageChatListResponse.result.size() > 0) {
                    mNextPageUrl = messageChatListResponse.nextPageUrl;
                    mDatas = messageChatListResponse.result;
                    mRvAdapter.addItemTop(messageChatListResponse.result);
                } else {
                    TastyToastUtil.toast(CircleChatActivity.this, R.string.circle_chat_error_nomore);
                }
            } else {
                TastyToastUtil.toast(CircleChatActivity.this, R.string.circle_chat_error);
            }
            mHomeSRLayout.setRefreshing(false);
        }
    };
}
