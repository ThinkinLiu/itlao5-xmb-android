package com.e7yoo.e7.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.CircleGvAdapterUtil;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.TimeUtil;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;

import java.util.List;

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

    public FeedDetailRecyclerAdapter(Context context) {
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
            View view = mInflater.inflate(R.layout.item_feed_item, parent, false);
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
            }
            addClickListener(viewHolderFeedItem.itemView, position);
        } else if(holder instanceof ViewHolderComment) {
            ViewHolderComment viewHolderComment = (ViewHolderComment) holder;
            Comment item = (Comment) mDatas.get(position);
            setUser(viewHolderComment, item.creator);
            setViewTypeComment(viewHolderComment, item);
            viewHolderComment.hint.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            viewHolderComment.divide.setVisibility(position < mDatas.size() - 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void setUser(BaseViewHolder viewHolderFeedItem, CommUser item) {
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.log_e7yoo_transport)
                .error(R.mipmap.log_e7yoo_transport)
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
    }

    private void setViewTypeFeeditem(BaseViewHolder viewHolderFeedItem, FeedItem item) {
        viewHolderFeedItem.contentTv.setMaxLines(1000);
        String content = "";
        if(item.topics != null) {
            for(int i = 0; i < item.topics.size(); i++) {
                if(item.topics.get(i) != null && !TextUtils.isEmpty(item.topics.get(i).name)) {
                    content = content + "<font color= 'blue'>" + item.topics.get(i).name + "</font> ";
                }
            }
        }
        content = content + item.text;
        viewHolderFeedItem.contentTv.setText(CommonUtil.getHtmlStr(content));
        CircleGvAdapterUtil.setGridView(mContext, viewHolderFeedItem.gridView, item.getImages());
        viewHolderFeedItem.timeTv.setText(TimeUtil.formatFeedTime(item.publishTime));
        viewHolderFeedItem.shareTv.setText(String.format("%-3d", item.forwardCount));
        viewHolderFeedItem.commentTv.setText(String.format("%-3d", item.commentCount));
        viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount));
    }

    private void setViewTypeComment(BaseViewHolder viewHolder, Comment item) {
        viewHolder.contentTv.setMaxLines(1000);
        String reply = "";
        if(item.replyUser != null && !TextUtils.isEmpty(item.replyUser.name)) {
            reply = "回复 <font color= 'blue'>" + item.replyUser.name + "</font> ";
        }
        viewHolder.contentTv.setText(CommonUtil.getHtmlStr(reply + item.text));
        CircleGvAdapterUtil.setGridView(mContext, viewHolder.gridView, item.imageUrls);
        viewHolder.timeTv.setText(TimeUtil.formatFeedTime(item.createTime));
        viewHolder.shareTv.setVisibility(View.GONE);
        viewHolder.commentTv.setVisibility(View.GONE);
        viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount));
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
