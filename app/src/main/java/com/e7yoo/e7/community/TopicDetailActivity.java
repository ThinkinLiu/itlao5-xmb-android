package com.e7yoo.e7.community;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.model.TextSet;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PopupWindowUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.ProfileResponse;
import com.umeng.comm.core.nets.responses.TopicItemResponse;
import com.umeng.comm.core.nets.responses.TopicResponse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/10/11.
 */

public class TopicDetailActivity extends BaseActivity implements View.OnClickListener {
    private Topic mTopic;
    protected ImageView mPostIv;
    private RecyclerView mRecyclerView;
    private TopicDetailRecyclerAdapter mRvAdapter;

    private String mNextPageUrl;

    private List<FeedItem> mFeedItemList = new ArrayList<>();

    @Override
    protected String initTitle() {
        return getString(R.string.circle_topic);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_topic_detail;
    }

    @Override
    protected void initView() {
        mPostIv = (ImageView) findViewById(R.id.circle_post);
        mRecyclerView = (RecyclerView) findViewById(R.id.topic_detail_rv);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("Topic")) {
            mTopic = getIntent().getParcelableExtra("Topic");
        }
        if(mTopic == null || mTopic.id == null) {
            TastyToastUtil.toast(this, R.string.circle_topic_not_exist);
            finish();
            return;
        }
        setTitleTv(mTopic.name.replace("#", ""));
        setRightTv(View.VISIBLE, R.mipmap.ic_menu_white_24dp, 0, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(getDivider());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new TopicDetailRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRvAdapter);

        mRvAdapter.refreshData(mTopic, mFeedItemList);

        loadTopicInfo();
        mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        loadNetFeed(true);

        mRvAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mRvAdapter.getItem(position) != null && mRvAdapter.getItem(position) instanceof FeedItem) {
                    ActivityUtil.toFeedDetail(TopicDetailActivity.this, (FeedItem) mRvAdapter.getItem(position));
                }
            }
        });
    }

    private void loadTopicInfo() {
        E7App.getCommunitySdk().fetchTopicWithId(mTopic.id, topicFetchListener);
    }

    private void loadNetFeed(boolean refresh) {
        if(mNextPageUrl == null || refresh) {
            E7App.getCommunitySdk().fetchTopicFeed(mTopic.id, firstFeedFetchListener);
        } else {
            E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, FeedsResponse.class, feedFetchListener);
        }
    }

    private RecyclerViewDivider getDivider() {
        RecyclerViewDivider divider = new RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL,
                0,
                ContextCompat.getColor(this, R.color.backgroud),
                false,
                0,
                true,
                getResources().getDimensionPixelOffset(R.dimen.item_robot_divider));
        return divider;
    }

    @Override
    protected void initViewListener() {
        mPostIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_post:
                toPost();
                break;
            case R.id.titlebar_right_tv:
                toMenu();
                break;
        }
    }
    private Listeners.FetchListener<FeedsResponse> firstFeedFetchListener = new Listeners.FetchListener<FeedsResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedsResponse feedsResponse) {
            if(feedsResponse.errCode == ErrorCode.NO_ERROR && feedsResponse.result != null && feedsResponse.result.size() > 0) {
                mNextPageUrl = feedsResponse.nextPageUrl;
                mRvAdapter.refreshFeedItems(feedsResponse.result);
                mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
            }
        }
    };

    private Listeners.FetchListener<FeedsResponse> feedFetchListener = new Listeners.FetchListener<FeedsResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedsResponse feedsResponse) {
            if(feedsResponse.errCode == ErrorCode.NO_ERROR && feedsResponse.result != null && feedsResponse.result.size() > 0) {
                mNextPageUrl = feedsResponse.nextPageUrl;
                mRvAdapter.addItemBottom(feedsResponse.result);
                mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
            }
        }
    };

    private Listeners.FetchListener<TopicItemResponse> topicFetchListener = new Listeners.FetchListener<TopicItemResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(TopicItemResponse topicItemResponse) {
            if (topicItemResponse.errCode == ErrorCode.NO_ERROR && topicItemResponse.result != null && !TextUtils.isEmpty(topicItemResponse.result.id)) {
                mTopic = topicItemResponse.result;
                mRvAdapter.refreshTopic(mTopic);
            }
        }
    };


    private void toMenu() {
        ArrayList<TextSet> textSets = new ArrayList<>();
        if(mTopic.isFocused) {
            textSets.add(new TextSet(R.string.topic_title_right_cancel_focus, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toFocus(false);
                }
            }));
        } else {
            textSets.add(new TextSet(R.string.topic_title_right_focus, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toFocus(true);
                }
            }));
        }
        textSets.add(new TextSet(R.string.space_title_right_share, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShare();
            }
        }));
        textSets.add(new TextSet(R.string.space_title_right_post, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPost();
            }
        }));
        PopupWindowUtil.showPopWindow(this, rootView, 0, textSets, true);
    }

    private void toFocus(boolean focus) {
        if(focus) {
            mTopic.isFocused = false;
            E7App.getCommunitySdk().cancelFollowTopic(mTopic, new Listeners.SimpleFetchListener<Response>() {
                @Override
                public void onComplete(Response response) {
                    switch (response.errCode) {
                        case ErrorCode.NO_ERROR:
                            break;
                        case ErrorCode.UNLOGIN_ERROR:
                            TastyToastUtil.toast(TopicDetailActivity.this, R.string.circle_no_login);
                        default:
                            mTopic.isFocused = true;
                            break;
                    }
                }
            });
        } else {
            mTopic.isFocused = true;
            E7App.getCommunitySdk().followTopic(mTopic, new Listeners.SimpleFetchListener<Response>() {
                @Override
                public void onComplete(Response response) {
                    switch (response.errCode) {
                        case ErrorCode.NO_ERROR:
                            break;
                        case ErrorCode.UNLOGIN_ERROR:
                            TastyToastUtil.toast(TopicDetailActivity.this, R.string.circle_no_login);
                        default:
                            mTopic.isFocused = false;
                            break;
                    }
                }
            });
        }
    }

    private void toShare() {
        String title = getResources().getString(R.string.mengquanfenxiang_topic);
        String text = getResources().getString(R.string.mengquanfenxiang2) + mTopic.name + mTopic.desc;
        if(text.length() > 30) {
            text = text.substring(0, 30);
        }
        String img = mTopic.icon != null && mTopic.icon.trim().length() > 0
                ? mTopic.icon.trim() : null;
        ShareDialogUtil.show(this, null, title, text, img);
    }

    private void toPost() {
        ActivityUtil.toPostOrLogin(this, mTopic);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_POST_FEED_SUCCESS:
                if(msg.obj != null && msg.obj instanceof FeedItem) {
                    mRvAdapter.addFeedItem((FeedItem) msg.obj);
                }
                break;
            case Constant.EVENT_BUS_DELETE_FEED_SUCCESS:
                if(msg.obj != null && msg.obj instanceof String && ((String) msg.obj).length() > 0) {
                    mRvAdapter.remove((String) msg.obj);
                }
                break;
        }
    }
}
