package com.e7yoo.e7.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */

public class PostGvAdapter extends BaseAdapter {
    private ArrayList<String> mDatas = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public PostGvAdapter(Context context, List<String> datas) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        if(datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
    }

    public ArrayList<String> getDatas() {
        return mDatas;
    }

    public void refreshDatas(List<String> datas) {
        mDatas.clear();
        if(datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size() >= 9 ? 9 : mDatas.size() + 1;
    }

    @Override
    public String getItem(int i) {
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
            holder.deleteIv = view.findViewById(R.id.item_feed_item_gv_delete_iv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final String str = getItem(i);
        if(str != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.log_e7yoo_transport).error(R.mipmap.log_e7yoo_transport);
            Glide.with(mContext).load(str)
                    .apply(options).into(holder.iv);
            holder.deleteIv.setVisibility(View.VISIBLE);
            holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(i);
                    notifyDataSetChanged();
                }
            });
        } else {
            Glide.with(mContext).load(R.mipmap.circle_img_add).into(holder.iv);
            holder.deleteIv.setVisibility(View.GONE);
            holder.deleteIv.setOnClickListener(null);
        }
        return view;
    }

    class ViewHolder{
        ImageView iv;
        ImageView deleteIv;
    }

}
