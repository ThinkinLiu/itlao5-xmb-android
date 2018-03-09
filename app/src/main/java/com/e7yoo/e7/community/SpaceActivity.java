package com.e7yoo.e7.community;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.model.TextSet;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.PopupWindowUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.ProfileResponse;
import com.umeng.comm.core.nets.responses.SimpleResponse;
import com.umeng.comm.core.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by andy on 2017/10/11.
 */

public class SpaceActivity extends BaseActivity implements View.OnClickListener {
    private TextView mNewMsgHintTv;
    private RecyclerView mRecyclerView;
    private SpaceRecyclerAdapter mRvAdapter;
    private CommUser mCommUser;

    private String mNextPageUrl;

    private List<FeedItem> mFeedItemList = new ArrayList<>();

    @Override
    protected String initTitle() {
        return getString(R.string.circle_space);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_space;
    }

    @Override
    protected void initView() {
        rootView = findViewById(R.id.root_layout);
        mNewMsgHintTv = (TextView) findViewById(R.id.space_new_msg_hint);
        mRecyclerView = (RecyclerView) findViewById(R.id.space_rv);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("CommUser")) {
            mCommUser = getIntent().getParcelableExtra("CommUser");
        }
        if(mCommUser == null || mCommUser.id == null) {
            if(CommonUtils.isLogin(this)) {
                mCommUser = CommonUtils.getLoginUser(this);
            } else {
                finish();
                return;
            }
        }
        if(CommonUtils.isMyself(mCommUser)) {
            setRightTv(View.VISIBLE, R.mipmap.title_right_friend, 0, this);
            if(CommConfig.getConfig().mMessageCount != null && CommConfig.getConfig().mMessageCount.newFansCount > 0) {
                mNewMsgHintTv.setVisibility(View.VISIBLE);
            } else {
                mNewMsgHintTv.setVisibility(View.GONE);
            }
        } else {
            setRightTv(View.VISIBLE, R.mipmap.ic_menu_white_24dp, 0, this);
            mNewMsgHintTv.setVisibility(View.GONE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(getDivider());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new SpaceRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRvAdapter);

        mRvAdapter.refreshData(mCommUser, mFeedItemList);
        loadDbFeed();
        loadUserInfo();
        mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        loadNetFeed(true);
    }

    private void loadDbFeed() {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        mDatabaseAPI.getFeedDBAPI().loadFavoritesFeed(new Listeners.SimpleFetchListener<List<FeedItem>>() {
            @Override
            public void onComplete(List<FeedItem> feedItems) {
                if(mNextPageUrl == null) {
                    mRvAdapter.refreshFeedItems(feedItems);
                }
            }
        });
    }

    private void saveDbFeed(List<FeedItem> feedItems) {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        mDatabaseAPI.getFeedDBAPI().saveFeedsToDB(feedItems);
    }

    private void loadUserInfo() {
        E7App.getCommunitySdk().fetchUserProfile(mCommUser.id, userFetchListener);
    }

    private void loadNetFeed(boolean refresh) {
        if(mNextPageUrl == null || refresh) {
            E7App.getCommunitySdk().fetchUserTimeLine(mCommUser.id, firstFeedFetchListener);
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
        mRvAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mRvAdapter.getItem(position) != null && mRvAdapter.getItem(position) instanceof FeedItem) {
                    ActivityUtil.toFeedDetail(SpaceActivity.this, (FeedItem) mRvAdapter.getItem(position));
                }
            }
        });
        initLoadMoreListener();
        mNewMsgHintTv.setOnClickListener(this);
    }

    private void initLoadMoreListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (isNeedLoadMore(newState)) {
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    loadNetFeed(false);
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
            case R.id.titlebar_right_tv:
            case R.id.space_new_msg_hint:
                if(CommonUtils.isMyself(mCommUser)) {
                    toFriend();
                } else {
                    toMenu();
                }
                break;
            case R.id.titlebar_title_tv:
                long nowTime = System.currentTimeMillis();
                if(num >= 4) {
                    showChat = true;
                } else if(nowTime - lastTime < 5000) {
                    num++;
                } else {
                    num = 0;
                    lastTime = nowTime;
                }
                break;
        }
    }

    private int num = 0;
    private long lastTime = 0;

    private void toFriend() {
        ActivityUtil.toFriend(this, false);
    }

    private boolean showChat = false;
    private void toMenu() {
        ArrayList<TextSet> textSets = new ArrayList<>();
        if(mCommUser.isFollowed) {
            if(showChat) {
                textSets.add(new TextSet(R.string.space_title_right_chat, false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toChat();
                    }
                }));
            }
            textSets.add(new TextSet(R.string.space_title_right_un_attention, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toAttention(false);
                }
            }));
        } else {
            textSets.add(new TextSet(R.string.space_title_right_attention, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toAttention(true);
                }
            }));
            textSets.add(new TextSet(R.string.space_title_right_spam, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toSpam();
                }
            }));
        }
        PopupWindowUtil.showPopWindow(this, rootView, 0, textSets, true);
    }

    private void toChat() {
        ActivityUtil.toCircleChat(this, mCommUser, false);
    }

    private void toAttention(boolean b) {
        if(b) {
            E7App.getCommunitySdk().followUser(mCommUser, new Listeners.SimpleFetchListener<Response>() {
                @Override
                public void onComplete(Response response) {
                    if(response.errCode == ErrorCode.NO_ERROR) {

                    } else {
                        TastyToastUtil.toast(SpaceActivity.this, R.string.attention_failed);
                        mCommUser.isFollowed = false;
                    }
                }
            });
            mCommUser.isFollowed = true;
        } else {
            E7App.getCommunitySdk().cancelFollowUser(mCommUser, new Listeners.SimpleFetchListener<Response>() {
                @Override
                public void onComplete(Response response) {
                    if(response.errCode == ErrorCode.NO_ERROR) {

                    } else {
                        TastyToastUtil.toast(SpaceActivity.this, R.string.attention_cancel_failed);
                        mCommUser.isFollowed = true;
                    }
                }
            });
            mCommUser.isFollowed = false;
        }
    }

    private void toSpam() {
        E7App.getCommunitySdk().spamUser(mCommUser.id, new Listeners.FetchListener<SimpleResponse>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(SimpleResponse simpleResponse) {
            }
        });
    }

    private void photoPicker(ArrayList<String> photoPaths) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .setSelected(photoPaths)
                .start(SpaceActivity.this, PhotoPicker.REQUEST_CODE);
    }

    private void photoPreview(ArrayList<String> photoPaths, int position) {
        PhotoPreview.builder()
                .setPhotos(photoPaths)
                .setCurrentItem(position)
                .setShowDeleteButton(false)
                .start(SpaceActivity.this, PhotoPreview.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    }
                }
                break;
            case PhotoPreview.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    }
                }
                break;
            default:
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
                saveDbFeed(feedsResponse.result);
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

    private Listeners.FetchListener<ProfileResponse> userFetchListener = new Listeners.FetchListener<ProfileResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(ProfileResponse profileResponse) {
            if (profileResponse.errCode == ErrorCode.NO_ERROR && profileResponse.result != null && !TextUtils.isEmpty(profileResponse.result.id)) {
                mCommUser = profileResponse.result;
                mRvAdapter.refreshCommUser(mCommUser);
            }
        }
    };
}
