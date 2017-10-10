package com.e7yoo.e7.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.GridItem;
import com.umeng.comm.core.beans.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */

public class FeedItemGvAdapter extends BaseAdapter {
    private List<ImageItem> mDatas = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public FeedItemGvAdapter(Context context, List<ImageItem> datas) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        if(datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
    }

    public void refreshDatas(List<ImageItem> datas) {
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
    public ImageItem getItem(int i) {
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
            view = mLayoutInflater.inflate(R.layout.item_feed_item_gv, null, false);
            holder.iv = view.findViewById(R.id.item_feed_item_gv_iv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final ImageItem item = getItem(i);
        if(item != null) {
            Glide.with(mContext).load(item.thumbnail).asBitmap().into(holder.iv);
        }
        return view;
    }

    class ViewHolder{
        ImageView iv;
    }

}
