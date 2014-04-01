package com.infthink.itmc;

import com.infthink.itmc.util.Html5PlayUrlRetriever;
import com.infthink.itmc.util.Html5PlayUrlRetriever.PlayUrlListener;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MediaPlayerActivity extends CoreActivity {
    private String mPlayUrl;
    private String mNextUrl;
    private VideoView mVideoView;
    private WebView mWebView;
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
        FrameLayout contentView = new FrameLayout(this);
        mVideoView = new VideoView(this);
        contentView.addView(mVideoView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
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

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }
    }

}
