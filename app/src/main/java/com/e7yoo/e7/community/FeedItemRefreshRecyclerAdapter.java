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
import com.umeng.comm.core.beans.FeedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */
public class FeedItemRefreshRecyclerAdapter extends RecyclerAdapter {
    private LayoutInflater mInflater;
    private List<FeedItem> mDatas = new ArrayList<>();
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

    public FeedItemRefreshRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        // DebugUtil.setDatas(mDatas, 1, true);
    }

    public void addItemTop(FeedItem newData) {
        mDatas.add(0, newData);
        notifyDataSetChanged();
    }

    public void addItemTop(List<FeedItem> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mDatas.addAll(0, newDatas);
            notifyItemRangeChanged(0, newDatas.size());
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(List<FeedItem> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mDatas.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void addItemBottom(FeedItem newData) {
        mDatas.add(newData);
        notifyDataSetChanged();
    }

    public void refreshData(List<FeedItem> newDatas) {
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

    public long getLastId() {
        return (mDatas == null || mDatas.size() == 0) ? -1 : mDatas.get(0).getId();
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
                view = mInflater.inflate(R.layout.item_feed_item, parent, false);
                viewHolder = new ViewHolderFeedItem(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderFeedItem) {
            ViewHolderFeedItem viewHolderFeedItem = (ViewHolderFeedItem) holder;
            FeedItem item = mDatas.get(position);
            if(item != null) {
                setUser(viewHolderFeedItem, item);
                viewHolderFeedItem.contentTv.setText(item.text);
                int size = item.getImages().size();
                if(size == 0) {
                    viewHolderFeedItem.gridView.setAdapter(null);
                } else if(size == 1) {
                    viewHolderFeedItem.gridView.setNumColumns(2);
                    viewHolderFeedItem.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.getImages()));
                } else if(size == 2 || size == 4) {
                    viewHolderFeedItem.gridView.setNumColumns(2);
                    viewHolderFeedItem.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.getImages()));
                } else {
                    viewHolderFeedItem.gridView.setNumColumns(3);
                    viewHolderFeedItem.gridView.setAdapter(new FeedItemGvAdapter(mContext, item.getImages()));
                }
                viewHolderFeedItem.timeTv.setText(item.addTime);
                viewHolderFeedItem.shareTv.setText(String.format("%-3d", item.forwardCount));
                viewHolderFeedItem.commentTv.setText(String.format("%-3d", item.commentCount));
                viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount));
            }
            addClickListener(viewHolderFeedItem.itemView, position);
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

    public FeedItem getItem(int position) {
        return mDatas != null && mDatas.size() > position && position >= 0 ? mDatas.get(position) : null;
    }

}
