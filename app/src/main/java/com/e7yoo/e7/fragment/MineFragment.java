package com.e7yoo.e7.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.AboutActivity;
import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.CollectActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.LoginActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.PushMsgActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.SettingsActivity;
import com.e7yoo.e7.model.User;
import com.e7yoo.e7.model.UserUtil;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.MyIconUtil;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.ShortCutUtils;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.util.UmengUtil;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MineFragment extends BaseFragment implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {
    private View mRootView;
    private RelativeLayout mHeadLayout;
    private ImageView mHeadIconIv;
    private TextView mine_label;
    private View mMsgLayout, mCollectLayout, mSetLayout, mShareLayout, mAboutLayout;
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
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
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
        mHeadLayout = mRootView.findViewById(R.id.mine_top_bg);
        mHeadIconIv = mRootView.findViewById(R.id.mine_icon);
        mine_label = mRootView.findViewById(R.id.mine_label);
        mMsgLayout = mRootView.findViewById(R.id.mine_msg_layout);
        mCollectLayout = mRootView.findViewById(R.id.mine_collect_layout);
        mSetLayout = mRootView.findViewById(R.id.mine_set_layout);
        mShareLayout = mRootView.findViewById(R.id.mine_share_layout);
        mAboutLayout = mRootView.findViewById(R.id.mine_about_layout);
        mineMsgPoint = mRootView.findViewById(R.id.mine_msg_point);
    }

    private void initListener() {
        mHeadLayout.setOnClickListener(this);
        mHeadIconIv.setOnClickListener(this);
        mMsgLayout.setOnClickListener(this);
        mCollectLayout.setOnClickListener(this);
        mShareLayout.setOnClickListener(this);
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
        User user = User.getCurrentUser(User.class);
        UserUtil.setIcon(getActivity(), mHeadIconIv, user);
        if(user == null) {
            mine_label.setText(R.string.mine_login_hint);
        } else {
            mine_label.setText(user.getLabel());
        }
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
            case R.id.mine_top_bg:
                break;
            case R.id.mine_icon:
                User user = User.getCurrentUser(User.class);
                if(user != null) {
                    // toTakePhone();
                    ActivityUtil.toUserInfo(getActivity(), user);
                } else {
                    ActivityUtil.toActivity(getActivity(), LoginActivity.class);
                }
                break;
            case R.id.mine_msg_layout:
                ActivityUtil.toActivity(getActivity(), PushMsgActivity.class);
                PreferenceUtil.commitInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                setMsgPoint();
                break;
            case R.id.mine_collect_layout:
                ActivityUtil.toActivity(getActivity(), CollectActivity.class);
                break;
            case R.id.mine_set_layout:
                ActivityUtil.toActivity(getActivity(), SettingsActivity.class);
                break;
            case R.id.mine_share_layout:
                ShareDialogUtil.show(getActivity(), null, getString(R.string.share_mine_title), getString(R.string.share_mine_content), null);
                UmengUtil.onEvent(UmengUtil.MINE_TO_SHARE);
                break;
            case R.id.mine_about_layout:
                ActivityUtil.toActivity(getActivity(), AboutActivity.class);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toTakePhone() {
        CropOptions cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
        CompressConfig compressConfig = new CompressConfig.Builder().setMaxSize(20 * 1024).setMaxPixel(300).create();
        getTakePhoto().onEnableCompress(compressConfig, true);
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        getTakePhoto().onPickFromGalleryWithCrop(imageUri, cropOptions);
    }

    private InvokeParam invokeParam;
    private TakePhoto takePhoto;

    private TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(isDetached() || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(getActivity(), type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        String path = result.getImage().getCompressPath();
        final User user = User.getCurrentUser(User.class);
        if(user != null && path != null) {
            showProgress(R.string.updateing);
            final BmobFile bmobFile = new BmobFile(new File(path));
            ((BaseActivity) getActivity()).addSubscription(bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null && getActivity() != null){ // 上传成功
                        user.setIcon(bmobFile.getFileUrl());
                        ((BaseActivity) getActivity()).addSubscription(user.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null && !isDetached()) { // 成功
                                    initDatas();
                                    dismissProgress();
                                } else {
                                    if(getActivity() != null) {
                                        TastyToastUtil.toast(getActivity(), R.string.update_icon_failed);
                                    }
                                    dismissProgress();
                                }
                            }
                        }));
                        initDatas();
                    } else { // 上传失败
                        if(getActivity() != null) {
                            TastyToastUtil.toast(getActivity(), R.string.update_icon_upload_failed);
                        }
                        dismissProgress();
                    }
                }
                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            }));
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        System.out.println("msg:" + msg);
        CrashReport.postCatchedException(new Throwable(msg));
    }

    @Override
    public void takeCancel() {
        System.out.println("takeCancel:");
    }
}
