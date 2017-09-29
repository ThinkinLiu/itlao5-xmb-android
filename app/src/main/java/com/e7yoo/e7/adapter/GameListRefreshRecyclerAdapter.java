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
import com.e7yoo.e7.model.GameInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */
public class GameListRefreshRecyclerAdapter extends RecyclerAdapter {
    private LayoutInflater mInflater;
    private List<GameInfo> mDatas = new ArrayList<>();
    private static final int VIEW_TYPE_GAME_INFO = 0;
    private static final int VIEW_TYPE_FOOTER = 10;
    /** 用于Footer的类型 */
    private FooterType mFooterType = FooterType.DEFAULT;
    /** 用于Footer的类型 */
    private boolean mFooterShowProgress = false;
    /** 用于Footer的文字显示，<= 0 时不显示GONE */
    private int mFooterStringId = 0;
    private static final int FOOTER_COUNT = 0;
    private Context mContext;

    public GameListRefreshRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        // DebugUtil.setDatas(mDatas, 1, true);
    }

    public void addItemTop(GameInfo newData) {
        mDatas.add(0, newData);
        notifyDataSetChanged();
    }

    public void addItemTop(List<GameInfo> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mDatas.addAll(0, newDatas);
            notifyItemRangeChanged(0, newDatas.size());
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(List<GameInfo> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mDatas.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(GameInfo newData) {
        mDatas.add(newData);
        notifyDataSetChanged();
    }

    public void refreshData(List<GameInfo> newDatas) {
        mDatas.clear();
        if(newDatas != null) {
            mDatas.addAll(newDatas);
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
        // notifyItemChanged(getItemCount() - 1);
    }

    public int getLastId() {
        return (mDatas == null || mDatas.size() == 0) ? -1 : mDatas.get(0).get_id();
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
            case VIEW_TYPE_GAME_INFO:
            default:
                view = mInflater.inflate(R.layout.item_game_list, parent, false);
                viewHolder = new ViewHolderGameInfo(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderGameInfo) {
            ViewHolderGameInfo viewHolderGameInfo = (ViewHolderGameInfo) holder;
            GameInfo gameInfo = mDatas.get(position);
            if(gameInfo != null) {
                if(TextUtils.isEmpty(gameInfo.getIcon())) {
                    if(gameInfo.getIconResId() > 0) {
                        viewHolderGameInfo.itemGameIcon.setImageResource(gameInfo.getIconResId());
                    } else {
                        viewHolderGameInfo.itemGameIcon.setImageResource(R.mipmap.log_e7yoo_transport);
                    }
                } else {
                    Glide.with(mContext).load(gameInfo.getIcon()).placeholder(R.mipmap.log_e7yoo_transport).error(R.mipmap.log_e7yoo_transport).into(viewHolderGameInfo.itemGameIcon);
                }
                viewHolderGameInfo.itemGameName.setText(gameInfo.getName());
                viewHolderGameInfo.itemGameContent.setText(gameInfo.getContent());
            }
            addClickListener(viewHolderGameInfo.itemView, position);
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
        return mDatas == null || mDatas.size() == 0 ? 0 : mDatas.size() - 1;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType;
        if (position == getItemCount() - FOOTER_COUNT) {
            itemViewType = VIEW_TYPE_FOOTER;
        } else {
            itemViewType = VIEW_TYPE_GAME_INFO;
        }
        return itemViewType;
    }

    /**
     * 消息item
     */
    public static class ViewHolderGameInfo extends RecyclerView.ViewHolder {
        public ImageView itemGameIcon;
        public TextView itemGameName;
        public TextView itemGameContent;

        public ViewHolderGameInfo(View view) {
            super(view);
            itemGameIcon = view.findViewById(R.id.item_game_list_icon);
            itemGameName = view.findViewById(R.id.item_game_list_name);
            itemGameContent = view.findViewById(R.id.item_game_list_content);
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

    public GameInfo getItem(int position) {
        return mDatas != null && mDatas.size() > position && position >= 0 ? mDatas.get(position) : null;
    }

}
