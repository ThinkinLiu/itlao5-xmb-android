package com.e7yoo.e7.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.CircleGvAdapterUtil;
import com.e7yoo.e7.adapter.RecyclerAdapter;
import com.e7yoo.e7.community.ListRefreshRecyclerAdapter;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.TimeUtil;
import com.e7yoo.e7.view.CircleGridView;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */
public class CommentListRefreshRecyclerAdapter extends ListRefreshRecyclerAdapter {

    public CommentListRefreshRecyclerAdapter(Activity context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comment_list, parent, false);
        return new ViewHolderComment(view);
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderComment) {
            ViewHolderComment viewHolderComment = (ViewHolderComment) holder;
            FeedItem item = (FeedItem) mDatas.get(position);
            if (item != null) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
                Glide.with(mContext).load(item.creator.iconUrl).apply(options).into(viewHolderComment.userIcon);
                viewHolderComment.nameTv.setText(item.creator.name);
                String text = item.imageUrls != null && item.imageUrls.size() > 0 ? "[图片] " : "";
                text = text + item.text == null ? "" : item.text;
                viewHolderComment.contentTv.setText(text);
                viewHolderComment.timeTv.setText(TimeUtil.formatFeedTime(item.publishTime));
                if(item.sourceFeed.imageUrls != null && item.sourceFeed.imageUrls.size() > 0) {
                    RequestOptions options2 = new RequestOptions();
                    options2.placeholder(R.mipmap.log_e7yoo_transport).error(R.mipmap.log_e7yoo_transport);
                    Glide.with(mContext).load(item.sourceFeed.imageUrls.get(0).thumbnail).apply(options2).into(viewHolderComment.feedPicIv);
                    viewHolderComment.feedContentTv.setText("");
                } else {
                    viewHolderComment.feedPicIv.setImageResource(0);
                    viewHolderComment.feedContentTv.setText(item.sourceFeed.text);
                }
                addIconClick(viewHolderComment.userIcon, item.creator);
            }
            addClickListener(viewHolderComment.itemView, position);
        }
    }

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
    public static class ViewHolderComment extends RecyclerView.ViewHolder {

        public ImageView userIcon;
        public TextView nameTv;
        public TextView contentTv;
        public TextView timeTv;
        public ImageView feedPicIv;
        public TextView feedContentTv;

        public ViewHolderComment(View view) {
            super(view);
            userIcon = view.findViewById(R.id.item_comment_list_icon);
            nameTv = view.findViewById(R.id.item_comment_list_name);
            contentTv = view.findViewById(R.id.item_comment_list_content);
            timeTv = view.findViewById(R.id.item_comment_list_time);
            feedPicIv = view.findViewById(R.id.item_comment_list_feed_pic);
            feedContentTv = view.findViewById(R.id.item_comment_list_feed_content);
        }
    }

}