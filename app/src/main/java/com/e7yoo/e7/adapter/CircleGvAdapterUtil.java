package com.e7yoo.e7.adapter;

import android.content.Context;
import android.widget.GridView;

import com.e7yoo.e7.community.FeedItemGvAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */

public class CircleGvAdapterUtil {

    public static void setGridView(Context context, GridView gridView, List iamges) {
        int size = iamges.size();
        if (size == 0) {
            gridView.setAdapter(null);
        } else if (size == 1) {
            gridView.setNumColumns(3);
            gridView.setAdapter(new FeedItemGvAdapter(context, iamges));
        } else if (size == 4) {
            gridView.setNumColumns(3);
            iamges.add(2, null);
            gridView.setAdapter(new FeedItemGvAdapter(context, iamges));
        } else {
            gridView.setNumColumns(3);
            gridView.setAdapter(new FeedItemGvAdapter(context, iamges));
        }
    }
}
