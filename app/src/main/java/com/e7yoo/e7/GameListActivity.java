package com.e7yoo.e7;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.e7yoo.e7.adapter.GameListRefreshRecyclerAdapter;
import com.e7yoo.e7.adapter.PushMsgRefreshRecyclerAdapter;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.game.GameActivity;
import com.e7yoo.e7.game.game2048.Game2048Activity;
import com.e7yoo.e7.game.killbird.KillBirdActivity;
import com.e7yoo.e7.game.plane.PlaneMainActivity;
import com.e7yoo.e7.model.GameInfo;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.sql.DbThreadPool;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.GameInfoUtil;

import java.util.ArrayList;

public class GameListActivity extends BaseActivity/* implements View.OnClickListener*/ {
    // private View m2048Layout, mKillBirdLayout, mPlaneLayout, mMoreLayout;
    private RecyclerView mRecyclerView;
    private GameListRefreshRecyclerAdapter mRvAdapter;
    private ArrayList<GameInfo> mGameInfos;
    public final static int PAGE_NUM = 15;

    @Override
    protected String initTitle() {
        return getString(R.string.more_game);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_game_list;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.game_list_rv);
        /*m2048Layout = findViewById(R.id.gamelist_2048_layout);
        mKillBirdLayout = findViewById(R.id.gamelist_killbird_layout);
        mPlaneLayout = findViewById(R.id.gamelist_plane_layout);
        mMoreLayout = findViewById(R.id.gamelist_more_layout);*/
    }

    @Override
    protected void initSettings() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // TODO mGameInfos = MessageDbHelper.getInstance(this).getPushMsgs(0, PAGE_NUM);
        if(mGameInfos == null) {
            mGameInfos = GameInfoUtil.getGameInfos(this);
        }
        mRvAdapter = new GameListRefreshRecyclerAdapter(this);
        mRvAdapter.setOnItemClickListener(mOnItemClickListener);
        mRvAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mRvAdapter.refreshData(mGameInfos);
        mRecyclerView.setAdapter(mRvAdapter);
        if(mGameInfos.size() <= 0) {
            mRvAdapter.setFooter(GameListRefreshRecyclerAdapter.FooterType.HINT, R.string.loading_no_push_msg, false);
        } else if(mGameInfos.size() < PAGE_NUM) {
            mRvAdapter.setFooter(GameListRefreshRecyclerAdapter.FooterType.HINT, R.string.loading_no_more, false);
        } else {
            mRvAdapter.setFooter(GameListRefreshRecyclerAdapter.FooterType.HINT, R.string.loading_up_load_more, false);
        }
    }

    @Override
    protected void initViewListener() {
        /*m2048Layout.setOnClickListener(this);
        mKillBirdLayout.setOnClickListener(this);
        mPlaneLayout.setOnClickListener(this);
        mMoreLayout.setOnClickListener(this);*/
        initLoadMoreListener();
    }


    /*@Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gamelist_2048_layout:
                ActivityUtil.toActivity(this, Game2048Activity.class);
                break;
            case R.id.gamelist_killbird_layout:
                ActivityUtil.toActivity(this, KillBirdActivity.class);
                break;
            case R.id.gamelist_plane_layout:
                ActivityUtil.toActivity(this, PlaneMainActivity.class);
                break;
            case R.id.gamelist_more_layout:
                ActivityUtil.toGameActivity(this,
                        "http://h.4399.com/play/191530.htm",
                        null, true);
                break;
        }
    }*/

    RecyclerAdapter.OnItemClickListener mOnItemClickListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            final GameInfo gameInfo = mRvAdapter.getItem(position);
            if(gameInfo == null) {
                return;
            }
            switch (gameInfo.getGame_type()) {
                case 0:
                    localGame(gameInfo);
                    break;
                case 1:
                    ActivityUtil.toGameActivity(GameListActivity.this,
                            gameInfo.getH5_url(),
                            null, false, gameInfo);
                    break;
                case 2:
                    ActivityUtil.toGameActivity(GameListActivity.this,
                            gameInfo.getH5_url(),
                            null, true, gameInfo);
                    break;
                case 3:
                    // 预留
                    break;
            }
        }
    };

    private void localGame(GameInfo gameInfo) {
        if(Game2048Activity.class.getSimpleName().equals(gameInfo.getGame_path())) {
            ActivityUtil.toActivity(this, Game2048Activity.class);
        } else if(KillBirdActivity.class.getSimpleName().equals(gameInfo.getGame_path())) {
            ActivityUtil.toActivity(this, KillBirdActivity.class);
        } else if(PlaneMainActivity.class.getSimpleName().equals(gameInfo.getGame_path())) {
            ActivityUtil.toActivity(this, PlaneMainActivity.class);
        }
    }

    RecyclerAdapter.OnItemLongClickListener mOnItemLongClickListener = new RecyclerAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(View view, int position) {
            return false;
        }
    };

    private void initLoadMoreListener() {
        // TODO
        /*mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (isNeedLoadMore(newState)) {
                    mRvAdapter.setFooter(GameInfoRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    DbThreadPool.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            if(mRvAdapter != null) {
                                mGameInfos = MessageDbHelper.getInstance(GameListActivity.this).getPushMsgs(mRvAdapter.getLastId(), PAGE_NUM);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!isFinishing() && mRvAdapter != null) {
                                            mRvAdapter.addItemBottom(mGameInfos);
                                            if(mGameInfos.size() < PAGE_NUM) {
                                                mRvAdapter.setFooter(GameInfoRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
                                            } else {
                                                mRvAdapter.setFooter(GameInfoRefreshRecyclerAdapter.FooterType.HINT, R.string.loading_up_load_more, false);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
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
                        && mRvAdapter.getFooter() != GameInfoRefreshRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != GameInfoRefreshRecyclerAdapter.FooterType.NO_MORE && mGameInfos.size() >= PAGE_NUM;
            }
        });*/

    }

}
