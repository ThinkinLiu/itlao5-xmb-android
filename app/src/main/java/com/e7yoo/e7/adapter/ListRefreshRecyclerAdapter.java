package com.e7yoo.e7.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.e7yoo.e7.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */
public abstract class ListRefreshRecyclerAdapter extends RecyclerAdapter {
    protected LayoutInflater mInflater;
    protected List mDatas = new ArrayList();
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_FOOTER = 10;
    /** 用于Footer的类型 */
    protected FooterType mFooterType = FooterType.DEFAULT;
    /** 用于Footer的类型 */
    private boolean mFooterShowProgress = false;
    /** 用于Footer的文字显示，<= 0 时不显示GONE */
    private int mFooterStringId = 0;
    private static final int FOOTER_COUNT = 1;
    protected Context mContext;

    public ListRefreshRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        // DebugUtil.setDatas(mDatas, 1, true);
    }

    public <T> void addItemTop(T newData) {
        mDatas.add(0, newData);
        notifyDataSetChanged();
    }

    public <T> void addItemTop(List<T> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mDatas.addAll(0, newDatas);
            notifyDataSetChanged();
        }
    }

    public <T> void addItemBottom(List <T> newDatas) {
        if(newDatas != null && newDatas.size() > 0) {
            mDatas.addAll(newDatas);
            beforeAddItemBottom();
            notifyDataSetChanged();
        }
    }

    public <T> void addItemBottom(T newData) {
        mDatas.add(newData);
        beforeAddItemBottom();
        notifyDataSetChanged();
    }

    public <T> void refreshData(List<T> newDatas) {
        mDatas.clear();
        if(newDatas != null) {
            mDatas.addAll(newDatas);
        }
        beforeRefreshData();
        notifyDataSetChanged();
    }

    public void beforeRefreshData() {

    }


    public void beforeAddItemBottom() {

    }

    public FooterType getFooter() {
        return mFooterType;
    }

    public void setFooter(FooterType type, int footerStringId, boolean footerShowProgress) {
        mFooterType = type;
        mFooterStringId = footerStringId;
        mFooterShowProgress = footerShowProgress;
        //notifyDataSetChanged();
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case VIEW_TYPE_FOOTER:
                View view = mInflater.inflate(R.layout.item_msg_footer, parent, false);
                viewHolder = new ViewHolderFooter(view);
                break;
            case VIEW_TYPE_DATA:
            default:
                viewHolder = initViewHolder(parent, viewType);
                break;
        }
        return viewHolder;
    }

    protected abstract RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderFooter) {
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
        } else {
            setHolderView(holder, position);
        }
        holder.itemView.setTag(position);
    }

    protected abstract void setHolderView(RecyclerView.ViewHolder holder, int position);

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
        if (FOOTER_COUNT > 0 && position >= getItemCount() - FOOTER_COUNT) {
            itemViewType = VIEW_TYPE_FOOTER;
        } else {
            itemViewType = initItemViewType(position);
        }
        return itemViewType;
    }

    public int initItemViewType(int position) {
        return VIEW_TYPE_DATA;
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

    public Object getItem(int position) {
        return mDatas != null && mDatas.size() > position && position >= 0 ? mDatas.get(position) : null;
    }

}
