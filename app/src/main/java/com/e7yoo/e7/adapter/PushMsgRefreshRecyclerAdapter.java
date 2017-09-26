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
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */
public class PushMsgRefreshRecyclerAdapter extends RecyclerAdapter {
    private LayoutInflater mInflater;
    private List<PushMsg> mMsgs = new ArrayList<>();
    private static final int VIEW_TYPE_PUSH_MSG = 0;
    private static final int VIEW_TYPE_FOOTER = 10;
    /** 用于Footer的类型 */
    private FooterType mFooterType = FooterType.DEFAULT;
    /** 用于Footer的类型 */
    private boolean mFooterShowProgress = false;
    /** 用于Footer的文字显示，<= 0 时不显示GONE */
    private int mFooterStringId = 0;
    private static final int FOOTER_COUNT = 1;
    private Context mContext;

    public PushMsgRefreshRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        // DebugUtil.setDatas(mMsgs, 1, true);
    }

    public void addItemTop(PushMsg newData) {
        mMsgs.add(0, newData);
        notifyDataSetChanged();
    }

    public void addItemTop(List<PushMsg> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mMsgs.addAll(0, newDatas);
            notifyItemRangeChanged(0, newDatas.size());
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(List<PushMsg> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mMsgs.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(PushMsg newData) {
        mMsgs.add(newData);
        notifyDataSetChanged();
    }

    public void refreshData(List<PushMsg> newDatas) {
        mMsgs.clear();
        if(newDatas != null) {
            mMsgs.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    public void setRead(int position) {
        try {
            mMsgs.get(position).setUnread(0);
            notifyDataSetChanged();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public FooterType getFooter() {
        return mFooterType;
    }

    public void setFooter(FooterType type, int footerStringId, boolean footerShowProgress) {
        mFooterType = type;
        mFooterStringId = footerStringId;
        mFooterShowProgress = footerShowProgress;
        notifyDataSetChanged();
        // notifyItemChanged(getItemCount() - 1);
    }

    public int getLastId() {
        return (mMsgs == null || mMsgs.size() == 0) ? -1 : mMsgs.get(0).get_id();
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
            case VIEW_TYPE_PUSH_MSG:
            default:
                view = mInflater.inflate(R.layout.item_push_msg, parent, false);
                viewHolder = new ViewHolderPushMsg(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderPushMsg) {
            ViewHolderPushMsg viewHolderPushMsg = (ViewHolderPushMsg) holder;
            String icon = mMsgs.get(position).getPic_url();
            if(TextUtils.isEmpty(icon)) {
                viewHolderPushMsg.itemMsgIcon.setVisibility(View.GONE);
            } else {
                Glide.with(mContext).load(icon).placeholder(R.mipmap.log_e7yoo_transport).into(viewHolderPushMsg.itemMsgIcon);
                viewHolderPushMsg.itemMsgIcon.setVisibility(View.VISIBLE);
            }
            String title = mMsgs.get(position).getTitle();
            if(TextUtils.isEmpty(title)) {
                viewHolderPushMsg.itemMsgTitle.setText(R.string.item_push_msg_title_default);
            } else {
                viewHolderPushMsg.itemMsgTitle.setText(title);
            }
            viewHolderPushMsg.itemMsgContent.setText(mMsgs.get(position).getContent());
            int unRead = mMsgs.get(position).getUnread();
            viewHolderPushMsg.itemMsgPoint.setVisibility(unRead > 0 ? View.VISIBLE : View.INVISIBLE);
            addClickListener(viewHolderPushMsg.itemView, position);
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

    private int voicePosition;
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
            itemViewType = VIEW_TYPE_PUSH_MSG;
        }
        return itemViewType;
    }

    /**
     * 消息item
     */
    public static class ViewHolderPushMsg extends RecyclerView.ViewHolder {
        public ImageView itemMsgIcon;
        public TextView itemMsgTitle;
        public TextView itemMsgContent;
        public TextView itemMsgPoint;

        public ViewHolderPushMsg(View view) {
            super(view);
            itemMsgIcon = view.findViewById(R.id.item_push_msg_icon);
            itemMsgTitle = view.findViewById(R.id.item_push_msg_title);
            itemMsgContent = view.findViewById(R.id.item_push_msg_content);
            itemMsgPoint = view.findViewById(R.id.item_push_msg_point);
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

    public PushMsg getItem(int position) {
        return mMsgs != null && mMsgs.size() > position && position >= 0 ? mMsgs.get(position) : null;
    }

}
