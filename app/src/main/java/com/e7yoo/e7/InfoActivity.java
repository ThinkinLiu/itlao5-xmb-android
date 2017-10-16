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
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.CommUserUtil;
import com.e7yoo.e7.util.CommonUtil;
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
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.PortraitUploadResponse;
import com.umeng.comm.core.utils.CommonUtils;

import java.io.File;

public class InfoActivity extends BaseActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    public static final int REQUEST_CODE_FOR_INPUT_NAME = 1003;
    public static final int REQUEST_CODE_FOR_INPUT_WELCOME = 1004;
    public static final int REQUEST_CODE_FOR_INPUT_SEX = 1005;
    private View iconLayout, nameLayout, sexLayout, welcomeLayout;
    private ImageView iconIv;
    private TextView nameTv, sexTv, welcomeTv;
    private TextView saveTv;
    /**
     * 0 新增，1 修改
     */
    public int FLAG = 0;

    private CommUser mCommUser;

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
        saveTv = (TextView) findViewById(R.id.info_save);
    }

    @Override
    protected void initSettings() {
        if (getIntent() != null && getIntent().hasExtra("CommUser")) {
            mCommUser = (CommUser) getIntent().getParcelableExtra("CommUser");
            if (mCommUser != null) {
                finish();
                return;
            }
            setTitleTv(R.string.add_robot_title_robot);
            saveTv.setText(R.string.update);
            int resIcon = R.mipmap.icon_me;
            Glide.with(this).load(mCommUser.iconUrl).placeholder(resIcon).error(resIcon).into(iconIv);
            iconIv.setTag(R.id.info_icon_iv, mCommUser);
            String name = RobotUtil.getString(mCommUser.name);
            nameTv.setText(name);
            sexTv.setText(CommUserUtil.getSexText(mCommUser.gender));
            welcomeTv.setText(CommUserUtil.getExtraString(mCommUser.extraData, "welcome"));
        }
    }

    @Override
    protected void initViewListener() {
        iconLayout.setOnClickListener(this);
        if(mCommUser == null || !getString(R.string.mengmeng).equals(CommUserUtil.getString(mCommUser.id))) {
            nameLayout.setOnClickListener(this);
            sexLayout.setOnClickListener(this);
            welcomeLayout.setOnClickListener(this);
            saveTv.setOnClickListener(this);
            saveTv.setVisibility(View.VISIBLE);
        } else {
            saveTv.setVisibility(View.GONE);
        }
    }

    private static final int TAKE_PHOTO_FOR_ICON = 0;
    private int mFlag = TAKE_PHOTO_FOR_ICON;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info_icon_layout:
                toTakePhone(TAKE_PHOTO_FOR_ICON);
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
            case R.id.info_save:
                int result = checkEmpty();
                if (result == 0) {
                    boolean update = update();
                    if(update) {
                        showProgress(R.string.updateing);
                        E7App.getCommunitySdk().updateUserProfile(mCommUser, new Listeners.CommListener() {
                            @Override
                            public void onStart() {
                            }
                            @Override
                            public void onComplete(Response response) {
                                dismissProgress();
                                CommonUtils.saveLoginUserInfo(InfoActivity.this, mCommUser);
                                finish(mCommUser);
                            }
                        });
                    } else {
                        finish();
                    }
                } else {
                    TastyToastUtil.toast(this, result);
                }
                break;
        }
    }

    private void finish(CommUser commUser) {
        EventBusUtil.post(Constant.EVENT_BUS_COMMUSER_MODIFY, commUser);
        Intent intent = new Intent();
        intent.putExtra("CommUser", commUser);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean update() {
        if (mCommUser == null) {
            return false;
        }
        boolean result = false;
        // 头像单独修改 mCommUser.iconUrl = ;
        String name = nameTv.getText().toString().trim();
        if(!TextUtils.isEmpty(name) && !mCommUser.name.equals(mCommUser.name)) {
            mCommUser.name = name;
            result = true;
        }
        String welcome = welcomeTv.getText().toString().trim();
        if(welcome.equals(CommUserUtil.getExtraString(mCommUser.extraData, "welcome"))) {
            CommUserUtil.setExtraString(mCommUser, "welcome", welcome);
            result = true;
        }
        CommUser.Gender gender = CommUserUtil.getSex(sexTv.getText().toString().trim());
        if(gender != mCommUser.gender) {
            mCommUser.gender = CommUserUtil.getSex(sexTv.getText().toString().trim());
            result = true;
        }
        return result;
    }

    private void toTakePhone(int flag) {
        if (flag == 1) {
            getTakePhoto().onPickFromGallery();
        } else {
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
        this.mFlag = flag;
    }

    /**
     * 检查输入框内容是否合法，合法则返回0，否则返回需要toast的stringID
     *
     * @return
     */
    private int checkEmpty() {
        if (nameTv.getText().toString().trim().isEmpty()) {
            return R.string.info_name_empty;
        }
        return 0;
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
                        nameTv.setText(data.getStringExtra(Constant.INTENT_TEXT));
                    }
                    return;
                case REQUEST_CODE_FOR_INPUT_WELCOME:
                    if(data != null && data.hasExtra(Constant.INTENT_TEXT)) {
                        welcomeTv.setText(data.getStringExtra(Constant.INTENT_TEXT));
                    }
                    return;
                case REQUEST_CODE_FOR_INPUT_SEX:
                    if(data != null && data.hasExtra(Constant.INTENT_INT)) {
                        sexTv.setText(RobotUtil.getSexText(data.getIntExtra(Constant.INTENT_INT, 0)));
                    }
                    return;
            }
        }
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void takeSuccess(TResult result) {
        if (mFlag == TAKE_PHOTO_FOR_ICON) {
            String path = result.getImage().getCompressPath();
            showProgress(R.string.updateing);
            E7App.getCommunitySdk().updateUserProtrait(path, new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
                @Override
                public void onComplete(PortraitUploadResponse portraitUploadResponse) {
                    String mIconUrl = portraitUploadResponse.mIconUrl;
                    Glide.with(InfoActivity.this).load(mIconUrl).placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me).into(iconIv);
                    iconIv.setTag(R.id.info_icon_iv, mIconUrl);
                    mCommUser.iconUrl = mIconUrl;
                    CommonUtils.saveLoginUserInfo(InfoActivity.this, mCommUser);
                    dismissProgress();
                }
            });
        }
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
}
