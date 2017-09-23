package com.e7yoo.e7;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventManager;
import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.bumptech.glide.Glide;
import com.e7yoo.e7.adapter.GridAdapter;
import com.e7yoo.e7.adapter.MsgRefreshRecyclerAdapter;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.app.news.NewsActivity;
import com.e7yoo.e7.model.GridItem;
import com.e7yoo.e7.model.GridItemClickListener;
import com.e7yoo.e7.model.PrivateMsg;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.net.NetHelper;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.BdVoiceUtil;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.JokeUtil;
import com.e7yoo.e7.util.Logs;
import com.e7yoo.e7.util.PrivateMsgUtil;
import com.e7yoo.e7.util.RandomUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.util.TtsUtils;
import com.e7yoo.e7.view.BlurTransformation;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener, GridItemClickListener, RecognitionListener, SpeechSynthesizerListener {

    public static final int REQUEST_CODE_FOR_ADD_ROBOT = 1002;
    private SwipeRefreshLayout mHomeSRLayout;
    private RecyclerView mRecyclerView;
    private MsgRefreshRecyclerAdapter mRvAdapter;
    private ImageView bgImage;

    private ImageView mVoiceImage;
    private EditText mEditText;
    private TextView mVoiceTv;
    private ImageView mSendOrMoreImage;

    private View mChatInputMoreLayout;
    private GridView mChatInputMoreGv;

    private Robot mRobot;


    private SpeechRecognizer mSpeechRecognizer;
    // 语音合成客户端
    private SpeechSynthesizer mSpeechSynthesizer;
    private EventManager mWpEventManager;

    private void init() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        // 注册监听器
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechSynthesizer = TtsUtils.getSpeechSynthesizer(this, this, getVoice(mRobot));
    }

    private int getVoice(Robot robot) {
        int voice = 4; // 默认播放童音 0 (普通女声), 1 (普通男声), 2 (特别男声), 3 (情感男声), 4 (童声)
        if(robot.getId() > 0) { // 0是萌萌，语音播放童音
            switch (robot.getSex()) {
                case 0: // 保密，特别男声
                    voice = 2;
                    break;
                case 1: // 男，男声
                    voice = 1;
                    break;
                case 2: // 女，女声
                    voice = 0;
                    break;
            }
        }
        return voice;
    }

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
        mVoiceTv = (TextView) findViewById(R.id.chat_voice_tv);
        mSendOrMoreImage = (ImageView) findViewById(R.id.chat_send_or_more);

        mChatInputMoreLayout = findViewById(R.id.chat_input_more_layout);
        mChatInputMoreGv = (GridView) findViewById(R.id.chat_input_more_gv);
    }

    @Override
    protected void initSettings() {
        CheckPermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, REQUEST_TO_BD_VOICE,
                R.string.dialog_voice_hint_title, R.string.dialog_voice_hint);
        if(getIntent() != null) {
            mRobot = (Robot) getIntent().getSerializableExtra(Constant.INTENT_ROBOT);
        } else {
            TastyToastUtil.toast(this, R.string.error_robot_is_null);
            return;
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
        init();
        mChatInputMoreGv.setAdapter(new GridAdapter(this, getGridItems()));
    }

    private ArrayList<GridItem> getGridItems() {
        ArrayList<GridItem> gridItems = new ArrayList<>();
        GridItem gridItem = new GridItem(R.mipmap.item_chat_gridview_picture, R.string.item_chat_gridview_picture, this);
        gridItems.add(gridItem);
        gridItem = new GridItem(R.mipmap.item_chat_gridview_news, R.string.item_chat_gridview_news, this);
        gridItems.add(gridItem);
        gridItem = new GridItem(R.mipmap.item_chat_gridview_joke, R.string.item_chat_gridview_joke, this);
        gridItems.add(gridItem);
        gridItem = new GridItem(R.mipmap.item_chat_gridview_game, R.string.item_chat_gridview_game, this);
        gridItems.add(gridItem);
        return gridItems;
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
                int bgblur = mRobot.getBgblur();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && bgblur > 0) {
                    Glide.with(this).load(mRobot.getBg()).crossFade(1000).bitmapTransform(new BlurTransformation(this, bgblur)).into(bgImage);
                } else {
                    Glide.with(this).load(mRobot.getBg()).crossFade(1000).into(bgImage);
                }
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
        mVoiceTv.setOnTouchListener(mTouchListener);
        mRvAdapter.setOnItemClickListener(mOnItemClickListener);
        mRvAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mRvAdapter.setOnVoiceClickListener(mOnVoiceClickListener);
    }

    boolean isNoMatch = false;
    boolean isBusy = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        private long time;
        private Toast lastToast;
        private boolean isNoNet = false;
        int maxTime = 10 * 1000;
        int minTime = 1 * 1000;
        private void showToast(int strResId) {
            if(lastToast != null) {
                lastToast.cancel();
            }
            lastToast = TastyToastUtil.toast(ChatActivity.this, strResId);
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(mSpeechRecognizer == null) {
                        return false;
                    } else {
                        actionDown(motionEvent);
                        view.setPressed(true);
                        ((TextView) view).setText(R.string.chat_voice_tv_end);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    boolean isInViewMove = actionMove(view, motionEvent);
                    if(isInViewMove) {
                        ((TextView) view).setText(R.string.chat_voice_tv_end);
                    } else {
                        ((TextView) view).setText(R.string.chat_voice_tv_cancel);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    actionUp(view, motionEvent);
                    view.setPressed(false);
                    ((TextView) view).setText(R.string.chat_voice_tv);
                    break;
            }
            return true;
        }
        private void actionDown(MotionEvent motionEvent) {
            time = System.currentTimeMillis();
            isNoNet = false;
            isNoMatch = false;
            isBusy = false;
            if(Net.isNetWorkConnected(ChatActivity.this)) {
                BdVoiceUtil.startASR(mSpeechRecognizer, mSpeechSynthesizer);
                showToast(R.string.input_voice_toast_start);
            } else {
                String noNetHint = getString(R.string.input_voice_hint_net_no);
                if(mRvAdapter != null && !mRvAdapter.isEndWithNetError()) {
                    PrivateMsg msg = new PrivateMsg(Constant.NET_NO, System.currentTimeMillis(), noNetHint, null, PrivateMsg.Type.HINT, mRobot.getId());
                    mRvAdapter.addItemBottom(msg);
                }
                isNoNet = true;
            }
        }

        /**
         * 是否有效
         * @param view
         * @param motionEvent
         * @return
         */
        private boolean actionMove(View view, MotionEvent motionEvent) {
            return isInView(view, motionEvent);
        }

        /**
         * 点击位置是否在view的区域内
         * @param view
         * @param motionEvent
         * @return
         */
        private boolean isInView(View view, MotionEvent motionEvent) {
            int l = view.getLeft();
            int r = view.getRight();
            int t = view.getTop();
            int b = view.getBottom();
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if(x >= 0 && x <= r - l && y >= 0 && y <= b - t) {
                return true;
            }
            return false;
        }

        private void actionUp(View view, MotionEvent motionEvent) {
            if(isNoNet) {
                return;
            }
            long now = System.currentTimeMillis();
            if(!isInView(view, motionEvent)) {
                BdVoiceUtil.cancelASR(mSpeechRecognizer);
                showToast(R.string.input_voice_toast_move_out);
            } else if(now - time < minTime) {
                BdVoiceUtil.cancelASR(mSpeechRecognizer);
                showToast(R.string.input_voice_toast_time_short);
            } else if(now - time > maxTime) {
                BdVoiceUtil.cancelASR(mSpeechRecognizer);
                showToast(R.string.input_voice_toast_time_long);
            } else {
                BdVoiceUtil.stopASR(mSpeechRecognizer);
                if(!isNoMatch && !isBusy) {
                    showToast(R.string.input_voice_toast_end);
                }
            }
        }
    };

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
                mSendOrMoreImage.setImageResource(R.mipmap.chat_send);
            } else {
                mSendOrMoreImage.setImageResource(R.mipmap.chat_more);
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
                if(mVoiceTv.getVisibility() == View.VISIBLE) {
                    mVoiceTv.setVisibility(View.GONE);
                    mEditText.setVisibility(View.VISIBLE);
                    mVoiceImage.setImageResource(R.mipmap.chat_input_voice);
                    if(mEditText.getText().toString().trim().length() > 0) {
                        mSendOrMoreImage.setImageResource(R.mipmap.chat_send);
                    } else {
                        mSendOrMoreImage.setImageResource(R.mipmap.chat_more);
                    }
                } else {
                    mVoiceTv.setVisibility(View.VISIBLE);
                    mEditText.setVisibility(View.GONE);
                    mVoiceImage.setImageResource(R.mipmap.chat_input_keyboard);
                    mSendOrMoreImage.setImageResource(R.mipmap.chat_more);
                }
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

    @Override
    public void onGridItemClick(int i, GridItem item) {
        if(item == null) {
            return;
        }
        int textResId = item.getTextResId();
        switch (textResId) {
            case R.string.item_chat_gridview_picture:
                sendText(getString(textResId));
                break;
            case R.string.item_chat_gridview_news:
                addMsgToView(getString(textResId));
                ActivityUtil.toActivity(this, NewsActivity.class);
                break;
            case R.string.item_chat_gridview_joke:
                addMsgToView(getString(textResId));
                if(isNetOk(true)) {
                    NetHelper.newInstance().jokeNew(RandomUtil.getRandomNum(100000)*1 + 1, 1);
                    mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                }
                break;
            case R.string.item_chat_gridview_game:
                addMsgToView(getString(textResId));
                ActivityUtil.toActivity(this, GameListActivity.class);
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

    private void addMsgToView(String content) {
        PrivateMsg msg = PrivateMsgUtil.getSendPrivateMsg(mRobot.getId(), content);
        mRvAdapter.addItemBottom(msg);
        mRecyclerView.smoothScrollToPosition(mRvAdapter.getLastPosition());
    }

    private boolean sendText(String content) {
        // mRvAdapter.setFooter(MsgRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        addMsgToView(content);
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

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Logs.logI("-----", "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Logs.logI("百度语音 onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
        Logs.logI("百度语音 onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Logs.logI("百度语音 onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Logs.logI("百度语音 onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        //　错误
        if(Logs.isDebug()) {
            Logs.logI("百度语音 错误 " + error);
        }
        switch (error) {
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                TastyToastUtil.toast(this, R.string.sr_error_network_timeout);
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                TastyToastUtil.toast(this, R.string.sr_error_network);
                break;
            case SpeechRecognizer.ERROR_AUDIO:
                // TastyToastUtil.toast(this, R.string.sr_error_audio);
                break;
            case SpeechRecognizer.ERROR_SERVER:
                TastyToastUtil.toast(this, R.string.sr_error_server);
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                // TastyToastUtil.toast(this, R.string.sr_error_client);
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                // TastyToastUtil.toast(this, R.string.sr_error_speech_timeout);
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                TastyToastUtil.toast(this, R.string.sr_error_no_match);
                isNoMatch = true;
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                TastyToastUtil.toast(this, R.string.sr_error_recognizer_busy);
                isBusy = true;
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                // TastyToastUtil.toast(this, R.string.sr_error_insufficient_permissions);
                break;
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        Logs.logI("百度语音 onResults");
        sendText(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Logs.logI("百度语音 onPartialResults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Logs.logI("百度语音 onEvent" + i);
    }

    @Override
    public void onSynthesizeStart(String s) {
        Logs.logI("百度语音 onSynthesizeStart" + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        Logs.logI("百度语音 onSynthesizeDataArrived" + s + " " + i);
    }

    @Override
    public void onSynthesizeFinish(String s) {
        Logs.logI("百度语音 onSynthesizeFinish" + s);
    }

    @Override
    public void onSpeechStart(String s) {
        Logs.logI("百度语音 onSpeechStart" + s);
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        Logs.logI("百度语音 onSpeechProgressChanged" + s + " " + i);
    }

    @Override
    public void onSpeechFinish(String s) {
        Logs.logI("百度语音 onSpeechFinish" + s);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        Logs.logI("百度语音 onError" + s);
    }

    @Override
    protected void onPause() {
        BdVoiceUtil.eventWekeUpStop(mWpEventManager);
        BdVoiceUtil.cancelASR(mSpeechRecognizer);
        BdVoiceUtil.stopTTS(mSpeechSynthesizer);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        BdVoiceUtil.releaseTTS(mSpeechSynthesizer);
        BdVoiceUtil.destroyASR(mSpeechRecognizer);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mChatInputMoreLayout.getVisibility() == View.VISIBLE) {
            mChatInputMoreLayout.setVisibility(View.GONE);
            return;
        }
        BdVoiceUtil.stopTTS(mSpeechSynthesizer);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        mWpEventManager = BdVoiceUtil.eventWakeUp(this, mWpEventManager);
        super.onResume();
    }

    RecyclerAdapter.OnItemClickListener mOnItemClickListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }
    };

    RecyclerAdapter.OnItemLongClickListener mOnItemLongClickListener = new RecyclerAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(View view, int position) {
            return false;
        }
    };

    MsgRefreshRecyclerAdapter.OnVoiceClickListener mOnVoiceClickListener = new MsgRefreshRecyclerAdapter.OnVoiceClickListener() {
        private int lastPosition = -1;
        @Override
        public void onVoiceClick(View view, int position) {
            PrivateMsg msg = mRvAdapter.getItem(position);
            if(msg != null) {
                long msgTime = msg.getTime();
                if(mRvAdapter.getTtsMsgTime() == msgTime) {
                    BdVoiceUtil.stopTTS(mSpeechSynthesizer);
                    view.setSelected(false);
                    mRvAdapter.setTtsMsgTime(0);
                } else {
                    BdVoiceUtil.startTTS(mSpeechSynthesizer, msg.getContent());
                    view.setSelected(true);
                    mRvAdapter.setTtsMsgTime(msgTime);
                    if(lastPosition < mRvAdapter.getItemCount() && lastPosition >= 0)
                    mRvAdapter.notifyItemChanged(lastPosition);
                }
            }
            lastPosition = position;
        }
    };
}
