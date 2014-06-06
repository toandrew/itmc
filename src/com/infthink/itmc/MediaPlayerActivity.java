package com.infthink.itmc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firefly.sample.castcompanionlibrary.cast.VideoCastManager;
import com.firefly.sample.castcompanionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.firefly.sample.castcompanionlibrary.cast.exceptions.CastException;
import com.firefly.sample.castcompanionlibrary.cast.exceptions.NoConnectionException;
import com.firefly.sample.castcompanionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.fireflycast.cast.ApplicationMetadata;
import com.fireflycast.cast.MediaInfo;
import com.fireflycast.cast.MediaMetadata;
import com.fireflycast.cast.MediaStatus;
import com.infthink.itmc.data.LocalPlayHistoryInfoManager;
import com.infthink.itmc.type.LocalPlayHistory;
import com.infthink.itmc.util.Html5PlayUrlRetriever;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.util.Html5PlayUrlRetriever.PlayUrlListener;
import com.infthink.itmc.widget.CastMediaController;
import com.infthink.itmc.widget.CastMediaController.OnChangeMediaStateListener;
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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
    
    private PlaybackLocation mLocation;
//    private PlaybackState mPlaybackState;
    
    private void recordMedia() {
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
        mCastMediaController.setOnChangeMediaStateListener(this);
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setBackgroundDrawable(
//                getResources().getDrawable(R.drawable.ab_transparent_democastoverlay));
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
        mVideoView.setMediaController(mCastMediaController);
        
        try {
            if ((mCastManager.isRemoteMoviePlaying() || mCastManager.isRemoteMoviePaused()) && processLocalVideoUrl(mPlayUrl).equals(mCastManager.getRemoteMovieUrl())) {
                mCastMediaController.show(0);
                mVideoView.setBackgroundResource(R.drawable.casting);
                mTextView.setVisibility(View.GONE);
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
        metadata.putString(MediaMetadata.KEY_TITLE, title);
        
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
        mVideoView.setBackgroundResource(R.drawable.casting);
        mVideoView.pause();
    }
    
    private void play() {
        mVideoView.setBackgroundResource(R.drawable.transparent);
//        mIsPlayToCast = false;
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
}
