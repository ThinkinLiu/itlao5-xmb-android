package com.e7yoo.e7.community;

import android.view.View;
import android.widget.EditText;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedItemResponse;

public class ForwardActivity extends BaseActivity implements View.OnClickListener {
    private EditText inputEt;
    private FeedItem mSourceFeedItem;

    @Override
    protected String initTitle() {
        return getString(R.string.forward_title);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_forward;
    }

    @Override
    protected void initView() {
        inputEt = (EditText) findViewById(R.id.forward_edit);
    }

    @Override
    protected void initSettings() {
        setRightTv(View.VISIBLE, R.mipmap.title_right_save, 0, this);
        if(getIntent() != null && getIntent().hasExtra("FeedItem")) {
            mSourceFeedItem = getIntent().getParcelableExtra("FeedItem");
        }
        if(mSourceFeedItem == null) {
            TastyToastUtil.toast(this, R.string.forward_failed, "");
            finish();
        }
    }

    @Override
    protected void initViewListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_right_tv:
                String text = inputEt.getText().toString().trim();
                forward(getFeedItem(mSourceFeedItem, text));
                break;
        }
    }

    private FeedItem getFeedItem(FeedItem sourceFeed, String text) {
        FeedItem feedItem = new FeedItem();
        feedItem.sourceFeed = sourceFeed;
        feedItem.sourceFeedId = sourceFeed.id;
        if(text == null || text.length() == 0) {
            feedItem.text = getString(R.string.feed_detail_forward_text);
        } else {
            feedItem.text = text;
        }
        return feedItem;
    }

    private void forward(FeedItem feedItem) {
        E7App.getCommunitySdk().forward(feedItem, new Listeners.SimpleFetchListener<FeedItemResponse>() {
            @Override
            public void onComplete(FeedItemResponse feedItemResponse) {
                if(feedItemResponse != null && feedItemResponse.errCode == ErrorCode.NO_ERROR) {
                    EventBusUtil.post(Constant.EVENT_BUS_POST_FEED_SUCCESS);
                    TastyToastUtil.toast(ForwardActivity.this, R.string.forward_success);
                    finish();
                } else {
                    String error = "";
                    if(feedItemResponse != null) {
                        error = String.valueOf(feedItemResponse.errCode);
                    }
                    TastyToastUtil.toast(ForwardActivity.this, R.string.forward_failed, error);
                }
            }
        });
    }
}
