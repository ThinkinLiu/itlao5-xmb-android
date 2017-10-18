package com.e7yoo.e7.community;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;

/**
 * Created by Administrator on 2017/9/25.
 */
public class TopicRefreshRecyclerAdapter extends ListRefreshRecyclerAdapter {

    public TopicRefreshRecyclerAdapter(Activity context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_topic, parent, false);
        return new ViewHolderTopic(view);
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderTopic) {
            final ViewHolderTopic viewHolder = (ViewHolderTopic) holder;
            final Topic item = (Topic) mDatas.get(position);
            if (item != null) {
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.log_e7yoo_transport)
                        .error(R.mipmap.log_e7yoo_transport)
                        .override(124, 124);
                Glide.with(mContext)
                        .load(item.icon)
                        .apply(options)
                        .into(viewHolder.topicIcon);
                viewHolder.nameTv.setText(item.name.replace("#", ""));
                viewHolder.descTv.setText(item.desc);
                if (mContext instanceof TopicListActivity) {
                    viewHolder.attentionIv.setVisibility(View.GONE);
                } else {
                    initAttentionIv(viewHolder, item);
                }
            }
            addClickListener(viewHolder.itemView, position);
        }
    }

    private void initAttentionIv(final ViewHolderTopic viewHolder, final Topic item) {
        if (item.isFocused) {
            viewHolder.attentionIv.setImageResource(R.mipmap.circle_attentioned);
            viewHolder.attentionIv.setOnClickListener(null);
        } else {
            viewHolder.attentionIv.setImageResource(R.drawable.circle_attention_selector);
            viewHolder.attentionIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isFocused = true;
                    viewHolder.attentionIv.setImageResource(R.mipmap.circle_attentioned);
                    viewHolder.attentionIv.setOnClickListener(null);
                    E7App.getCommunitySdk().followTopic(item, new Listeners.SimpleFetchListener<Response>() {
                        @Override
                        public void onComplete(Response response) {
                            switch (response.errCode) {
                                case ErrorCode.NO_ERROR:
                                    break;
                                case ErrorCode.UNLOGIN_ERROR:
                                    TastyToastUtil.toast(mContext, R.string.circle_no_login);
                                default:
                                    item.isFocused = false;
                                    notifyDataSetChanged();
                                    break;
                            }
                        }
                    });
                }
            });
        }
    }

    private void addClickListener(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
    }

/**
 * 消息item
 */
public static class ViewHolderTopic extends RecyclerView.ViewHolder {

    public ImageView topicIcon;
    public TextView nameTv;
    public TextView descTv;
    public ImageView attentionIv;

    public ViewHolderTopic(View view) {
        super(view);
        topicIcon = view.findViewById(R.id.item_topic_icon);
        nameTv = view.findViewById(R.id.item_topic_name);
        descTv = view.findViewById(R.id.item_topic_desc);
        attentionIv = view.findViewById(R.id.item_topic_attention);
    }
}

}
