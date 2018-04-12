//package com.e7yoo.e7.fragment;
//
//import android.os.Bundle;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.View;
//
//import com.e7yoo.e7.R;
//import com.e7yoo.e7.adapter.JokeListRefreshRecyclerAdapter;
//import com.e7yoo.e7.adapter.JokeListRefreshRecyclerAdapterAd;
//import com.e7yoo.e7.adapter.ListRefreshRecyclerAdapter;
//import com.e7yoo.e7.model.Joke;
//import com.e7yoo.e7.model.JokeType;
//import com.e7yoo.e7.net.NetHelper;
//import com.e7yoo.e7.util.Constant;
//import com.e7yoo.e7.util.IOUtils;
//import com.e7yoo.e7.util.JokeUtil;
//import com.e7yoo.e7.util.PreferenceUtil;
//import com.e7yoo.e7.util.UmengUtil;
//import com.qq.e.ads.nativ.ADSize;
//import com.qq.e.ads.nativ.NativeExpressAD;
//import com.qq.e.ads.nativ.NativeExpressADView;
//import com.qq.e.comm.util.AdError;
//import com.tencent.bugly.crashreport.CrashReport;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by andy on 2018/4/6.
// */
//
//public class JokeListFragmentAd extends ListFragment implements NativeExpressAD.NativeExpressADListener {
//    private static final String TAG = JokeListFragmentAd.class.getSimpleName();
//    public static final int AD_COUNT = 10;    // 加载广告的条数，取值范围为[1, 10]
//    private NativeExpressAD mADManager;
//    private List<NativeExpressADView> mAdViewList;
//
//    public static JokeListFragmentAd newInstance() {
//        JokeListFragmentAd fragment = new JokeListFragmentAd();
//        return fragment;
//    }
//
//    private JokeType jokeType = JokeType.JOKE;
//
//    public JokeListFragmentAd setJokeType(JokeType jokeType) {
//        this.jokeType = jokeType;
//        return this;
//    }
//
//    @Override
//    public void onEventMainThread(Message msg) {
//        if(isDetached()) {
//            return;
//        }
//        switch (msg.what) {
//            case Constant.EVENT_BUS_NET_jokeRand:
//                if(JokeType.JOKE == jokeType) {
//                    doMsg(msg);
//                    if(isRefresh) {
//                        UmengUtil.onEvent(UmengUtil.JOKE_LIST_JOKE_REFRESH);
//                    } else {
//                        UmengUtil.onEvent(UmengUtil.JOKE_LIST_JOKE_MORE);
//                    }
//                }
//                break;
//            case Constant.EVENT_BUS_NET_jokeRand_pic:
//                if(JokeType.PIC == jokeType) {
//                    doMsg(msg);
//                    if(isRefresh) {
//                        UmengUtil.onEvent(UmengUtil.JOKE_LIST_PIC_REFRESH);
//                    } else {
//                        UmengUtil.onEvent(UmengUtil.JOKE_LIST_PIC_MORE);
//                    }
//                }
//                break;
//        }
//    }
//
//    private void doMsg(Message msg) {
//        mSRLayout.setRefreshing(false);
//        ArrayList<Joke> joke = JokeUtil.parseJokeRand((JSONObject) msg.obj);
//        if(isRefresh) {
//            if(joke != null && joke.size() > 0) {
//                saveDataToDb(joke);
//                refreshData(joke, true);
//            }
//            mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
//        } else {
//            mRvAdapter.addItemBottom(joke);
//            if(joke == null && joke.size() > 0) {
//                mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.NO_MORE, R.string.loading_no_more, false);
//            } else {
//                mRvAdapter.setFooter(ListRefreshRecyclerAdapter.FooterType.END, R.string.loading_up_load_more, false);
//            }
//        }
//    }
//
//    protected void refreshData(List<Joke> jokes, boolean refresh) {
//        if(mDatas == null || refresh) {
//            mDatas = jokes;
//            mRvAdapter.refreshData(mDatas);
//        }
//    }
//
//    @Override
//    protected ListRefreshRecyclerAdapter initAdapter() {
//        return new JokeListRefreshRecyclerAdapterAd(getContext());
//    }
//
//    @Override
//    protected void addListener() {
//
//    }
//
//    boolean isRefresh;
//    @Override
//    protected void loadDataFromNet(boolean isRefresh) {
//        this.isRefresh = isRefresh;
//        if(jokeType == null) {
//            jokeType = JokeType.JOKE;
//        }
//        switch (jokeType) {
//            case PIC:
//                NetHelper.newInstance().jokeRand(true);
//                break;
//            case JOKE:
//                NetHelper.newInstance().jokeRand(false);
//                break;
//            case ALL:
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void loadDataFromDb() {
//        String jokeList = PreferenceUtil.getString(getKey(jokeType), null);
//        try {
//            if(jokeList == null) {
//                return;
//            }
//            Object obj = IOUtils.UnserializeStringToObject(jokeList);
//            if(obj != null) {
//                ArrayList<Joke> jokes = (ArrayList<Joke>) obj;
//                refreshData(jokes, false);
//            }
//        } catch (Throwable e) {
//            CrashReport.postCatchedException(e);
//        }
//    }
//
//    private void saveDataToDb(ArrayList<Joke> jokes) {
//        PreferenceUtil.commitString(getKey(jokeType), IOUtils.SerializeObjectToString(jokes));
//    }
//
//    private String getKey(JokeType jokeType) {
//        String preferenceKey;
//        if(jokeType == null) {
//            jokeType = JokeType.JOKE;
//        }
//        switch (jokeType) {
//            case JOKE:
//                preferenceKey = Constant.PREFERENCE_CIRCLE_JOKE_JOKE;
//                break;
//            case PIC:
//                preferenceKey = Constant.PREFERENCE_CIRCLE_JOKE_PIC;
//                break;
//            case ALL:
//            default:
//                preferenceKey = Constant.PREFERENCE_CIRCLE_JOKE_ALL;
//                break;
//        }
//        return preferenceKey;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initNativeExpressAD();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // 使用完了每一个NativeExpressADView之后都要释放掉资源。
//        if (mAdViewList != null) {
//            for (NativeExpressADView view : mAdViewList) {
//                if(view != null) {
//                    view.destroy();
//                }
//            }
//        }
//    }
//
//    /**
//     */
//    private void initNativeExpressAD() {
//        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
//        mADManager = new NativeExpressAD(getContext(), adSize, Constant.APPID, Constant.NativeExpressPosID, this);
//        mADManager.loadAD(AD_COUNT);
//    }
//
//    @Override
//    public void onNoAD(AdError adError) {
//        Log.i(
//                TAG,
//                String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
//                        adError.getErrorMsg()));
//    }
//
//    @Override
//    public void onADLoaded(List<NativeExpressADView> adList) {
//        Log.i(TAG, "onADLoaded: " + adList.size());
//        mAdViewList = adList;
//        if(mRvAdapter != null && mRvAdapter instanceof JokeListRefreshRecyclerAdapterAd) {
//            ((JokeListRefreshRecyclerAdapterAd) mRvAdapter).setAdViewList(adList);
//        }
//    }
//
//    @Override
//    public void onRenderFail(NativeExpressADView adView) {
//        Log.i(TAG, "onRenderFail: " + adView.toString());
//    }
//
//    @Override
//    public void onRenderSuccess(NativeExpressADView adView) {
//        Log.i(TAG, "onRenderSuccess: " + adView.toString());
//    }
//
//    @Override
//    public void onADExposure(NativeExpressADView adView) {
//        Log.i(TAG, "onADExposure: " + adView.toString());
//    }
//
//    @Override
//    public void onADClicked(NativeExpressADView adView) {
//        Log.i(TAG, "onADClicked: " + adView.toString());
//    }
//
//    @Override
//    public void onADClosed(NativeExpressADView adView) {
//        Log.i(TAG, "onADClosed: " + adView.toString());
//        if (mRvAdapter != null && mRvAdapter instanceof JokeListRefreshRecyclerAdapterAd) {
//            ((JokeListRefreshRecyclerAdapterAd) mRvAdapter).removeADView(adView);
//        }
//    }
//
//    @Override
//    public void onADLeftApplication(NativeExpressADView adView) {
//        Log.i(TAG, "onADLeftApplication: " + adView.toString());
//    }
//
//    @Override
//    public void onADOpenOverlay(NativeExpressADView adView) {
//        Log.i(TAG, "onADOpenOverlay: " + adView.toString());
//    }
//
//    @Override
//    public void onADCloseOverlay(NativeExpressADView adView) {
//        Log.i(TAG, "onADCloseOverlay");
//    }
//}
