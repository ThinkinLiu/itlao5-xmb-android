package com.e7yoo.e7.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.model.GridItem;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/21.
 */

public class GridAdapter extends BaseAdapter {
    private ArrayList<GridItem> mDatas = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public GridAdapter(Context context, ArrayList<GridItem> datas) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        if(datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
    }

    public void refreshDatas(ArrayList<GridItem> datas) {
        mDatas.clear();
        if(datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public GridItem getItem(int i) {
        return mDatas.size() > i ? mDatas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null) {
            holder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.item_chat_gridview, null, false);
            holder.iv = view.findViewById(R.id.item_chat_gridview_iv);
            holder.tv = view.findViewById(R.id.item_chat_gridview_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final GridItem item = getItem(i);
        if(item != null) {
            holder.iv.setImageResource(item.getTopDrawableResId());
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(item != null && item.getGridItemClickListener() != null) {
                        item.getGridItemClickListener().onGridItemClick(i, item);
                    }
                }
            });
            holder.tv.setText(item.getTextResId());
        }
        return view;
    }

    class ViewHolder{
        ImageView iv;
        TextView tv;
    }

}
