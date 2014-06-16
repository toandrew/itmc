package com.infthink.itmc.v2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.firefly.sample.castcompanionlibrary.cast.VideoCastManager;
import com.firefly.sample.castcompanionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.firefly.sample.castcompanionlibrary.cast.exceptions.CastException;
import com.firefly.sample.castcompanionlibrary.cast.exceptions.NoConnectionException;
import com.firefly.sample.castcompanionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.fireflycast.cast.ApplicationMetadata;
import com.fireflycast.cast.MediaInfo;
import com.fireflycast.cast.MediaMetadata;
import com.fireflycast.cast.MediaStatus;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.infthink.itmc.v2.data.DataManager;
import com.infthink.itmc.v2.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.v2.data.LocalPlayHistoryInfoManager;
import com.infthink.itmc.v2.data.DataManager.IOnloadListener;
import com.infthink.itmc.v2.type.LocalPlayHistory;
import com.infthink.itmc.v2.type.MediaDetailInfo2;
import com.infthink.itmc.v2.util.Html5PlayUrlRetriever;
import com.infthink.itmc.v2.util.Util;
import com.infthink.itmc.v2.util.Html5PlayUrlRetriever.PlayUrlListener;
import com.infthink.itmc.v2.widget.CastMediaController;
import com.infthink.itmc.v2.widget.CastMediaController.OnChangeMediaStateListener;
import com.infthink.libs.cache.simple.ImageLoader;
import com.nanohttpd.webserver.src.main.java.fi.iki.elonen.SimpleWebServer;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController.OnHiddenListener;
import io.vov.vitamio.widget.MediaController.OnShownListener;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Display;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MediaPlayerActivity extends CoreActivity implements
        PlayUrlListener, OnChangeMediaStateListener {
    private static final String TAG = MediaPlayerActivity.class.getSimpleName();
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
//    private boolean mIsPlayToCast;
    private String mMediaId;
    private String mPageUrl;
    private long mPlayCurrentTime;
    private ActionBar mActionBar;
    private VideoCastManager mCastManager;
    private VideoCastConsumerImpl mCastConsumer;
    
    private String mIpAddress;

    private SimpleWebServer mNanoHTTPD;
    private int port = 8080;
    private String mRootDir = "/";
    
    private LinearLayout mLinearLayout;
    private ImageView mImageView;
    private TextView mCastNameView;
    
    private DataManager mDataManager;
    private boolean mFromSend;
    
    private Handler mHandler;
    
    /*
     * indicates whether we are doing a local or a remote playback
     */
    public static enum PlaybackLocation {
        LOCAL,
        REMOTE;
    }

    /*
     * List of various states that we can be in
     */
//    public static enum PlaybackState {
//        PLAYING, PAUSED, BUFFERING, IDLE;
//    }
    private FrameLayout mFrameLayout;
    private PlaybackLocation mLocation;
//    private PlaybackState mPlaybackState;
    
    private void recordMedia() {
        if (mFromSend) return;
        long playTime;
        if (mCastManager.isConnected()) {
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

        mActionBar = getSupportActionBar();
        mLocation = PlaybackLocation.LOCAL;
        mCastManager = ITApp.getCastManager(this);
        setupActionBar();
        setupCastListener();

        Intent intent = getIntent();
        mHandler = new Handler();
        if (intent.getAction() == "android.intent.action.SEND") {
            mFromSend = true;
            mPlayUrl = intent.getStringExtra("path");
            if (mPlayUrl == null || mPlayUrl.length() <= 0) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                android.util.Log.d("XXXXXXXX", "text = " + text);
                String head = "http://youtu.be/";
                int last = text.lastIndexOf(head);
                String id = "";
                if (last < 0) {
                    String[] strs = text.split("&");
                    head = "https://www.youtube.com/watch?v=";
                    last = text.lastIndexOf(head);
                    id = strs[0].substring(last + head.length(), strs[0].length());
                } else {
                    id = text.substring(last + head.length(), text.length());
                }
                
                android.util.Log.d("XXXXXXXXX", "url = " + id);
                final String info_url = "http://www.youtube.com/get_video_info?video_id=" + id;
                android.util.Log.d("XXXXXXXXX", "info_url = " + info_url);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        calculateYouTubeUrl(info_url);
                    }
                });
                t.start();
            }
        } else {
            mMediaId = String.valueOf(intent.getIntExtra("media_id", -1));
            mPlayUrl = intent.getStringExtra("path");
            if (mMediaId.equals("-1")) {
                mMediaId = mPlayUrl;
            }
            mMediaTitle = intent.getStringExtra("meidaTitle");
            mMediaCount = intent.getIntExtra("available_episode_count", -1);
            mCi = intent.getIntExtra("current_episode", -1);
            mSource = intent.getIntExtra("source", -1);
            mPageUrl = intent.getStringExtra("pageUrl");
        }

        getSupportActionBar().setTitle(mMediaTitle);

        final FrameLayout contentView = new FrameLayout(this);
        mVideoView = new VideoView(this);
        mCastMediaController = new CastMediaController(
                this);
        mCastMediaController.setOnChangeMediaStateListener(this);
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
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int size;
        if (width > height) {
            size = height / 3 * 2;
        } else {
            size = width / 3 * 2;
        }
        
        mFrameLayout = new FrameLayout(this);
        mFrameLayout.setBackgroundColor(Color.BLACK);
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mImageView = new ImageView(this);
        mImageView.setScaleType(ScaleType.FIT_XY);
        mImageView.setImageResource(R.drawable.default_videothumb);
        mCastNameView = new TextView(this);
        mCastNameView.setSingleLine();
        mCastNameView.setBackgroundResource(R.drawable.cast_name_bg);
        mCastNameView.setEllipsize(TruncateAt.MARQUEE);
        mCastNameView.setMarqueeRepeatLimit(3);
        mCastNameView.setGravity(Gravity.CENTER);
//        mCastNameView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.cast_icon), null, null, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.weight = 1;

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLinearLayout.addView(mImageView, lp1);
        mLinearLayout.addView(mCastNameView, lp2);
        
        
        mFrameLayout.addView(mLinearLayout, new FrameLayout.LayoutParams(
                size, size, Gravity.CENTER));
        mFrameLayout.setVisibility(View.GONE);
        
        contentView.addView(mVideoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        contentView.addView(mTextView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        
        contentView.addView(mFrameLayout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        contentView.setBackgroundColor(Color.BLACK);
        setContentView(contentView);
    }
    
    @Override
    protected void onInitialized() {
        android.util.Log.d(TAG, "onInitialized");
        mDataManager = getService().getDataManager();
        download();
    }
    
    private void download() {
        if (Util.isEmpty(mMediaId)) return;
        mDataManager.loadDetail(mMediaId, new IOnloadListener<MediaDetailInfo2>() {
            @Override
            public void onLoad(MediaDetailInfo2 entity) {
                if (entity != null && entity.mediaDetailInfo != null) {
                    String url = Util.replaceString(entity.mediaDetailInfo.posterurl, "\\", "").trim();
                    ImageLoader.loadImage(getService().getBitmapCache(),
                            mImageView, url);
//                    mImageView.setImageURI(Uri.parse(entity.mediaSetInfoList.));
                }
            }
        });
    }
    
    private void setupCastListener() {
        mCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata,
                    String sessionId, boolean wasLaunched) {
                updatePlaybackLocation(PlaybackLocation.REMOTE);
            }

            @Override
            public void onApplicationDisconnected(int errorCode) {
//                Log.d(TAG, "onApplicationDisconnected() is reached with errorCode: " + errorCode);
                updatePlaybackLocation(PlaybackLocation.LOCAL);
            }

            @Override
            public void onDisconnected() {
//                Log.d(TAG, "onDisconnected() is reached");
//                mPlaybackState = PlaybackState.PAUSED;
                updatePlaybackLocation(PlaybackLocation.LOCAL);
            }

            @Override
            public void onRemoteMediaPlayerMetadataUpdated() {
//                try {
//                    mRemoteMediaInformation = mCastManager.getRemoteMediaInformation();
//                } catch (Exception e) {
//                    // silent
//                }
            }

            @Override
            public void onFailed(int resourceId, int statusCode) {
                updatePlaybackLocation(PlaybackLocation.LOCAL);
            }

            @Override
            public void onConnectionSuspended(int cause) {
//                Utils.showToast(LocalPlayerActivity.this,
//                        R.string.connection_temp_lost);
            }

            @Override
            public void onConnectivityRecovered() {
//                Utils.showToast(LocalPlayerActivity.this,
//                        R.string.connection_recovered);
            }

            @Override
            public void onRemoteMediaPlayerStatusUpdated() {
                try {
                    mCastMediaController.setCastIsPlaying(mCastManager.getPlaybackStatus() == MediaStatus.PLAYER_STATE_PLAYING);
                    mCastMediaController.setCastCurrentPosition(mCastManager.getCurrentMediaPosition());
                    mCastMediaController.setCastDuration(mCastManager.getMediaDuration());
                } catch (TransientNetworkDisconnectionException e) {
                    e.printStackTrace();
                } catch (NoConnectionException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setBackgroundDrawable(
//                getResources().getDrawable(R.drawable.ab_transparent_democastoverlay));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop:
                mCastManager.disconnect();
                break;
        }
        return true;
    }
    private ProgressDialog mDialog;
    private ProgressDialog mDialog1;
    private void initVideo() {
        if (mPlayUrl == null || mPlayUrl.length() <= 0) {
            if (mFromSend) {
                if (mDialog == null) {
                    mDialog = ProgressDialog.show(this, null, "正在获取播放地址...", true, false);
                }
                mDialog.show();
            }
            return;
        }
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
        mVideoView.setMediaController(mCastMediaController);
        
        try {
            if ((mCastManager.isRemoteMoviePlaying() || mCastManager.isRemoteMoviePaused()) && processLocalVideoUrl(mPlayUrl).equals(mCastManager.getRemoteMovieUrl())) {
                mCastMediaController.show(0);
                showCastingView();
                mLocation = PlaybackLocation.REMOTE;
                mCastMediaController.setPlayMode(true);
                mCastMediaController.setCastIsPlaying(mCastManager.getPlaybackStatus() == MediaStatus.PLAYER_STATE_PLAYING);
                mCastMediaController.setCastCurrentPosition(mCastManager.getCurrentMediaPosition());
                mCastMediaController.setCastDuration(mCastManager.getMediaDuration());
            } else if (mCastManager.isConnected()) {
                playToCast(mPlayUrl, mMediaTitle, seekTo);
                mTextView.setVisibility(View.GONE);
            } else {
                mTextView.setVisibility(View.VISIBLE);
            }
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }

        mVideoView.requestFocus();
        
        final long position = seekTo;
        mVideoView
                .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // optional need Vitamio 4.0
                        mediaPlayer.setPlaybackSpeed(1.0f);
                        mTextView.setVisibility(View.GONE);
                        if (!mCastManager.isConnected()) {
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
                    if (!mCastManager.isConnected()) {
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
    
    private void updatePlaybackLocation(PlaybackLocation location) {
        this.mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            stopServer();
            play();
        } else {
            playToCast(mPlayUrl, mMediaTitle, mVideoView.getCurrentPosition());
            
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mLocation == PlaybackLocation.LOCAL) {
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            onVolumeChange(0.1);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            onVolumeChange(-0.1);
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    private void onVolumeChange(double volumeIncrement) {
        if (mCastManager == null) {
            return;
        }
        try {
            mCastManager.incrementVolume(volumeIncrement);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        
        mCastManager = ITApp.getCastManager(this);
        mCastManager.addVideoCastConsumer(mCastConsumer);
        mCastManager.incrementUiCounter();
        initVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordMedia();
        
        try {
            if (mCastManager.isRemoteMediaLoaded()) {
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
                notificationIntent.putExtra("path", mPlayUrl);
                PendingIntent contentItent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setLatestEventInfo(this, contentTitle, contentText,
                        contentItent);
                notificationManager.notify(0, notification);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
        mCastManager.decrementUiCounter();
        mCastManager.removeVideoCastConsumer(mCastConsumer);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_UP:
        default:
            break;
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
        if (null != mCastManager) {
            mCastManager.clearContext(this);
        }
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
        MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        metadata.putString(MediaMetadata.KEY_TITLE, (title == null) ? "" : title);
        
        MediaInfo mediaInfo = new MediaInfo.Builder(processLocalVideoUrl(url))
        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
        .setContentType("video/mp4").setMetadata(metadata).build();
        
        try {
            mCastManager.loadMedia(mediaInfo, true, (int) millisecond);
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
        mCastMediaController.setPlayMode(true);
        mCastMediaController.show(0);
        showCastingView();
        mVideoView.pause();
    }

    private void showCastingView() {
        mCastNameView.setText("Casting to " + mCastManager.getDeviceName());
        mFrameLayout.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);
    }

    private void hideCastingView() {
        mFrameLayout.setVisibility(View.GONE);
    }

    private void play() {
        hideCastingView();
//        mIsPlayToCast = false;
        mCastMediaController.setCastIsPlaying(false);
        mCastMediaController.setPlayMode(false);
        mCastMediaController.show();
//        ITApp.getNetcastManager().setCastStatusUpdateListener(null);
        long time = 0;
        try {
            time = mCastManager.getCurrentMediaPosition();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
        if (time > 0 && Math.abs(time - mVideoView.getCurrentPosition()) > 1000) {
            mVideoView.seekTo(time);
        }
        mVideoView.start();
    }

    @Override
    public void seekEnd(long position) {
        try {
            if (mCastManager != null && mCastManager.isRemoteMediaLoaded()) {
                mCastMediaController.stopTrickplayTimer();
                mCastManager.seek((int) position);
            }
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startMedia() {
        try {
            if (mCastManager != null && mCastManager.isRemoteMediaLoaded())
                mCastManager.play();
        } catch (CastException e) {
            e.printStackTrace();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseMedia() {
        try {
            if (mCastManager != null && mCastManager.isRemoteMediaLoaded())
                mCastManager.pause();
        } catch (CastException e) {
            e.printStackTrace();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
    }
    
    private String processLocalVideoUrl(String url) {
        String real_url = url;
        if (url != null && url.startsWith("file://")) {

            initWebserver();
            // remove "file://"
            real_url = url.replaceAll("file://", "");
            return "http://" + mIpAddress + ":8080" + real_url;

        } else if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            initWebserver();
            return "http://" + mIpAddress + ":8080" + real_url;
        }
        return url;
    }
    
    private void initWebserver() {
        stopServer();
        startServer(8080);
    }
    
    private void stopServer() {
        if (mNanoHTTPD != null) {
            mNanoHTTPD.stop();
        } else {
            Log.e(TAG, "Cannot kill server!? Please restart your phone.");
        }
    }
    
    private void startServer(int port) {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            mIpAddress = intToIp(wifiInfo.getIpAddress());

            if (wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
                throw new Exception("Please connect to a WIFI-network.");
            }

            Log.e(TAG, "Starting server " + mIpAddress + ":" + port + ".");

            List<File> rootDirs = new ArrayList<File>();
            boolean quiet = false;
            Map<String, String> options = new HashMap<String, String>();
            rootDirs.add(new File(mRootDir).getAbsoluteFile());

            // mNanoHTTPD
            try {
                mNanoHTTPD = new SimpleWebServer(mIpAddress, port, rootDirs,
                        quiet);
                mNanoHTTPD.start();
            } catch (IOException ioe) {
                Log.e(TAG, "Couldn't start server:\n" + ioe);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    
    private String intToIp(int i) {
        return ((i) & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    @Override
    public long getCurrentPosition() {
        try {
            if (mCastManager != null && mCastManager.isRemoteMediaLoaded())
                return mCastManager.getCurrentMediaPosition();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        
        try {
            if (mCastManager != null && mCastManager.isRemoteMediaLoaded())
                return mCastManager.getMediaDuration();
        } catch (TransientNetworkDisconnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void calculateYouTubeUrl(String infoUrl) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(infoUrl);
            HttpResponse response = httpClient.execute(httpGet);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            response.getEntity().writeTo(baos);
            String info = new String(baos.toString("UTF-8"));
            android.util.Log.d("XXXXXXX", info);
            String[] subs = info.split("&");
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < subs.length; i++) {
                String[] sub = subs[i].split("=");
                if (sub != null) {
                    if (sub.length >= 2) {
                        map.put(sub[0], URLDecoder.decode(sub[1], "utf-8"));
                    }
                }
            }
            String fmt = map.get("fmt_list");
            if (fmt == null || fmt.length() <= 0) {
                mPlayUrl = "";
                if (mDialog != null && mDialog.isShowing()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            if (mDialog1 == null) {
                                mDialog1 = ProgressDialog.show(getContext(), null, "播放地址获取失败！", true, false);
                                mDialog1.setCancelable(true);
                            }
                            mDialog1.show();
                        }
                    });
                }
                return;
            }
            String fmtListStr = URLDecoder.decode(fmt, "utf-8");
            ArrayList<Format> formatList = new ArrayList<Format>();
            if (null != fmtListStr) {
                String formates[] = fmtListStr.split(",");
                for (String formateStr : formates) {
                    Format formate = new Format(formateStr);
                    formatList.add(formate);
                }
            }

            String urlMap = map.get("url_encoded_fmt_stream_map");
            if (urlMap != null) {
                String videoUrls[] = urlMap.split(",");
                ArrayList<VideoStream> videoList = new ArrayList<VideoStream>();
                for (String videoUrl : videoUrls) {
                    VideoStream video = new VideoStream(videoUrl);
                    videoList.add(video);
                }

                int formatId = Integer.parseInt("18");
                Format format = new Format(formatId);
                while (!formatList.contains(format)) {
                    int oldId = format.getId();
                    int newId = getSupportedFallbackId(oldId);
                    if (oldId == newId) {
                        break;
                    }
                    format = new Format(newId);
                }
                int index = formatList.indexOf(format);
                if (index >= 0) {
                    VideoStream video = videoList.get(index);
                    mPlayUrl = URLDecoder.decode(video.getUrl(), "utf-8");
                    if (mDialog != null && mDialog.isShowing()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                                initVideo();
                            }
                        });
                    }
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayUrl = "";
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDialog.dismiss();
                if (mDialog1 == null) {
                    mDialog1 = ProgressDialog.show(getContext(), null, "播放地址获取失败！", true, false);
                    mDialog1.setCancelable(true);
                }
                mDialog1.show();
            }
        });
    }

    public static int getSupportedFallbackId(int pOldId) {
        final int lSupportedFormatIds[] = { 13, // 3GPP (MPEG-4 encoded) Low
                                                // quality
                17, // 3GPP (MPEG-4 encoded) Medium quality
                18, // MP4 (H.264 encoded) Normal quality
                22, // MP4 (H.264 encoded) High quality
                37 // MP4 (H.264 encoded) High quality
        };
        int lFallbackId = pOldId;
        for (int i = lSupportedFormatIds.length - 1; i >= 0; i--) {
            if (pOldId == lSupportedFormatIds[i] && i > 0) {
                lFallbackId = lSupportedFormatIds[i - 1];
            }
        }
        return lFallbackId;
    }

    class Format {
        protected int mId;

        /**
         * * Construct this object from one of the strings in the "fmt_list"
         * parameter * @param pFormatString one of the comma separated strings
         * in the "fmt_list" parameter
         */
        public Format(String pFormatString) {
            String lFormatVars[] = pFormatString.split("/");
            mId = Integer.parseInt(lFormatVars[0]);
        }

        /**
         * * Construct this object using a format id * @param pId id of this
         * format
         */
        public Format(int pId) {
            this.mId = pId;
        }

        /** * Retrieve the id of this format * @return the id */
        public int getId() {
            return mId;
        } /* (non-Javadoc) * @see java.lang.Object#equals(java.lang.Object) */

        @Override
        public boolean equals(Object pObject) {
            if (!(pObject instanceof Format)) {
                return false;
            }
            return ((Format) pObject).mId == mId;
        }
    }

    class VideoStream {
        protected String mUrl;

        /**
         * * Construct a video stream from one of the strings obtained * from
         * the "url_encoded_fmt_stream_map" parameter if the video_info * @param
         * pStreamStr - one of the strings from "url_encoded_fmt_stream_map"
         */
        public VideoStream(String pStreamStr) {
            String[] lArgs = pStreamStr.split("&");
            Map<String, String> lArgMap = new HashMap<String, String>();
            for (int i = 0; i < lArgs.length; i++) {
                String[] lArgValStrArr = lArgs[i].split("=");
                if (lArgValStrArr != null) {
                    if (lArgValStrArr.length >= 2) {
                        lArgMap.put(lArgValStrArr[0], lArgValStrArr[1]);
                    }
                }
            }
            mUrl = lArgMap.get("url");
        }

        public String getUrl() {
            return mUrl;
        }
    }

}
