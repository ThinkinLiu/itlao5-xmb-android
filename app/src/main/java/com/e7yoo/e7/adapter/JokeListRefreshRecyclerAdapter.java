package com.e7yoo.e7.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.ChatActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.model.Joke;
import com.e7yoo.e7.util.ActivityUtil;

/**
 * Created by andy on 2018/4/6.
 */

public class JokeListRefreshRecyclerAdapter extends ListRefreshRecyclerAdapter {

    public JokeListRefreshRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_joke, parent, false);
        return new ViewHolderJoke(view);
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderJoke) {
            ViewHolderJoke viewHolderJoke = (ViewHolderJoke) holder;
            if(position % 2 == 0) {
                viewHolderJoke.root.setBackgroundResource(R.drawable.rounded_corners_bg_joke2);
            } else {
                viewHolderJoke.root.setBackgroundResource(R.drawable.rounded_corners_bg_joke1);
            }
            Joke item = (Joke) mDatas.get(position);
            if (item != null) {
                viewHolderJoke.icon.setVisibility(View.GONE);
                viewHolderJoke.name.setVisibility(View.GONE);
                viewHolderJoke.content.setText(item.getContent());
                final String url = item.getUrl();
                if(url != null) {
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
        private TextView content;
        private ImageView pic;
        public ViewHolderJoke(View view) {
            super(view);
            root = view.findViewById(R.id.root_layout);
            icon = view.findViewById(R.id.item_joke_icon);
            name = view.findViewById(R.id.item_joke_username);
            content = view.findViewById(R.id.item_joke_content);
            pic = view.findViewById(R.id.item_joke_pic);
        }
    }
}
