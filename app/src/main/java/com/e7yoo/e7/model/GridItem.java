package com.e7yoo.e7.model;

import android.view.View;

/**
 * Created by Administrator on 2017/9/21.
 */

public class GridItem {
    private int topDrawableResId;
    private int textResId;
    private GridItemClickListener gridItemClickListener;

    public GridItem(int topDrawableResId, int textResId, GridItemClickListener gridItemClickListener) {
        this.topDrawableResId = topDrawableResId;
        this.textResId = textResId;
        this.gridItemClickListener = gridItemClickListener;
    }

    public int getTopDrawableResId() {
        return topDrawableResId;
    }

    public void setTopDrawableResId(int topDrawableResId) {
        this.topDrawableResId = topDrawableResId;
    }

    public int getTextResId() {
        return textResId;
    }

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }

    public GridItemClickListener getGridItemClickListener() {
        return gridItemClickListener;
    }

    public void setGridItemClickListener(GridItemClickListener gridItemClickListener) {
        this.gridItemClickListener = gridItemClickListener;
    }
}
