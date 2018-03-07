package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.PreferenceUtil;
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

public class AddRobotActivity extends BaseActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    public static final int REQUEST_CODE_FOR_INPUT_NAME = 1003;
    public static final int REQUEST_CODE_FOR_INPUT_WELCOME = 1004;
    public static final int REQUEST_CODE_FOR_INPUT_SEX = 1005;
    public static final int REQUEST_CODE_FOR_INPUT_VOICE = 1006;
    private View iconLayout, nameLayout, sexLayout, voiceLayout, welcomeLayout, bgLayout, bgBlurLayout;
    private ImageView iconIv, bgIv;
    private ToggleButton bgBlurTb;
    private TextView nameTv, sexTv, voiceTv, welcomeTv;
    private TextView saveTv;
    private View nameArrow;
    /**
     * 0 新增，1 修改
     */
    public int FLAG = 0;

    private Robot mRobot;

    @Override
    protected String initTitle() {
        return getString(R.string.title_add_robot);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_add_robot;
    }

    @Override
    protected void initView() {
        iconLayout = findViewById(R.id.add_robot_icon_layout);
        nameLayout = findViewById(R.id.add_robot_name_layout);
        sexLayout = findViewById(R.id.add_robot_sex_layout);
        voiceLayout = findViewById(R.id.add_robot_voice_layout);
        welcomeLayout = findViewById(R.id.add_robot_welcome_layout);
        bgLayout = findViewById(R.id.add_robot_bg_layout);
        bgBlurLayout = findViewById(R.id.add_robot_bg_blur_layout);
        iconIv = (ImageView) findViewById(R.id.add_robot_icon_iv);
        bgIv = (ImageView) findViewById(R.id.add_robot_bg_iv);
        bgBlurTb = (ToggleButton) findViewById(R.id.add_robot_bg_blur_tb);
        nameTv = (TextView) findViewById(R.id.add_robot_name_tv);
        nameArrow = findViewById(R.id.add_robot_name_arrow);
        sexTv = (TextView) findViewById(R.id.add_robot_sex_tv);
        voiceTv = (TextView) findViewById(R.id.add_robot_voice_tv);
        welcomeTv = (TextView) findViewById(R.id.add_robot_welcome_tv);
        saveTv = (TextView) findViewById(R.id.add_robot_save);
    }

    @Override
    protected void initSettings() {
        if (getIntent() != null && getIntent().hasExtra(Constant.INTENT_ROBOT)) {
            mRobot = (Robot) getIntent().getSerializableExtra(Constant.INTENT_ROBOT);
            if (mRobot != null) {
                FLAG = 1;
                setTitleTv(R.string.add_robot_title_robot);
                saveTv.setText(R.string.save);
                int resIcon = RobotUtil.getDefaultIconResId(mRobot);
                RequestOptions options = new RequestOptions();
                options.placeholder(resIcon).error(resIcon);
                Glide.with(this).load(mRobot.getIcon()).apply(options).into(iconIv);
                iconIv.setTag(R.id.add_robot_icon_iv, mRobot.getIcon());
                String name = RobotUtil.getString(mRobot.getName());
                if(getString(R.string.mengmeng).equals(name)) {
                    // 不能修改名字，名字与聊天消息对应，修改会找不到聊天消息
                    nameLayout.setBackgroundResource(R.color.item_unselected2);
                    nameLayout.setFocusable(false);
                    nameLayout.setClickable(false);
                    nameTv.setTextColor(getResources().getColor(R.color.text_l));
                    nameArrow.setVisibility(View.GONE);
                }
                nameTv.setText(name);
                sexTv.setText(RobotUtil.getSexText(mRobot.getSex()));
                voiceTv.setText(RobotUtil.getVoiceText(mRobot.getVoice()));
                welcomeTv.setText(RobotUtil.getString(mRobot.getWelcome()));
                Glide.with(this).load(mRobot.getBg()).into(bgIv);
                bgIv.setTag(R.id.add_robot_bg_iv, mRobot.getBg());
                if(mRobot.getBgblur() == 25) {
                    bgBlurTb.setChecked(true);
                }
            }
        }
    }

    @Override
    protected void initViewListener() {
        iconLayout.setOnClickListener(this);
        if(/*FLAG != 1 */mRobot == null || !getString(R.string.mengmeng).equals(RobotUtil.getString(mRobot.getName()))) {
            nameLayout.setOnClickListener(this);
        }
        sexLayout.setOnClickListener(this);
        voiceLayout.setOnClickListener(this);
        welcomeLayout.setOnClickListener(this);
        bgLayout.setOnClickListener(this);
        saveTv.setOnClickListener(this);
    }

    private static final int TAKE_PHOTO_FOR_ICON = 0;
    private static final int TAKE_PHOTO_FOR_BG = 1;
    private int mFlag = TAKE_PHOTO_FOR_ICON;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_robot_icon_layout:
                toTakePhone(TAKE_PHOTO_FOR_ICON);
                break;
            case R.id.add_robot_name_layout:
                ActivityUtil.toInputActivityForResult(this, R.string.add_robot_name, 20, 1,
                        nameTv.getText().toString().trim(), getString(R.string.input_name_hint),  REQUEST_CODE_FOR_INPUT_NAME);
                break;
            case R.id.add_robot_sex_layout:
                ActivityUtil.toSexActivityForResult(this, R.string.add_robot_sex, true, RobotUtil.getSex(sexTv.getText().toString().trim()), REQUEST_CODE_FOR_INPUT_SEX);
                break;
            case R.id.add_robot_voice_layout:
                ActivityUtil.toVoiceActivityForResult(this, R.string.add_robot_voice, true, RobotUtil.getVoice(voiceTv.getText().toString().trim()), REQUEST_CODE_FOR_INPUT_VOICE);
                break;
            case R.id.add_robot_welcome_layout:
                ActivityUtil.toInputActivityForResult(this, R.string.add_robot_welcome, 30, 0,
                        welcomeTv.getText().toString().trim(), getString(R.string.input_welcome_hint), REQUEST_CODE_FOR_INPUT_WELCOME);
                break;
            case R.id.add_robot_bg_layout:
                toTakePhone(TAKE_PHOTO_FOR_BG);
                break;
            case R.id.add_robot_save:
                int result = checkEmpty();
                if (result == 0) {
                    insertOrUpdate();
                    finish(mRobot);
                } else {
                    TastyToastUtil.toast(this, result);
                }
                break;
        }
    }

    private void finish(Robot robot) {
        if(FLAG == 0) {
            EventBusUtil.post(Constant.EVENT_BUS_REFRESH_RecyclerView_ADD_ROBOT, robot);
        } else {
            EventBusUtil.post(Constant.EVENT_BUS_REFRESH_RecyclerView_UPDATE_ROBOT, robot);
        }
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_ROBOT, robot);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void insertOrUpdate() {
        if (mRobot == null) {
            mRobot = new Robot();
            mRobot.setTime(System.currentTimeMillis());
            mRobot.setBirthTime(mRobot.getTime());
            mRobot.setScore(0);
        }
        mRobot.setName(nameTv.getText().toString().trim());
        mRobot.setWelcome(welcomeTv.getText().toString().trim());
        mRobot.setIcon(String.valueOf(iconIv.getTag(R.id.add_robot_icon_iv)));
        mRobot.setBg(String.valueOf(bgIv.getTag(R.id.add_robot_bg_iv)));
        mRobot.setBgblur(bgBlurTb.isChecked() ? 25 : 0);
        mRobot.setSex(RobotUtil.getSex(sexTv.getText().toString()));
        mRobot.setVoice(RobotUtil.getVoice(voiceTv.getText().toString()));
        if (FLAG == 0) {
            long id = MessageDbHelper.getInstance(this).insertRobot(mRobot);
            mRobot.setId((int) id);
        } else {
            MessageDbHelper.getInstance(this).updateRobot(mRobot);
        }
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
            return R.string.add_robot_name_empty;
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
                case REQUEST_CODE_FOR_INPUT_VOICE:
                    if(data != null && data.hasExtra(Constant.INTENT_INT)) {
                        voiceTv.setText(RobotUtil.getVoiceText(data.getIntExtra(Constant.INTENT_INT, 4)));
                    }
                    return;
            }
        }
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void takeSuccess(TResult result) {
        if (mFlag == 0) {
            String path = result.getImage().getCompressPath();
            int resIcon = RobotUtil.getDefaultIconResId(mRobot);
            RequestOptions options = new RequestOptions();
            options.placeholder(resIcon).error(resIcon);
            Glide.with(this).load(path).apply(options).into(iconIv);
            iconIv.setTag(R.id.add_robot_icon_iv, path);
        } else {
            String path = result.getImage().getOriginalPath();
            Glide.with(this).load(path).into(bgIv);
            bgIv.setTag(R.id.add_robot_bg_iv, path);
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
