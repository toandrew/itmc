package com.infthink.itmc;

import com.infthink.itmc.util.Util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BaseWebViewActivity extends CoreActivity implements
        DownloadListener {

    public static final String TAG = BaseWebViewActivity.class.getName();
    protected MyWebViewClient mClient = new MyWebViewClient();
    // protected PageProgressView pageLoadingProgressView;
    protected WebView mWebView;

    @SuppressLint({ "SetJavaScriptEnabled" })
    protected void initWebView() {
//        mWebView = ((WebView) findViewById(2131165432));
        mWebView = new WebView(this);
        mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        
        // UC浏览器的 User－Agent
//        String ua = "Mozilla/5.0 (Linux; U; Android 4.1.1; zh-CN; MI 2S Build/JRO03L) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/9.5.2.394 U3/0.8.0 Mobile Safari/533.1";
//        mWebView.getSettings().setUserAgentString(ua);
        mWebView.clearCache(false);
        // DeviceInfo localDeviceInfo = DKApp.deviceInfo();
        // if (localDeviceInfo.isWapApnUsed()) {
        // this.webView.setHttpAuthUsernamePassword(localDeviceInfo.getProxyHost(),
        // localDeviceInfo.getProxyPort() + "", "", "");
        // } else {
        // this.webView.setHttpAuthUsernamePassword("", "", "", "");
        // }
        
        mWebView.setHttpAuthUsernamePassword("", "", "", "");
        mWebView.setWebViewClient(mClient);
        mWebView.setDownloadListener(this);
        // this.pageLoadingProgressView =
        // ((PageProgressView)findViewById(2131165433));

        // }
    }

    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);
    }

    @Override
    public void onDownloadStart(String url, String userAgent,
            String contentDisposition, String mimetype, long contentLength) {
        Intent localIntent = null;
        if (!Util.isEmpty(url))
            localIntent = new Intent("android.intent.action.VIEW",
                    Uri.parse(url));
        try {
            startActivity(localIntent);
        } catch (Exception localException) {
            // DKLog.e(TAG, localException.getLocalizedMessage());
        }
    }

    protected void onPageFinish(WebView webView, String url) {
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onUpdateUrlLoading(WebView webView, String url) {
    }

    protected boolean onUrlLoading(WebView webView, String url) {
        return false;
    }

    public class MyWebViewClient extends WebViewClient {
        public MyWebViewClient() {
        }

        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            onPageFinish(webView, url);
        }

        public void onUpdateLoadingUrl(WebView webView, String url) {
            onUpdateUrlLoading(webView, url);
            // super.onUpdateLoadingUrl(webView, url);
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            // DKLog.i(BaseWebViewActivity.TAG,
            // " shouldOverrideUrlLoading url : " + paramString);
            if (!onUrlLoading(webView, url))
                webView.loadUrl(url);
            return true;
        }
    }
}
