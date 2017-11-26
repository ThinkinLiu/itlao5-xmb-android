package com.e7yoo.e7.community.listener;

import com.umeng.comm.core.beans.FeedItem;

/**
 * Created by andy on 2017/11/24.
 */

public interface OnFeedClickListener {
    boolean onSourceFeedClick(int position, FeedItem feedItem);
    boolean onShare2Click(int position, FeedItem feedItem);
    boolean onShareClick(int position, FeedItem feedItem);
    boolean onCommentClick(int position, FeedItem feedItem);
    boolean onPriseClick(int position, FeedItem feedItem);
}
