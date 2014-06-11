package com.infthink.itmc.v2.util;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class Html5PlayUrlRetriever {
    private static final String TAG = "Html5PlayUrlRetriever";

    private static final String JS_GET_URL = "javascript:(function() {if(window.Methods == undefined || window.Methods.isVideoReady == undefined || window.Methods.isVideoReady()){\treturn;}var pageUrl = window.location.href;var videoTags = document.getElementsByTagName('video');if(videoTags == null || videoTags == undefined || videoTags.length == 0){return;}window.Methods.onVideoReady();var url = videoTags[0].src;var sources = videoTags[0].getElementsByTagName('source');    if(sources != null && sources != undefined){        for(var i = 0; i < sources.length; i++){            var source = sources[i].src;            if(source != null || source != undefined || source.length > 0){                url = source;\t\t\t\t break;            }        }    }if(url != null && url != undefined && url.length > 0){window.Methods.getUrl(pageUrl, url);}videoTags[0].addEventListener('loadstart',function() {var videoTags = document.getElementsByTagName('video');if(videoTags == null || videoTags == undefined || videoTags.length == 0){    return;}var url = videoTags[0].src;var sources = videoTags[0].getElementsByTagName('source');    if(sources != null && sources != undefined){        for(var i = 0; i < sources.length; i++){            var source = sources[i].src;            if(source != null || source != undefined || source.length > 0){                url = source;\t\t\t\t break;            }        }    }if(url == null || url == undefined || url.length == 0){    return; }window.Methods.getUrl(pageUrl, url);}, false); })()";
    private static final String JS_YOUKU_PLAY = "javascript:(function() {if(ykPlayerH5 == undefined || ykPlayerH5._h5player == undefined || \tykPlayerH5._h5player.realStartPlay == undefined){\treturn;}try{ykPlayerH5._h5player.realStartPlay(true);if(window.Methods == undefined || window.Methods.onYoukuPlayed == undefined){\treturn;}window.Methods.onYoukuPlayed();}catch(e){}})()";

    private static final int URL_LOOP_INTERVAL = 200;
    private static final int URL_LOOP_INTERVAL_QIYI = 3000;
    private boolean mAutoPlay = false;

    private Handler mHandler;
    private LogCatMonitor mLogCat = null;
    private String mPlayUrl = null;
    private PlayUrlListener mPlayUrlListener = null;
    private boolean mReleased = false;
    private int mSource = 0;
    private boolean mVideoReady = false;
    private WebView mWebView = null;

    private void dumpLog(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }

    private Runnable mGetVideoUrlQiyiRunnale = new Runnable() {
        public void run() {
            dumpLog("Html5PlayUrlRetriever", "get video url qiyi run...");
            if ((!mReleased) && (Util.isEmpty(mPlayUrl))) {
                exeJs(JS_GET_URL);
                getVideoUrlQiyiLoop(URL_LOOP_INTERVAL_QIYI);
            }
        }
    };
    private Runnable mGetVideoUrlRunnale = new Runnable() {
        public void run() {
            dumpLog("Html5PlayUrlRetriever", "get video url run...: mReleased = " + mReleased
                    + "; mVideoReady = " + mVideoReady);
            if ((!mReleased) && (!mVideoReady)) {
                exeJs(JS_GET_URL);
                getVideoUrlLoop(URL_LOOP_INTERVAL);
            }
        }
    };

    private Runnable mYoukuAutoPlayRunnale = new Runnable() {
        public void run() {
            dumpLog("Html5PlayUrlRetriever", "set youku auto play.");
            if ((!mReleased) && (!mAutoPlay) && (Util.isEmpty(mPlayUrl))) {
                exeJs(JS_YOUKU_PLAY);
                setYoukuPlayLoop(500);
            }
        }
    };

    public Html5PlayUrlRetriever(WebView webView, int source) {
        init(webView, source);
    }

    private void exeJs(String js) {
        try {
            mWebView.loadUrl(js);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void getVideoUrlLoop(int delay) {
        if (!mReleased) {
            dumpLog("Html5PlayUrlRetriever", "get video url");
            mHandler.removeCallbacks(mGetVideoUrlRunnale);
            mHandler.postDelayed(mGetVideoUrlRunnale, delay);
        }
    }

    private void getVideoUrlQiyiLoop(int delay) {
        if (!this.mReleased) {
            dumpLog("Html5PlayUrlRetriever", "get video url qiyi");
            mHandler.removeCallbacks(mGetVideoUrlQiyiRunnale);
            mHandler.postDelayed(mGetVideoUrlQiyiRunnale, delay);
        }
    }

    private void init(WebView webView, int source) {
        mSource = source;
        mWebView = webView;
        mHandler = new Handler(webView.getContext().getMainLooper());
        mWebView.addJavascriptInterface(new H5Object(), "Methods");
    }

    private synchronized void notifyUrlReady(String pageUrl, String playUrl) {
        if (mPlayUrlListener != null)
            mPlayUrlListener.onUrlUpdate(pageUrl, playUrl);
    }

    private synchronized void setYoukuPlayLoop(int delay) {
        if (!mReleased) {
            mHandler.removeCallbacks(mYoukuAutoPlayRunnale);
            mHandler.postDelayed(mYoukuAutoPlayRunnale, delay);
        }
    }

    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    public synchronized void release() {
        mReleased = true;
        mVideoReady = false;
        mPlayUrl = null;
        setPlayUrlListener(null);
        mHandler.removeCallbacks(mGetVideoUrlQiyiRunnale);
        mHandler.removeCallbacks(mGetVideoUrlRunnale);
        mHandler.removeCallbacks(mYoukuAutoPlayRunnale);
//        if (mSource == 8)
//            mLogCat.interrupt();

    }

    public synchronized void setPlayUrlListener(PlayUrlListener playUrlListener) {
        mPlayUrlListener = playUrlListener;
    }

    public synchronized void start() {
        if (mSource != 8) {
            getVideoUrlLoop(500);
        } else {
            getVideoUrlQiyiLoop(URL_LOOP_INTERVAL_QIYI);
        }
        if (mSource != 20)
            mAutoPlay = true;
    }
    
    public synchronized void startQiyiLoop() {
        getVideoUrlQiyiLoop(URL_LOOP_INTERVAL_QIYI);
    }

    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    public class H5Object {
        public H5Object() {
        }

        @JavascriptInterface
        public void getUrl(String pageUrl, String playUrl) {
            dumpLog("Html5PlayUrlRetriever", "getUrl pageUrl = " + pageUrl
                    + ", playUrl = " + playUrl);
            if (playUrl.contains(".html"))
                return;
            mPlayUrl = playUrl;
            if (mSource == 20)
                mHandler.removeCallbacks(mYoukuAutoPlayRunnale);
            notifyUrlReady(pageUrl, playUrl);
        }

        @JavascriptInterface
        public boolean isVideoReady() {
            dumpLog("Html5PlayUrlRetriever", "isVideoReady " + mVideoReady);
            return mVideoReady;
        }

        @JavascriptInterface
        public void onVideoReady() {
            dumpLog("Html5PlayUrlRetriever", "onVideoReady.");
            mVideoReady = true;
            mHandler.post(new Runnable() {
                public void run() {
                    if (mSource == 20)
                        setYoukuPlayLoop(500);
                }
            });
        }

        @JavascriptInterface
        public void onYoukuPlayed() {
            dumpLog("Html5PlayUrlRetriever", "onYoukuPlayed.");
            setAutoPlay(true);
        }
    }

    private class LogCatMonitor extends Thread {
        private LogCatMonitor() {
        }

        public void run() {
            try {
                Pattern localPattern = Pattern
                        .compile("MediaPlayer.*start().*Uri is http");
                Runtime.getRuntime().exec("logcat -c").waitFor();
                BufferedReader localBufferedReader = new BufferedReader(
                        new InputStreamReader(Runtime.getRuntime()
                                .exec("logcat").getInputStream()));
                boolean bool;
                do {
                    if (localPattern.matcher(localBufferedReader.readLine())
                            .find())
                        synchronized (Html5PlayUrlRetriever.this) {
                            getVideoUrlLoop(0);
                            return;
                        }
                    bool = isInterrupted();
                } while (!bool);
                return;
            } catch (Exception localException) {
            }
        }
    }

    public static abstract interface PlayUrlListener {
        public abstract void onUrlUpdate(String pageUrl, String playUrl);
    }
}