package com.e7yoo.e7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.fragment.BaseFragment;
import com.e7yoo.e7.fragment.CircleFragment;
import com.e7yoo.e7.fragment.HomeFragment;
import com.e7yoo.e7.fragment.MineFragment;
import com.e7yoo.e7.fragment.MoreFragment;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.model.User;
import com.e7yoo.e7.service.E7Service;
import com.e7yoo.e7.sql.DbThreadPool;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.AppConfigsUtil;
import com.e7yoo.e7.util.BottomNavigationViewHelper;
import com.e7yoo.e7.util.CheckNotification;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ServiceUtil;
import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateStatus;

//import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;

/**
 * app主Activity
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 主页，萌圈，更多，我的
     */
    private final ArrayList<BaseFragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    private BottomNavigationView navigation;
    private final int[] titleResIds = {R.string.title_home, R.string.title_circle, /*R.string.title_findphone,*/ R.string.title_more, R.string.title_mine};
    private ImageView mMorePoint;
    private TextView mMinePoint;

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected String initTitle() {
        return getString(titleResIds[0]);
    }

    @Override
    protected void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mMorePoint = (ImageView) findViewById(R.id.more_point);
        mMinePoint = (TextView) findViewById(R.id.mine_point);
    }

    /**
     * 控制more菜单是否显示new图标
     * @param isShowNew 是否显示new图标，不传则去获取SharedPreference值，传入true或false则直接使用该值
     */
    public void showMoreNew(boolean... isShowNew) {
        boolean showNew;
        if(isShowNew == null || isShowNew.length == 0) {
            showNew = PreferenceUtil.getBoolean(Constant.PREFERENCE_MORE_POINT_NEW, true);
        } else {
            showNew = isShowNew[0];
        }
        if(showNew) {
            mMorePoint.setVisibility(View.VISIBLE);
        } else {
            mMorePoint.setVisibility(View.GONE);
        }
    }

    public void showMinePoint() {
        int count = PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
        showMinePoint(count);
    }

    public void showMinePoint(int count) {
        if(count > 0) {
            mMinePoint.setText(String.valueOf(count > 99 ? 99 : count));
            mMinePoint.setVisibility(View.VISIBLE);
        } else {
            mMinePoint.setText(String.valueOf(0));
            mMinePoint.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initSettings() {
        initPermission();
        initBmob();
        initRobot();
        setLeftTv(View.GONE);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        fragments.add(HomeFragment.newInstance());
        fragments.add(CircleFragment.newInstance());
        //fragments.add(MoreFragment.newInstance());
        fragments.add(MoreFragment.newInstance());
        fragments.add(MineFragment.newInstance());
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));

        CheckNotification.checkAndOpenNotification(this);

        // UpdateHelper.getInstance().init(getApplicationContext(), getResources().getColor(R.color.titlebar_bg));/*Color.parseColor("#459F47"));*/
        // UpdateHelper.getInstance().autoUpdate(getApplicationContext().getPackageName(), false, 12 * 60 * 60 * 1000);

        showMinePoint();
        showMoreNew();
        EventBusUtil.post(Constant.EVENT_BUS_REFRESH_UN_READ_MSG);

        E7App.mApp.queryAndLoadNewPatch();
    }

    private boolean isInitBmob = false;
    private void initBmob() {
        if(isInitBmob) {
            return;
        }

        /*try { 改为放入BaseActivity中
            Bmob.initialize(this.getApplicationContext(), "468e16137326f78942150e3f3e5d588f");
        */
        isInitBmob = true;
        /*} catch (Throwable e) {
            CrashReport.postCatchedException(e);
        }*/
        try {
            initBMobUpdate();
        } catch (Throwable e) {
            CrashReport.postCatchedException(e);
        }
        try {
            AppConfigsUtil.queryConfigs();
        } catch (Throwable e) {
            CrashReport.postCatchedException(e);
        }
    }

    private void initBMobUpdate() {
        BmobUpdateAgent.setUpdateCheckConfig(false);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        long showUpdateTime = PreferenceUtil.getLong(Constant.PREFERENCE_SHOW_UPDATE_TIME, 0);
        long now = System.currentTimeMillis();
        if(now - showUpdateTime > 3 * 24 * 60 * 60 * 1000) {
            PreferenceUtil.commitLong(Constant.PREFERENCE_SHOW_UPDATE_TIME, now);
            BmobUpdateAgent.update(this);

            //设置对对话框按钮的点击事件的监听
            BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {
                @Override
                public void onClick(int status) {
                    // TODO Auto-generated method stub
                    BmobUpdateAgent.setDialogListener(null);
                    switch (status) {
                        case UpdateStatus.Update:
                            // Toast.makeText(ActAutoUpdate.this, "点击了立即更新按钮" , Toast.LENGTH_SHORT).show();
                            break;
                        case UpdateStatus.NotNow:
                            // Toast.makeText(ActAutoUpdate.this, "点击了以后再说按钮" , Toast.LENGTH_SHORT).show();
                            BmobUpdateAgent.silentUpdate(MainActivity.this);
                            break;
                        case UpdateStatus.Close://只有在强制更新状态下才会在更新对话框的右上方出现close按钮,如果用户不点击”立即更新“按钮，这时候开发者可做些操作，比如直接退出应用等
                            // Toast.makeText(ActAutoUpdate.this, "点击了对话框关闭按钮" , Toast.LENGTH_SHORT).show();
                            BmobUpdateAgent.silentUpdate(MainActivity.this);
                            break;
                    }
                }
            });
        } else {
            long silentUpdateTime = PreferenceUtil.getLong(Constant.PREFERENCE_SHOW_UPDATE_TIME_silentUpdate, 0);

            if(now - silentUpdateTime > 10 * 60 * 1000) {
                PreferenceUtil.commitLong(Constant.PREFERENCE_SHOW_UPDATE_TIME_silentUpdate, silentUpdateTime);
                BmobUpdateAgent.silentUpdate(MainActivity.this);
            }
        }
//        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
//            @Override
//            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
//                if (updateStatus == UpdateStatus.Yes) {//版本有更新
//
//                }else if(updateStatus == UpdateStatus.No){
//                    //Toast.makeText(ActAutoUpdate.this, "版本无更新", Toast.LENGTH_SHORT).show();
//                }else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
//                    //Toast.makeText(ActAutoUpdate.this, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
//                }else if(updateStatus==UpdateStatus.IGNORED){
//                    //Toast.makeText(ActAutoUpdate.this, "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
//                    BmobUpdateAgent.silentUpdate(MainActivity.this);
//                }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
//                    //Toast.makeText(ActAutoUpdate.this, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
//                }else if(updateStatus==UpdateStatus.TimeOut){
//                    //Toast.makeText(ActAutoUpdate.this, "查询出错或查询超时", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void initRobot() {
        boolean init = PreferenceUtil.getBoolean(Constant.SP_IS_NEED_INIT_ROBOT, true);
        if (init) {
            DbThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Robot robot = new Robot(E7App.mApp);
                    long count = MessageDbHelper.getInstance(E7App.mApp).insertRobot(robot);
                    if (count > 0) {
                        EventBusUtil.post(Constant.EVENT_BUS_REFRESH_RecyclerView_INIT_ROBOT, robot);
                        PreferenceUtil.commitBoolean(Constant.SP_IS_NEED_INIT_ROBOT, false);
                    }
                }
            });
        }
    }

    @Override
    protected void initViewListener() {
        initViewPager(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mMinePoint.setOnClickListener(this);
    }

    MenuItem prevMenuItem;
    /**
     * @param scrollable ViewPager的onTouch是否有效，ViewPager是否可滑动
     */
    private void initViewPager(final boolean scrollable) {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return !scrollable;
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setItem(0);
                    return true;
                case R.id.navigation_circle:
                    setItem(1);
                    return true;
                /*case R.id.navigation_findphone:
                    setItem(2);
                    return true;*/
                case R.id.navigation_more:
                    setItem(titleResIds.length - 2);
                    return true;
                case R.id.navigation_mine:
                    setItem(titleResIds.length - 1);
                    return true;
            }
            return false;
        }

    };

    private void setPage(int position) {
        setTitleText(position);
        if (prevMenuItem != null) {
            prevMenuItem.setChecked(false);
        } else {
            navigation.getMenu().getItem(0).setChecked(false);
        }
        navigation.getMenu().getItem(position).setChecked(true);
        prevMenuItem = navigation.getMenu().getItem(position);
    }

    private void setItem(int position) {
        mViewPager.setCurrentItem(position);
        setTitleText(position);
    }

    public void showCircle() {
        setPage(1);
        setItem(1);
    }

    public void setTitleText(int position) {
        if(position == titleResIds.length - 1) {
            User user = User.getCurrentUser(User.class);
            if(user != null && !CommonUtil.isEmptyTrim(user.getNickname())) {
                setTitleTv(user.getNickname());
            } else {
                setTitleTv(titleResIds[position]);
            }
        } else {
            setTitleTv(titleResIds[position]);
        }
    }

    public void setTitleText(BaseFragment fragment) {
        if(fragment instanceof MineFragment) {
            setTitleText(titleResIds.length - 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_REFRESH_RecyclerView_ADD_ROBOT:
            case Constant.EVENT_BUS_REFRESH_RecyclerView_UPDATE_ROBOT:
            case Constant.EVENT_BUS_REFRESH_RecyclerView_INIT_ROBOT:
                if (!isFinishing() && fragments != null && fragments.size() > 0) {
                    if (fragments.get(0) != null) {
                        fragments.get(0).onEventMainThread(msg);
                    }
                }
                break;
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_SUCCESS:
                showMinePoint();
            case Constant.EVENT_BUS_CIRCLE_LOGIN:
            case Constant.EVENT_BUS_CIRCLE_LOGOUT:
            case Constant.EVENT_BUS_COMMUSER_MODIFY:
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_COMMENT_IS_READ:
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_LIKE_IS_READ:
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG_PUSH_IS_READ:
                if (!isFinishing() && fragments != null && fragments.size() > 0) {
                    if (fragments.get(fragments.size() - 1) != null) {
                        fragments.get(fragments.size() - 1).onEventMainThread(msg);
                    }
                }
                break;
            case Constant.EVENT_BUS_REFRESH_UN_READ_MSG:
                break;
            case Constant.EVENT_BUS_POST_FEED_SUCCESS:
            case Constant.EVENT_BUS_DELETE_FEED_SUCCESS:
                if (!isFinishing() && fragments != null && fragments.size() > 0) {
                    for(int i = 1; i < fragments.size(); i++) {
                        if(fragments.get(i) != null && fragments.get(i) instanceof CircleFragment) {
                            fragments.get(i).onEventMainThread(msg);
                            break;
                        }
                    }
                }
                break;
            case Constant.EVENT_BUS_NET_jokeRand:
            case Constant.EVENT_BUS_NET_jokeRand_pic:
                if (!isFinishing() && fragments != null && fragments.size() > 0) {
                    for(int i = 1; i < fragments.size(); i++) {
                        if(fragments.get(i) != null && fragments.get(i) instanceof CircleFragment) {
                            fragments.get(i).onEventMainThread(msg);
                            break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - onBackPressedTime > 3000) {
            TastyToast.makeText(this, getString(R.string.app_exit), TastyToast.LENGTH_SHORT, TastyToast.WARNING);
            onBackPressedTime = nowTime;
            return;
        }
        super.onBackPressed();

        ServiceUtil.startService(this.getApplicationContext(), new Intent(this, E7Service.class));
        //startService(new Intent(this, E7Service.class));
    }

    long onBackPressedTime = 0;

    String PERMISSIONS[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_PHONE_STATE,

            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            /*该权限无法弹出框口进行提醒*/Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.BROADCAST_PACKAGE_REMOVED,
            Manifest.permission.DELETE_PACKAGES,
            Manifest.permission.INSTALL_PACKAGES,
            Manifest.permission.REQUEST_DELETE_PACKAGES,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.PACKAGE_USAGE_STATS,

            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,

            Manifest.permission.CAMERA
    };

    String NOTIFY_PERMISSIONS[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_SMS,
    };

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            try {
                ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
            } catch (Throwable e) {
                CrashReport.postCatchedException(e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (permissions == null || grantResults == null || permissions.length != grantResults.length) {
                    return;
                }
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if(permission != null && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if(isNeedNotify(permission)) {
                            CheckPermissionUtil.AskForPermission(MainActivity.this, R.string.dialog_file_hint_title, R.string.dialog_file_hint);
                            return;
                        }

                    }
                }
                initBmob();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !getPackageManager().canRequestPackageInstalls()) {
                    CheckPermissionUtil.AskForInstallAppPermission(MainActivity.this);
                }
                break;
        }
    }

    private boolean isNeedNotify(String permission) {
        for(String p : NOTIFY_PERMISSIONS) {
            if(p.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_point:
                int position = titleResIds.length - 1;
                setItem(position);
                setTitleText(position);
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
                break;
        }
    }
}
