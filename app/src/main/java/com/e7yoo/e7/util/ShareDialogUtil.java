package com.e7yoo.e7.util;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.GridAdapter;
import com.e7yoo.e7.model.GridItem;
import com.e7yoo.e7.model.GridItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.qqmodel.QQ;
import cn.jiguang.share.qqmodel.QZone;
import cn.jiguang.share.wechat.Wechat;
import cn.jiguang.share.wechat.WechatMoments;
import cn.jiguang.share.weibo.SinaWeibo;

public class ShareDialogUtil {
    public static final String SHARE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.e7yoo.e7";
    public static final String SHARE_TITLE = "懂你的【小萌伴】";
    public static final String SHARE_CONTENT = "拥有【小萌伴】，闲暇时光·陪你";
    public static final String SHARE_IMAGEPATH = null;
    private static String share_url = SHARE_URL;
    private static String share_title = SHARE_TITLE;
    private static String share_content = SHARE_CONTENT;
    private static String share_imagePath = SHARE_IMAGEPATH;

    private static Dialog dialog;
    private static Context context;

    public static void show(Context context, String url, String title, String content, String iamgePath) {
        show(context);
        share_url = TextUtils.isEmpty(url) ? SHARE_URL : url;
        share_title = TextUtils.isEmpty(title) ? SHARE_TITLE : title;
        share_content = TextUtils.isEmpty(content) ? SHARE_CONTENT : content;
        share_imagePath = TextUtils.isEmpty(iamgePath) ? SHARE_IMAGEPATH : iamgePath;
    }

    public static void show(Context context){
        share_url = SHARE_URL;
        share_title = SHARE_TITLE;
        share_content = SHARE_CONTENT;
        share_imagePath = SHARE_IMAGEPATH;
        if(ShareDialogUtil.context != context || dialog == null) {
            ShareDialogUtil.context = context;
            dialog = new Dialog(context, R.style.ShareDialogStyle);
            //填充对话框的布局
            View view = LayoutInflater.from(context).inflate(R.layout.activity_share, null);
            initDialog(view);
        }
        dialog.show();//显示对话框
    }

    public static void dismiss() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void release() {
        dismiss();
        dialog = null;
        context = null;
        gridItemClickListener = null;
    }

    private static void initDialog(View view) {
        initView(view);
        //将布局设置给Dialog
        dialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
        // 将属性设置给窗体
        dialogWindow.setAttributes(lp);
    }

    private static void initView(View view) {
        GridView gridView = view.findViewById(R.id.share_gv);
        GridAdapter mAdapter = new GridAdapter(gridView.getContext(), getDatas(), true);
        gridView.setAdapter(mAdapter);
    }

    private static ArrayList<GridItem> getDatas() {
        ArrayList<GridItem> items = new ArrayList<>();
        items.add(new GridItem(R.mipmap.share_wechat, R.string.share_to_wx, gridItemClickListener));
        items.add(new GridItem(R.mipmap.share_wxcircle, R.string.share_to_circle, gridItemClickListener));
        items.add(new GridItem(R.mipmap.share_qq, R.string.share_to_qq, gridItemClickListener));
        items.add(new GridItem(R.mipmap.share_qzone, R.string.share_to_qzone, gridItemClickListener));
        // items.add(new GridItem(R.mipmap.share_sina, R.string.share_to_sina, gridItemClickListener));
        return items;
    }

    private static GridItemClickListener gridItemClickListener = new GridItemClickListener() {
        @Override
        public void onGridItemClick(int i, GridItem item) {
            if(item == null) {
                return;
            }
            String name = null;
            ShareParams shareParams = new ShareParams();
            switch (item.getTextResId()) {
                case R.string.share_to_wx:
                    name = Wechat.Name;
                    shareParams.setTitle(share_title);
                    shareParams.setText(share_content);
                    break;
                case R.string.share_to_circle:
                    // 没有text
                    name = WechatMoments.Name;
                    shareParams.setTitle(share_content);
                    break;
                case R.string.share_to_qq:
                    name = QQ.Name;
                    shareParams.setTitle(share_title);
                    shareParams.setText(share_content);
                    break;
                case R.string.share_to_qzone:
                    name = QZone.Name;
                    shareParams.setTitle(share_title);
                    shareParams.setText(share_content);
                    break;
                case R.string.share_to_sina:
                    // 没有title
                    name = SinaWeibo.Name;
                    shareParams.setText(share_content);
                    break;
            }
            if(name != null) {
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setUrl(share_url);//必须
                String imagePath = TextUtils.isEmpty(share_imagePath) ? getImagePath() : share_imagePath;
                if(TextUtils.isEmpty(imagePath)) {
                    if(name.equals(Wechat.Name) || name.equals(WechatMoments.Name)) {
                        shareParams.setImageData(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_share));
                    } else {
                        shareParams.setImageUrl("http://e7yoo.com/apk/logo_share.png");
                    }
                } else {
                    shareParams.setImagePath(imagePath);
                }
                JShareInterface.share(name, shareParams, new PlatActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                        Logs.isDebug();
                    }

                    @Override
                    public void onError(Platform platform, int i, int i1, Throwable throwable) {
                        Logs.isDebug();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Logs.isDebug();

                    }
                });
            }
        }
    };

    private static String getImagePath() {
        try {
            String filePath = FileUtil.getFilePath(context, "share.png");
            if(FileUtil.isFileExists(context, "share.png")) {
                return filePath;
            } else {
                boolean result = FileUtil.copyFromAssetsToSdcard(true, "log_share.png", filePath);
                if(result) {
                    return filePath;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }
}
