package com.e7yoo.e7.webview;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.e7yoo.e7.E7App;


public class ReWebViewClient extends WebViewClient {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        //编写 javaScript方法  
        /*String javascript =  "javascript:function hideOther() {" +
                "document.getElementsByTagName('body')[0].innerHTML;" +  
                *//*"document.getElementsByTagName('section')[0].style.display='none';" +
                "document.getElementsByTagName('section')[0].remove();" +*//*
                *//*"document.getElementsByTagName('interest-news')[0].style.display='none';" + *//*
                *//*"document.getElementsByClassName('gg-item news-gg-img3')[0].style.display='none';" +
                "document.getElementsByClassName('gg-item news-gg-img3')[0].remove();" +  
                "document.getElementsByClassName('gg-item news-gg-img1')[0].style.display='none';" +
                "document.getElementsByClassName('gg-item news-gg-img1')[0].remove();" +  
                "document.getElementsByClassName('gg-item news-gg-img2')[0].style.display='none';" +
                "document.getElementsByClassName('gg-item news-gg-img2')[0].remove();" +
                "document.getElementsByClassName('interest-news')[0].style.display='none';" +
                "document.getElementsByClassName('interest-news')[0].remove();" +
                "document.getElementsByClassName('bdgg-wrap')[0].style.display='none';" +
                "document.getElementsByClassName('bdgg-wrap')[0].remove();" +
                "document.getElementsByClassName('toapp-btn')[0].style.display='none';" +
                "document.getElementsByClassName('toapp-btn')[0].remove();"+ *//*
        		jsStrs("getElementsByClassName('gg-item news-gg-img3')")*//* +
        		jsStr("getElementById('BAIDU_DUP_fp_wrapper')") +   
                jsStr("getElementById('new_check')") +   
        		jsStr("getElementById('new_check').getElementById('J_interest_news')") +   
        		jsStrs("getElementById('new_check').getElementById('J_hot_news').getElementsByClassName('comment-appbtn-wrap')[0].getElementsByClassName('toapp-btn')") + 
        		jsStrs("getElementById('new_check').getElementById('J_hot_news').getElementById('J_hn_list').getElementsByClassName('bdgg-wrap')") +  
        		jsStrs("getElementById('new_check').getElementById('J_hot_news').getElementById('J_hn_list').getElementsByClassName('gg-item news-gg-img1')") +  
                jsStrs("getElementById('new_check').getElementById('J_hot_news').getElementById('J_hn_list').getElementsByClassName('gg-item news-gg-img2')") +  
                jsStrs("getElementById('new_check').getElementById('J_hot_news').getElementById('J_hn_list').getElementsByClassName('gg-item news-gg-img3')") +  
        		jsStrs("getElementById('new_check').getElementById('J_hot_news').getElementById('J_loading').getElementsByClassName('J-toapp toapp toapp-bottom')");
                *//*
                *//* +
                "var divs = document.getElementsByTagName('div');" +  
                "var lastDiv = divs[divs.length-1];" +  
                "lastDiv.remove();" +  
                "document.getElementsByClassName('showme')[0].remove();" +  
                "document.getElementsByClassName('nei-t3')[1].remove();}"*//*;
        //创建方法  
        view.loadUrl(javascript);  */

        // 加载方法
        // view.loadUrl("javascript:hideOther();");
        view.loadUrl(ADFilterUtil.getClearAdDivJs(E7App.mApp));
    }

    /*private String jsStr(String jsClassName) {
    	return "var div = document."+jsClassName+";div.style.display='none';div.remove();";
                
    }*/
    private String jsStrs(String jsClassNames) {
    	return "var divs = document."+jsClassNames+";for(var i=0; i<divs.length; i++){divs[i].style.display='none';divs[i].remove();}";
                
    }
    
    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
    	//  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
        view.loadUrl(url);
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @SuppressLint("DefaultLocale")
	@Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (view != null && view.getContext() != null && !url.toLowerCase().contains("")) {
            return new WebResourceResponse(null,null,null);
        }
        if (view != null && view.getContext() != null && ADFilterUtil.hasAd(view.getContext(), url.toLowerCase())) {
            return new WebResourceResponse(null,null,null);
        }
        if (view != null && view.getContext() != null && ADFilterUtil.isAd(view.getContext(), url.toLowerCase())) {
            return new WebResourceResponse(null,null,null);
        }
        if (url != null && !(url.contains("e7yoo") || url.contains("xmb") || url.contains("xiaomengban")) && url.endsWith(".apk")) {
        	return new WebResourceResponse(null,null,null);
        }
        return super.shouldInterceptRequest(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        try {
            String url = request.getUrl().getHost().toLowerCase() +  request.getUrl().getPath().toLowerCase();
            if (url != null && view != null && view.getContext() != null && ADFilterUtil.hasAd(view.getContext(), url)) {
                return new WebResourceResponse(null,null,null);
            }
            if (view != null && view.getContext() != null && ADFilterUtil.isAd(view.getContext(), url)) {
                return new WebResourceResponse(null,null,null);
            }
            if (url != null && !(url.contains("e7yoo") || url.contains("xmb") || url.contains("xiaomengban")) && url.endsWith(".apk")) {
                return new WebResourceResponse(null,null,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.shouldInterceptRequest(view, request);
    }
}
