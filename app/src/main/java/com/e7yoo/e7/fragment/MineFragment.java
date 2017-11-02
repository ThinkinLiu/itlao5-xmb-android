package com.e7yoo.e7.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.AboutActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.MsgActivity;
import com.e7yoo.e7.PushMsgActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.SettingsActivity;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShortCutUtils;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.utils.CommonUtils;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mHeadIconIv;
    private TextView mine_label;
    private View mSpaceLayout, mMsgLayout, mCollectLayout, mSetLayout, mAboutLayout;
    private TextView mineMsgPoint, mCollectPoint;
    // private Me mMe;
    private CommUser mUser;


    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_CIRCLE_LOGIN:
            case Constant.EVENT_BUS_CIRCLE_LOGOUT:
            case Constant.EVENT_BUS_COMMUSER_MODIFY:
                if(msg.obj != null && msg.obj instanceof CommUser) {
                    initDatas((CommUser) msg.obj);
                } else {
                    initDatas(null);
                }
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
        mSpaceLayout = mRootView.findViewById(R.id.mine_page_layout);
        mMsgLayout = mRootView.findViewById(R.id.mine_msg_layout);
        mCollectLayout = mRootView.findViewById(R.id.mine_collect_layout);
        mSetLayout = mRootView.findViewById(R.id.mine_set_layout);
        mAboutLayout = mRootView.findViewById(R.id.mine_about_layout);
        mineMsgPoint = mRootView.findViewById(R.id.mine_msg_point);
        mCollectPoint = mRootView.findViewById(R.id.mine_collect_point);
    }

    private void initListener() {
        mHeadIconIv.setOnClickListener(this);
        mSpaceLayout.setOnClickListener(this);
        mMsgLayout.setOnClickListener(this);
        mCollectLayout.setOnClickListener(this);
        mSetLayout.setOnClickListener(this);
        mAboutLayout.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ((BaseActivity) getActivity()).hintTitle();
        initDatas(null);
    }

    private void initDatas(CommUser commUser) {
        if(CommonUtils.isLogin(getActivity())) {
            mUser = CommonUtils.getLoginUser(getActivity());
        } else {
            mUser = null;
        }
        if(mUser != null && !TextUtils.isEmpty(mUser.id)) {
            // 用户名在MainActivity中设置（setTitleText方法）
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
            Glide.with(getActivity()).load(mUser.iconUrl).apply(options).into(mHeadIconIv);
            mine_label.setText(mUser.name);
        } else {
            mHeadIconIv.setImageResource(R.mipmap.icon_me);
            mine_label.setText(R.string.mine_label_hint);
        }
        if(PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0) > 0
                || (mUser != null && mUser.unReadCount > 0)) {
            mineMsgPoint.setVisibility(View.VISIBLE);
        } else {
            mineMsgPoint.setVisibility(View.GONE);
        }
        mCollectPoint.setVisibility(View.GONE);
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
                if(CommonUtils.isLogin(getActivity())) {
                    ActivityUtil.toCommUserInfo(getActivity(), mUser);
                } else {
                    ActivityUtil.toLogin(getActivity());
                }
                break;
            case R.id.mine_page_layout:
                ActivityUtil.toSpace(getActivity(), mUser, true);
                break;
            case R.id.mine_msg_layout:
                if(CommonUtils.isLogin(getActivity()) && mUser != null) {
                    ActivityUtil.toActivity(getActivity(), MsgActivity.class);
                } else {
                    ActivityUtil.toActivity(getActivity(), PushMsgActivity.class);
                    PreferenceUtil.commitInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                    mineMsgPoint.setVisibility(View.GONE);
                    try {
                        ShortCutUtils.deleteShortCut(getContext(), MainActivity.class);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.mine_collect_layout:
                ActivityUtil.toCollect(getActivity(), true);
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
