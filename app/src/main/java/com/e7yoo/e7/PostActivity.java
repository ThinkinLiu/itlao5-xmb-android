package com.e7yoo.e7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.e7yoo.e7.community.FeedItemGvAdapter;
import com.e7yoo.e7.community.PostGvAdapter;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
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
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
        initTopic(getIntent());
        mGvAdapter = new PostGvAdapter(this, null);
        mImgGv.setAdapter(mGvAdapter);
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
        }
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

    int i  =1;
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

}
