package com.e7yoo.e7.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.TimeUtil;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.MessageChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/28.
 */
public class CircleMsgRefreshRecyclerAdapter extends RecyclerAdapter {
    private LayoutInflater mInflater;
    private List<MessageChat> mMsgs = new ArrayList<>();
    private static final int VIEW_TYPE_SEND = 0;
    private static final int VIEW_TYPE_REV = 1;
    private static final int VIEW_TYPE_HINT = 2;
    private static final int VIEW_TYPE_FOOTER = 10;
    /** 用于Footer的类型 */
    private FooterType mFooterType = FooterType.DEFAULT;
    /** 用于Footer的类型 */
    private boolean mFooterShowProgress = false;
    /** 用于Footer的文字显示，<= 0 时不显示GONE */
    private int mFooterStringId = 0;
    private static final int FOOTER_COUNT = 1;
    private Context mContext;
    private CommUser mCommUser;

    public CircleMsgRefreshRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mCommUser = CommConfig.getConfig().loginedUser;
    }

    public void addItemTop(MessageChat newData) {
        mMsgs.add(0, newData);
        notifyDataSetChanged();
    }

    public void addItemTop(List<MessageChat> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mMsgs.addAll(0, newDatas);
            notifyItemRangeChanged(0, newDatas.size());
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(List<MessageChat> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mMsgs.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(MessageChat newData) {
        mMsgs.add(newData);
        notifyDataSetChanged();
    }

    public void refreshData(List<MessageChat> newDatas) {
        mMsgs.clear();
        if(newDatas != null) {
            mMsgs.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    public FooterType getFooter() {
        return mFooterType;
    }

    public void setFooter(FooterType type, int footerStringId, boolean footerShowProgress) {
        mFooterType = type;
        mFooterStringId = footerStringId;
        mFooterShowProgress = footerShowProgress;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        switch (viewType) {
            case VIEW_TYPE_FOOTER:
                view = mInflater.inflate(R.layout.item_msg_footer, parent, false);
                viewHolder = new ViewHolderFooter(view);
                break;
            case VIEW_TYPE_SEND:
                view = mInflater.inflate(R.layout.item_msg_send, parent, false);
                viewHolder = new ViewHolderSend(view);
                break;
            case VIEW_TYPE_REV:
                view = mInflater.inflate(R.layout.item_msg_rev, parent, false);
                viewHolder = new ViewHolderRev(view);
                break;
            case VIEW_TYPE_HINT:
            default:
                view = mInflater.inflate(R.layout.item_msg_hint, parent, false);
                viewHolder = new ViewHolderHint(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean showTime = showTime(position);
        if(holder instanceof ViewHolderSend) {
            ViewHolderSend viewHolderSend = (ViewHolderSend) holder;
            if(showTime) {
                viewHolderSend.itemMsgTime.setText(TimeUtil.formatFeedTime(mMsgs.get(position).createTime));
                viewHolderSend.itemMsgTime.setBackgroundResource(R.drawable.rounded_corners_tag_gray_trans);
            } else {
                viewHolderSend.itemMsgTime.setText("");
                viewHolderSend.itemMsgTime.setBackgroundResource(0);
            }
            viewHolderSend.itemMsgContent.setText(mMsgs.get(position).content);
            if(mCommUser != null && TextUtils.isEmpty(mCommUser.iconUrl)) {
                Glide.with(mContext).load(mCommUser.iconUrl).apply(getIconMeRequestOptions()).into(viewHolderSend.itemMsgIcon);
            } else {
                Glide.with(mContext).load(R.mipmap.icon_me).apply(getIconMeRequestOptions()).into(viewHolderSend.itemMsgIcon);
            }
            viewHolderSend.itemMsgVoice.setVisibility(View.GONE);
            // addClickListener(viewHolderSend.contentLayout, null, position);
        } else if(holder instanceof ViewHolderRev) {
            ViewHolderRev viewHolderRev = (ViewHolderRev) holder;
            if(showTime) {
                viewHolderRev.itemMsgTime.setText(TimeUtil.formatFeedTime(mMsgs.get(position).createTime));
                viewHolderRev.itemMsgTime.setBackgroundResource(R.drawable.rounded_corners_tag_gray_trans);
            } else {
                viewHolderRev.itemMsgTime.setText("");
                viewHolderRev.itemMsgTime.setBackgroundResource(0);
            }
            viewHolderRev.itemMsgContent.setText(mMsgs.get(position).content);
            Glide.with(mContext).load(mMsgs.get(position).creator.iconUrl).apply(getIconRequestOptions()).into(viewHolderRev.itemMsgIcon);
            viewHolderRev.itemMsgVoice.setVisibility(View.GONE);
            viewHolderRev.itemMsgUrl.setVisibility(View.GONE);
            // addClickListener(viewHolderRev.contentLayout, urlView, position);
        } else if(holder instanceof ViewHolderHint) {
            ViewHolderHint viewHolderHint = (ViewHolderHint) holder;
            if(showTime) {
                viewHolderHint.itemMsgTime.setText(TimeUtil.formatFeedTime(mMsgs.get(position).createTime));
                viewHolderHint.itemMsgTime.setBackgroundResource(R.drawable.rounded_corners_tag_gray_trans);
            } else {
                viewHolderHint.itemMsgTime.setText("");
                viewHolderHint.itemMsgTime.setBackgroundResource(0);
            }
            viewHolderHint.itemMsgHint.setText(mMsgs.get(position).content);
        } else if(holder instanceof ViewHolderFooter) {
            ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;
            int footerStringId = getFooterStringId();
            if(footerStringId > 0) {
                viewHolderFooter.loadingTv.setText(footerStringId);
                viewHolderFooter.layout.setBackgroundResource(R.drawable.rounded_corners_tag_gray_trans);
            } else {
                viewHolderFooter.loadingTv.setText("　");
                viewHolderFooter.layout.setBackgroundResource(0);
            }
            viewHolderFooter.loadingPb.setVisibility(mFooterShowProgress ? View.VISIBLE : View.GONE);
        }
        holder.itemView.setTag(position);
    }

    private RequestOptions iconOptions;
    private RequestOptions getIconRequestOptions() {
        if(iconOptions == null) {
            iconOptions = new RequestOptions();
            iconOptions.placeholder(R.mipmap.icon).error(R.mipmap.icon);
        }
        return iconOptions;
    }
    private RequestOptions iconMeOptions;
    private RequestOptions getIconMeRequestOptions() {
        if(iconMeOptions == null) {
            iconMeOptions = new RequestOptions();
            iconMeOptions.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
        }
        return iconMeOptions;
    }

    private void addClickListener(View view, View url, final int position) {
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
        if(url != null) {
            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnUrlClickListener != null) {
                        mOnUrlClickListener.onUrlClick(view, position);
                    }
                }
            });
        }
    }

    private boolean showTime(int position) {
        if(position == 0) {
            return true;
        } else if(position >= mMsgs.size()) {
            return false;
        } else {
            return !mMsgs.get(position).createTime.equals(mMsgs.get(position - 1).createTime);
        }
    }

    private int getFooterStringId() {
        int footerStringId = mFooterStringId;
        switch (mFooterType) {
            case DEFAULT:
                break;
            case LOADING:
                if(footerStringId <= 0) {
                    footerStringId = R.string.loading;
                }
                break;
            case NO_MORE:
                if(footerStringId <= 0) {
                    footerStringId = R.string.loading_no_more;
                }
                break;
            case END:
                break;
            case HINT:
                if(footerStringId <= 0) {
                    footerStringId = R.string.loading_up_load_more;
                }
                break;
            default:
                break;
        }
        return footerStringId;
    }

    public int getLastPosition() {
        return mMsgs == null || mMsgs.size() == 0 ? 0 : mMsgs.size() - 1;
    }

    @Override
    public int getItemCount() {
        return mMsgs.size() + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType;
        if (position == getItemCount() - FOOTER_COUNT) {
            itemViewType = VIEW_TYPE_FOOTER;
        } else {
            if (mMsgs.get(position).isCurrentUser()) {
                itemViewType = VIEW_TYPE_SEND;
            } else {
                itemViewType = VIEW_TYPE_REV;
            }
        }
        return itemViewType;
    }

    /**
     * 消息基类
     */
    public static class BaseMsgViewHolder extends RecyclerView.ViewHolder {
        public TextView itemMsgTime;
        public ImageView itemMsgIcon;
        public TextView itemMsgContent;
        public View contentLayout;
        public ImageView itemMsgVoice;

        public BaseMsgViewHolder(View view) {
            super(view);
            itemMsgTime = view.findViewById(R.id.item_msg_time);
            itemMsgIcon = view.findViewById(R.id.item_msg_icon);
            itemMsgContent = view.findViewById(R.id.item_msg_content);
            contentLayout = view.findViewById(R.id.item_msg_content_layout);
            itemMsgVoice = view.findViewById(R.id.item_msg_voice);
        }
    }

    /**
     * 发送消息item
     */
    public static class ViewHolderSend extends BaseMsgViewHolder {

        public ViewHolderSend(View view) {
            super(view);
        }
    }

    /**
     * 接收消息item
     */
    public static class ViewHolderRev extends BaseMsgViewHolder {

        public TextView itemMsgUrl;
        public ViewHolderRev(View view) {
            super(view);
            itemMsgUrl = view.findViewById(R.id.item_msg_url);
        }
    }

    /**
     * 提示消息item
     */
    public static class ViewHolderHint extends RecyclerView.ViewHolder {
        public TextView itemMsgTime;
        public TextView itemMsgHint;

        public ViewHolderHint(View view) {
            super(view);
            itemMsgTime = view.findViewById(R.id.item_msg_time);
            itemMsgHint = view.findViewById(R.id.item_msg_hint);
        }
    }

    /**
     * 上拉刷新提示item
     */
    public static class ViewHolderFooter extends RecyclerView.ViewHolder {
        public View layout;
        public ProgressBar loadingPb;
        public TextView loadingTv;
        public ViewHolderFooter(View view) {
            super(view);
            layout = view.findViewById(R.id.item_msg_footer_layout);
            loadingPb = view.findViewById(R.id.item_msg_loading_progressbar);
            loadingTv = view.findViewById(R.id.item_msg_loading);
        }
    }

    public enum FooterType {
        DEFAULT,
        END,
        LOADING,
        NO_MORE,
        HINT;
    }

    public MessageChat getItem(int position) {
        return mMsgs != null && mMsgs.size() > position && position >= 0 ? mMsgs.get(position) : null;
    }

    private OnUrlClickListener mOnUrlClickListener;
    public void setOnUrlClickListener(OnUrlClickListener onUrlClickListener) {
        mOnUrlClickListener = onUrlClickListener;
    }
    public interface OnUrlClickListener{
        void onUrlClick(View view, int position);
    }

    private OnVoiceClickListener mOnVoiceClickListener;
    public void setOnVoiceClickListener(OnVoiceClickListener onVoiceClickListener) {
        mOnVoiceClickListener = onVoiceClickListener;
    }
    public interface OnVoiceClickListener{
        void onVoiceClick(View view, int position);
    }

    private long ttsMsgTime = -1;
    public void setTtsMsgTime(long ttsMsgTime) {
        this.ttsMsgTime = ttsMsgTime;
    }
    public long getTtsMsgTime() {
        return ttsMsgTime;
    }
}
