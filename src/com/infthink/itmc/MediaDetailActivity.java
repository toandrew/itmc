package com.infthink.itmc;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.infthink.itmc.adapter.SeriesAdapter;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.data.DataManager.IOnloadListener;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.MediaDetailInfo;
import com.infthink.itmc.type.MediaDetailInfo2;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.MediaSetInfo;
import com.infthink.itmc.type.MediaSetInfoList;
import com.infthink.itmc.util.UIUtil;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.widget.ActorsView;
import com.infthink.itmc.widget.MediaImageView;
import com.infthink.itmc.widget.MediaView;
import com.infthink.itmc.widget.PagerView;
import com.infthink.itmc.widget.RatingView;
import com.infthink.libs.cache.simple.BitmapCachePool;
import com.infthink.libs.cache.simple.ImageLoader;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.FrameLayout.LayoutParams;

public class MediaDetailActivity extends CoreActivity
        implements
            OnClickListener,
            ViewSwitcher.ViewFactory,
            MediaImageView.OnMediaImageReadyCallback {

    private DataManager mDataManager;

    private Button btnMyFavorite;
    private Button btnPlay;
    private Button btnSelectSource;
    private Button btnViewReviews;
    private int ci = 1;
    private TextView descView;
    private ImageView ivPlay;
    private View mediaSummary;

    private boolean isBanner = false;
    private MediaInfo mediaInfo;

    private ActorsView vActorsView;
    private ImageSwitcher vBigPoster;
    private View vBigPosterMask;
    private View vBottomBar;
    private View vDetailContainer;
    private ViewGroup vDetailContentWrap;
    private View vDetailFooter;
    private View vDetailHeader;
    // private ListViewEx vDetalListView;
    // private LoadingListView vLoadingDetailListView;
    // private LoadingListView vLoadingSeriesListView;
    private View vSeriesShadowLeft;
    private View vSeriesShadowMiddle;
    private View vSeriesShadowRight;
    private ViewGroup vShadowContainer;
    private boolean vStart = false;
    private View vZBottomItem;
    // private VarietyListAdapter varietyAdapter;
    // private ListViewEx varietyListView = null;
    private int varietyPageNo = 0;
    private int varietyPageSize = 15;;
    private MediaView mediaView;
    private ViewGroup viewGroup;

    private PagerView mPagerView;
    private SeriesAdapter seriesAdapter = new SeriesAdapter(this);
    private int[] mPageNo = new int[2];
    private GridView mSeriesGridView;
    
    private int ci_count;
    private MediaDetailInfo mMediaDetailInfo;
    private MediaSetInfoList mMediaSetInfoList;
    private MediaDetailInfo2 mMediaDetailInfo2;
    TextView descEdit;
    
    public static final int MSG_UPDATE_DETAIL_INFO = 1;
    
    private void cust(ActionBar bar) {
        try {
            Class<?> actionBarImpl = Class.forName("com.android.internal.app.ActionBarImpl");
            Class<?> actionBarView = Class.forName("com.android.internal.widget.ActionBarView");

            Field actionView = actionBarImpl.getDeclaredField("mActionView");
            actionView.setAccessible(true);
            Object objActionView = actionView.get(bar);

            Field fHomeLayout = actionBarView.getDeclaredField("mHomeLayout");
            fHomeLayout.setAccessible(true);
            FrameLayout objHomeLayout = (FrameLayout) fHomeLayout.get(objActionView);
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
        textView.setText(this.mediaInfo.mediaName.trim());
        // UIUtil.getMediaStatus(this, this.mediaInfo);
        FrameLayout.LayoutParams textLayout =
                new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP);
        frameLayout.addView(textView, textLayout);

        ActionBar actionBar = getActionBar();
        cust(actionBar);
        actionBar.setBackgroundDrawable(null);
        actionBar.setDisplayShowHomeEnabled(true);
        // actionBar.setSubtitle("WW");
        actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBar.LayoutParams lp =
                new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, 21);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(frameLayout, lp);
    }

    private void onActivate() {
        initActionBar();
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // ActionBar localActionBar = getActionBar();
        // localActionBar.setTitle(this.mediaInfo.mediaName.trim());
        // localActionBar.setSubtitle(UIUtil.getMediaStatus(this, this.mediaInfo));
        // localActionBar.setDisplayShowCustomEnabled(true);

        // ActionBarMovableLayout localActionBarMovableLayout =
        // (ActionBarMovableLayout)findViewById(101384347);
        // localActionBarMovableLayout.setCallback(this);
        // localActionBarMovableLayout.setOnScrollListener(this);

        // Resources localResources = getResources();
        // int i =
        // localResources.getDimensionPixelSize(com.infthink.itmc.R.dimen.multi_detail_title_overscroll_distance);
        // int j =
        // localResources.getDimensionPixelSize(com.infthink.itmc.R.dimen.multi_detail_title_scroll_range);
        // if (!this.mediaInfo.isMultiSetType())
        // {
        // i = localResources.getDimensionPixelSize(2131296452);
        // j = localResources.getDimensionPixelSize(2131296451);
        // }
        // // localActionBarMovableLayout.setScrollRange(j);
        // // localActionBarMovableLayout.setOverScrollDistance(i);
        // // this.mActionBarMovableLayout = localActionBarMovableLayout;

        // this.vZBottomItem = View.inflate(this,
        // com.infthink.itmc.R.layout.media_detail_bottom_item, null);
        // this.viewGroup.addView(this.vZBottomItem);
        // // this.mActionBarMovableLayout.addView(this.vZBottomItem, 0);
        // this.vBottomBar = View.inflate(this, com.infthink.itmc.R.layout.media_detail_bottom_bar,
        // null);
        // // this.mActionBarMovableLayout.addView(this.vBottomBar);
        // this.viewGroup.addView(this.vBottomBar);
        this.vBigPoster = (ImageSwitcher) this.findViewById(R.id.media_big_poster);
        this.vBigPoster.setFactory(this);
//          this.vBigPoster.setInAnimation(AnimationUtils.loadAnimation(this, 17432576));
//          this.vBigPoster.setOutAnimation(AnimationUtils.loadAnimation(this, 17432577));
        this.vBigPosterMask = this.findViewById(R.id.media_big_poster_mask);
        this.vBigPosterMask.setVisibility(4);
        // ActorsView.resetActorViewWidth();
        // this.vActorsView = ((ActorsView)findViewById(R.id.actors));
        // this.vActorsView.setOnActorViewClickListener(this);
        // this.btnPlay = ((Button)findViewById(R.id.btn_play));
        // this.btnPlay.setOnClickListener(this);
        // this.ivPlay = ((ImageView)findViewById(R.id.ivPlay));
        // this.ivPlay.setOnClickListener(this);
        // this.btnSelectSource = ((Button)findViewById(R.id.btn_select_source));
        // this.btnSelectSource.setOnClickListener(this);
        // this.btnMyFavorite = ((Button)findViewById(R.id.btn_myfavorite));
        // this.btnMyFavorite.setOnClickListener(this);
        // // LocalMyFavoriteInfo.getInstance().registerMyFavoriteInfoChangedListenenr(this);
        this.mediaSummary = findViewById(R.id.media_summary);
        MediaView localMediaView = (MediaView) findViewById(R.id.media_view);
        // localMediaView.setOnMediaClickListener(this);
        ((MediaImageView) localMediaView.getPosterImage()).setMediaImageReadyCallback(this);
        // this.vDetailContentWrap = ((ViewGroup)findViewById(2131165317));
        // this.reviewsAdapter = new ReviewListAdapter(this);
        // if (this.mediaInfo != null)
        // {
        // setTitle(this.mediaInfo.mediaName);
        // this.mediaID = this.mediaInfo.mediaID;
        // // if (this.mediaInfo.smallImageURL != null)
        // // new Handler().postDelayed(new Runnable()
        // // {
        // // public void run()
        // // {
        // //
        // ImageManager.getInstance().fetchImage(MediaDetailActivity.this.mediaInfo.smallImageURL,
        // MediaDetailActivity.this.mediaView.getPosterImage());
        // // }
        // // }
        // // , 500L);
        // // fillMediaInfo(this.mediaInfo);
        // // getMediaDetailInfo();
        // }
        // // while (true)
        // // {
        // // fillDetail();
        // // return;
        // // if (this.personInfo == null)
        // // continue;
        // // setTitle(this.personInfo.getName());
        // // fillPersonInfo(this.personInfo);
        // // }
        mPagerView = ((PagerView) findViewById(R.id.detail_pagerview));
        if (this.mediaInfo.setCount > 1 || this.mediaInfo.setNow > 1) {

            int margin = getResources().getDimensionPixelSize(R.dimen.page_margin);
            int marginTop = getResources().getDimensionPixelSize(R.dimen.home_banner_margin_top);

            View[] views = new View[2];
            views[0] = View.inflate(this, R.layout.series_gridview, null);
            views[1] = View.inflate(this, R.layout.detail_desc_view, null);
            mPagerView.setIndicatorBackgroundResource(R.drawable.detail_page_indicator_arrowbar, getResources().getDimensionPixelSize(R.dimen.detial_page_indicator_arrowbar_width), getResources().getDimensionPixelSize(R.dimen.page_indicator_arrowbar_height));
            mPagerView.setTabs(getResources().getStringArray(R.array.series_detail_tabs));
            mSeriesGridView = (GridView)views[0].findViewById(R.id.series_gridview);
            mSeriesGridView.setAdapter(seriesAdapter);
            mPagerView.setPageViews(views);
            mPagerView.setCurPage(0);
            
            descEdit = (TextView)views[1].findViewById(R.id.TextView02);
        } else {
//            descEdit = (EditText)views[1].findViewById(R.id.desc_text);
//            mPagerView.setVisibility(4);
            View[] views = new View[1];
            views[0] = View.inflate(this, R.layout.detail_desc_view, null);
            descEdit = (TextView)views[0].findViewById(R.id.TextView02);
            mPagerView.setPageViews(views);
        }
        this.vBottomBar = View.inflate(this, R.layout.media_detail_bottom_bar, null);
        fillMediaInfo(this.mediaInfo);
    }
    @Override
    protected void onCreateAfterSuper(Bundle paramBundle) {
        super.onCreateAfterSuper(paramBundle);
        setContentView(R.layout.media_detail_layout);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataManager = getService().getDataManager();
                download();
            }
        }, 1000);

        Intent localIntent = getIntent();
        this.isBanner = localIntent.getBooleanExtra("isBanner", false);
        // if (localIntent.getBooleanExtra("fromNotification", false))
        // ReceiveNewMediaSetNotificationService.resetCount();
        Bundle localBundle = getIntent().getExtras();
        if (localBundle.containsKey("mediaInfo")) {
            this.mediaInfo = ((MediaInfo) localBundle.getSerializable("mediaInfo"));
            // this.enterPathStatisticInfo =
            // ((MediaDetailEnterStatisticInfo)localBundle.getSerializable("enterPathInfo"));
            // if (this.isBanner)
            // {
            // this.bannerImageUrl = this.mediaInfo.smallImageURL;
            // this.mediaInfo.smallImageURL = null;
            // DataStore localDataStore = DataStore.getInstance();
            // localDataStore.loadBannerUrlInfo();
            // this.mediaInfo.smallImageURL =
            // localDataStore.getBannerUrlInfo(this.mediaInfo.mediaID);
            // }
        }

        // while ((this.mediaInfo != null))
        // {
        // this.isCreated = true;
        // return;
        // if (!localBundle.containsKey("personInfo"))
        // continue;
        // this.personInfo = ((PersonInfo)localBundle.getSerializable("mediaInfo"));
        // if (!this.isBanner)
        // continue;
        // this.bannerImageUrl = this.personInfo.bigImageUrl;
        // this.personInfo.bigImageUrl.imageUrl = null;
        // }
        onActivate();


    }
    private void download() {
        String mediaID = this.mediaInfo.mediaID + "";
        android.util.Log.d("XXXXXXXXXX", "download mediaID = "
                + mediaID);
        mDataManager.loadDetail(mediaID, new IOnloadListener<MediaDetailInfo2>() {
            
            @Override
            public void onLoad(MediaDetailInfo2 entity) {
                // TODO Auto-generated method stub
                if(entity == null) return;
                if(entity.mediaDetailInfo == null) return;
                if(entity.mediaSetInfoList == null) return;
                mMediaDetailInfo = entity.mediaDetailInfo;
                mMediaSetInfoList = entity.mediaSetInfoList;
                mMediaDetailInfo2 = entity;
                mHandler.sendEmptyMessage(MSG_UPDATE_DETAIL_INFO);
            }
        });
        
    }
    
    private void setDetailAdapter(){
        ArrayList localArrayList = (ArrayList) this.mMediaSetInfoList.getAvailableCiList();
       seriesAdapter.setGroup(localArrayList);
       if(descEdit == null) return;
       descEdit.setText(this.mMediaDetailInfo.desc);
       
       
    }
    
    private void fillMediaInfo(MediaInfo paramMediaInfo) {
        // ((RatingView)findViewById(2131165219)).setScore(paramMediaInfo.score);
        // if (paramMediaInfo.score > 0.0F)
        // ((TextView)findViewById(2131165332)).setText(Util.formatScore(paramMediaInfo.score));
        UIUtil.fillMediaSummary(findViewById(R.id.media_summary), paramMediaInfo);
        this.mediaView = ((MediaView) findViewById(R.id.media_view));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        android.util.Log.d("XXXXXXXX", "onClick ");
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_DETAIL_INFO:
                    setDetailAdapter();
                    break;
            }
        }
    };

    @Override
    public View makeView() {
        ImageView localImageView = new ImageView(this);
        localImageView.setBackgroundResource(R.drawable.detail_default_poster);
        localImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return localImageView;
    }

    @Override
    public void onMediaImageReady(Bitmap paramBitmap) {
        // TODO Auto-generated method stub
        this.vBigPoster.setImageDrawable(new BitmapDrawable(getResources(), UIUtil
                .BoxBlurFilter(paramBitmap)));
        this.vBigPosterMask.setVisibility(0);
        this.vBigPosterMask.setAlpha(0.6f);
    }

}
