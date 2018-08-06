package com.e7yoo.e7.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.R;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.model.feed;
import com.e7yoo.e7.util.ActivityUtil;

/**
 * Created by andy on 2018/4/6.
 */

public class Joke2ListRefreshRecyclerAdapter extends ListRefreshRecyclerAdapter {
    private static final int[] BG_RESOURCE = {
            R.drawable.rounded_corners_bg_joke1,
            R.drawable.rounded_corners_bg_joke2,
            R.drawable.rounded_corners_bg_joke3,
            R.drawable.rounded_corners_bg_joke4,
            R.drawable.rounded_corners_bg_joke5
    };

    public Joke2ListRefreshRecyclerAdapter(Context context) {
        super(context);
    }

    private boolean showCollect = false;
    public void setShowCollect(boolean showCollect) {
        this.showCollect = showCollect;
        notifyDataSetChanged();
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_joke, parent, false);
        return new ViewHolderJoke(view);
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ViewHolderJoke) {
            final ViewHolderJoke viewHolderJoke = (ViewHolderJoke) holder;
            viewHolderJoke.root.setBackgroundResource(BG_RESOURCE[position % BG_RESOURCE.length]);
            final feed item = (feed) mDatas.get(position);
            if (item != null) {
                viewHolderJoke.icon.setVisibility(View.GONE);
                viewHolderJoke.name.setVisibility(View.GONE);
                if(showCollect) {
                    viewHolderJoke.collect.setVisibility(View.VISIBLE);
                    viewHolderJoke.collect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onCollectListener != null) {
                                onCollectListener.onCollect(v, item, position);
                            }
                        }
                    });
                } else {
                    viewHolderJoke.collect.setVisibility(View.GONE);
                }
                String content = item.getTitle();
                if(item.getContent() != null) {
                    content = content + item.getContent();
                }
                viewHolderJoke.content.setText(content);
                final String url = item.getImg();
                if(url != null && url.length() > 0) {
                    viewHolderJoke.pic.setVisibility(View.VISIBLE);
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.mipmap.log_e7yoo_transport).error(R.mipmap.log_e7yoo_transport);
                    if(url.endsWith(".gif")) {
                        Glide.with(mContext).asGif().load(url).apply(options).into(viewHolderJoke.pic);
                    } else {
                        Glide.with(mContext).asBitmap().load(url).apply(options).into(viewHolderJoke.pic);
                    }
                    viewHolderJoke.pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityUtil.toNewsWebviewActivity(mContext, url, NewsWebviewActivity.INTENT_FROM_JOKE_LIST);
                        }
                    });
                } else {
                    viewHolderJoke.pic.setVisibility(View.GONE);
                }
            }
        }
    }

    public static class ViewHolderJoke extends RecyclerView.ViewHolder {
        private View root;
        private ImageView icon;
        private TextView name;
        private ImageView collect;
        private TextView content;
        private ImageView pic;
        public ViewHolderJoke(View view) {
            super(view);
            root = view.findViewById(R.id.root_layout);
            icon = view.findViewById(R.id.item_joke_icon);
            name = view.findViewById(R.id.item_joke_username);
            collect = view.findViewById(R.id.item_joke_collect);
            content = view.findViewById(R.id.item_joke_content);
            pic = view.findViewById(R.id.item_joke_pic);
        }
    }

    public void remove(int position) {
        if(position >= 0 && position < mDatas.size()) {
            mDatas.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mDatas.size() - position);
        }
    }

    public feed getLastJoke() {
        if(mDatas == null || mDatas.size() == 0) {
            return null;
        } else {
            return ((feed) mDatas.get(mDatas.size() - 1));
        }
    }

    private OnCollectListener onCollectListener;
    public void setOnCollectListener(OnCollectListener onCollectListener) {
        this.onCollectListener = onCollectListener;
    }

    public interface OnCollectListener {
        void onCollect(View view, feed joke, int position);
    }
}
