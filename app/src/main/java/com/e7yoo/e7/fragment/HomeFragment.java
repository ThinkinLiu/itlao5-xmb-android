package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7yoo.e7.AddRobotActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RobotRefreshRecyclerAdapter;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.sql.DbThreadPool;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.view.RecyclerViewDivider;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private RobotRefreshRecyclerAdapter mRvAdapter;
    private ArrayList<Robot> mRobots;
    public static final int REQUEST_CODE_FOR_ADD_ROBOT = 1001;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_REFRESH_RecyclerView_ADD_ROBOT:
                Robot addRobot = (Robot) msg.obj;
                if(addRobot != null) {
                    if (mRobots == null) {
                        mRobots = new ArrayList<>();
                    }
                    mRobots.add(addRobot);
                }
                if(mRvAdapter != null && !isDetached()) {
                    mRvAdapter.refreshData(mRobots);
                }
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView_UPDATE_ROBOT:
                Robot updateRobot = (Robot) msg.obj;
                if(updateRobot != null) {
                    if(mRobots == null) {
                        mRobots = new ArrayList<>();
                    }
                    boolean hasRobot = false;
                    for(int i = 0; i < mRobots.size(); i++) {
                        if(mRobots.get(i) != null && mRobots.get(i).getId() == updateRobot.getId()) {
                            mRobots.set(i, updateRobot);
                            hasRobot = true;
                            break;
                        }
                    }
                    if(!hasRobot) {
                        mRobots.add(updateRobot);
                    }
                }
                if(mRvAdapter != null && !isDetached()) {
                    mRvAdapter.refreshData(mRobots);
                }
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView_INIT_ROBOT:
                Robot initRobot = (Robot) msg.obj;
                if(initRobot != null) {
                    if(mRobots == null) {
                        mRobots = new ArrayList<>();
                    }
                    if(mRobots.size() > 0) {
                        mRobots.set(0, initRobot);
                    } else {
                        mRobots.add(initRobot);
                    }
                }
                if(mRvAdapter != null && !isDetached()) {
                    mRvAdapter.refreshData(mRobots);
                }
                break;
        }
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRobots = MessageDbHelper.getInstance(getContext()).getRobots();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_home, container, false);
            mRecyclerView = mRootView.findViewById(R.id.home_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mRecyclerView.addItemDecoration(new RecyclerViewDivider(
                    getContext(), LinearLayoutManager.VERTICAL,
                    getResources().getDimensionPixelOffset(R.dimen.item_robot_divider),
                    ContextCompat.getColor(getContext(), R.color.backgroud)));
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mRvAdapter == null) {
            mRvAdapter = new RobotRefreshRecyclerAdapter(getContext());
            // mRvAdapter.refreshData();
            mRvAdapter.setOnItemClickListener(onItemClickListener);
            mRvAdapter.setOnItemLongClickListener(onItemLongClickListener);
            mRvAdapter.refreshData(mRobots);
        }
        mRecyclerView.setAdapter(mRvAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private RobotRefreshRecyclerAdapter.OnItemClickListener onItemClickListener = new RobotRefreshRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position, Robot robot) {
            if(robot == null) {
                // 新建机器人
                ActivityUtil.toAddRobotActivityForResult(getActivity(), robot, REQUEST_CODE_FOR_ADD_ROBOT);
            } else {
                // 调往聊天页
                ActivityUtil.toChatActivity(getContext(), robot);
            }
        }
    };

    private RobotRefreshRecyclerAdapter.OnItemLongClickListener onItemLongClickListener = new RobotRefreshRecyclerAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(View view, int position, Robot robot) {
            return true;
        }
    };

}

