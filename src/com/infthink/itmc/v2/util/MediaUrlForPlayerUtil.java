package com.infthink.itmc.v2.util;

import java.util.Timer;
import java.util.TimerTask;

import com.infthink.itmc.v2.util.Html5PlayUrlRetriever.PlayUrlListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MediaUrlForPlayerUtil implements PlayUrlListener {
    private static final String TAG = MediaUrlForPlayerUtil.class.getName();
    private static MediaUrlForPlayerUtil mMediaUrlForPalForPlayerUtil;
    private TimerTask mCancelGetUrlForPlayerTask;
    // private MediaUrlInfo mActMediaUrlInfo;
    private Context mContext;
    Handler mHandler = null;
    // private MediaUrlInfoList mMediaUrlInfoList;
    // private MediaUrlInfoListUtil mMediaUrlInfoListUtil;
    private PlayUrlObserver mObserver;
    private Html5PlayUrlRetriever mUrlRetriever = null;
    private WebView mWebView;
    private int mMediaCi;
    private int mMediaId;
    private int mSource = -1;
    private String statisticInfo;
    private Timer mTimer = new Timer();

    private MediaUrlForPlayerUtil(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(mContext.getMainLooper());
        // this.mMediaUrlInfoListUtil = MediaUrlInfoListUtil.getInstance();
    }

    private void cancelGetUrlForPlayer() {
        // DKLog.d(TAG, "cancel get url for player");
        tearDown();
    }

    public synchronized static MediaUrlForPlayerUtil getInstance(Context context) {
        if (mMediaUrlForPalForPlayerUtil == null)
            mMediaUrlForPalForPlayerUtil = new MediaUrlForPlayerUtil(
                    context.getApplicationContext());
        return mMediaUrlForPalForPlayerUtil;
    }

    private synchronized void getPlayerUrl(int source, String url,
            String paramString2) {
        // DKLog.d(TAG, "get player url ");
        initWebView();
        mWebView.loadUrl(url);
        startUrlRetriever();
    }

    @SuppressLint({ "SetJavaScriptEnabled" })
    private synchronized void initWebView() {
        if (mWebView == null) {
            mWebView = new WebView(mContext);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new MyWebViewClient());
            mWebView.setHttpAuthUsernamePassword("", "", "", "");
        }
    }

    private synchronized void onGetPlayerUrlError() {
        if (mObserver != null)
            mObserver.onError();
        tearDown();
    }

    private synchronized void onGetPlayerUrlFinish(String pageUrl,
            String playUrl) {
        if (mObserver != null)
            mObserver.onUrlUpdate(mMediaId, mMediaCi, pageUrl, playUrl);
        tearDown();

    }

    private synchronized void startUrlRetriever() {
        if (mUrlRetriever != null)
            mUrlRetriever.release();
        mUrlRetriever = new Html5PlayUrlRetriever(mWebView, mSource);
        mUrlRetriever.setPlayUrlListener(this);
        mUrlRetriever.start();
    }

    public synchronized void getMediaUrlForPlayer(int mediaId, int ci,
            int source, String info) {
        // DKLog.d(TAG, "public get media url for player");
        statisticInfo = info;
        this.mMediaId = mediaId;
        mMediaCi = ci;
        if (source != -1)
            this.mSource = source;
        // this.mMediaUrlInfoListUtil.setMediaUrlInfoListCompleteObserver(this);
        // this.mMediaUrlInfoListUtil.getMediaUrlInfoList(paramInt1, paramInt2,
        // paramInt3);
        mCancelGetUrlForPlayerTask = new CancelGetUrlForPlayerTask();
        mTimer.schedule(mCancelGetUrlForPlayerTask, 20000L);

    }

    //
    // public void onGetMediaUrlInfoListComplete(DKRequest paramDKRequest,
    // DKResponse paramDKResponse)
    // {
    // if (paramDKResponse.isSuccessful())
    // {
    // this.mMediaUrlInfoList =
    // ((MediaUrlInfoListResponse)paramDKResponse).urlList;
    // DKLog.d(TAG, "get url list done");
    // this.mActMediaUrlInfo =
    // MediaUrlInfoListUtil.getInstance().filterMediaUrlInfoList(this.mMediaUrlInfoList,
    // this.source);
    // if (this.mActMediaUrlInfo != null)
    // {
    // DKLog.d(TAG, "filter url list act url: " +
    // this.mActMediaUrlInfo.mediaUrl);
    // this.source = this.mActMediaUrlInfo.mediaSource;
    // DKLog.d(TAG, "source is: " + this.source);
    // if (this.mActMediaUrlInfo.isHtml == 1)
    // {
    // getPlayerUrl(this.mActMediaUrlInfo.mediaSource,
    // this.mActMediaUrlInfo.mediaUrl, this.statisticInfo);
    // return;
    // }
    // onGetPlayerUrlFinish(this.mActMediaUrlInfo.mediaUrl, "");
    // return;
    // }
    // }
    // onGetPlayerUrlError();
    // }

    public void onUrlUpdate(String pageUrl, String playUrl) {
        // DKLog.d(TAG, "url for player getting res: " + paramString2);
        if (!Util.isEmpty(playUrl)) {
            // DKLog.d(TAG, "url for player done: " + paramString2);
            onGetPlayerUrlFinish(pageUrl, playUrl);
        }
    }

    public void setObserver(PlayUrlObserver paramPlayUrlObserver) {
        mObserver = paramPlayUrlObserver;
    }

    public void setPrefrenceSource(int source) {
        this.mSource = source;
    }

    public synchronized void tearDown() {
        // DKLog.d(TAG, "tear down");
        if (mCancelGetUrlForPlayerTask != null)
            mCancelGetUrlForPlayerTask.cancel();
        // if (this.mMediaUrlInfoListUtil != null)
        // this.mMediaUrlInfoListUtil.setMediaUrlInfoListCompleteObserver(null);
        if (mUrlRetriever != null)
            mUrlRetriever.release();
        if (mObserver != null)
            mObserver.onReleaseLock();
        if (mWebView != null) {
            final WebView webView = mWebView;
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    webView.destroy();
                }
            }, 500L);
            mWebView = null;
        }

    }

    private class CancelGetUrlForPlayerTask extends TimerTask {
        public void run() {
            cancelGetUrlForPlayer();
        }
    }

    public class MyWebViewClient extends WebViewClient {

        public void onPageFinished(WebView webview, String url) {
            super.onPageFinished(webview, url);
            // DKLog.d(MediaUrlForPlayerUtil.TAG, "on page finish");
            if (mSource == 8)
                mUrlRetriever.startQiyiLoop();
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            startUrlRetriever();
            return super.shouldOverrideUrlLoading(webView, url);
        }
    }

    public static abstract interface PlayUrlObserver {
        public abstract void onError();

        public abstract void onReleaseLock();

        public abstract void onUrlUpdate(int id, int ci, String pageUrl,
                String playUrl);
    }
}
