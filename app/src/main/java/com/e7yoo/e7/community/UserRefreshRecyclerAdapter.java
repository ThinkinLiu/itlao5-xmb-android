package com.e7yoo.e7.community;

import android.app.Activity;
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
import com.e7yoo.e7.util.CommUserUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;

/**
 * Created by Administrator on 2017/9/25.
 */
public class UserRefreshRecyclerAdapter extends ListRefreshRecyclerAdapter {

    private int mFlag = UserListFragment.FLAG_RECOMMENDED;

    public UserRefreshRecyclerAdapter(Activity context, int flag) {
        super(context);
        mFlag = flag;
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_user, parent, false);
        return new ViewHolderUser(view);
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderUser) {
            final ViewHolderUser viewHolder = (ViewHolderUser) holder;
            final CommUser item = (CommUser) mDatas.get(position);
            if (item != null) {
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.icon_me)
                        .error(R.mipmap.icon_me)
                        .override(124, 124);
                Glide.with(mContext)
                        .load(item.iconUrl)
                        .apply(options)
                        .into(viewHolder.topicIcon);
                viewHolder.nameTv.setText(item.name);
                viewHolder.descTv.setText(CommUserUtil.getExtraString(item, "welcome"));
                initAttentionIv(viewHolder, item);
            }
            addClickListener(viewHolder.itemView, position);
        }
    }

    private void initAttentionIv(final ViewHolderUser viewHolder, final CommUser item) {
        if("社区管理员".equals(item.name)) {
            viewHolder.attentionIv.setImageResource(0);
            viewHolder.attentionIv.setOnClickListener(null);
            return;
        }
        if(item.isFollowed && item.isFollowingMe) {
            // 互相关注
            viewHolder.attentionIv.setImageResource(R.mipmap.circle_attentioned_too);
        } else if (item.isFollowed) {
            // 仅关注
            viewHolder.attentionIv.setImageResource(R.mipmap.circle_attentioned);
        } else if (item.isFollowingMe) {
            // 仅被关注（粉丝）
            viewHolder.attentionIv.setImageResource(R.drawable.circle_attention_selector);
        } else {
            // 未关注&未被关注
            viewHolder.attentionIv.setImageResource(R.drawable.circle_attention_selector);
        }

        viewHolder.attentionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isFollowed) {
                    viewHolder.attentionIv.setImageResource(R.drawable.circle_attention_selector);
                    E7App.getCommunitySdk().cancelFollowUser(item, new Listeners.SimpleFetchListener<Response>() {
                        @Override
                        public void onComplete(Response response) {
                            switch (response.errCode) {
                                case ErrorCode.NO_ERROR:
                                    break;
                                case ErrorCode.UNLOGIN_ERROR:
                                    TastyToastUtil.toast(mContext, R.string.circle_no_login);
                                    item.isFollowed = true;
                                    notifyDataSetChanged();
                                    break;
                                default:
                                    TastyToastUtil.toast(mContext, R.string.cancel_follow_failed);
                                    item.isFollowed = true;
                                    notifyDataSetChanged();
                                    break;
                            }
                        }
                    });
                } else {
                    if(item.isFollowingMe) {
                        viewHolder.attentionIv.setImageResource(R.mipmap.circle_attentioned_too);
                    } else {
                        viewHolder.attentionIv.setImageResource(R.mipmap.circle_attentioned);
                    }
                    E7App.getCommunitySdk().followUser(item, new Listeners.SimpleFetchListener<Response>() {
                        @Override
                        public void onComplete(Response response) {
                            switch (response.errCode) {
                                case ErrorCode.NO_ERROR:
                                    break;
                                case ErrorCode.UNLOGIN_ERROR:
                                    TastyToastUtil.toast(mContext, R.string.circle_no_login);
                                    item.isFollowed = false;
                                    notifyDataSetChanged();
                                    break;
                                default:
                                    TastyToastUtil.toast(mContext, R.string.follow_failed);
                                    item.isFollowed = false;
                                    notifyDataSetChanged();
                                    break;
                            }
                        }
                    });
                }
            }
        });
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
public static class ViewHolderUser extends RecyclerView.ViewHolder {

    public ImageView topicIcon;
    public TextView nameTv;
    public TextView descTv;
    public ImageView attentionIv;

    public ViewHolderUser(View view) {
        super(view);
        topicIcon = view.findViewById(R.id.item_list_user_icon);
        nameTv = view.findViewById(R.id.item_list_user_name);
        descTv = view.findViewById(R.id.item_list_user_desc);
        attentionIv = view.findViewById(R.id.item_list_user_attention);
    }
}

}
