package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.ListRefreshRecyclerAdapter;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.view.RecyclerViewDivider;

import java.util.List;

public abstract class ListFragment extends BaseFragment {

    protected SwipeRefreshLayout mSRLayout;
    protected RecyclerView mRecyclerView;
    protected ListRefreshRecyclerAdapter mRvAdapter;
    protected List mDatas;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_list, container, false);
            mSRLayout = mRootView.findViewById(R.id.list_sr_layout);
            mRecyclerView = mRootView.findViewById(R.id.list_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mRecyclerView.addItemDecoration(getDivider());
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        return mRootView;
    }

    protected RecyclerViewDivider getDivider() {
        return new RecyclerViewDivider(
                getContext(), LinearLayoutManager.VERTICAL,
                getResources().getDimensionPixelOffset(R.dimen.item_robot_divider),
                ContextCompat.getColor(getContext(), R.color.backgroud),
                true,
                getResources().getDimensionPixelOffset(R.dimen.space_3x));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(mRvAdapter == null) {
            mRvAdapter = initAdapter();
            loadDataFromDb();
        }
        mRecyclerView.setAdapter(mRvAdapter);
        mSRLayout.setOnRefreshListener(mOnRefreshListener);
        initLoadMoreListener();
        addListener();
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract ListRefreshRecyclerAdapter initAdapter();

    protected abstract void addListener();

    public boolean isFirstShow = true;

    /**
     * 这个只在FragmentPagerAdapter中调用
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            firstLoadData();
        }
    }

    public void firstLoadData() {
        if(isFirstShow) {
            isFirstShow = false;
            if(mSRLayout != null) {
                mSRLayout.setRefreshing(true);
            }
            loadDataFromNet(true);
        }
    }

    protected void loadData(boolean isRefresh) {
        if(isNetOk()) {
            loadDataFromNet(isRefresh);
        }
    }

    protected abstract void loadDataFromNet(boolean isRefresh);

    protected abstract void loadDataFromDb();

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(true);
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
                    mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    loadData(false);
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
                return  isPullUpLoadMore() && !isDetached() && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRvAdapter.getItemCount()
                        && mRvAdapter.getFooter() != ListRefreshRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != ListRefreshRecyclerAdapter.FooterType.NO_MORE
                        && !mSRLayout.isRefreshing();
            }
        });

    }

    private boolean pullUpLoadMore = true;
    public void setPullUpLoadMore(boolean pullUpLoadMore) {
        this.pullUpLoadMore = pullUpLoadMore;
    }

    public boolean isPullUpLoadMore() {
        return pullUpLoadMore;
    }

    private boolean isNetOk() {
        if (!Net.isNetWorkConnected(getActivity())) {
            mSRLayout.setRefreshing(false);
            mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.END, 0, false);
            return false;
        } else{
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mRvAdapter != null) {
            mRvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
