package com.infthink.itmc;

import com.infthink.itmc.util.Html5PlayUrlRetriever;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.util.Html5PlayUrlRetriever.PlayUrlListener;
import com.infthink.itmc.util.MediaUrlForPlayerUtil.MyWebViewClient;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MediaPlayerActivity extends CoreActivity implements PlayUrlListener {
    private String mPlayUrl;
    private String mNextUrl;
    private VideoView mVideoView;
    private WebView mWebView;
    Html5PlayUrlRetriever mRetriever;

    @Override
    protected void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        mPlayUrl = intent.getStringExtra("path");
        final FrameLayout contentView = new FrameLayout(this);
        mVideoView = new VideoView(this);
        contentView.addView(mVideoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextUrl();
            }
            
        }, 20000);
        setContentView(contentView);
        android.util.Log.d("XXXXXXXXX", "mPlayUrl = " + mPlayUrl);
        if (mPlayUrl == "") {
            // Tell the user to provide a media file URL/path.
            android.util.Log.d("XXXXXXXXX", "");
            return;
        } else {
            /*
             * Alternatively,for streaming media you can use
             * mVideoView.setVideoURI(Uri.parse(URLstring));
             */
            mVideoView.setVideoPath(mPlayUrl);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView
                    .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            // optional need Vitamio 4.0
                            mediaPlayer.setPlaybackSpeed(1.0f);
                        }
                    });
            mVideoView.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mNextUrl != null && mNextUrl.length() > 0) {
                        mVideoView.setVideoPath(mNextUrl);
                        mNextUrl = null;
                    }
                }
            });
        }
    }

    //TODO: 需要抽象到MediaUrlForPlayerUtil类中
    private void loadNextUrl() {
        getPlayerUrl(34, "http://m.funshion.com/subject?mediaid=112875&number=20140213&malliance=1660");
    }

    @SuppressLint({ "SetJavaScriptEnabled" })
    private synchronized void initWebView() {
        if (mWebView == null) {
            mWebView = new WebView(this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new MyWebViewClient());
            mWebView.setHttpAuthUsernamePassword("", "", "", "");
        }
    }
    Html5PlayUrlRetriever mUrlRetriever;
    int mSource;
    private synchronized void getPlayerUrl(int source, String url) {
        // DKLog.d(TAG, "get player url ");
        mSource = source;
        initWebView();
        mWebView.loadUrl(url);
        startUrlRetriever();
    }

    private synchronized void startUrlRetriever() {
        if (mUrlRetriever != null)
            mUrlRetriever.release();
        mUrlRetriever = new Html5PlayUrlRetriever(mWebView, mSource);
        mUrlRetriever.setPlayUrlListener(this);
        mUrlRetriever.start();
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

    @Override
    public void onUrlUpdate(String pageUrl, String playUrl) {
        if (!Util.isEmpty(playUrl)) {
            mNextUrl = playUrl;
            Toast.makeText(getContext(), "next ok", Toast.LENGTH_LONG).show();
        }
    }
}
