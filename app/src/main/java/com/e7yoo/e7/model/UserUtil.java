package com.e7yoo.e7.model;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.MyIconUtil;

/**
 * Created by Administrator on 2018/5/11.
 */

public class UserUtil {

    public static String getSex(int sex) {
        switch (sex) {
            case 1:
                return E7App.mApp.getString(R.string.sex_male);
            case 2:
                return E7App.mApp.getString(R.string.sex_female);
            case 0:
            default:
                return E7App.mApp.getString(R.string.sex_unknow);
        }
    }

    public static int getSex(String sex) {
        if(E7App.mApp.getString(R.string.sex_male).equals(sex)) {
            return 1;
        } else if(E7App.mApp.getString(R.string.sex_female).equals(sex)) {
            return 2;
        }
        return 0;
    }

    public static void setIcon(Context context, ImageView icon, String path) {
        if(path != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
            Glide.with(context).load(path).apply(options).into(icon);
        } else {
            icon.setImageResource(R.mipmap.icon_me);
        }
    }

    public static void setIcon(Context context, ImageView icon, User user) {
        if(user != null && user.getIcon() != null){
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
            Glide.with(context).load(user.getIcon()).apply(options).into(icon);
        } else {
             String myIcon = MyIconUtil.getMyIcon();
            if(myIcon != null) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.icon_me).error(R.mipmap.icon_me);
                Glide.with(context).load(myIcon).apply(options).into(icon);
            } else {
                icon.setImageResource(R.mipmap.icon_me);
            }
        }
    }
}
