package com.e7yoo.umeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * Created by andy on 2018/8/30.
 */

public class UmengUtils {

    /**
     * 在application create中调用
     * @param context
     */
    public static void init(Context context) {
        UMConfigure.init(context,"5a12384aa40fa3551f0001d1"
                ,"umeng", UMConfigure.DEVICE_TYPE_PHONE,"afbf92d21efd4a6c01d749e645cac08d");

        PlatformConfig.setWeixin("wx9976c5bd53820af3", "507a0ac39c9f63f7946a042a3130bc87");
        PlatformConfig.setSinaWeibo("1532929699", "1badc6e2cadca25c3863658df419e3af","http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1105639038", "6r7zWLha1AzDv8Gk");
    }

    /**
     * Activity 的onActivityResult回调中调用
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    public static void share(Activity activity, SHARE_MEDIA media, String url, String title, String text, UMImage image, UMShareListener umShareListener) {
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(text);//描述
        new ShareAction(activity)
                .setPlatform(media)//传入平台
                .withText(text)//分享内容
                .withMedia(web)
                .setCallback(umShareListener)//回调监听器
                .share();
    }
}
