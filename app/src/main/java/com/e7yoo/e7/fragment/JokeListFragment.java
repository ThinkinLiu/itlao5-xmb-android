package com.e7yoo.e7.fragment;

import android.os.Message;
import android.view.View;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.Joke2ListRefreshRecyclerAdapter;
import com.e7yoo.e7.adapter.ListRefreshRecyclerAdapter;
import com.e7yoo.e7.model.Feed;
import com.e7yoo.e7.model.Joke;
import com.e7yoo.e7.model.JokeType;
import com.e7yoo.e7.sql.DbThreadPool;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.IOUtils;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.RandomUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.util.TimeUtil;
import com.e7yoo.e7.util.UmengUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by andy on 2018/4/6.
 */

public class JokeListFragment extends ListFragment {
    private static final int PAGE_SIZE_PIC = 20;
    private static final int PAGE_SIZE_JOKE = 10;
    private int pageNum = 0;
    public static JokeListFragment newInstance() {
        JokeListFragment fragment = new JokeListFragment();
        return fragment;
    }

    private JokeType jokeType = JokeType.JOKE;

    public JokeListFragment setJokeType(JokeType jokeType) {
        this.jokeType = jokeType;
        if(jokeType == JokeType.JOKE) {
            pageNum = PreferenceUtil.getInt(Constant.PREFERENCE_PAGE_NUM_TXT, 0);
        } else if(jokeType == JokeType.PIC) {
            pageNum = PreferenceUtil.getInt(Constant.PREFERENCE_PAGE_NUM_PIC, 0);
        } else {
            pageNum = RandomUtil.getRandomNum(1000);
        }
        return this;
    }

    @Override
    public void onEventMainThread(Message msg) {
        if(isDetached()) {
            return;
        }
        switch (msg.what) {
        }
    }

    protected void refreshData(List<Feed> jokes, boolean refresh) {
        if(mDatas == null || refresh) {
            mDatas = jokes;
            if(mRvAdapter != null) {
                mRvAdapter.refreshData(mDatas);
            }
        }
    }

    @Override
    protected ListRefreshRecyclerAdapter initAdapter() {
        Joke2ListRefreshRecyclerAdapter jokeListRefreshRecyclerAdapter = new Joke2ListRefreshRecyclerAdapter(getContext());
        jokeListRefreshRecyclerAdapter.setShowCollect(true);
        return jokeListRefreshRecyclerAdapter;
    }

    @Override
    protected void addListener() {
        ((Joke2ListRefreshRecyclerAdapter) mRvAdapter).setOnCollectListener(new Joke2ListRefreshRecyclerAdapter.OnCollectListener() {
            @Override
            public void onCollect(View view, Feed feed, int position) {
                Joke joke = new Joke();
                joke.setContent(feed.getTitle() + (feed.getContent() == null ? "" : feed.getContent()));
                joke.setUrl(feed.getImg());
                joke.setHashId(feed.getObjectId().hashCode() + "");
                joke.setUpdatetime(feed.getUpdatedAt());
                joke.setUnixtime(TimeUtil.getTime(feed.getTime()));
                DbThreadPool.getInstance().insertCollect(E7App.mApp, joke);
                if(mRvAdapter != null) {
                    ((Joke2ListRefreshRecyclerAdapter) mRvAdapter).remove(position);
                }
                TastyToastUtil.toast(getActivity(), R.string.collect_suc);
            }
        });
    }

    boolean isRefresh;
    @Override
    protected void loadDataFromNet(boolean isRefresh) {
        this.isRefresh = isRefresh;
        if(jokeType == null) {
            jokeType = JokeType.JOKE;
        }
        switch (jokeType) {
            case PIC:
                query(Feed.FeedType_PIC, PAGE_SIZE_PIC, pageNum);
                break;
            case JOKE:
                query(Feed.FeedType_JOKE, PAGE_SIZE_JOKE, pageNum);
                break;
            case ALL:
            default:
                break;
        }
    }

    /**
     *
     * @param type 参考Feed#type  类型 0 普通帖子，1 笑话， 2 趣图，其他待拓展
     * @param pageSize 页大小
     * @param pageNum 第几页 从0开始
     */
    private void query(final int type, int pageSize, final int pageNum) {
        BmobQuery<Feed> query = new BmobQuery<>();
        query.addWhereEqualTo("type", type)
                .setLimit(pageSize)
                .setSkip(pageNum * pageSize)
                .order("-createdAt")
                .findObjects(new FindListener<Feed>() {
            @Override
            public void done(List<Feed> list, BmobException e) {
                if(mSRLayout == null) {
                    return;
                }
                mSRLayout.setRefreshing(false);
                int nextPage = pageNum;
                if(e != null) {
                    if(list == null || list.size() == 0) {
                        nextPage = 0;
                        mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
                    } else {
                        nextPage++;
                        if(isRefresh) {
                            saveDataToDb(list);
                            refreshData(list, true);
                        } else {
                            mRvAdapter.addItemBottom(list);
                        }
                        mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
                    }
                } else {
                    mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
                }
                switch (type) {
                    case Feed.FeedType_TOPIC:

                        break;
                    case Feed.FeedType_JOKE:
                        PreferenceUtil.commitInt(Constant.PREFERENCE_PAGE_NUM_TXT, nextPage);

                        if(isRefresh) {
                            UmengUtil.onEvent(UmengUtil.JOKE_LIST_JOKE_REFRESH);
                        } else {
                            UmengUtil.onEvent(UmengUtil.JOKE_LIST_JOKE_MORE);
                        }
                        break;
                    case Feed.FeedType_PIC:
                        PreferenceUtil.commitInt(Constant.PREFERENCE_PAGE_NUM_PIC, nextPage);

                        if(isRefresh) {
                            UmengUtil.onEvent(UmengUtil.JOKE_LIST_PIC_REFRESH);
                        } else {
                            UmengUtil.onEvent(UmengUtil.JOKE_LIST_PIC_MORE);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void loadDataFromDb() {
        String jokeList = PreferenceUtil.getString(getKey(jokeType), null);
        try {
            if(jokeList == null) {
                return;
            }
            Object obj = IOUtils.UnserializeStringToObject(jokeList);
            if(obj != null) {
                List<Feed> jokes = (List<Feed>) obj;
                refreshData(jokes, false);
            }
        } catch (Throwable e) {
            CrashReport.postCatchedException(e);
        }
    }

    private void saveDataToDb(List<Feed> jokes) {
        PreferenceUtil.commitString(getKey(jokeType), IOUtils.SerializeObjectToString(jokes));
    }

    private String getKey(JokeType jokeType) {
        String preferenceKey;
        if(jokeType == null) {
            jokeType = JokeType.JOKE;
        }
        switch (jokeType) {
            case JOKE:
                preferenceKey = Constant.PREFERENCE_CIRCLE_JOKE_JOKE_feed;
                break;
            case PIC:
                preferenceKey = Constant.PREFERENCE_CIRCLE_JOKE_PIC_feed;
                break;
            case ALL:
            default:
                preferenceKey = Constant.PREFERENCE_CIRCLE_JOKE_ALL_feed;
                break;
        }
        return preferenceKey;
    }

}
