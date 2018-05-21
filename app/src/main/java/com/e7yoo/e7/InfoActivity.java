package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.model.User;
import com.e7yoo.e7.model.UserUtil;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.RobotUtil;
import com.e7yoo.e7.util.TastyToastUtil;
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

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class InfoActivity extends BaseActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    public static final int REQUEST_CODE_FOR_INPUT_NAME = 1003;
    public static final int REQUEST_CODE_FOR_INPUT_WELCOME = 1004;
    public static final int REQUEST_CODE_FOR_INPUT_SEX = 1005;
    private View iconLayout, nameLayout, sexLayout, welcomeLayout;
    private ImageView iconIv;
    private TextView nameTv, sexTv, welcomeTv;
    private TextView logoutTv;
    /**
     * 0 新增，1 修改
     */
    public int FLAG = 0;

    private User mUser;

    @Override
    protected String initTitle() {
        return getString(R.string.title_info);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_info;
    }

    @Override
    protected void initView() {
        iconLayout = findViewById(R.id.info_icon_layout);
        nameLayout = findViewById(R.id.info_name_layout);
        sexLayout = findViewById(R.id.info_sex_layout);
        welcomeLayout = findViewById(R.id.info_welcome_layout);
        iconIv = (ImageView) findViewById(R.id.info_icon_iv);
        nameTv = (TextView) findViewById(R.id.info_name_tv);
        sexTv = (TextView) findViewById(R.id.info_sex_tv);
        welcomeTv = (TextView) findViewById(R.id.info_welcome_tv);
        logoutTv = (TextView) findViewById(R.id.info_logout_tv);
    }

    @Override
    protected void initSettings() {
        if (getIntent() != null && getIntent().hasExtra("User")) {
            mUser = (User) getIntent().getSerializableExtra("User");
            if (mUser == null) {
                finish();
                return;
            }
            setTitleTv(R.string.add_robot_title_robot);
            int resIcon = R.mipmap.icon_me;
            RequestOptions options = new RequestOptions();
            options.placeholder(resIcon).error(resIcon);
            Glide.with(this).load(mUser.getIcon()).apply(options).into(iconIv);
            nameTv.setText(mUser.getNickname());
            sexTv.setText(UserUtil.getSex(mUser.getSex()));
            welcomeTv.setText(mUser.getLabel());
        }
    }

    @Override
    protected void initViewListener() {
        iconLayout.setOnClickListener(this);
        nameLayout.setOnClickListener(this);
        sexLayout.setOnClickListener(this);
        welcomeLayout.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        logoutTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info_icon_layout:
                toTakePhone();
                break;
            case R.id.info_name_layout:
                ActivityUtil.toInputActivityForResult(this, R.string.info_name, 20, 1,
                        nameTv.getText().toString().trim(), getString(R.string.info_input_name_hint),  REQUEST_CODE_FOR_INPUT_NAME);
                break;
            case R.id.info_sex_layout:
                ActivityUtil.toSexActivityForResult(this, R.string.add_robot_sex, false, RobotUtil.getSex(sexTv.getText().toString().trim()), REQUEST_CODE_FOR_INPUT_SEX);
                break;
            case R.id.info_welcome_layout:
                ActivityUtil.toInputActivityForResult(this, R.string.add_robot_welcome, 30, 0,
                        welcomeTv.getText().toString().trim(), getString(R.string.info_input_welcome_hint), REQUEST_CODE_FOR_INPUT_WELCOME);
                break;
            case R.id.info_logout_tv:
                logout();
                break;
        }
    }

    private void logout() {
        User.logOut();
        EventBusUtil.post(Constant.EVENT_BUS_CIRCLE_LOGOUT);
        finish();
    }

    private boolean update() {
        if (mUser == null) {
            return false;
        }
        boolean result = false;
        // 头像单独修改 mUser.iconUrl = ;
        String name = nameTv.getText().toString().trim();
        if(!TextUtils.isEmpty(name) && !name.equals(mUser.getNickname())) {
            mUser.setNickname(name);
            result = true;
        }
        String welcome = welcomeTv.getText().toString().trim();
        if(!welcome.equals(mUser.getLabel())) {
            mUser.setLabel(welcome);
            result = true;
        }
        int sex = UserUtil.getSex(sexTv.getText().toString().trim());
        if(sex != mUser.getSex()) {
            mUser.setSex(sex);
            result = true;
        }
        return result;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case REQUEST_CODE_FOR_INPUT_NAME:
                    if(data != null && data.hasExtra(Constant.INTENT_TEXT)) {
                        String name = data.getStringExtra(Constant.INTENT_TEXT);
                        nameTv.setText(name);
                        mUser.setNickname(name);
                        updateUserProfile(true);
                    }
                    return;
                case REQUEST_CODE_FOR_INPUT_WELCOME:
                    if(data != null && data.hasExtra(Constant.INTENT_TEXT)) {
                        String welcome = data.getStringExtra(Constant.INTENT_TEXT);
                        welcomeTv.setText(welcome);
                        mUser.setLabel(welcome);
                        updateUserProfile(true);
                    }
                    return;
                case REQUEST_CODE_FOR_INPUT_SEX:
                    if(data != null && data.hasExtra(Constant.INTENT_INT)) {
                        int sexInt = data.getIntExtra(Constant.INTENT_INT, 0);
                        String sex = UserUtil.getSex(data.getIntExtra(Constant.INTENT_INT, 0));
                        sexTv.setText(sex);
                        mUser.setSex(sexInt);
                        updateUserProfile(true);
                    }
                    return;
            }
        }
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void takeSuccess(TResult result) {
        String path = result.getImage().getCompressPath();
        uploadImg(path);
    }

    private void uploadImg(String path) {
        showProgress(R.string.updateing);
        final BmobFile bmobFile = new BmobFile(new File(path));
        addSubscription(bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null && !isFinishing()){ // 上传成功
                    mUser.setIcon(bmobFile.getFileUrl());
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
                    Glide.with(InfoActivity.this).load(bmobFile.getFileUrl()).apply(options).into(iconIv);
                    updateUserProfile(false);
                } else { // 上传失败
                    TastyToastUtil.toast(InfoActivity.this, R.string.update_icon_upload_failed);
                    dismissProgress();
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        }));
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

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
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    private boolean hasUpdate = false;

    private void updateUserProfile(boolean showProgress) {
        if(showProgress) {
            showProgress(R.string.updateing);
        }
        addSubscription(mUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null) {
                    hasUpdate = true;
                } else {
                    TastyToastUtil.toast(InfoActivity.this, R.string.update_failed);
                }
                dismissProgress();
            }
        }));
    }

    @Override
    public void onBackPressed() {
        if(hasUpdate) {
            EventBusUtil.post(Constant.EVENT_BUS_COMMUSER_MODIFY, mUser);
        }
        super.onBackPressed();
    }
}
