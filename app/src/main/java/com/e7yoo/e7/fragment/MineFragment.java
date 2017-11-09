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
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.MsgActivity;
import com.e7yoo.e7.PushMsgActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.SettingsActivity;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShortCutUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.utils.CommonUtils;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mHeadIconIv;
    private TextView mine_label;
    private View mSpaceLayout, mMsgLayout, mCollectLayout, mSetLayout, mAboutLayout;
    private TextView mPageHintTv;
    private TextView mPagePoint, mineMsgPoint, mCollectPoint;
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
        mSpaceLayout = mRootView.findViewById(R.id.mine_page_layout);
        mMsgLayout = mRootView.findViewById(R.id.mine_msg_layout);
        mCollectLayout = mRootView.findViewById(R.id.mine_collect_layout);
        mSetLayout = mRootView.findViewById(R.id.mine_set_layout);
        mAboutLayout = mRootView.findViewById(R.id.mine_about_layout);
        mPageHintTv = mRootView.findViewById(R.id.mine_page_hint);
        mPagePoint = mRootView.findViewById(R.id.mine_page_point);
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
        mPagePoint.setVisibility(View.GONE);
        setPagePoint();
        setMsgPoint();
        mCollectPoint.setVisibility(View.GONE);
    }

    private void setPagePoint() {
        if(mPageHintTv == null/* || mPagePoint == null*/) {
            return;
        }
        if(mUser != null && CommConfig.getConfig().mMessageCount != null
                && CommConfig.getConfig().mMessageCount.newFansCount > 0) {
            int count = CommConfig.getConfig().mMessageCount.newFansCount;
            mPageHintTv.setText(getString(R.string.new_fans_hint_count, count));
            // mineMsgPoint.setText(String.valueOf(count > 99 ? 99 : count));
            // mPagePoint.setVisibility(View.VISIBLE);
        } else {
            mPageHintTv.setText("");
            // mPagePoint.setVisibility(View.GONE);
        }
    }

    private void setMsgPoint() {
        int count = PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
        if(mUser != null && CommConfig.getConfig().mMessageCount != null) {
            count += CommConfig.getConfig().mMessageCount.unReadCommentsCount + CommConfig.getConfig().mMessageCount.unReadLikesCount;
        }
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
            if(mUser != null && CommConfig.getConfig().mMessageCount != null) {
                count = count + CommConfig.getConfig().mMessageCount.newFansCount;
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
