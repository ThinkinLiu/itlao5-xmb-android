package com.e7yoo.e7;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.e7yoo.e7.community.FeedItemGvAdapter;
import com.e7yoo.e7.community.PostGvAdapter;
import com.e7yoo.e7.service.E7Service;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.CheckPermissionUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.Loc;
import com.e7yoo.e7.util.TastyToastUtil;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedItemResponse;
import com.umeng.comm.core.sdkmanager.ImagePickerManager;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class PostActivity extends BaseActivity implements View.OnClickListener {
    private View mTopicLayout;
    private TextView mTopicTv;
    private EditText mInputEt;
    private GridView mImgGv;
    private PostGvAdapter mGvAdapter;
    private TextView mLocationTv;
    private BDLocation mBDLocation;

    private Loc mLoc;

    public static final int REQUEST_TO_TOPIC_LIST = 100;

    @Override
    protected String initTitle() {
        return getString(R.string.title_post);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_post;
    }

    @Override
    protected void initView() {
        mTopicLayout = findViewById(R.id.post_topic_layout);
        mTopicTv = (TextView) findViewById(R.id.post_topic_tv);
        mInputEt = (EditText) findViewById(R.id.post_edit);
        mImgGv = (GridView) findViewById(R.id.post_gv);
        mLocationTv = (TextView) findViewById(R.id.post_loc_tv);
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
        initTopic(getIntent());
        mGvAdapter = new PostGvAdapter(this, null);
        mImgGv.setAdapter(mGvAdapter);
        Loc.getInstance(mLoc).startLocation(myListener);
    }

    @Override
    protected void initViewListener() {
        mTopicLayout.setOnClickListener(this);
        mImgGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mGvAdapter != null) {
                    if(mGvAdapter.getItem(position) == null) {
                        photoPicker(mGvAdapter.getDatas());
                    } else {
                        photoPreview(mGvAdapter.getDatas(), position);
                    }
                }
            }
        });
        mLocationTv.setOnClickListener(this);
    }

    private void photoPicker(ArrayList<String> photoPaths) {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .setSelected(photoPaths)
                .start(PostActivity.this, PhotoPicker.REQUEST_CODE);
    }

    private void photoPreview(ArrayList<String> photoPaths, int position) {
        PhotoPreview.builder()
                .setPhotos(photoPaths)
                .setCurrentItem(position)
                .setShowDeleteButton(true)
                .start(PostActivity.this);
    }

    long time = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_right_tv:
                long now = System.currentTimeMillis();
                if(now - time < 500) {
                    return;
                }
                time = now;
                Topic topic = checkTopic();
                String text = checkText();
                if(topic != null && text != null) {
                    FeedItem feedItem = new FeedItem();
                    feedItem.topics = new ArrayList<>();
                    feedItem.topics.add(topic);
                    feedItem.text = text;
                    // 发表的用户
                    feedItem.creator = CommConfig.getConfig().loginedUser;
                    feedItem.type = feedItem.creator.permisson == CommUser.Permisson.ADMIN ? 1 : 0;
                    feedItem.location = getLocation();
                    if(mBDLocation != null) {
                        feedItem.locationAddr = mBDLocation.getAddrStr();
                    }
                    if (mGvAdapter.getDatas() != null) {
                        for (String url : mGvAdapter.getDatas()) {
                            if(!TextUtils.isEmpty(url)) {
                                // 图片地址
                                feedItem.imageUrls.add(new ImageItem("", "", url));
                            }
                        }
                    }
                    post(feedItem);
                }
                break;
            case R.id.post_topic_layout:
                if(mTopicTv.getTag(R.id.post_topic_tv) != null && mTopicTv.getTag(R.id.post_topic_tv) instanceof Topic) {
                    ActivityUtil.toTopicListActivityForResult(this, (Topic) mTopicTv.getTag(R.id.post_topic_tv), REQUEST_TO_TOPIC_LIST);
                } else {
                    ActivityUtil.toTopicListActivityForResult(this, null, REQUEST_TO_TOPIC_LIST);
                }
                break;
            case R.id.post_loc_tv:
                try {
                    initPermission();
                    if(mBDLocation == null || TextUtils.isEmpty(mBDLocation.getAddrStr())) {
                        Loc.getInstance(mLoc).startLocation(myListener);
                    } else {
                        mLocationTv.setSelected(true);
                        mLocationTv.setText(mBDLocation.getAddrStr());
                    }
                } catch(Throwable e) {
                    Loc.getInstance(mLoc).startLocation(myListener);
                }
                break;
        }
    }

    private Location getLocation() {
        if(mBDLocation == null) {
            return null;
        }
        Location loc = null;
        switch (mBDLocation.getLocType()) {
            case BDLocation.TypeNetWorkLocation:
            if(loc == null) {
                loc = new Location(LocationManager.NETWORK_PROVIDER);
            }
            case BDLocation.TypeGpsLocation:
            if(loc == null) {
                loc = new Location(LocationManager.GPS_PROVIDER);
            }
            case BDLocation.TypeOffLineLocation:
            if(loc == null) {
                loc = new Location(LocationManager.PASSIVE_PROVIDER);
            }
        }
        loc.setLatitude(mBDLocation.getLatitude());
        loc.setLongitude(mBDLocation.getLongitude());
        return loc;
    }

    private Topic checkTopic() {
        if(mTopicTv.getTag(R.id.post_topic_tv) != null && mTopicTv.getTag(R.id.post_topic_tv) instanceof Topic) {
            return (Topic) mTopicTv.getTag(R.id.post_topic_tv);
        } else {
            TastyToastUtil.toast(this, R.string.post_topic_empty);
            return null;
        }
    }

    private String checkText() {
        String text = mInputEt.getText().toString();
        if(text.trim().length() > 0) {
            return text;
        } else {
            TastyToastUtil.toast(this, R.string.post_content_empty);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TO_TOPIC_LIST:
                if (resultCode == Activity.RESULT_OK) {
                    initTopic(data);
                }
                break;
            case PhotoPicker.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        mGvAdapter.refreshDatas(photos);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void post(final FeedItem feedItem) {
        showProgress(R.string.posting, 2 * 60 * 1000);
        E7App.getCommunitySdk().postFeed(feedItem, new Listeners.SimpleFetchListener<FeedItemResponse>() {
            @Override
            public void onComplete(FeedItemResponse feedItemResponse) {
                if(feedItemResponse.errCode == 0 && feedItemResponse.result != null) {
                    EventBusUtil.post(Constant.EVENT_BUS_POST_FEED_SUCCESS, feedItemResponse.result);
                    finish();
                } else {
                    TastyToastUtil.toast(PostActivity.this, R.string.post_failed);
                }
                dismissProgress();
            }
        });
    }

    private void initTopic(Intent data) {
        if(data == null) {
            return;
        }
        Topic topic = data.getParcelableExtra("Topic");
        if(topic != null) {
            mTopicTv.setText(topic.name);
            mTopicTv.setTag(R.id.post_topic_tv, topic);
        }
    }

    private BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location != null) {
                int locType = location.getLocType();
                switch (locType) {
                    case BDLocation.TypeNetWorkLocation:
                    case BDLocation.TypeGpsLocation:
                    case BDLocation.TypeOffLineLocation:
                        mBDLocation = location;
                        if(!TextUtils.isEmpty(location.getAddrStr())) {
                            mLocationTv.setSelected(true);
                            mLocationTv.setText(mBDLocation.getAddrStr());
                        }
                        Loc.getInstance(mLoc).stopLocation();
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        if(mLoc != null) {
            Loc.getInstance(mLoc).stopLocation();
            mLoc = null;
        }
        super.onDestroy();
    }




    String PERMISSIONS[] = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    String NOTIFY_PERMISSIONS[] = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
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
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
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
                    if(permission != null && grantResults[i] != PackageManager.PERMISSION_GRANTED
                            && isNeedNotify(permission)) {
                        CheckPermissionUtil.AskForPermission(PostActivity.this, R.string.dialog_location_hint_title, R.string.dialog_location_hint);
                        return;
                    }
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
}
