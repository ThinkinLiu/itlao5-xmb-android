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

import com.e7yoo.e7.GameListActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.adapter.RobotRefreshRecyclerAdapter;
import com.e7yoo.e7.app.findphone.FindPhoneActivity;
import com.e7yoo.e7.app.light.FlashLightActivity;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.UmengUtil;
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

    /**
     *
     * @param msg
     * @param flag 0 增加data, 1 刷新data, 2 初始化后刷新data
     */
    private void refreshData(Message msg, int flag) {
        Robot robot = (Robot) msg.obj;
        if(robot != null) {
            if (mRobots == null) {
                mRobots = new ArrayList<>();
            }
            switch (flag) {
                case 0:
                    mRobots.add(robot);
                    break;
                case 1:
                    boolean hasRobot = false;
                    for(int i = 0; i < mRobots.size(); i++) {
                        if(mRobots.get(i) != null && mRobots.get(i).getId() == robot.getId()) {
                            mRobots.set(i, robot);
                            hasRobot = true;
                            break;
                        }
                    }
                    if(!hasRobot) {
                        mRobots.add(robot);
                    }
                    break;
                case 2:
                    if(mRobots.size() > 0) {
                        mRobots.set(0, robot);
                    } else {
                        mRobots.add(robot);
                    }
                    break;
            }
        }
        if(mRvAdapter != null && !isDetached()) {
            mRvAdapter.refreshData(mRobots);
        }
    }

    @Override
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_REFRESH_RecyclerView_ADD_ROBOT:
                refreshData(msg, 0);
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView_UPDATE_ROBOT:
                refreshData(msg, 1);
                break;
            case Constant.EVENT_BUS_REFRESH_RecyclerView_INIT_ROBOT:
                refreshData(msg, 2);
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
                    ContextCompat.getColor(getContext(), R.color.backgroud),
                    true,
                    getResources().getDimensionPixelOffset(R.dimen.space_3x)));
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
            mRvAdapter.setHeaderClickListener(headerClickListener);
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

    private RecyclerAdapter.OnItemClickListener onItemClickListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Robot robot = mRvAdapter.getRobot(position);
            if(robot == null) {
                // 新建机器人
                ActivityUtil.toAddRobotActivityForResult(getActivity(), robot, REQUEST_CODE_FOR_ADD_ROBOT);
            } else {
                // 调往聊天页
                ActivityUtil.toChatActivity(getContext(), robot);
            }
        }
    };

    private RecyclerAdapter.OnItemLongClickListener onItemLongClickListener = new RecyclerAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(View view, int position) {
            return true;
        }
    };

    private RobotRefreshRecyclerAdapter.HeaderClickListener headerClickListener = new RobotRefreshRecyclerAdapter.HeaderClickListener() {
        @Override
        public void onClickListener(View view, int index) {
            switch (index) {
                case 0:
                    if(getActivity() != null) {
                        ActivityUtil.toPostOrLogin(getActivity(), null);
                        UmengUtil.onEvent(UmengUtil.POST_FROM_HOME_HEADER);
                    }
                    break;
                case 1:
                    if(getActivity() != null) {
                        ActivityUtil.toActivity(getActivity(), GameListActivity.class);
                        UmengUtil.onEvent(UmengUtil.GAME_FROM_HOME_HEADER);
                    }
                    break;
                case 2:
                    if(getActivity() != null) {
                        ActivityUtil.toActivity(getActivity(), FindPhoneActivity.class);
                        UmengUtil.onEvent(UmengUtil.FINEPHONE_FROM_HOME_HEADER);
                    }
                    break;
                case 3:
                    if(getActivity() != null) {
                        ActivityUtil.toActivity(getActivity(), FlashLightActivity.class);
                        UmengUtil.onEvent(UmengUtil.FLASHLIGHT_FROM_HOME_HEADER);
                    }
                    break;
            }
        }
    };
}

