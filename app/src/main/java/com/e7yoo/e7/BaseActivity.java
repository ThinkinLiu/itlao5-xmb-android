package com.e7yoo.e7;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.e7yoo.e7.app.light.FlashLightActivity;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.ProgressDialogEx;
import com.e7yoo.e7.util.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected View rootView;
    private View titleView;
    private TextView leftTv;
    private TextView titleTv;
    private TextView rightTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutResId());
        String title = initTitle();
        if(title != null) {
            initTitleBar(title);
        }
        initView();
        initSettings();
        initViewListener();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        if(initTheme()) {
            super.setContentView(layoutResId);
        } else {
            super.setContentView(layoutResId);
            initSystemBar();
        }
    }

    private void initTitleBar(String title) {
        titleView = findViewById(R.id.titleBar);
        leftTv = (TextView) findViewById(R.id.titlebar_left_tv);
        titleTv = (TextView) findViewById(R.id.titlebar_title_tv);
        rightTv = (TextView) findViewById(R.id.titlebar_right_tv);
        titleTv.setText(title);
        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        leftTv.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.GONE);
    }

    /**
     * text，用于title.setText（text），返回null表示不显示标题栏
     * @return text
     */
    protected abstract String initTitle();

    /**
     * 标题栏标题显示
     * @param text 标题
     * @param onClickListeners 点击事件
     */
    public void setTitleTv(String text, View.OnClickListener... onClickListeners) {
        if(titleTv == null) {
            return;
        }
        titleTv.setText(text);
        if(onClickListeners != null && onClickListeners.length > 0) {
            titleTv.setOnClickListener(onClickListeners[0]);
        }
    }

    /**
     * 标题栏标题显示
     * @param textResId 标题
     * @param onClickListeners 点击事件
     */
    protected void setTitleTv(int textResId, View.OnClickListener... onClickListeners) {
        if(titleTv == null) {
            return;
        }
        if(textResId == 0) {
            titleTv.setText("");
        } else {
            titleTv.setText(textResId);
        }
        if(onClickListeners != null && onClickListeners.length > 0) {
            titleTv.setOnClickListener(onClickListeners[0]);
        }
    }

    protected void setLeftTvListener(View.OnClickListener onClickListener) {
        if(leftTv == null) {
            return;
        }
        leftTv.setOnClickListener(onClickListener);
    }

    /**
     * 标题栏左边按钮
     * @param visibility 可见性
     */
    protected void setLeftTv(int visibility) {
        if(leftTv == null) {
            return;
        }
        leftTv.setVisibility(visibility);
    }

    /**
     * 标题栏左边按钮
     * @param visibility 可见性
     * @param drawableResId 显示在左边的图标
     * @param textResId 文字
     * @param onClickListener 点击事件
     */
    protected void setLeftTv(int visibility, int drawableResId, int textResId, View.OnClickListener onClickListener) {
        if(leftTv == null) {
            return;
        }
        leftTv.setVisibility(visibility);
        leftTv.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0);
        if(textResId == 0) {
            leftTv.setText("");
        } else {
            leftTv.setText(textResId);
        }
        leftTv.setOnClickListener(onClickListener);
    }

    /**
     * 标题栏右边按钮
     * @param visibility 可见性
     */
    protected void setRightTv(int visibility) {
        if (rightTv == null) {
            return;
        }
        rightTv.setVisibility(visibility);
    }

    /**
     * 标题栏右边按钮
     * @param visibility 可见性
     * @param drawableResId 显示在右边的图标
     * @param textResId 文字
     * @param onClickListener 点击事件
     */
    protected void setRightTv(int visibility, int drawableResId, int textResId, View.OnClickListener onClickListener) {
        if(rightTv == null) {
            return;
        }
        rightTv.setVisibility(visibility);
        rightTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0);
        if(textResId == 0) {
            rightTv.setText("");
        } else {
            rightTv.setText(textResId);
        }
        rightTv.setOnClickListener(onClickListener);
    }

    public void setRightTvTag(int tag) {
        if(rightTv == null) {
            return;
        }
        rightTv.setTag(R.id.titlebar_right_tv, tag);
    }

    public int getRightTvTag() {
        if(rightTv == null || rightTv.getTag() == null) {
            return 0;
        }
        return (int) rightTv.getTag();
    }

    /**
     * 主题设置，在setContentView之前调用
     * @return isOverride 重写后返回true
     */
    protected boolean initTheme() {
        //  在manifests中配置了no title   requestWindowFeature(Window.FEATURE_NO_TITLE);
        return false;
    }

    /**
     * layoutResId，用于setContentView（layoutResId）
     * @return layoutResId
     */
    protected abstract int initLayoutResId();

    /**
     * view初始化
     */
    protected abstract void initView();

    /**
     * 基础设置
     */
    protected abstract void initSettings();

    /**
     * view监听
     */
    protected abstract void initViewListener();

    public void hintTitle() {
        if(titleView == null) {
            return;
        }
        titleView.setVisibility(View.GONE);
    }

    public void showTitle() {
        if(titleView == null) {
            return;
        }
        titleView.setVisibility(View.VISIBLE);
    }

    /**
     * 沉侵式状态栏
     * 使用时在layout根布局增加
     *
     android:clipToPadding="true"
     android:fitsSystemWindows="true"
     */
    protected void initSystemBar() {
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        // 自定义颜色
        tintManager.setTintColor(getResources().getColor(R.color.titlebar_bg));
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Message msg) {
    }

    /** 用于手电筒 */
    protected final static int REQUEST_TO_CAMERA = 10000;
    /** 用于生成桌面图标 */
    protected final static int REQUEST_TO_SHOT_CUT = 10001;
    /** 用于短信 */
    protected final static int REQUEST_TO_SMS = 10002;
    /** 用于百度语音 */
    protected final static int REQUEST_TO_BD_VOICE = 10003;
    /** 用于百度语音 */
    protected final static int REQUEST_TO_RECV_SMS = 10004;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_TO_CAMERA:
                doPermissionsResult(grantResults, Manifest.permission.CAMERA, R.string.dialog_camera_hint_title, R.string.dialog_camera_hint);
                break;
            case REQUEST_TO_SHOT_CUT:
                doPermissionsResult(grantResults, Manifest.permission.INSTALL_SHORTCUT, R.string.dialog_shot_cut_hint_title, R.string.dialog_shot_cut_hint);
                break;
            case REQUEST_TO_SMS:
                doPermissionsResult(grantResults, Manifest.permission.CAMERA, R.string.dialog_camera_hint_title, R.string.dialog_camera_hint);
                break;
            case REQUEST_TO_BD_VOICE:
                doPermissionsResult(grantResults, Manifest.permission.RECORD_AUDIO, R.string.dialog_voice_hint_title, R.string.dialog_voice_hint);
                break;
            case REQUEST_TO_RECV_SMS:
                doPermissionsResult(grantResults, Manifest.permission.RECORD_AUDIO, R.string.dialog_voice_hint_title, R.string.dialog_voice_hint);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void doPermissionsResult(@NonNull int[] grantResults, String permission, int titleResId, int hintResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (grantResults.length > 0
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && !shouldShowRequestPermissionRationale(permission)) {
                // 选择了禁止权限，下次不会再提醒，在此给予提示
                CheckPermissionUtil.AskForPermission(BaseActivity.this, titleResId, hintResId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }



    protected ProgressDialogEx progressDialogEx;
    protected void showProgress(int loading){
        showProgress(loading, 30 * 1000);
    }
    protected void showProgress(int loading, int time){
        progressDialogEx = ProgressDialogEx.show(BaseActivity.this, "", getString(loading), true, time, new ProgressDialogEx.OnCancelListener2() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
            @Override
            public void onAutoCancel(DialogInterface dialog) {
            }
        });
    }

    protected void dismissProgress() {
        if(!isFinishing() && progressDialogEx != null && progressDialogEx.isShowing()) {
            progressDialogEx.dismiss();
            progressDialogEx = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected void addVisiable() {

    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
