package com.e7yoo.e7.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.util.TimeUtil;
import com.umeng.comm.core.beans.FeedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */
public class FeedItemRefreshRecyclerAdapter extends ListRefreshRecyclerAdapter {

    public FeedItemRefreshRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_feed_item, parent, false);
        return new ViewHolderFeedItem(view);
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderFeedItem) {
            ViewHolderFeedItem viewHolderFeedItem = (ViewHolderFeedItem) holder;
            FeedItem item = (FeedItem) mDatas.get(position);
            if (item != null) {
                setUser(viewHolderFeedItem, item);
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
            addClickListener(viewHolderFeedItem.itemView, position);
        }
    }

    private void setUser(ViewHolderFeedItem viewHolderFeedItem, FeedItem item) {
        Glide.with(mContext)
                .load(item.creator.iconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.log_e7yoo_transport)
                .error(R.mipmap.log_e7yoo_transport)
                .override(124, 124)
                .into(viewHolderFeedItem.userIcon);
        int sexIcon = R.mipmap.sex_unknow_selected;
        switch (item.creator.gender) {
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
        viewHolderFeedItem.usernameTv.setText(item.creator.name);
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

    /**
     * 消息item
     */
    public static class ViewHolderFeedItem extends RecyclerView.ViewHolder {

        public ImageView userIcon;
        public ImageView sexIcon;
        public TextView usernameTv;
        public TextView contentTv;
        public GridView gridView;
        public TextView timeTv;
        public TextView shareTv;
        public TextView commentTv;
        public TextView praiseTv;

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

}
