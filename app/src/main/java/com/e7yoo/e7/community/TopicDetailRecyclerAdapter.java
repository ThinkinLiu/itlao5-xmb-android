package com.e7yoo.e7.community;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.CircleGvAdapterUtil;
import com.e7yoo.e7.util.CommUserUtil;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.TimeUtil;
import com.e7yoo.e7.view.CircleGridView;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPreview;

/**
 * Created by Administrator on 2017/9/25.
 */
public class TopicDetailRecyclerAdapter extends ListRefreshRecyclerAdapter {
    private static final int VIEW_TYPE_TOPIC = 100;

    public void refreshData(Topic topic, List<FeedItem> feedItemList) {
        mDatas.clear();
        mDatas.add(topic);
        mDatas.addAll(feedItemList);
        notifyDataSetChanged();
    }

    public void addFeedItem(FeedItem feedItem) {
        int num = 0;
        if(mDatas.size() > 0 && mDatas.get(0) instanceof  CommUser) {
            num = 1;
        }
        mDatas.add(num, feedItem);
        notifyDataSetChanged();
    }

    public void refreshTopic(Topic topic) {
        if(mDatas.size() > 0) {
            if(mDatas.get(0) instanceof  Topic) {
                mDatas.set(0, topic);
                return;
            }
        }
        mDatas.add(0, topic);
        notifyDataSetChanged();
    }

    public void refreshFeedItems(List<FeedItem> feedItemList) {
        Topic topic = null;
        if(mDatas.size() > 0 && mDatas.get(0) instanceof  Topic) {
            topic = (Topic) mDatas.get(0);
        }
        mDatas.clear();
        if(topic != null) {
            mDatas.add(topic);
        }
        mDatas.addAll(feedItemList);
        notifyDataSetChanged();
    }

    public TopicDetailRecyclerAdapter(Activity context) {
        super(context);
    }

    @Override
    public int initItemViewType(int position) {
        if(getItem(position) instanceof Topic) {
            return VIEW_TYPE_TOPIC;
        } else {
            return super.initItemViewType(position);
        }
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_TOPIC) {
            View view = mInflater.inflate(R.layout.item_topic_detail, parent, false);
            return new ViewHolderTopic(view);
        } else {
            View view = mInflater.inflate(R.layout.item_feed_item, parent, false);
            return new ViewHolderFeedItem(view);
        }
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderFeedItem) {
            ViewHolderFeedItem viewHolderFeedItem = (ViewHolderFeedItem) holder;
            FeedItem item = (FeedItem) mDatas.get(position);
            if (item != null) {
                setUser(viewHolderFeedItem, item.creator);
                setViewTypeFeedItem(viewHolderFeedItem, item);
            }
            addClickListener(viewHolderFeedItem.itemView, position);
            addItemClickForGridView(viewHolderFeedItem.gridView, viewHolderFeedItem.itemView, position);
        } else if(holder instanceof ViewHolderTopic) {
            ViewHolderTopic viewHolderUser = (ViewHolderTopic) holder;
            Topic item = (Topic) mDatas.get(position);
            if(item != null) {
                setViewTypeTopic(viewHolderUser, item);
            }
        }
    }

    private void setUser(BaseViewHolder viewHolderFeedItem, CommUser item) {
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.log_e7yoo_transport)
                .error(R.mipmap.log_e7yoo_transport)
                .override(124, 124);
        Glide.with(mContext)
                .load(item.iconUrl)
                .apply(options)
                .into(viewHolderFeedItem.userIcon);
        int sexIcon;
        switch (item.gender) {
            case FEMALE:
                sexIcon = R.mipmap.sex_female_selected;
                break;
            case MALE:
                sexIcon = R.mipmap.sex_male_selected;
                break;
            default:
                sexIcon = R.mipmap.sex_unknow_selected;
                break;
        }
        viewHolderFeedItem.sexIcon.setImageResource(sexIcon);
        viewHolderFeedItem.usernameTv.setText(item.name);
    }

    private void setViewTypeFeedItem(final BaseViewHolder viewHolderFeedItem, final FeedItem item) {
        viewHolderFeedItem.contentTv.setMaxLines(1000);
        String content = "";
        if(item.topics != null) {
            for(int i = 0; i < item.topics.size(); i++) {
                if(item.topics.get(i) != null && !TextUtils.isEmpty(item.topics.get(i).name)) {
                    content = content + "<font color= 'blue'>" + item.topics.get(i).name + "</font> ";
                }
            }
        }
        content = content + item.text;
        viewHolderFeedItem.contentTv.setText(CommonUtil.getHtmlStr(content));
        CircleGvAdapterUtil.setGridView(mContext, viewHolderFeedItem.gridView, item.getImages(), mGvItemClick);
        viewHolderFeedItem.timeTv.setText(TimeUtil.formatFeedTime(item.publishTime));
        viewHolderFeedItem.shareTv.setText(String.format("%-3d", item.forwardCount));
        viewHolderFeedItem.commentTv.setText(String.format("%-3d", item.commentCount));
        viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount));
        viewHolderFeedItem.praiseTv.setSelected(item.isLiked);
        viewHolderFeedItem.praiseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.isLiked) {
                    viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount - 1));
                    viewHolderFeedItem.praiseTv.setSelected(!item.isLiked);
                } else {
                    viewHolderFeedItem.praiseTv.setText(String.format("%-3d", item.likeCount + 1));
                    viewHolderFeedItem.praiseTv.setSelected(!item.isLiked);
                }
                likeFeed(viewHolderFeedItem, item);
            }
        });
    }

    private void setViewTypeTopic(final ViewHolderTopic viewHolder, final Topic topic) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.log_e7yoo_transport).error(R.mipmap.log_e7yoo_transport);
        Glide.with(mContext).load(topic.icon).apply(options).into(viewHolder.userIcon);
        viewHolder.nameTv.setText(topic.name);
        viewHolder.infoTv.setText(mContext.getString(R.string.topic_detail_info, topic.feedCount, topic.fansCount));
        viewHolder.descTv.setText(topic.desc);
    }

    private void addItemClickForGridView(GridView gridView, final View mView, final int mPosition) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mView, mPosition);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(mView, mPosition);
                }
                return false;
            }
        });
        if(gridView instanceof CircleGridView) {
            ((CircleGridView) gridView).setOnTouchInvalidPositionListener(new CircleGridView.OnTouchInvalidPositionListener() {
                @Override
                public boolean onTouchInvalidPosition(int motionEvent) {
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mView, mPosition);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void likeFeed(final BaseViewHolder viewHolder, final FeedItem item) {
        if(item.isLiked) {
            E7App.getCommunitySdk().postUnLike(item.id, new Listeners.SimpleFetchListener<SimpleResponse>() {
                @Override
                public void onComplete(SimpleResponse simpleResponse) {
                    if(simpleResponse.errCode == ErrorCode.NO_ERROR) {
                        item.isLiked = false;
                        item.likeCount--;
                        viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount));
                        viewHolder.praiseTv.setSelected(item.isLiked);
                    }
                }
            });
        } else {
            E7App.getCommunitySdk().postLike(item.id, new Listeners.SimpleFetchListener<SimpleResponse>() {
                @Override
                public void onComplete(SimpleResponse simpleResponse) {
                    if(simpleResponse.errCode == ErrorCode.NO_ERROR) {
                        item.isLiked = true;
                        item.likeCount++;
                        viewHolder.praiseTv.setText(String.format("%-3d", item.likeCount));
                        viewHolder.praiseTv.setSelected(item.isLiked);
                    }
                }
            });
        }
    }

    private AdapterView.OnItemClickListener mGvItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(parent != null && parent.getAdapter() != null && parent.getAdapter() instanceof FeedItemGvAdapter) {
                FeedItemGvAdapter adapter = (FeedItemGvAdapter) parent.getAdapter();
                ArrayList<String> images = new ArrayList<>();
                for(ImageItem imageItem : adapter.getDatas()) {
                    if(imageItem != null && imageItem.originImageUrl != null) {
                        images.add(imageItem.originImageUrl);
                    }
                }
                if(images.size() > 0) {
                    if(position >= images.size()) {
                        position = images.size() - 1;
                    }
                    PhotoPreview.builder()
                            .setPhotos(images)
                            .setCurrentItem(position)
                            .setShowDeleteButton(false)
                            .start(mContext);
                }
            }
        }
    };

    private void addClickListener(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public View rootLayout;
        public ImageView userIcon;
        public ImageView sexIcon;
        public TextView usernameTv;
        public TextView contentTv;
        public GridView gridView;
        public TextView timeTv;
        public TextView shareTv;
        public TextView commentTv;
        public TextView praiseTv;

        public BaseViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }

    /**
     * 消息item
     */
    public static class ViewHolderFeedItem extends BaseViewHolder {


        public ViewHolderFeedItem(View view) {
            super(view);
            userIcon = view.findViewById(R.id.item_feed_item_icon);
            sexIcon = view.findViewById(R.id.item_feed_item_sex);
            usernameTv = view.findViewById(R.id.item_feed_item_username);
            contentTv = view.findViewById(R.id.item_feed_item_content);
            gridView = view.findViewById(R.id.item_feed_item_gv);
            timeTv = view.findViewById(R.id.item_feed_item_time);
            shareTv = view.findViewById(R.id.item_feed_item_share);
            commentTv = view.findViewById(R.id.item_feed_item_comment);
            praiseTv = view.findViewById(R.id.item_feed_item_praise);
        }
    }

    /**
     * 消息item
     */
    public static class ViewHolderTopic extends RecyclerView.ViewHolder {
        public ImageView userIcon;
        public TextView nameTv;
        public TextView infoTv;
        public TextView descTv;

        public ViewHolderTopic(View view) {
            super(view);
            userIcon = view.findViewById(R.id.item_topic_detail_icon);
            nameTv = view.findViewById(R.id.item_topic_detail_name);
            infoTv = view.findViewById(R.id.item_topic_detail_info);
            descTv = view.findViewById(R.id.item_topic_detail_desc);
        }
    }
}
