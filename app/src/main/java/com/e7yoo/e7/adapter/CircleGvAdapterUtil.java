package com.e7yoo.e7.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.GridView;

import com.e7yoo.e7.community.FeedItemGvAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */

public class CircleGvAdapterUtil {

    public static FeedItemGvAdapter setGridView(Activity context, GridView gridView, List iamges, AdapterView.OnItemClickListener onItemClickListener) {
        FeedItemGvAdapter feedItemGvAdapter;
        int size = iamges.size();
        if (size == 0) {
            feedItemGvAdapter = null;
        } else if (size == 1) {
            gridView.setNumColumns(3);
            feedItemGvAdapter = new FeedItemGvAdapter(context, iamges);
        } else if (size == 4) {
            gridView.setNumColumns(3);
            iamges.add(2, null);
            feedItemGvAdapter = new FeedItemGvAdapter(context, iamges);
        } else {
            gridView.setNumColumns(3);
            feedItemGvAdapter = new FeedItemGvAdapter(context, iamges);
        }
        gridView.setAdapter(feedItemGvAdapter);
        gridView.setOnItemClickListener(onItemClickListener);
        return feedItemGvAdapter;
    }
}
