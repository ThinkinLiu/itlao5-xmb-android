package com.e7yoo.e7.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.e7yoo.e7.R;
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
        mDatas.add(commentList);
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
        }
    }

    private void setUser(BaseViewHolder viewHolderFeedItem, CommUser item) {
        Glide.with(mContext)
                .load(item.iconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.log_e7yoo_transport)
                .error(R.mipmap.log_e7yoo_transport)
                .override(124, 124)
                .into(viewHolderFeedItem.userIcon);
        int sexIcon = R.mipmap.sex_unknow_selected;
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
        viewHolderFeedItem.contentTv.setMaxLines(0);
        viewHolderFeedItem.contentTv.setText(item.text);
        int size = item.getImages().size();
        if (size == 0) {
            viewHolderFeedItem.gridView.setAdapter(null);
        } else if (size == 1) {
            viewHolderFeedItem.gridView.setNumColumns(2);
            viewHolderFeedItem.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.getImages()));
        } else if (size == 2 || size == 4) {
            viewHolderFeedItem.gridView.setNumColumns(2);
            viewHolderFeedItem.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.getImages()));
        } else {
            viewHolderFeedItem.gridView.setNumColumns(3);
            viewHolderFeedItem.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.getImages()));
        }
        viewHolderFeedItem.timeTv.setText(TimeUtil.formatFeedTime(item.publishTime));
        viewHolderFeedItem.shareTv.setText(String.format("%-3d", item.forwardCount));
        viewHolderFeedItem.commentTv.setText(String.format("%-3d", item.commentCount));
        viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount));
    }

    private void setViewTypeComment(BaseViewHolder viewHolder, Comment item) {
        viewHolder.contentTv.setMaxLines(0);
        viewHolder.contentTv.setText(item.text);
        int size = item.imageUrls.size();
        if (size == 0) {
            viewHolder.gridView.setAdapter(null);
        } else if (size == 1) {
            viewHolder.gridView.setNumColumns(2);
            viewHolder.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.imageUrls));
        } else if (size == 2 || size == 4) {
            viewHolder.gridView.setNumColumns(2);
            viewHolder.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.imageUrls));
        } else {
            viewHolder.gridView.setNumColumns(3);
            viewHolder.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.imageUrls));
        }
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

        public ViewHolderComment(View view) {
            super(view);
            userIcon = view.findViewById(R.id.item_comment_icon);
            sexIcon = view.findViewById(R.id.item_comment_sex);
            usernameTv = view.findViewById(R.id.item_comment_username);
            contentTv = view.findViewById(R.id.item_comment_content);
            gridView = view.findViewById(R.id.item_comment_gv);
            timeTv = view.findViewById(R.id.item_comment_time);
            shareTv = view.findViewById(R.id.item_comment_share);
            commentTv = view.findViewById(R.id.item_comment_comment);
            praiseTv = view.findViewById(R.id.item_comment_praise);
        }
    }
}
