package com.e7yoo.e7.community;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.PostActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.CircleGvAdapterUtil;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.BaseBeanUtil;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.TimeUtil;
import com.e7yoo.e7.util.UmengUtil;
import com.e7yoo.e7.view.CircleGridView;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.SimpleResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPreview;

/**
 * Created by Administrator on 2017/9/25.
 */
public class FeedDetailRecyclerAdapter extends ListRefreshRecyclerAdapter {
    private static final int VIEW_TYPE_FEEDITEM = 100;

    public void refreshData(FeedItem item, List<Comment> commentList) {
        mDatas.clear();
        mDatas.add(item);
        mDatas.addAll(commentList);
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        int num = 0;
        if(mDatas.size() > 0 && mDatas.get(0) instanceof  FeedItem) {
            num = 1;
        }
        mDatas.add(num, comment);
        notifyDataSetChanged();
    }

    public void refreshFeedItem(FeedItem item) {
        if(mDatas.size() > 0) {
            if(mDatas.get(0) instanceof  FeedItem) {
                mDatas.set(0, item);
                return;
            }
        }
        mDatas.add(0, item);
        notifyDataSetChanged();
    }

    public void refreshComments(List<Comment> commentList) {
        FeedItem item = null;
        if(mDatas.size() > 0 && mDatas.get(0) instanceof  FeedItem) {
            item = (FeedItem) mDatas.get(0);
        }
        mDatas.clear();
        if(item != null) {
            mDatas.add(item);
        }
        mDatas.addAll(commentList);
        notifyDataSetChanged();
    }

    public FeedDetailRecyclerAdapter(Activity context) {
        super(context);
    }

    @Override
    public int initItemViewType(int position) {
        if(getItem(position) instanceof FeedItem) {
            return VIEW_TYPE_FEEDITEM;
        } else {
            return super.initItemViewType(position);
        }
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_FEEDITEM) {
            View view = mInflater.inflate(R.layout.item_feed_item_detail, parent, false);
            return new ViewHolderFeedItem(view);
        } else {
            View view = mInflater.inflate(R.layout.item_comment, parent, false);
            return new ViewHolderComment(view);
        }
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderFeedItem) {
            ViewHolderFeedItem viewHolderFeedItem = (ViewHolderFeedItem) holder;
            FeedItem item = (FeedItem) mDatas.get(position);
            if (item != null) {
                setUser(viewHolderFeedItem, item.creator);
                setViewTypeFeeditem(viewHolderFeedItem, item);
                setViewRichContent(viewHolderFeedItem, item);
            }
            addItemClickForGridView(viewHolderFeedItem.gridView, viewHolderFeedItem.itemView, position);
            addClickListener(viewHolderFeedItem.itemView, position);
        } else if(holder instanceof ViewHolderComment) {
            ViewHolderComment viewHolderComment = (ViewHolderComment) holder;
            Comment item = (Comment) mDatas.get(position);
            setUser(viewHolderComment, item.creator);
            setViewTypeComment(viewHolderComment, item);
            viewHolderComment.hint.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            viewHolderComment.divide.setVisibility(position < mDatas.size() - 1 ? View.VISIBLE : View.GONE);
            addItemClickForGridView(viewHolderComment.gridView, viewHolderComment.itemView, position);
            addClickListener(viewHolderComment.itemView, position);
        }
    }

    private void setViewRichContent(ViewHolderFeedItem viewHolderFeedItem, FeedItem item) {
        String more = BaseBeanUtil.getExtraString(item, BaseBeanUtil.TEXT_MORE);
        if(TextUtils.isEmpty(more) || more.trim().length() < 5) {
            viewHolderFeedItem.urlTv.setVisibility(View.GONE);
            viewHolderFeedItem.urlTv.setOnClickListener(null);
            return;
        }
        viewHolderFeedItem.urlTv.setVisibility(View.VISIBLE);
        if(more.trim().contains(Constant.CIRCLE_URL_TITLE)) {
            String[] mores = more.split(Constant.CIRCLE_URL_TITLE, 2);
            viewHolderFeedItem.urlTv.setText(mores[0]);
            more = mores[1];
        } else {
            viewHolderFeedItem.urlTv.setText(R.string.feed_detail_url);
        }
        final String url = more.startsWith("http://") || more.startsWith("https://") ? more : "http://" + more;
        viewHolderFeedItem.urlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toNewsWebviewActivity(mContext, url, NewsWebviewActivity.INTENT_FROM_FEED_DETAILS);
                UmengUtil.onEvent(UmengUtil.FEED_DETAILS);
            }
        });
        /* String more = BaseBeanUtil.getExtraString(item, BaseBeanUtil.TEXT_MORE);
        if(!TextUtils.isEmpty(more)) {
            viewHolderFeedItem.moreContentTv.setText(CommonUtil.getHtmlStr(more));
            setMore(viewHolderFeedItem, false);
            return;
        } else {
            viewHolderFeedItem.moreContentTv.setText("");
            setMore(viewHolderFeedItem, null);
        }*/
    }

    /**
     *
     * @param viewHolderFeedItem
     * @param open true,当前是展开状态， false,当前是收起状态， null,按钮不可见
     */
    private void setMore(final ViewHolderFeedItem viewHolderFeedItem, Boolean open) {
        /*if(open == null) {
            viewHolderFeedItem.moreContentTv.setVisibility(View.GONE);
            viewHolderFeedItem.moreLayout.setOnClickListener(null);
            viewHolderFeedItem.moreTv.setVisibility(View.GONE);
        } else if(open) {
            viewHolderFeedItem.moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMore(viewHolderFeedItem, false);
                }
            });
            viewHolderFeedItem.moreContentTv.setVisibility(View.VISIBLE);
            viewHolderFeedItem.moreTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.feed_detail_more_close, 0, 0, 0);
            viewHolderFeedItem.moreTv.setText(R.string.feed_detail_more_open);
            viewHolderFeedItem.moreTv.setVisibility(View.VISIBLE);
        } else {
            viewHolderFeedItem.moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMore(viewHolderFeedItem, true);
                }
            });
            viewHolderFeedItem.moreContentTv.setVisibility(View.GONE);
            viewHolderFeedItem.moreLayout.setOnClickListener(null);
            viewHolderFeedItem.moreTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.feed_detail_more_open, 0, 0, 0);
            viewHolderFeedItem.moreTv.setText(R.string.feed_detail_more_close);
            viewHolderFeedItem.moreTv.setVisibility(View.VISIBLE);
        }*/
    }

    private void setUser(BaseViewHolder viewHolderFeedItem, CommUser item) {
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.icon_me)
                .error(R.mipmap.icon_me)
                .override(124, 124);
        Glide.with(mContext)
                .load(item.iconUrl)
                .apply(options)
                .into(viewHolderFeedItem.userIcon);
        int sexIcon;
        switch (item.gender) {
            case FEMALE:
                sexIcon = R.mipmap.sex_female_selected;
                break;
            case MALE:
                sexIcon = R.mipmap.sex_male_selected;
                break;
            default:
                sexIcon = R.mipmap.sex_unknow_selected;
                break;
        }
        viewHolderFeedItem.sexIcon.setImageResource(sexIcon);
        viewHolderFeedItem.usernameTv.setText(item.name);
        addIconClick(viewHolderFeedItem.userIcon, item);
    }

    private void setViewTypeFeeditem(final BaseViewHolder viewHolderFeedItem, final FeedItem item) {
        viewHolderFeedItem.contentTv.setMaxLines(1000);
        String content = "";
        if(item.sourceFeed != null && item.sourceFeed.creator != null) {
            content = item.text + "<font color= 'blue'> @" + item.sourceFeed.creator.name + " </font>\n\n--------------------\n";
            if (item.sourceFeed.topics != null) {
                for (int i = 0; i < item.sourceFeed.topics.size(); i++) {
                    if (item.sourceFeed.topics.get(i) != null && !TextUtils.isEmpty(item.sourceFeed.topics.get(i).name)) {
                        content = content + "<font color= 'blue'>" + item.sourceFeed.topics.get(i).name + " </font>";
                    }
                }
            }
            content = content + item.sourceFeed.text;
        } else {
            if (item.topics != null) {
                for (int i = 0; i < item.topics.size(); i++) {
                    if (item.topics.get(i) != null && !TextUtils.isEmpty(item.topics.get(i).name)) {
                        content = content + "<font color= 'blue'>" + item.topics.get(i).name + " </font>";
                    }
                }
            }
            content = content + item.text;
        }
        viewHolderFeedItem.contentTv.setText(CommonUtil.getHtmlStr(content));
        CircleGvAdapterUtil.setGridView(mContext, viewHolderFeedItem.gridView, item.getImages(), mGvItemClick);
        viewHolderFeedItem.timeTv.setText(TimeUtil.formatFeedTime(item.publishTime));
        viewHolderFeedItem.shareTv.setText(String.format("%-3d", item.forwardCount));
        viewHolderFeedItem.commentTv.setText(String.format("%-3d", item.commentCount));
        viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount));
        viewHolderFeedItem.praiseTv.setSelected(item.isLiked);
        viewHolderFeedItem.praiseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.isLiked) {
                    viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount - 1));
                    viewHolderFeedItem.praiseTv.setSelected(!item.isLiked);
                } else {
                    viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount + 1));
                    viewHolderFeedItem.praiseTv.setSelected(!item.isLiked);
                }
                likeFeed(viewHolderFeedItem, item);
            }
        });
    }

    private void setViewTypeComment(final BaseViewHolder viewHolder, final Comment item) {
        viewHolder.contentTv.setMaxLines(1000);
        String reply = "";
        if(item.replyUser != null && !TextUtils.isEmpty(item.replyUser.name)) {
            reply = "回复 <font color= 'blue'>" + item.replyUser.name + "</font> ";
        }
        viewHolder.contentTv.setText(CommonUtil.getHtmlStr(reply + item.text));
        CircleGvAdapterUtil.setGridView(mContext, viewHolder.gridView, item.imageUrls, mGvItemClick);
        viewHolder.timeTv.setText(TimeUtil.formatFeedTime(item.createTime));
        viewHolder.shareTv.setVisibility(View.GONE);
        viewHolder.commentTv.setVisibility(View.GONE);
        viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount));
        viewHolder.praiseTv.setSelected(item.liked);
        viewHolder.praiseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.liked) {
                    viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount - 1));
                    viewHolder.praiseTv.setSelected(!item.liked);
                } else {
                    viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount + 1));
                    viewHolder.praiseTv.setSelected(!item.liked);
                }
                likeComment(viewHolder, item);
            }
        });
    }

    private void likeFeed(final BaseViewHolder viewHolder, final FeedItem item) {
        if(item.isLiked) {
            E7App.getCommunitySdk().postUnLike(item.id, new Listeners.SimpleFetchListener<SimpleResponse>() {
                @Override
                public void onComplete(SimpleResponse simpleResponse) {
                    if(simpleResponse.errCode == ErrorCode.NO_ERROR) {
                        item.isLiked = false;
                        item.likeCount--;
                        viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount));
                        viewHolder.praiseTv.setSelected(item.isLiked);
                    }
                }
            });
        } else {
            E7App.getCommunitySdk().postLike(item.id, new Listeners.SimpleFetchListener<SimpleResponse>() {
                @Override
                public void onComplete(SimpleResponse simpleResponse) {
                    if(simpleResponse.errCode == ErrorCode.NO_ERROR) {
                        item.isLiked = true;
                        item.likeCount++;
                        viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount));
                        viewHolder.praiseTv.setSelected(item.isLiked);
                    }
                }
            });
        }
    }

    private void likeComment(final BaseViewHolder viewHolder, final Comment comment) {
        E7App.getCommunitySdk().likeComment(comment, new Listeners.FetchListener<SimpleResponse>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(SimpleResponse simpleResponse) {
                if(simpleResponse.errCode == ErrorCode.NO_ERROR) {
                    if(simpleResponse.mJsonObject != null) {
                        int addScroe = simpleResponse.mJsonObject.optInt("add_score", 0);
                        if(addScroe == 0) {
                        } else if(addScroe > 0) {
                            comment.liked = true;
                            comment.likeCount++;
                        } else {
                            comment.liked = false;
                            comment.likeCount--;
                        }
                        viewHolder.praiseTv.setText(String.format("%-3d", comment.likeCount));
                        viewHolder.praiseTv.setSelected(comment.liked);
                    }
                }
            }
        });

    }

    private AdapterView.OnItemClickListener mGvItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(parent != null && parent.getAdapter() != null && parent.getAdapter() instanceof FeedItemGvAdapter) {
                FeedItemGvAdapter adapter = (FeedItemGvAdapter) parent.getAdapter();
                ArrayList<String> images = new ArrayList<>();
                for(ImageItem imageItem : adapter.getDatas()) {
                    if(imageItem != null && imageItem.originImageUrl != null) {
                        images.add(imageItem.originImageUrl);
                    }
                }
                if(images.size() > 0) {
                    if(position >= images.size()) {
                        position = images.size() - 1;
                    }
                    PhotoPreview.builder()
                            .setPhotos(images)
                            .setCurrentItem(position)
                            .setShowDeleteButton(false)
                            .start(mContext);
                }
            }
        }
    };


    private void addIconClick(View view, final CommUser commUser) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commUser != null) {
                    ActivityUtil.toSpace(mContext, commUser, false);
                }
            }
        });
    }

    private void addItemClickForGridView(GridView gridView, final View mView, final int mPosition) {
        if(gridView instanceof CircleGridView) {
            ((CircleGridView) gridView).setOnTouchInvalidPositionListener(new CircleGridView.OnTouchInvalidPositionListener() {
                @Override
                public boolean onTouchInvalidPosition(int motionEvent) {
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mView, mPosition);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void addClickListener(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public View rootLayout;
        public ImageView userIcon;
        public ImageView sexIcon;
        public TextView usernameTv;
        public TextView contentTv;
        public GridView gridView;
        public TextView timeTv;
        public TextView shareTv;
        public TextView commentTv;
        public TextView praiseTv;

        public BaseViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }

    /**
     * 消息item
     */
    public static class ViewHolderFeedItem extends BaseViewHolder {
        /*public TextView moreContentTv;
        public View moreLayout;
        public TextView moreTv;*/
        public TextView urlTv;


        public ViewHolderFeedItem(View view) {
            super(view);
            userIcon = view.findViewById(R.id.item_feed_item_icon);
            sexIcon = view.findViewById(R.id.item_feed_item_sex);
            usernameTv = view.findViewById(R.id.item_feed_item_username);
            contentTv = view.findViewById(R.id.item_feed_item_content);
            gridView = view.findViewById(R.id.item_feed_item_gv);
            timeTv = view.findViewById(R.id.item_feed_item_time);
            shareTv = view.findViewById(R.id.item_feed_item_share);
            commentTv = view.findViewById(R.id.item_feed_item_comment);
            praiseTv = view.findViewById(R.id.item_feed_item_praise);

            /*moreContentTv = view.findViewById(R.id.item_feed_item_content_more);
            moreLayout = view.findViewById(R.id.item_feed_item_more_layout);
            moreTv = view.findViewById(R.id.item_feed_item_more);*/

            urlTv = view.findViewById(R.id.item_feed_item_url);
        }
    }

    /**
     * 消息item
     */
    public static class ViewHolderComment extends BaseViewHolder {
        private TextView hint;
        public View divide;

        public ViewHolderComment(View view) {
            super(view);
            hint = view.findViewById(R.id.hint);
            userIcon = view.findViewById(R.id.item_comment_icon);
            sexIcon = view.findViewById(R.id.item_comment_sex);
            usernameTv = view.findViewById(R.id.item_comment_username);
            contentTv = view.findViewById(R.id.item_comment_content);
            gridView = view.findViewById(R.id.item_comment_gv);
            timeTv = view.findViewById(R.id.item_comment_time);
            shareTv = view.findViewById(R.id.item_comment_share);
            commentTv = view.findViewById(R.id.item_comment_comment);
            praiseTv = view.findViewById(R.id.item_comment_praise);
            divide = view.findViewById(R.id.divide);
        }
    }
}
