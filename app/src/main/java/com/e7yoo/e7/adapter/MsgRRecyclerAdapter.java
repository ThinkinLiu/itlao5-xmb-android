package com.e7yoo.e7.adapter;

import android.app.Activity;
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
import com.e7yoo.e7.CommentListActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.PraiseListActivity;
import com.e7yoo.e7.PushMsgActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShortCutUtils;
import com.umeng.comm.core.CommentAPI;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.MessageSession;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.db.ctrl.CommentDBAPI;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommentAPIImpl;
import com.umeng.comm.core.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */
public class MsgRRecyclerAdapter extends RecyclerAdapter {
    private LayoutInflater mInflater;
    private List<MessageSession> mMsgs = new ArrayList<>();
    private static final int VIEW_TYPE_PUSH_MSG = 0;
    private static final int VIEW_TYPE_CIRCLE_MSG = 1;
    private static final int VIEW_TYPE_FRIEND_MSG = 2;
    private static final int VIEW_TYPE_FOOTER = 10;
    private static final int COUNT_UN_FRIEND_MSG = 3;
    /** 用于Footer的类型 */
    private FooterType mFooterType = FooterType.DEFAULT;
    /** 用于Footer的类型 */
    private boolean mFooterShowProgress = false;
    /** 用于Footer的文字显示，<= 0 时不显示GONE */
    private int mFooterStringId = 0;
    private static final int FOOTER_COUNT = 1;
    private Activity mContext;

    public MsgRRecyclerAdapter(Activity context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        // DebugUtil.setDatas(mMsgs, 1, true);
    }

    public void addItemTop(MessageSession newData) {
        mMsgs.add(0, newData);
        notifyDataSetChanged();
    }

    public void addItemTop(List<MessageSession> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mMsgs.addAll(0, newDatas);
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(List<MessageSession> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mMsgs.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(MessageSession newData) {
        mMsgs.add(newData);
        notifyDataSetChanged();
    }

    public void refreshData(List<MessageSession> newDatas) {
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
            case VIEW_TYPE_PUSH_MSG:
            case VIEW_TYPE_CIRCLE_MSG:
                view = mInflater.inflate(R.layout.item_unfriend_msg, parent, false);
                viewHolder = new ViewHolderUnFriendMsg(view);
                break;
            case VIEW_TYPE_FRIEND_MSG:
            default:
                view = mInflater.inflate(R.layout.item_friend_msg, parent, false);
                viewHolder = new ViewHolderFriendMsg(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderUnFriendMsg) {
            final ViewHolderUnFriendMsg viewHolderUnFriendMsg = (ViewHolderUnFriendMsg) holder;
            if(position == 0) {
                viewHolderUnFriendMsg.itemMsgBottom.setVisibility(View.GONE);
                viewHolderUnFriendMsg.itemMsgTitle.setText(R.string.push_msg);
                int count = PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                setCount(count, viewHolderUnFriendMsg.itemMsgPoint);
                viewHolderUnFriendMsg.itemMsgLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtil.toActivity(mContext, PushMsgActivity.class);
                        PreferenceUtil.commitInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                        viewHolderUnFriendMsg.itemMsgPoint.setVisibility(View.GONE);
                        try {
                            ShortCutUtils.deleteShortCut(mContext, MainActivity.class);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if(position == 1)  {
                viewHolderUnFriendMsg.itemMsgBottom.setVisibility(View.GONE);
                viewHolderUnFriendMsg.itemMsgTitle.setText(R.string.comment_msg);
                int count = CommConfig.getConfig().mMessageCount.unReadCommentsCount;
                setCount(count, viewHolderUnFriendMsg.itemMsgPoint);
                viewHolderUnFriendMsg.itemMsgLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtil.toActivity(mContext, CommentListActivity.class);
                        CommConfig.getConfig().mMessageCount.unReadCommentsCount = 0;
                        viewHolderUnFriendMsg.itemMsgPoint.setVisibility(View.GONE);
                    }
                });
            } else {
                viewHolderUnFriendMsg.itemMsgBottom.setVisibility(View.VISIBLE);
                viewHolderUnFriendMsg.itemMsgTitle.setText(R.string.praise_msg);
                int count = CommConfig.getConfig().mMessageCount.unReadLikesCount;
                setCount(count, viewHolderUnFriendMsg.itemMsgPoint);
                viewHolderUnFriendMsg.itemMsgLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtil.toActivity(mContext, PraiseListActivity.class);
                        CommConfig.getConfig().mMessageCount.unReadLikesCount = 0;
                        viewHolderUnFriendMsg.itemMsgPoint.setVisibility(View.GONE);
                    }
                });
            }
        } else if(holder instanceof ViewHolderFriendMsg) {
            ViewHolderFriendMsg viewHolderFriendMsg = (ViewHolderFriendMsg) holder;
            MessageSession messageSession = getItem(position);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
            Glide.with(mContext).load(messageSession.user.iconUrl).apply(options).into(viewHolderFriendMsg.itemMsgIcon);
            viewHolderFriendMsg.itemMsgName.setText(messageSession.user.name);
            viewHolderFriendMsg.itemMsgTime.setText(messageSession.lastChat.createTime);
            viewHolderFriendMsg.itemMsgContent.setText(messageSession.lastChat.content);
            viewHolderFriendMsg.itemMsgPoint.setText(messageSession.unReadConut);
            addClickListener(viewHolderFriendMsg.itemView, position);
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

    private void setCount(int count, TextView view) {
        if(count >= 99) {
            view.setVisibility(View.VISIBLE);
            view.setText("99");
        } else if(count > 0) {
            view.setVisibility(View.VISIBLE);
            view.setText(String.valueOf(count));
        } else {
            view.setVisibility(View.GONE);
            view.setText("");
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

    @Override
    public int getItemCount() {
        return mMsgs.size() + COUNT_UN_FRIEND_MSG + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType;
        if (position == getItemCount() - FOOTER_COUNT) {
            itemViewType = VIEW_TYPE_FOOTER;
        } else if(position == 0) {
            itemViewType = VIEW_TYPE_PUSH_MSG;
        } else if(position > 0 && position < COUNT_UN_FRIEND_MSG) {
            itemViewType = VIEW_TYPE_CIRCLE_MSG;
        } else {
            itemViewType = VIEW_TYPE_FRIEND_MSG;
        }
        return itemViewType;
    }

    /**
     * 消息item
     */
    public static class ViewHolderUnFriendMsg extends RecyclerView.ViewHolder {
        public View itemMsgLayout;
        public TextView itemMsgTitle;
        public TextView itemMsgPoint;
        public View itemMsgBottom;

        public ViewHolderUnFriendMsg(View view) {
            super(view);
            itemMsgLayout = view.findViewById(R.id.item_unfriend_msg_lyout);
            itemMsgTitle = view.findViewById(R.id.item_unfriend_msg_title);
            itemMsgPoint = view.findViewById(R.id.item_unfriend_msg_point);
            itemMsgBottom = view.findViewById(R.id.item_unfriend_msg_bottom);
        }
    }

    /**
     * 消息item
     */
    public static class ViewHolderFriendMsg extends RecyclerView.ViewHolder {
        public ImageView itemMsgIcon;
        public TextView itemMsgPoint;
        public TextView itemMsgName;
        public TextView itemMsgTime;
        public TextView itemMsgContent;

        public ViewHolderFriendMsg(View view) {
            super(view);
            itemMsgIcon = view.findViewById(R.id.item_friend_msg_icon);
            itemMsgPoint = view.findViewById(R.id.item_friend_msg_point);
            itemMsgName = view.findViewById(R.id.item_friend_msg_name);
            itemMsgTime = view.findViewById(R.id.item_friend_msg_time);
            itemMsgContent = view.findViewById(R.id.item_friend_msg_content);
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

    public MessageSession getItem(int position) {
        position = position - COUNT_UN_FRIEND_MSG;
        return mMsgs != null && mMsgs.size() > position && position >= 0 ? mMsgs.get(position) : null;
    }

}
