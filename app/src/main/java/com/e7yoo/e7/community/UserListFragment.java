package com.e7yoo.e7.community;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.util.ActivityUtil;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FansResponse;
import com.umeng.comm.core.nets.responses.UsersResponse;

import java.util.List;

public class UserListFragment extends ListFragment {
    public final static int FLAG_RECOMMENDED = 0;
    public final static int FLAG_ATTENTION = 1;
    public final static int FLAG_FANS = 2;
    private int mFlag = FLAG_RECOMMENDED;
    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("flag")) {
            mFlag = bundle.getInt("flag");
        }
    }

    @Override
    protected ListRefreshRecyclerAdapter initAdapter() {
        return new UserRefreshRecyclerAdapter(getActivity(), mFlag);
    }

    @Override
    protected void addListener() {
        mRvAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mRvAdapter.getItem(position) != null && mRvAdapter.getItem(position) instanceof CommUser) {
                    ActivityUtil.toSpace(getActivity(),(CommUser) mRvAdapter.getItem(position),  false);
                }
            }
        });
    }

    @Override
    protected void loadDataFromNet(boolean isRefresh, String nextPageUrl) {
        switch (mFlag) {
            case FLAG_ATTENTION:
                if(isRefresh || nextPageUrl == null) {
                    E7App.getCommunitySdk().fetchFollowedUser(CommConfig.getConfig().loginedUser.id, mRefreshFansListener);
                } else {
                    E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, FansResponse.class, mFansFetchListener);
                }
                break;
            case FLAG_FANS:
                if(isRefresh || nextPageUrl == null) {
                    E7App.getCommunitySdk().fetchFans(CommConfig.getConfig().loginedUser.id, mRefreshFansListener);
                } else {
                    E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, FansResponse.class, mFansFetchListener);
                }
                break;
            case FLAG_RECOMMENDED:
            default:
                if(isRefresh || nextPageUrl == null) {
                    E7App.getCommunitySdk().fetchRecommendedUsers(mRefreshUsersListener);
                } else {
                    E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, UsersResponse.class, mUsersFetchListener);
                }
                break;
        }
    }

    @Override
    protected void loadDataFromDb() {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        switch (mFlag) {
            case FLAG_ATTENTION:
                mDatabaseAPI.getFollowDBAPI().loadFollowedUsersFromDB(CommConfig.getConfig().loginedUser.id, new Listeners.SimpleFetchListener<List<CommUser>>() {
                    @Override
                    public void onComplete(List<CommUser> commUsers) {
                        refreshData(commUsers);
                    }
                });
                break;
            case FLAG_FANS:
                mDatabaseAPI.getFansDBAPI().loadFansFromDB(CommConfig.getConfig().loginedUser.id, new Listeners.SimpleFetchListener<List<CommUser>>() {
                    @Override
                    public void onComplete(List<CommUser> commUsers) {
                        refreshData(commUsers);
                    }
                });
                break;
            case FLAG_RECOMMENDED:
            default:
                break;
        }
    }

    protected void saveDataToDb(List<CommUser> fanses) {
        DatabaseAPI mDatabaseAPI = DatabaseAPI.getInstance();
        switch (mFlag) {
            case FLAG_ATTENTION:
                mDatabaseAPI.getFollowDBAPI().follow(fanses);
                break;
            case FLAG_FANS:
                mDatabaseAPI.getFansDBAPI().saveFansToDB(CommConfig.getConfig().loginedUser.id, fanses);
                break;
            default:
                break;
        }
    }

    protected void refreshData(List<CommUser> commUsers) {
        if(mDatas == null) {
            mDatas = commUsers;
            mRvAdapter.refreshData(mDatas);
        }
    }

    protected Listeners.FetchListener mRefreshFansListener = new Listeners.FetchListener<FansResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FansResponse fansResponse) {
            mSRLayout.setRefreshing(false);
            mRvAdapter.setFooter(FeedItemRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            if(fansResponse != null && fansResponse.result != null) {
                mNextPageUrl = fansResponse.nextPageUrl;
                mDatas = fansResponse.result;
                mRvAdapter.refreshData(mDatas);
                saveDataToDb(mDatas);
            }
        }
    };

    protected Listeners.FetchListener mFansFetchListener = new Listeners.FetchListener<FansResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FansResponse fansResponse) {
            mSRLayout.setRefreshing(false);
            if(fansResponse != null && fansResponse.result != null && fansResponse.result.size() != 0) {
                mNextPageUrl = fansResponse.nextPageUrl;
                mDatas = fansResponse.result;
                mRvAdapter.addItemBottom(mDatas);
                mRvAdapter.setFooter(TopicRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(TopicRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
            }
        }
    };



    protected Listeners.FetchListener mRefreshUsersListener = new Listeners.FetchListener<UsersResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(UsersResponse usersResponse) {
            mSRLayout.setRefreshing(false);
            mRvAdapter.setFooter(FeedItemRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            if(usersResponse != null && usersResponse.result != null) {
                mNextPageUrl = usersResponse.nextPageUrl;
                mDatas = usersResponse.result;
                mRvAdapter.refreshData(mDatas);
                saveDataToDb(mDatas);
            }
        }
    };



    protected Listeners.FetchListener mUsersFetchListener = new Listeners.FetchListener<UsersResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(UsersResponse usersResponse) {
            mSRLayout.setRefreshing(false);
            if(usersResponse != null && usersResponse.result != null && usersResponse.result.size() != 0) {
                mNextPageUrl = usersResponse.nextPageUrl;
                mDatas = usersResponse.result;
                mRvAdapter.addItemBottom(mDatas);
                mRvAdapter.setFooter(TopicRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(TopicRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
            }
        }
    };

    @Override
    public void onEventMainThread(Message msg) {

    }
}
