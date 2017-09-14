package com.e7yoo.e7.app.light;

import android.Manifest;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.PreferenceUtil;

public class FlashLightSetActivity extends BaseActivity implements OnCheckedChangeListener {
    private ToggleButton openFlashBtn;
    private ToggleButton createShotCutBtn;
    public final static String Preference_FlashLight_AutoOpen = "preference_flashlight_autoopen";
    public final static String Preference_FlashLight_CreateShotCut = "preference_flashlight_createshotcut";

    @Override
    protected String initTitle() {
        return getString(R.string.flashlight_set);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_flashlight_settings;
    }

    @Override
    protected void initView() {
        openFlashBtn = (ToggleButton) findViewById(R.id.tb_open);
        createShotCutBtn = (ToggleButton) findViewById(R.id.tb_create_shotcut);
    }

    @Override
    protected void initSettings() {
        openFlashBtn.setChecked(PreferenceUtil.getBoolean(Preference_FlashLight_AutoOpen, false));
        createShotCutBtn.setChecked(PreferenceUtil.getBoolean(Preference_FlashLight_CreateShotCut, false));
    }

    @Override
    protected void initViewListener() {
        openFlashBtn.setOnCheckedChangeListener(this);
        createShotCutBtn.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.tb_open:
                if (isChecked) {
                    PreferenceUtil.commitBoolean(Preference_FlashLight_AutoOpen, true);
                } else {
                    PreferenceUtil.commitBoolean(Preference_FlashLight_AutoOpen, false);
                }
                break;
            case R.id.tb_create_shotcut:
                if (isChecked) {
                    PreferenceUtil.commitBoolean(Preference_FlashLight_CreateShotCut, true);
                    try {
                        CheckPermissionUtil.checkPermission(this, Manifest.permission.INSTALL_SHORTCUT, REQUEST_TO_SHOT_CUT,
                                R.string.dialog_shot_cut_hint_title, R.string.dialog_shot_cut_hint);
                        ShotCut.createShortCut(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PreferenceUtil.commitBoolean(Preference_FlashLight_CreateShotCut, false);
                }
                break;
            default:
                break;
        }
    }
}
