package com.e7yoo.e7.webview;

import android.content.Context;
import android.content.res.Resources;

import com.e7yoo.e7.R;

public class ADFilterUtil {
    /*public static final String[] adUrls = {
			"ubmcmm.baidustatic.com", 
			"cpro2.baidustatic.com", 
			"cpro.baidustatic.com",
			"s.lianmeng.360.cn", 
			"nsclick.baidu.com", 
			"pos.baidu.com", 
			"cbjs.baidu.com", 
			"cpro.baidu.com",
			"images.sohu.com/cs/jsfile/js/c.js", 
			"union.sogou.com/", 
			"sogou.com/", 
			"a.baidu.com", 
			"c.baidu.com", 
			};*/

    public static boolean hasAd(Context context, String url) {
        Resources res = context.getResources();
        String[] adUrls = res.getStringArray(R.array.adBlockUrl);
        for (String adUrl : adUrls) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAd(Context context, String url) {
        Resources res = context.getResources();
        String[] adUrls = res.getStringArray(R.array.adUrl);
        for (String adUrl : adUrls) {
            if (url.equals(adUrl)) {
                return true;
            }
        }
        return false;
    }

    public static String getClearAdDivJs(Context context) {
        String js = "javascript:";
        Resources res = context.getResources();
        String[] adDivs = res.getStringArray(R.array.adBlockDiv);
        for (int i = 0; i < adDivs.length; i++) {

            js += "var adDiv" + i + "= document.getElementById('news_check').getElementById('" + adDivs[i] + "');" +
                    "if(adDiv" + i + " != null)" +
                    "adDiv" + i + ".parentNode.removeChild(adDiv" + i + ");";
        }
        String[] adDivsC = res.getStringArray(R.array.adBlockDivClass);
        for (int i = 0; i < adDivsC.length; i++) {

            js += "var adDivsC" + i + "= document.getElementsByClassName('" + adDivsC[i] + "');" +
                    "if(adDivsC" + i + " != null)" +
                    "adDivsC" + i + ".parentNode.removeChild(adDivsC" + i + ");";
        }
        String[] adSections = res.getStringArray(R.array.adBlockSectionClass);
        for (int i = 0; i < adSections.length; i++) {

            js += "var adSection" + i + "= document.getElementById('news_check').getElementById('J_hot_news').getElementsByClassName('" + adSections[i] + "');" +
                    "if(adSection" + i + " != null)" +
                    "adSection" + i + ".parentNode.removeChild(adSection" + i + ");";
        }
        return js;
    }

}
