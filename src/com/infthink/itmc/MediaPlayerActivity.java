package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.Calendar;

import com.fireflycast.cast.CastMediaControlIntent;
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
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController.OnHiddenListener;
import io.vov.vitamio.widget.MediaController.OnShownListener;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

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
    private ActionBar mActionBar;
    
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

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mMediaRouter = MediaRouter.getInstance(this);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
        .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
        .build();
        mMediaRouterCallback = ITApp.getInstance().getCastManager().getMediaRouterCallback();

        mActionBar = getActionBar();

        Intent intent = getIntent();

        mMediaId = String.valueOf(intent.getIntExtra("media_id", -1));
        mPlayUrl = intent.getStringExtra("path");
        mMediaTitle = intent.getStringExtra("meidaTitle");
        mMediaCount = intent.getIntExtra("available_episode_count", -1);
        mCi = intent.getIntExtra("current_episode", -1);
        mSource = intent.getIntExtra("source", -1);
        mPageUrl = intent.getStringExtra("pageUrl");
        if (mMediaId.equals("-1")) {
            mMediaId = mPlayUrl;
        }
        
        final FrameLayout contentView = new FrameLayout(this);
        mVideoView = new VideoView(this);
        mCastMediaController = new CastMediaController(
                this);
        mCastMediaController.setFileName(mMediaTitle);
        mCastMediaController.setOnHiddenListener(new OnHiddenListener() {
            @Override
            public void onHidden() {
                mActionBar.hide();
            }
        });
        mCastMediaController.setOnShownListener(new OnShownListener() {
            @Override
            public void onShown() {
                mActionBar.show(); 
            }
        });
        mTextView = new TextView(this);
        mTextView.setText("正在加载 " + mMediaTitle + " , 请稍后...");
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setBackgroundColor(Color.TRANSPARENT);
        contentView.addView(mVideoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        contentView.addView(mTextView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        contentView.setBackgroundColor(Color.BLACK);
        setContentView(contentView);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        android.util.Log.d("XXXXXXXXXXX", "mediaRouteMenuItem = " + mediaRouteMenuItem);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        android.util.Log.d("XXXXXXXXXXX", "mediaRouteActionProvider = " + mediaRouteActionProvider);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        return true;
    }
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    
    private void initVideo() {
        if (mPlayUrl == "") return;
        android.util.Log.d("XXXXXXXXX", "mPlayUrl = " + mPlayUrl);
        long seekTo = 0;
        LocalPlayHistory history = LocalPlayHistoryInfoManager.getInstance(this).getHistoryById(mMediaId);
        if (history != null && history.mediaCi == mCi && history.mediaSource == mSource) {
            seekTo = Integer.valueOf(history.playSeconds);
        }

        /*
         * Alternatively,for streaming media you can use
         * mVideoView.setVideoURI(Uri.parse(URLstring));
         */

        mVideoView.setVideoPath(mPlayUrl);
        updateCastBtnState();
        mVideoView.setMediaController(mCastMediaController);
        
        mIsPlayToCast = ITApp.getNetcastManager().isDevicePlaying();
        if (mIsPlayToCast) {
            ITApp.getNetcastManager().setCastStatusUpdateListener(this);
            mCastMediaController.setPlayMode(mIsPlayToCast);
            mCastMediaController.show(0);
            mVideoView.setBackgroundResource(R.drawable.casting);
            mTextView.setVisibility(View.GONE);
        } else if (ITApp.getNetcastManager().isConnectedDevice()) {
            playToCast(mPlayUrl, mMediaTitle, seekTo);
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }
        
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
                        android.util.Log.d("QQQQQQQQQQ", "mIsPlayToCast = " + mIsPlayToCast);
                        if (!mIsPlayToCast) {
                            if (mVideoView.getDuration() > (position + 1000))
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
        mVideoView.setOnInfoListener(new OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    if (!mIsPlayToCast) {
                        mVideoView.start();
                    }
                }
                return false;
            }
        });
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        
        initVideo();
    }

    protected void updateCastBtnState() {
        mCastMediaController.updateCastBtnState(isSessionEstablished());
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        recordMedia();
        
        if (mIsPlayToCast) {
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            String appName = this.getResources().getString(R.string.app_name);
            Notification notification = new Notification(
                    R.drawable.ic_launcher, appName, System.currentTimeMillis());
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            CharSequence contentTitle = appName;
            CharSequence contentText = "正在播放: " + mMediaTitle;

            Intent notificationIntent = new Intent(MediaPlayerActivity.this,
                    MediaPlayerActivity.class);
            notificationIntent.putExtras(getIntent());
            PendingIntent contentItent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(this, contentTitle, contentText,
                    contentItent);
            notificationManager.notify(0, notification);
        }
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
        mCastMediaController.show(0);
        mVideoView.setBackgroundResource(R.drawable.casting);
        mVideoView.pause();
    }
    
    private void play() {
        mVideoView.setBackgroundResource(R.drawable.transparent);
        mIsPlayToCast = false;
        mCastMediaController.setPlayMode(mIsPlayToCast);
        mCastMediaController.show();
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
