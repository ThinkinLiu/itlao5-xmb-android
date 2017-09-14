package com.e7yoo.e7.app.light;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.PreferenceUtil;

public class FlashLightActivity extends BaseActivity implements OnClickListener, IFlashControl {

    private ImageView flash_bg;
    private View on_off, set;
    private boolean isOpen = false;
    public static final String INTENT_FROM = "intent_from";

    private void init() {
        try {
            if (PreferenceUtil.getBoolean(FlashLightSetActivity.Preference_FlashLight_AutoOpen, false)) {
                openFlash();
            }
            if (PreferenceUtil.getBoolean(FlashLightSetActivity.Preference_FlashLight_CreateShotCut, false)) {
                ShotCut.createShortCut(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.on_off:
                if (isOpen) {
                    closeFlash();
                } else {
                    openFlash();
                }
                break;
            case R.id.set:
                startActivity(new Intent(this, FlashLightSetActivity.class));
                break;
            default:
                break;
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FlashLightWidget.ACTION_LED_ON.equals(intent.getAction())) {
                flashOpend();
            } else if (FlashLightWidget.ACTION_LED_OFF.equals(intent.getAction())) {
                flashClosed();
            }
        }
    };

    @Override
    public void closeFlash() {
        sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_OFF));
        flashClosed();
    }

    @Override
    public void openFlash() {
        sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_ON));
        flashOpend();
    }

    public void flashOpend() {
        isOpen = true;
        if (flash_bg != null) {
            flash_bg.setImageResource(R.mipmap.flashlight_on);
        }
    }

    public void flashClosed() {
        isOpen = false;
        if (flash_bg != null) {
            flash_bg.setImageResource(R.mipmap.flashlight_off);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mReceiver);
            closeFlash();
            CameraManager.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected String initTitle() {
        return getString(R.string.flashlight);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_flashlight;
    }

    @Override
    protected void initView() {
        flash_bg = (ImageView) findViewById(R.id.flash_bg);
        on_off = findViewById(R.id.on_off);
        set = findViewById(R.id.set);
    }

    @Override
    protected void initSettings() {
        CheckPermissionUtil.checkPermission(this, Manifest.permission.CAMERA, REQUEST_TO_CAMERA,
                R.string.dialog_camera_hint_title, R.string.dialog_camera_hint);
        IntentFilter filter = new IntentFilter();
        filter.addAction(FlashLightWidget.ACTION_LED_OFF);
        registerReceiver(mReceiver, filter);
        if (isOpen) {
            flashOpend();
        }
        if (getIntent() != null) {
            String from = getIntent().getStringExtra(INTENT_FROM);
            if ("shotcut".equals(from)) {
                openFlash();
            } else {
                init();
            }
        } else {
            init();
        }
    }

    @Override
    protected void initViewListener() {
        on_off.setOnClickListener(this);
        set.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TO_CAMERA:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
