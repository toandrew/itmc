package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.List;

import com.infthink.itmc.MediaDetailActivity.MediaPlayInfo;
import com.infthink.itmc.adapter.PlayHistoryAdapter;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.data.LocalPlayHistoryInfoManager;
import com.infthink.itmc.type.BaseLocalPlayHistory;
import com.infthink.itmc.type.LocalPlayHistory;
import com.infthink.itmc.util.Html5PlayUrlRetriever;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.util.Html5PlayUrlRetriever.PlayUrlListener;
import com.infthink.itmc.widget.LoadingListView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class RecentPlayHistoryActivity extends CoreActivity implements PlayUrlListener {
    private Button mClearBtn;
    private LoadingListView mLoadingListView;
    private ListView mPlayHistoryListView;
    private PlayHistoryAdapter mPlayHistoryAdapter;
    private List<LocalPlayHistory> mCurLocalPlayHistoryList;
    
    private DataManager mDataManager;
    private MediaPlayInfo mPlayInfo;
    private ProgressDialog mDialog;
    private String mPlayUrl;
    private ProgressDialog mDialog1;
    private WebView mWebView;
    private Html5PlayUrlRetriever mUrlRetriever;
    
    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);
        setContentView(R.layout.playhistory_activity);
        onActivate();
        loadPlayHistory();
    }

    private void layoutActionBar(ActionBar bar) {
        try {
            Class<?> actionBarImpl = Class
                    .forName("com.android.internal.app.ActionBarImpl");
            Class<?> actionBarView = Class
                    .forName("com.android.internal.widget.ActionBarView");

            Field actionView = actionBarImpl.getDeclaredField("mActionView");
            actionView.setAccessible(true);
            Object objActionView = actionView.get(bar);

            Field fHomeLayout = actionBarView.getDeclaredField("mHomeLayout");
            fHomeLayout.setAccessible(true);
            FrameLayout objHomeLayout = (FrameLayout) fHomeLayout
                    .get(objActionView);
            View v = objHomeLayout.findViewById(android.R.id.home);
            FrameLayout.LayoutParams fl = (LayoutParams) v.getLayoutParams();
            fl.width = 0;
            v.setLayoutParams(fl);
            v.setVisibility(View.GONE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void initActionBar() {
        FrameLayout frameLayout = new FrameLayout(this);
        TextView textView = new TextView(this);
        textView.setText("最近播放");
        FrameLayout.LayoutParams textLayout = new FrameLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT
                        | Gravity.CENTER);
        frameLayout.addView(textView, textLayout);
        mClearBtn = new Button(this);
        mClearBtn.setBackgroundResource(R.drawable.btn_clearhistory);
        mClearBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mCurLocalPlayHistoryList != null) {
                    LocalPlayHistoryInfoManager.getInstance(getContext()).clearHistory(getContext());
                    mCurLocalPlayHistoryList.clear();
                    mPlayHistoryAdapter.setGroup(mCurLocalPlayHistoryList);
                    showNoPlayHistoryTip(true);
                }
            }
            
        });
        FrameLayout.LayoutParams imageLayout = new FrameLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                        | Gravity.CENTER);
        frameLayout.addView(mClearBtn, imageLayout);
        ActionBar actionBar = getActionBar();
        layoutActionBar(actionBar);
        actionBar.setBackgroundDrawable(null);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, 21);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(frameLayout, lp);
    }

    private void onActivate() {
        initActionBar();

        mLoadingListView = ((LoadingListView) findViewById(R.id.lv_playhistory));
        mPlayHistoryListView = mLoadingListView.getListView();
        mPlayHistoryListView.setSelector(R.drawable.clickable_item_bg_part);
        // mPlayHistoryListView.setOnScrollListener(this);
        mPlayHistoryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                    long id) {
                LocalPlayHistory history = mCurLocalPlayHistoryList.get(position);
                if (Util.isEmpty(history.html5Page)) {
                    Intent intent = new Intent(RecentPlayHistoryActivity.this, MediaPlayerActivity.class);
                    intent.putExtra("media_id", -1);
                    intent.putExtra("pageUrl", history.html5Page);
                    intent.putExtra("source", history.mediaSource);
                    intent.putExtra("meidaTitle", history.videoName);
                    intent.putExtra("available_episode_count", history.mediaCi);
                    intent.putExtra("current_episode", history.mediaCi);
                    intent.putExtra("path", history.mediaId);
                    startActivity(intent);
                } else {
                    mPlayInfo = new MediaPlayInfo(Integer.valueOf(history.mediaId), Util.replaceString(history.html5Page, "\\",
                            "").trim(), history.mediaSource, history.videoName, history.mediaCi, history.mediaCi);
                    getUrlAndPlay();
                }
            }
         });

        // VideoThumbCacheManager.getInstance().clearPendingVideoThumbTask(false);
        // this.mTvInfoSparseArray = new SparseArray();
        // this.mTvPlayManager = new
        // TelevisionPlayManager(getApplicationContext());
        // this.mTvUpdateManager = TelevisionUpdateManager.getInstance();
        // this.mTvUpdateManager.registerListeners(this);
        mPlayHistoryAdapter = new PlayHistoryAdapter(this);
        mPlayHistoryListView.setAdapter(mPlayHistoryAdapter);
    }

    private void loadPlayHistory() {
        LocalPlayHistoryInfoManager localHistory = LocalPlayHistoryInfoManager
                .getInstance(this);
        mCurLocalPlayHistoryList = localHistory.getHistoryVideos(this);
        if (mCurLocalPlayHistoryList != null && mCurLocalPlayHistoryList.size() > 0) {
            mPlayHistoryAdapter.setGroup(mCurLocalPlayHistoryList);
            showNoPlayHistoryTip(false);
        } else {
            showNoPlayHistoryTip(true);
        }
    }

    private void showNoPlayHistoryTip(boolean show) {
        View view = findViewById(R.id.tv_noplayhistorytip);
        if (show) {
            mClearBtn.setVisibility(4);
            view.setVisibility(0);
            return;
        }
        mClearBtn.setVisibility(0);
        view.setVisibility(4);
    }
    
    @Override
    protected void onInitialized() {
        mDataManager = getService().getDataManager();
    }

    private void getUrlAndPlay() {
        if (mDialog == null) {
            mDialog = ProgressDialog.show(this, null, "正在过滤广告，获取视频地址，请稍等", true, false);
        }
        mDialog.show();
        
        mDataManager.loadMediaPlayUrl(mPlayInfo.mediaId, mPlayInfo.ci, mPlayInfo.source, new DataManager.IOnloadListener<String>() {
            @Override
            public void onLoad(String entity) {
                mDialog.dismiss();
                if (entity != null && entity.length() > 0) {
                    playVideo(entity);
                } else {
                    startRetriever();
                }
            }
        });
    }
    
    private void playVideo(String url) {
        mPlayInfo.setPlayUrl(url);
        startPlay();
    }
    
    private void startRetriever() {
        if (mDialog1 == null) {
            mDialog1 = ProgressDialog.show(this, null, "获取高清地址失败，尝试获取低清地址", true, false);
        }
        mDialog1.show();
        getPlayerUrl();
    }
    
    @SuppressLint({ "SetJavaScriptEnabled" })
    private void initWebView() {
        if (mWebView == null) {
            mWebView = new WebView(this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView webview, String url) {
                    super.onPageFinished(webview, url);
                    // DKLog.d(MediaUrlForPlayerUtil.TAG, "on page finish");
                    if (mPlayInfo.source == 8)
                        mUrlRetriever.startQiyiLoop();
                }
            });
            mWebView.setHttpAuthUsernamePassword("", "", "", "");
        }
    }

    private void getPlayerUrl() {
        initWebView();
        mWebView.loadUrl(mPlayInfo.pageUrl);
        startUrlRetriever();
    }
    
    private void startUrlRetriever() {
        if (mUrlRetriever != null)
            mUrlRetriever.release();
        mUrlRetriever = new Html5PlayUrlRetriever(mWebView, mPlayInfo.source);
        mUrlRetriever.setPlayUrlListener(this);
        mUrlRetriever.start();
    }

    private void startPlay() {
        Intent intent = new Intent(RecentPlayHistoryActivity.this, MediaPlayerActivity.class);
        intent.putExtra("media_id", mPlayInfo.mediaId);
        intent.putExtra("pageUrl", mPlayInfo.pageUrl);
        intent.putExtra("source", mPlayInfo.source);
        intent.putExtra("meidaTitle", mPlayInfo.title);
        intent.putExtra("available_episode_count", mPlayInfo.episodeCount);
        intent.putExtra("current_episode", mPlayInfo.ci);
        intent.putExtra("ci", mPlayInfo.ci);
        intent.putExtra("path", mPlayInfo.playUrl);

        startActivity(intent);
    }
    
    private void showFailDialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("该视频地址为空，请播放其他视频");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.create().show();
    }
    
    @Override
    public void onUrlUpdate(String pageUrl, String playUrl) {
        mDialog1.dismiss();
        if (playUrl != null && playUrl.length() > 0) {
            playVideo(playUrl);
        } else {
            showFailDialog();
        }
        if (mUrlRetriever != null)
            mUrlRetriever.release();
    }
    
    class MediaPlayInfo {
        int mediaId;
        String pageUrl;
        int source;
        String title;
        int episodeCount;
        int ci;
        String playUrl;
        
        public MediaPlayInfo(int mediaId, String pageUrl, int source, String title, int count, int ci) {
            this.mediaId = mediaId;
            this.pageUrl = pageUrl;
            this.source = source;
            this.title = title;
            this.episodeCount = count;
            this.ci = ci;
        }
        
        public void setPlayUrl(String url) {
            playUrl = url;
        }
    }
}
