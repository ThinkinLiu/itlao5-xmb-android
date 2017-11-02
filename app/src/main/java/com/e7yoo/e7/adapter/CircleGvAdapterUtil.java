package com.e7yoo.e7.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.GridView;

import com.e7yoo.e7.community.FeedItemGvAdapter;
import com.umeng.comm.core.beans.ImageItem;

import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */

public class CircleGvAdapterUtil {

    public static FeedItemGvAdapter setGridView(Activity context, GridView gridView, List<ImageItem> images, AdapterView.OnItemClickListener onItemClickListener) {
        FeedItemGvAdapter feedItemGvAdapter;
        removeEmptyImg(images);
        int size = images.size();
        if (size == 0) {
            feedItemGvAdapter = null;
        } else if (size == 1) {
            gridView.setNumColumns(3);
            feedItemGvAdapter = new FeedItemGvAdapter(context, images);
        } else if (size == 4) {
            gridView.setNumColumns(3);
            images.add(2, null);
            feedItemGvAdapter = new FeedItemGvAdapter(context, images);
        } else {
            gridView.setNumColumns(3);
            feedItemGvAdapter = new FeedItemGvAdapter(context, images);
        }
        gridView.setAdapter(feedItemGvAdapter);
        gridView.setOnItemClickListener(onItemClickListener);
        return feedItemGvAdapter;
    }

    private static void removeEmptyImg(List<ImageItem> items) {
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i) == null || (TextUtils.isEmpty(items.get(i).thumbnail) && TextUtils.isEmpty(items.get(i).originImageUrl))) {
                items.remove(i);
            }
        }
    }
}
