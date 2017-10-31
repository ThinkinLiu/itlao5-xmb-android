package com.e7yoo.e7.community;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.PostActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.model.TextSet;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.EventBusUtil;
import com.e7yoo.e7.util.PopupWindowUtil;
import com.e7yoo.e7.util.ProgressDialogEx;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.view.RecyclerViewDivider;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.CommentResponse;
import com.umeng.comm.core.nets.responses.FeedItemResponse;
import com.umeng.comm.core.nets.responses.ImageResponse;
import com.umeng.comm.core.nets.responses.PostCommentResponse;
import com.umeng.comm.core.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by andy on 2017/10/11.
 */

public class FeedDetailActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private FeedDetailRecyclerAdapter mRvAdapter;
    private FeedItem mFeedItem;
    private List<Comment> mComments;
    private String mNextPageUrl;

    private EditText mReplyEt;
    private ImageView mReplyIv;
    private ImageView mReplySend;

    private ImageView mReplyPicIv;

    private Comment mReplyComment;

    private ArrayList<String> mImgs;

    private HashMap<String, String> mContentMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> mImgMap = new HashMap<>();

    @Override
    protected String initTitle() {
        return getString(R.string.circle_feed_detail);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_feed_detail;
    }

    @Override
    protected void initView() {
        rootView = findViewById(R.id.root_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.feed_detail_rv);
        mReplyEt = (EditText) findViewById(R.id.feed_detail_input_edit);
        mReplyIv = (ImageView) findViewById(R.id.feed_detail_input_img);
        mReplySend = (ImageView) findViewById(R.id.feed_detail_input_send);

        mReplyPicIv = (ImageView) findViewById(R.id.feed_detail_input_pic);
    }

    @Override
    protected void initSettings() {
        if(getIntent() != null && getIntent().hasExtra("FeedItem")) {
            try {
                mFeedItem = getIntent().getParcelableExtra("FeedItem");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if(mFeedItem == null || mFeedItem.id == null) {
            TastyToastUtil.toast(this, R.string.circle_feed_not_exist);
            finish();
            return;
        }
        if(CommonUtils.isMyself(mFeedItem.creator)) {
            setRightTv(View.VISIBLE, R.mipmap.ic_menu_white_24dp, 0, this);
        } else {
            setRightTv(View.VISIBLE, R.mipmap.title_right_post, 0, this);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(getDivider());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRvAdapter = new FeedDetailRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRvAdapter);

        mRvAdapter.refreshData(mFeedItem, mFeedItem.comments);

        E7App.getCommunitySdk().fetchFeedWithId(mFeedItem.id, mFetchListener);
        mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
        E7App.getCommunitySdk().fetchFeedComments(mFeedItem.id, mSimpleFetchListener);

        mRvAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mRvAdapter.getItem(position) instanceof Comment) {
                    Comment comment = (Comment) mRvAdapter.getItem(position);
                    if(comment.creator != null && !TextUtils.isEmpty(comment.creator.name)) {
                        if(mReplyComment != comment) {
                            clearReplyInput(comment, 0, false);
                        }
                        mReplyEt.setHint(String.format(getString(R.string.feed_detail_input_edit_hint_reply_comment), comment.creator.name));
                    } else {
                        if(mReplyComment != null) {
                            clearReplyInput(null, 0, false);
                        }
                        mReplyEt.setHint(R.string.feed_detail_input_edit_hint);
                    }
                } else if(mRvAdapter.getItem(position) instanceof FeedItem) {
                    if(mReplyComment != null) {
                        clearReplyInput(null, 0, false);
                    }
                    mReplyEt.setHint(R.string.feed_detail_input_edit_hint);
                }
            }
        });
    }

    /**
     *
     * @param comment
     * @param hintStrId <= 0 时不改变原值
     */
    private void clearReplyInput(Comment comment, int hintStrId, boolean deleteCache) {
        resetInputCache(comment, deleteCache);
        mReplyComment = comment;
        // mReplyEt.setText("");
        if(hintStrId > 0) {
            mReplyEt.setHint(hintStrId);
        }
        /*if(mImgs != null) {
            mImgs.clear();
        }*/
        setPic();
        // mReplyIv.setImageResource(R.mipmap.feed_detail_input_img);
        mReplyPicIv.setVisibility(View.GONE);
        // Glide.with(FeedDetailActivity.this).load(R.mipmap.circle_img_add).into(mReplyPicIv);
    }

    private void resetInputCache(Comment comment, boolean deleteCache) {
        if(deleteCache) {
            if(mReplyComment != null) {
                mContentMap.remove(mReplyComment.id);
                mImgMap.remove(mReplyComment.id);
            } else {
                mContentMap.remove(mFeedItem.id);
                mImgMap.remove(mFeedItem.id);
            }
            mReplyEt.setText("");
        } else {
            if (mReplyComment != null) {
                mContentMap.put(mReplyComment.id, mReplyEt.getText().toString().trim());
                mImgMap.put(mReplyComment.id, mImgs);
            } else {
                mContentMap.put(mFeedItem.id, mReplyEt.getText().toString().trim());
                mImgMap.put(mFeedItem.id, mImgs);
            }
            if (comment != null) {
                String text = mContentMap.get(comment.id);
                mReplyEt.setText(text == null ? "" : text);
                mImgs = mImgMap.get(comment.id);
            } else {
                String text = mContentMap.get(mFeedItem.id);
                mReplyEt.setText(text == null ? "" : text);
                mImgs = mImgMap.get(mFeedItem.id);
            }
        }
    }

    private RecyclerViewDivider getDivider() {
        RecyclerViewDivider divider = new RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL,
                0,
                ContextCompat.getColor(this, R.color.backgroud),
                true,
                getResources().getDimensionPixelOffset(R.dimen.space_3x),
                true,
                getResources().getDimensionPixelOffset(R.dimen.item_robot_divider));
        return divider;
    }

    @Override
    protected void initViewListener() {
        initLoadMoreListener();
        mReplyEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && mReplyPicIv.getVisibility() == View.VISIBLE) {
                    mReplyPicIv.setVisibility(View.GONE);
                }
            }
        });
        mReplyIv.setOnClickListener(this);
        mReplySend.setOnClickListener(this);

        mReplyPicIv.setOnClickListener(this);
    }

    private Listeners.FetchListener<FeedItemResponse> mFetchListener = new Listeners.FetchListener<FeedItemResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(FeedItemResponse feedItemResponse) {
            if(feedItemResponse != null && feedItemResponse.result != null && TextUtils.isEmpty(feedItemResponse.result.id)) {
                mRvAdapter.refreshFeedItem(feedItemResponse.result);
                mFeedItem = feedItemResponse.result;
            }
        }
    };

    private Listeners.SimpleFetchListener<CommentResponse> mSimpleFetchListener = new Listeners.SimpleFetchListener<CommentResponse>() {
        @Override
        public void onComplete(CommentResponse commentResponse) {
            if(commentResponse != null && commentResponse.result != null) {
                mNextPageUrl = commentResponse.nextPageUrl;
                mComments = commentResponse.result;
                mRvAdapter.refreshComments(commentResponse.result);
                if(commentResponse.result.size() > 0) {
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
                } else {
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more_comment, false);
                }
            } else {
                mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more_comment, false);
            }
        }
    };

    private Listeners.FetchListener mCommentFetchListener = new Listeners.FetchListener<CommentResponse>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onComplete(CommentResponse commentResponse) {
            if(commentResponse != null && commentResponse.result != null && commentResponse.result.size() > 0) {
                mNextPageUrl = commentResponse.nextPageUrl;
                mComments = commentResponse.result;
                mRvAdapter.addItemBottom(commentResponse.result);
                mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
            } else {
                mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more_comment, false);
            }
        }
    };

    private void initLoadMoreListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (isNeedLoadMore(newState)) {
                    mRvAdapter.setFooter(FeedDetailRecyclerAdapter.FooterType.LOADING, R.string.loading, true);
                    if(mNextPageUrl == null) {
                        E7App.getCommunitySdk().fetchFeedComments(mFeedItem.id, mSimpleFetchListener);
                    } else {
                        E7App.getCommunitySdk().fetchNextPageData(mNextPageUrl, CommentResponse.class, mCommentFetchListener);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }

            private boolean isNeedLoadMore(int newState) {
                return !isFinishing() && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRvAdapter.getItemCount()
                        && mRvAdapter.getFooter() != FeedDetailRecyclerAdapter.FooterType.LOADING
                        && mRvAdapter.getFooter() != FeedDetailRecyclerAdapter.FooterType.NO_MORE;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_detail_input_img:
                if(mReplyPicIv.getVisibility() == View.VISIBLE) {
                    mReplyPicIv.setVisibility(View.GONE);
                } else {
                    mReplyPicIv.setVisibility(View.VISIBLE);
                    if (mImgs == null || mImgs.size() == 0) {
                        photoPicker(mImgs);
                    }
                }
                break;
            case R.id.feed_detail_input_send:
                String text = mReplyEt.getText().toString().trim();
                if(text.length() == 0 && (mImgs == null || mImgs.size() == 0 || TextUtils.isEmpty(mImgs.get(0)))) {
                    TastyToastUtil.toast(this, R.string.circle_feed_detail_reply_empty);
                } else {
                    if(Net.isNetWorkConnected(this)) {
                        if(CommonUtils.isLogin(this)) {
                            reply(text);
                        } else {
                            ActivityUtil.toLogin(this);
                        }
                    } else {
                        TastyToastUtil.toast(this, R.string.net_no);
                    }
                }
                break;
            case R.id.feed_detail_input_pic:
                if(mImgs == null || mImgs.size() == 0) {
                    photoPicker(mImgs);
                } else {
                    photoPreview(mImgs, 0);
                }
                break;
            case R.id.titlebar_right_tv:
                if(CommonUtils.isMyself(mFeedItem.creator)) {
                    showMore();
                } else {
                    toPost();
                }
                break;
        }
    }

    private void showMore() {
        ArrayList<TextSet> textSets = new ArrayList<>();
        textSets.add(new TextSet(R.string.feed_detail_title_right_post, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPost();
            }
        }));
        textSets.add(new TextSet(R.string.feed_detail_title_right_delete, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDelete();
            }
        }));
        PopupWindowUtil.showPopWindow(this, rootView, 0, textSets, true);
    }

    private void toPost() {
        Topic topic = mFeedItem != null && mFeedItem.topics != null && mFeedItem.topics.size() > 0 ? mFeedItem.topics.get(0) : null;
        ActivityUtil.toPostOrLogin(this, topic);
    }

    private void toDelete() {
        ArrayList<TextSet> textSets = new ArrayList<>();
        textSets.add(new TextSet(R.string.feed_detail_title_right_delete, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        }));
        PopupWindowUtil.showPopWindow(this, rootView, R.string.feed_detail_title_right_delete_hint, textSets, true);
    }

    private void delete() {
        showProgress(R.string.deleting);
        E7App.getCommunitySdk().deleteFeed(mFeedItem.id, new Listeners.CommListener() {
            @Override
            public void onStart() {

            }
            @Override
            public void onComplete(Response response) {
                dismissProgress();
                if(response.errCode == ErrorCode.NO_ERROR) {
                    TastyToastUtil.toast(FeedDetailActivity.this, R.string.delete_success);
                    EventBusUtil.post(Constant.EVENT_BUS_DELETE_FEED_SUCCESS, mFeedItem != null ? mFeedItem.id : null);
                    finish();
                } else {
                    TastyToastUtil.toast(FeedDetailActivity.this, R.string.delete_failed);
                }
            }
        });
    }

    private Comment getReplyComment(String text) {
        Comment comment = new Comment();
        comment.creator = CommConfig.getConfig().loginedUser;
        comment.feedId = mFeedItem.id;
        comment.text = text;
        if(mReplyComment != null) {
            comment.replyCommentId = mReplyComment.id;
            comment.replyUser = mReplyComment.creator;
        }
        return comment;
    }

    private void reply(String text) {
        replyImg(getReplyComment(text), mImgs, 0);
    }

    private void replyText(Comment comment) {
        E7App.getCommunitySdk().postCommentforResult(comment, new Listeners.FetchListener<PostCommentResponse>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(PostCommentResponse postCommentResponse) {
                if(postCommentResponse.errCode == ErrorCode.NO_ERROR && postCommentResponse.getComment() != null && !TextUtils.isEmpty(postCommentResponse.getComment().id)) {
                    mRvAdapter.addComment(postCommentResponse.getComment());
                    clearReplyInput(null, R.string.feed_detail_input_edit_hint, true);
                } else {
                    TastyToastUtil.toast(FeedDetailActivity.this, R.string.feed_detail_reply_failed);
                }
                dismissProgress();
            }
        });
    }

    private void replyImg(final Comment comment, final List<String> path, final int position) {
        if(position == 0) {
            showProgress(R.string.feed_detail_reply_ing);
        }
        if(path == null || position >= path.size()) {
            replyText(comment);
            return;
        } else if(TextUtils.isEmpty(path.get(position))) {
            replyImg(comment, path, position + 1);
            return;
        }
        E7App.getCommunitySdk().uploadImage(path.get(position), new Listeners.SimpleFetchListener<ImageResponse>() {
            @Override
            public void onComplete(ImageResponse imageResponse) {
                if(imageResponse.errCode != ErrorCode.NO_ERROR || imageResponse == null || imageResponse.result == null) {
                    TastyToastUtil.toast(FeedDetailActivity.this, R.string.feed_detail_reply_failed);
                    dismissProgress();
                    return;
                }
                if(comment.imageUrls == null) {
                    comment.imageUrls = new ArrayList<>();
                }
                comment.imageUrls.add(imageResponse.result);
                replyImg(comment, path, position + 1);
            }
        });
    }

    private void photoPicker(ArrayList<String> photoPaths) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .setSelected(photoPaths)
                .start(FeedDetailActivity.this, PhotoPicker.REQUEST_CODE);
    }

    private void photoPreview(ArrayList<String> photoPaths, int position) {
        PhotoPreview.builder()
                .setPhotos(photoPaths)
                .setCurrentItem(position)
                .setShowDeleteButton(true)
                .start(FeedDetailActivity.this, PhotoPreview.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        mImgs = photos;
                        setPic();
                    }
                }
                break;
            case PhotoPreview.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        mImgs = photos;
                        setPic();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setPic() {
        if(mImgs == null || mImgs.size() == 0) {
            Glide.with(FeedDetailActivity.this).load(R.mipmap.circle_img_add).into(mReplyPicIv);
            mReplyIv.setImageResource(R.mipmap.feed_detail_input_img);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.log_e7yoo_transport).error(R.mipmap.log_e7yoo_transport);
            Glide.with(FeedDetailActivity.this).load(mImgs.get(0)).apply(options).into(mReplyPicIv);
            mReplyIv.setImageResource(R.mipmap.feed_detail_input_img_selected);
        }
    }
}
