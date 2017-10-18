package com.e7yoo.e7.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.e7yoo.e7.util.CommonUtil;
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
            addClickListener(viewHolderFeedItem.itemView, position);
        }
    }

    private void setUser(ViewHolderFeedItem viewHolderFeedItem, FeedItem item) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.log_e7yoo_transport)
                .error(R.mipmap.log_e7yoo_transport);
        Glide.with(mContext)
                .load(item.creator.iconUrl)
                .apply(options)
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
