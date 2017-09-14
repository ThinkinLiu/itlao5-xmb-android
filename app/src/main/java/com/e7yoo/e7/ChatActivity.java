package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.e7yoo.e7.adapter.MsgRefreshRecyclerAdapter;
import com.e7yoo.e7.model.PrivateMsg;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.net.NetHelper;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.JokeUtil;
import com.e7yoo.e7.util.PrivateMsgUtil;
import com.e7yoo.e7.util.RandomUtil;
import com.e7yoo.e7.util.TastyToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_FOR_ADD_ROBOT = 1002;
    private SwipeRefreshLayout mHomeSRLayout;
    private RecyclerView mRecyclerView;
    private MsgRefreshRecyclerAdapter mRvAdapter;
    private ImageView bgImage;

    private ImageView mVoiceImage;
    private EditText mEditText;
    private ImageView mSendOrMoreImage;

    private View mChatInputMoreLayout;
    private GridView mChatInputMoreGv;

    private Robot mRobot;

    @Override
    protected String initTitle() {
        return getString(R.string.mengmeng);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        bgImage = (ImageView) findViewById(R.id.chat_background);
        mHomeSRLayout = (SwipeRefreshLayout) findViewById(R.id.chat_sr_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_rv);
        mVoiceImage = (ImageView) findViewById(R.id.chat_voice);
        mEditText = (EditText) findViewById(R.id.chat_edit);
        mSendOrMoreImage = (ImageView) findViewById(R.id.chat_send_or_more);

        mChatInputMoreLayout = findViewById(R.id.chat_input_more_layout);
        mChatInputMoreGv = (GridView) findViewById(R.id.chat_input_more_gv);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null) {
            mRobot = (Robot) getIntent().getSerializableExtra(Constant.INTENT_ROBOT);
        }
        refresh(mRobot);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new MsgRefreshRecyclerAdapter(this, mRobot, null);
        mRecyclerView.setAdapter(mRvAdapter);
        mHomeSRLayout.setColorSchemeResources(R.color.titlebar_bg);

        ArrayList<PrivateMsg> msgs = MessageDbHelper.getInstance(this).getPrivateMsgs(mRobot.getId(), 0, 100);
        mRvAdapter.addItemBottom(msgs);
        int lastPosition = mRvAdapter.getLastPosition();
        if(lastPosition > 20) {
            mRecyclerView.scrollToPosition(lastPosition - 15);
            mRecyclerView.smoothScrollToPosition(lastPosition);
        } else if(lastPosition > 0) {
            mRecyclerView.smoothScrollToPosition(lastPosition);
        }
    }

    private void refresh(Robot robot) {
        if(mRobot != null) {
            mRobot = robot;
            setTitleTv(mRobot.getName());
            if(getString(R.string.mengmeng).equals(mRobot.getName())) {
                // 萌萌的信息不能修改
                setRightTv(View.GONE);
            } else {
                setRightTv(View.VISIBLE, R.mipmap.title_right_chat_robot, 0, this);
            }
            if(!TextUtils.isEmpty(mRobot.getBg())) {
                Glide.with(this).load(mRobot.getBg()).into(bgImage/*new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getResources(), resource);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mHomeSRLayout.setBackground(drawable);
                        } else {
                            mHomeSRLayout.setBackgroundDrawable(drawable);
                        }
                    }
                }*/);
            }
            if(mRvAdapter != null) {
                mRvAdapter.refreshRobot(mRobot);
            }
        }
    }

    @Override
    protected void initViewListener() {
        mVoiceImage.setOnClickListener(this);
        mSendOrMoreImage.setOnClickListener(this);
        mHomeSRLayout.setOnRefreshListener(onRefreshListener);
        initLoadMoreListener();
        mEditText.addTextChangedListener(mEditTextWatcher);
    }

    private TextWatcher mEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void afterTextChanged(Editable editable) {
            if(isFinishing() || mSendOrMoreImage == null) {
                return;
            }
            if(editable.toString().trim().length() > 0) {
                mSendOrMoreImage.setImageResource(R.drawable.ic_send_black_24dp);
            } else {
                mSendOrMoreImage.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_right_tv:
                ActivityUtil.toAddRobotActivityForResult(this, mRobot, REQUEST_CODE_FOR_ADD_ROBOT);
                break;
            case R.id.chat_voice:
                break;
            case R.id.chat_send_or_more:
                String content = mEditText.getText().toString().trim();
                if(content.length() > 0) {
                    // 发送
                    boolean send = sendText(content);
                    if(send) {
                        mEditText.setText("");
                    }
                } else {
                    if(mChatInputMoreLayout.getVisibility() == View.GONE) {
                        mChatInputMoreLayout.setVisibility(View.VISIBLE);
                    } else {
                        mChatInputMoreLayout.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            final int lastId = mRvAdapter.getLastId();
            if(lastId <= 0) {
                EventBusUtil.post(Constant.EVENT_BUS_REFRESH_RecyclerView);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<PrivateMsg> newDatas = MessageDbHelper.getInstance(ChatActivity.this)
                            .getPrivateMsgs(mRobot.getId(), lastId, 100);
                    if(newDatas == null || newDatas.size() == 0) {
                        EventBusUtil.post(Constant.EVENT_BUS_REFRESH_RecyclerView_NOMORE);
                    } else {
                        EventBusUtil.post(Constant.EVENT_BUS_REFRESH_RecyclerView_FROM_SQL, newDatas);
                    }
                }
            }).start();
        }
    };

    private boolean sendText(String content) {
        // mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        PrivateMsg msg = PrivateMsgUtil.getSendPrivateMsg(mRobot.getId(), content);
        mRvAdapter.addItemBottom(msg);
        mRecyclerView.smoothScrollToPosition(mRvAdapter.getLastPosition());
        if(isNetOk(true)) {
            if(getResources().getString(R.string.mengmeng).equals(mRobot.getName())) {
                NetHelper.newInstance().rootAsk("", content);
            } else {
                NetHelper.newInstance().rootAsk(String.valueOf(mRobot.getId()), content);
            }
        }
        return true;
    }

    private boolean isNetOk(boolean sendNetNoMsg) {
        if (!Net.isNetWorkConnected(this)) {
            if(sendNetNoMsg) {
                if (!mRvAdapter.isEndWithNetError()) {
                    PrivateMsg msg = new PrivateMsg(Constant.NET_NO, System.currentTimeMillis(),
                            getString(R.string.net_no), null, PrivateMsg.Type.HINT, mRobot.getId());
                    mRvAdapter.addItemBottom(msg);
                    mRecyclerView.smoothScrollToPosition(mRvAdapter.getLastPosition());
                }
                mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.END, 0, false);
            }
            return false;
        } else{
            return true;
        }
    }

    private void initLoadMoreListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (isNeedLoadMore(newState)) {
                    if(isNetOk(true)) {
                        NetHelper.newInstance().jokeNew(RandomUtil.getRandomNum(100000)*1 + 1, 1);
                        mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    }
                    /*mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    String content = "你好啊！你叫什么名字？";
                    mRvAdapter.addItemBottom(PrivateMsgUtil.getSendPrivateMsg(content));
                    NetHelper.newInstance().rootAsk(content);*/
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
                        && mRvAdapter.getFooter() != MsgRefreshRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != MsgRefreshRecyclerAdapter.FooterType.NO_MORE
                        && !mHomeSRLayout.isRefreshing();
            }
        });

    }

    public void onEventMainThread(Message msg) {
        if(isFinishing()) {
            return;
        }
        String robotName = null;
        int robotId = 0;
        if(mRobot == null || mRobot.getName() == null) {
            robotName = getString(R.string.mengmeng);
        } else {
            robotName = mRobot.getName();
            robotId = mRobot.getId();
        }
        switch (msg.what) {
            case Constant.EVENT_BUS_NET_tobotAsk:
                PrivateMsg privateMsg = PrivateMsgUtil.getRobotPrivateMsg((JSONObject) msg.obj, robotName, robotId);
                mRvAdapter.addItemBottom(privateMsg);
                mRecyclerView.smoothScrollToPosition(mRvAdapter.getLastPosition());
                mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.END, 0, false);
                break;
            case Constant.EVENT_BUS_NET_jokeNew:
                PrivateMsg jokeMsg = JokeUtil.parseJoke(robotId, robotName, (JSONObject) msg.obj);
                mRvAdapter.addItemBottom(jokeMsg);
                mRecyclerView.smoothScrollToPosition(mRvAdapter.getLastPosition());
                mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.END, 0, false);
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView:
                mHomeSRLayout.setRefreshing(false);
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView_NOMORE:
                TastyToastUtil.toast(ChatActivity.this, R.string.history_message_nomore);
                mHomeSRLayout.setRefreshing(false);
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView_FROM_SQL:
                mRvAdapter.addItemTop((ArrayList<PrivateMsg>) msg.obj);
                mHomeSRLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_FOR_ADD_ROBOT && resultCode == Activity.RESULT_OK) {
            if(data != null && data.hasExtra(Constant.INTENT_ROBOT)) {
                refresh((Robot) data.getSerializableExtra(Constant.INTENT_ROBOT));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
