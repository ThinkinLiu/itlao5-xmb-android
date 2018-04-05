package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e7yoo.e7.AboutActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.PushMsgActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.SettingsActivity;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShortCutUtils;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mHeadIconIv;
    private TextView mine_label;
    private View mMsgLayout, mSetLayout, mAboutLayout;
    private TextView mPageHintTv;
    private TextView mineMsgPoint;


    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_CIRCLE_LOGIN:
            case Constant.EVENT_BUS_CIRCLE_LOGOUT:
            case Constant.EVENT_BUS_COMMUSER_MODIFY:
                initDatas();
                break;
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_SUCCESS:
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_COMMENT_IS_READ:
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_LIKE_IS_READ:
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_PUSH_IS_READ:
                setMsgPoint();
                break;
        }

    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_mine, container, false);
            initView();
            initListener();
        }
        return mRootView;
    }

    private void initView() {
        mHeadIconIv = mRootView.findViewById(R.id.mine_icon);
        mine_label = mRootView.findViewById(R.id.mine_label);
        mMsgLayout = mRootView.findViewById(R.id.mine_msg_layout);
        mSetLayout = mRootView.findViewById(R.id.mine_set_layout);
        mAboutLayout = mRootView.findViewById(R.id.mine_about_layout);
        mineMsgPoint = mRootView.findViewById(R.id.mine_msg_point);
    }

    private void initListener() {
        mHeadIconIv.setOnClickListener(this);
        mMsgLayout.setOnClickListener(this);
        mSetLayout.setOnClickListener(this);
        mAboutLayout.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ((BaseActivity) getActivity()).hintTitle();
        initDatas();
    }

    private void initDatas() {
        mHeadIconIv.setImageResource(R.mipmap.icon_me);
        mine_label.setText(R.string.mine_label_hint);
    }

    private void setMsgPoint() {
        int count = PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
        if(count > 0) {
            if(mineMsgPoint != null) {
                mineMsgPoint.setText(String.valueOf(count > 99 ? 99 : count));
                mineMsgPoint.setVisibility(View.VISIBLE);
            }
        } else {
            if(mineMsgPoint != null) {
                mineMsgPoint.setVisibility(View.GONE);
            }
        }
        try {
            try {
                if(getActivity() != null && !getActivity().isFinishing()) {
                    ((MainActivity) getActivity()).showMinePoint(count);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if(count > 0) {
                ShortCutUtils.addNumShortCut(E7App.mApp, MainActivity.class, true, String.valueOf(count));
            } else {
                ShortCutUtils.deleteShortCut(E7App.mApp, MainActivity.class);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            setMsgPoint();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_icon:
                break;
            case R.id.mine_msg_layout:
                ActivityUtil.toActivity(getActivity(), PushMsgActivity.class);
                PreferenceUtil.commitInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                setMsgPoint();
                break;
            case R.id.mine_set_layout:
                ActivityUtil.toActivity(getActivity(), SettingsActivity.class);
                break;
            case R.id.mine_about_layout:
                ActivityUtil.toActivity(getActivity(), AboutActivity.class);
                break;
        }
    }
}
