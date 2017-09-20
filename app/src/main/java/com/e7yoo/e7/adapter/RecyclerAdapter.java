package com.e7yoo.e7.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.e7yoo.e7.model.Robot;

/**
 * Created by Administrator on 2017/9/19.
 */

public abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(View view, int position);
    }
}
