package com.e7yoo.e7;

import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.e7yoo.e7.adapter.ViewPagerAdapter;
import com.e7yoo.e7.app.light.NotificationControl;
import com.e7yoo.e7.fragment.BaseFragment;
import com.e7yoo.e7.fragment.CircleFragment;
import com.e7yoo.e7.fragment.HomeFragment;
import com.e7yoo.e7.fragment.MineFragment;
import com.e7yoo.e7.fragment.MoreFragment;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.sql.DbThreadPool;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.BottomNavigationViewHelper;
import com.e7yoo.e7.util.CheckNotification;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.PreferenceUtil;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * app主Activity
 */
public class MainActivity extends BaseActivity {
    /**
     * 主页，萌圈，更多，我的
     */
    private final ArrayList<BaseFragment> fragments = new ArrayList<>();
    private ViewPager mViewPager;
    private BottomNavigationView navigation;
    private final int[] titleResIds = {R.string.title_home, R.string.title_circle, R.string.title_more, R.string.title_mine};

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
    }

    @Override
    protected void initSettings() {
        initRobot();
        setLeftTv(View.GONE);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        fragments.add(HomeFragment.newInstance());
        fragments.add(CircleFragment.newInstance());
        fragments.add(MoreFragment.newInstance());
        fragments.add(MineFragment.newInstance());
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));

        CheckNotification.checkAndOpenNotification(this);
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
    }

    /**
     * @param scrollable ViewPager的onTouch是否有效，ViewPager是否可滑动
     */
    private void initViewPager(final boolean scrollable) {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            MenuItem prevMenuItem;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleTv(titleResIds[position]);
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
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
                case R.id.navigation_more:
                    setItem(2);
                    return true;
                case R.id.navigation_mine:
                    setItem(3);
                    return true;
            }
            return false;
        }

        private void setItem(int position) {
            mViewPager.setCurrentItem(position);
            setTitleTv(titleResIds[position]);
        }

    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Message msg) {
        switch (msg.what) {
            case Constant.EVENT_BUS_REFRESH_RecyclerView_ADD_ROBOT:
            case Constant.EVENT_BUS_REFRESH_RecyclerView_UPDATE_ROBOT:
            case Constant.EVENT_BUS_REFRESH_RecyclerView_INIT_ROBOT:
                if (!isFinishing() && fragments != null && fragments.size() > 0) {
                    BaseFragment fragment = fragments.get(0);
                    if (fragment != null) {
                        fragment.onEventMainThread(msg);
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
    }

    long onBackPressedTime = 0;
}
