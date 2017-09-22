package com.e7yoo.e7.util;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
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
    public static String share_url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.e7yoo.e7";
    public static String share_title = "懂你的【小萌伴】";
    public static String share_content = "拥有【小萌伴】，闲暇时光·陪你";

    private static Dialog dialog;
    private static Context context;

    public static void show(Context context){
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
        GridAdapter mAdapter = new GridAdapter(gridView.getContext(), getDatas());
        gridView.setAdapter(mAdapter);
    }

    private static ArrayList<GridItem> getDatas() {
        ArrayList<GridItem> items = new ArrayList<>();
        items.add(new GridItem(R.mipmap.item_chat_gridview_picture, R.string.share_to_wx, gridItemClickListener));
        items.add(new GridItem(R.mipmap.item_chat_gridview_picture, R.string.share_to_circle, gridItemClickListener));
        items.add(new GridItem(R.mipmap.item_chat_gridview_picture, R.string.share_to_qq, gridItemClickListener));
        items.add(new GridItem(R.mipmap.item_chat_gridview_picture, R.string.share_to_qzone, gridItemClickListener));
        items.add(new GridItem(R.mipmap.item_chat_gridview_picture, R.string.share_to_sina, gridItemClickListener));
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
                shareParams.setImagePath(getImagePath(R.mipmap.logo_share));
                JShareInterface.share(name, shareParams, new PlatActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                    }

                    @Override
                    public void onError(Platform platform, int i, int i1, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
            }
        }
    };

    private static String getImagePath(int resId) {
        try {
            return ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getResources().getResourcePackageName(resId)
                    + "/" + context.getResources().getResourceTypeName(resId)
                    + "/" + context.getResources().getResourceEntryName(resId);
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }
}
