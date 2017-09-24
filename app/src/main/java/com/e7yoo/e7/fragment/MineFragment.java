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

import com.bumptech.glide.Glide;
import com.e7yoo.e7.AboutActivity;
import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.SettingsActivity;
import com.e7yoo.e7.model.Me;
import com.e7yoo.e7.util.ActivityUtil;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mHeadIconIv;
    private TextView mine_label;
    private View mSpaceLayout, mMsgLayout, mSetLayout, mAboutLayout;
    private Me mMe;


    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onEventMainThread(Message msg) {

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
        mSetLayout = mRootView.findViewById(R.id.mine_set_layout);
        mAboutLayout = mRootView.findViewById(R.id.mine_about_layout);
    }

    private void initListener() {
        mSpaceLayout.setOnClickListener(this);
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
        if(mMe != null) {
            // 用户名在MainActivity中设置（setTitleText方法）
            Glide.with(getActivity()).load(mMe.getIcon()).placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me).into(mHeadIconIv);
            mine_label.setText(mMe.getLabel());
        }
        mine_label.setText(R.string.mine_label_hint);

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
            case R.id.mine_page_layout:
                break;
            case R.id.mine_msg_layout:
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
