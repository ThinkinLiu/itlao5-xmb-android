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
import com.e7yoo.e7.model.Joke;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.RandomUtil;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andy on 2018/4/6.
 */

public class JokeListRefreshRecyclerAdapterAd extends ListRefreshRecyclerAdapter {
    public static int FIRST_AD_POSITION = 5; // 第一条广告的位置
    public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告

    public JokeListRefreshRecyclerAdapterAd(Context context) {
        super(context);
    }
    private List<NativeExpressADView> mAdViewList;

    // 把返回的NativeExpressADView添加到数据集里面去
    public void setAdViewList(List<NativeExpressADView> adList) {
        mAdViewList = adList;
        int result = 0;
        if (adList != null && adList.size() > 0) {
            result = setAdViewListToData(adList);
        }
        if(result > 0) {
            notifyDataSetChanged();
        }
    }

    private int setAdViewListToData(List<NativeExpressADView> adList) {
        int result = 0;
        if(adList != null) {
            for(int i = mAdViewPositionMap.size(); i < adList.size(); i++) {
                int position = FIRST_AD_POSITION + ITEMS_PER_AD * i + RandomUtil.getRandomNum(5) - 2;
                if(position < mDatas.size()) {
                    mAdViewPositionMap.put(adList.get(i), position);
                    mDatas.add(position, adList.get(i));
                    result++;
                } else {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void beforeRefreshData() {
        mAdViewPositionMap.clear();
        setAdViewListToData(mAdViewList);
        super.beforeRefreshData();
    }

    @Override
    public void beforeAddItemBottom() {
        setAdViewListToData(mAdViewList);
        super.beforeAddItemBottom();
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(NativeExpressADView adView) {
        Integer position = mAdViewPositionMap.get(adView);
        if(position == null) {
            return;
        }
        mDatas.remove(position);
        notifyItemRemoved(position); // position为adView在当前列表中的位置
        notifyItemRangeChanged(0, mDatas.size() - 1);
    }

    static final int VIEW_TYPE_AD = 100;

    @Override
    public int initItemViewType(int position) {
        if(mDatas.get(position) instanceof NativeExpressADView) {
            return VIEW_TYPE_AD;
        } else {
            return super.initItemViewType(position);
        }
    }

    @Override
    protected RecyclerView.ViewHolder initViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == VIEW_TYPE_AD) {
            View view = mInflater.inflate(R.layout.item_joke_ad, parent, false);
            viewHolder = new ViewHolderAd(view);
        } else {
            View view = mInflater.inflate(R.layout.item_joke, parent, false);
            viewHolder = new ViewHolderJoke(view);
        }
        return viewHolder;
    }
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();

    public HashMap<NativeExpressADView, Integer> getmAdViewPositionMap() {
        return mAdViewPositionMap;
    }

    @Override
    protected void setHolderView(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderAd) {
            ViewHolderAd viewHolderAd = (ViewHolderAd) holder;
            final NativeExpressADView adView = (NativeExpressADView) mDatas.get(position);
            mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
            if (viewHolderAd.container.getChildCount() > 0
                    && viewHolderAd.container.getChildAt(0) == adView) {
                return;
            }

            if (viewHolderAd.container.getChildCount() > 0) {
                viewHolderAd.container.removeAllViews();
            }

            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }

            viewHolderAd.container.addView(adView);
            adView.render(); // 调用render方法后sdk才会开始展示广告
        } else if(holder instanceof ViewHolderJoke) {
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

    class ViewHolderAd extends RecyclerView.ViewHolder {
        public ViewGroup container;

        public ViewHolderAd(View view) {
            super(view);
            container = (ViewGroup) view.findViewById(R.id.joke_ad_container);
        }
    }
}
