package com.infthink.itmc;

import java.util.Calendar;

import com.infthink.itmc.data.LocalPlayHistoryInfoManager;
import com.infthink.itmc.data.NetcastManager.CastStatusUpdateListener;
import com.infthink.itmc.type.LocalPlayHistory;
import com.infthink.itmc.util.Html5PlayUrlRetriever;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.util.Html5PlayUrlRetriever.PlayUrlListener;
import com.infthink.itmc.widget.CastMediaController;
import com.infthink.itmc.widget.CastMediaController.OnCastButtonClickListener;
import com.infthink.netcast.sdk.MediaStatus;
import com.infthink.netcast.sdk.RampConstants;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MediaPlayerActivity extends CoreActivity implements
        PlayUrlListener, CastStatusUpdateListener {
    private String mPlayUrl;
    private String mNextUrl;
    private VideoView mVideoView;
    private WebView mWebView;
    private Html5PlayUrlRetriever mRetriever;
    private TextView mTextView;
    private int mSource;
    private int mCi;
    private int mMediaCount;
    private String mMediaTitle;
    private int mCastPosition = 0;
    private CastMediaController mCastMediaController;
    private int mCastSeekPosition = -1;
    private boolean mIsPlayToCast;
    private String mMediaId;
    private String mPageUrl;
    private long mPlayCurrentTime;
    
    private void recordMedia() {
        long playTime;
        if (mIsPlayToCast) {
            playTime = mPlayCurrentTime;
        } else {
            playTime = mVideoView.getCurrentPosition();
        }
        LocalPlayHistoryInfoManager.getInstance(this).saveHistory(this, mMediaId, mCi, String.valueOf(playTime), String.valueOf(Calendar.getInstance().getTimeInMillis()), mSource, mMediaTitle, mPlayUrl, mPageUrl, "none");
    }

    @Override
    protected void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        mMediaId = String.valueOf(intent.getIntExtra("media_id", -1));
        mPlayUrl = intent.getStringExtra("path");
        mMediaTitle = intent.getStringExtra("meidaTitle");
        mMediaCount = intent.getIntExtra("available_episode_count", -1);
        mCi = intent.getIntExtra("current_episode", -1);
        mSource = intent.getIntExtra("source", -1);
        mPageUrl = intent.getStringExtra("pageUrl");
        if (mMediaId.equals("-1")) {
            mMediaId = mMediaTitle;
        }
        long seekTo = 0;
        LocalPlayHistory history = LocalPlayHistoryInfoManager.getInstance(this).getHistoryById(mMediaId);
        if (history != null && history.mediaCi == mCi) {
            seekTo = Integer.valueOf(history.playSeconds);
        }
        
        final FrameLayout contentView = new FrameLayout(this);
        mVideoView = new VideoView(this);
        mTextView = new TextView(this);
        mTextView.setText("正在加载 " + mMediaTitle + " , 请稍后...");
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setBackgroundColor(Color.TRANSPARENT);
        contentView.addView(mVideoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        contentView.addView(mTextView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        contentView.setBackgroundColor(Color.BLACK);
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
            mCastMediaController = new CastMediaController(
                    this);
            mCastMediaController.setFileName(mMediaTitle);
            if (ITApp.getNetcastManager().isConnectedDevice()) {
                playToCast(mPlayUrl, mMediaTitle, seekTo);
            }
            mVideoView.setVideoPath(mPlayUrl);
            updateCastBtnState();
            mVideoView.setMediaController(mCastMediaController);
            mCastMediaController
                    .setCastButtonClickListener(new OnCastButtonClickListener() {
                        @Override
                        public void onClick() {
                            showCastList();
                        }
                    });
            mVideoView.requestFocus();

            final long position = seekTo;
            mVideoView
                    .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            // optional need Vitamio 4.0
                            mediaPlayer.setPlaybackSpeed(1.0f);
                            mTextView.setVisibility(View.GONE);
                            if (mIsPlayToCast) {
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        mVideoView.pause();
                                    }
                                }, 1000);
                                
                            } else {
                                mVideoView.seekTo(position);
                            }
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

//        Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadNextUrl();
//            }
//
//        }, 20000);
    }
    
    protected void updateCastBtnState() {
        mCastMediaController.updateCastBtnState(isSessionEstablished());
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        recordMedia();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            if (mIsPlayToCast) {
                ITApp.getNetcastManager().setVolumeDown();
                return true;
            }
        case KeyEvent.KEYCODE_VOLUME_UP:
            if (mIsPlayToCast) {
                ITApp.getNetcastManager().setVolumeUp();
                return true;
            }
        default:
            break;
        }
        return super.dispatchKeyEvent(event);
    }

    // TODO: 需要抽象到MediaUrlForPlayerUtil类中
    private void loadNextUrl() {
        getPlayerUrl(34,
                "http://m.funshion.com/subject?mediaid=112875&number=20140213&malliance=1660");
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

    private void playToCast(String url, String title, long millisecond) {
        mIsPlayToCast = true;
        mCastMediaController.setPlayMode(mIsPlayToCast);
        ITApp.getNetcastManager().setCastStatusUpdateListener(this);
        ITApp.getNetcastManager().playVideo(url,
                title);
        android.util.Log.d("XXXXXXXXXX", "url = " + url);
        if (millisecond > 1000) {
            mCastSeekPosition = (int) (millisecond / 1000);
        }
        mVideoView.pause();
    }
    
    private void play() {
        mIsPlayToCast = false;
        mCastMediaController.setPlayMode(mIsPlayToCast);
        ITApp.getNetcastManager().setCastStatusUpdateListener(null);
        long time = mCastPosition * 1000;
        if (time > 0 && Math.abs(time - mVideoView.getCurrentPosition()) > 1000) {
            mVideoView.seekTo(time);
        }
        mVideoView.start();
    }

    @Override
    public void onSessionStarted() {
        super.onSessionStarted();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                playToCast(mPlayUrl, mMediaTitle, mVideoView.getCurrentPosition());
            }
        }, 1000);
    }

    @Override
    public void onSessionFailed() {
        super.onSessionFailed();
    }

    @Override
    public void onSessionEnded() {
        super.onSessionEnded();
        play();
    }

    @Override
    public void updateStatus(MediaStatus status) {
        if (status.getState() == RampConstants.PLAYER_STATUS_PLAYING) {
            mCastMediaController.setCastIsPlaying(true);
            mCastMediaController.setCastDuration(status.getDuration() * 1000);
            mPlayCurrentTime = status.getCurrentTime() * 1000;
            mCastMediaController.setCastCurrentPosition(mPlayCurrentTime);
        } else {
            mCastMediaController.setCastIsPlaying(false);
        }
        switch (status.getState()) {
        case RampConstants.PLAYER_STATUS_PLAYING:
            if (mCastSeekPosition > 0) {
                ITApp.getNetcastManager().seekTo(mCastSeekPosition);
                mCastSeekPosition = -1;
            }
            mCastPosition = status.getCurrentTime();
            break;
        case RampConstants.PLAYER_STATUS_IDLE:
            break;
        case RampConstants.PLAYER_STATUS_PAUSE:
            break;
        case RampConstants.PLAYER_STATUS_STOP:
            break;
        case RampConstants.PLAYER_STATUS_PREPAREING:
        case RampConstants.PLAYER_STATUS_BUFFERING:
           break;
        default:
            break;
        }
    }
}
