package com.infthink.itmc;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.infthink.itmc.adapter.SeriesAdapter;
import com.infthink.itmc.adapter.SourceListAdapter;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.data.DataManager.IOnloadListener;
import com.infthink.itmc.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.LocalMyFavoriteItemInfo;
import com.infthink.itmc.type.MediaDetailInfo;
import com.infthink.itmc.type.MediaDetailInfo2;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.MediaSetInfo;
import com.infthink.itmc.type.MediaSetInfoList;
import com.infthink.itmc.type.MediaUrlInfo;
import com.infthink.itmc.type.MediaUrlInfoList;
import com.infthink.itmc.util.AlertMessage;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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

    private MediaDetailInfo mMediaDetailInfo;
    private MediaSetInfoList mMediaSetInfoList;
    private MediaDetailInfo2 mMediaDetailInfo2;
    private MediaUrlInfoList mMediaUrlInfoList;
    TextView mDescTextview;

    private int mPreferenceSource = -1;
    private String mMediaUrl;

    public static final int MSG_UPDATE_DETAIL_INFO = 1;
    public static final int MSG_UPDATE_MEDIA_URL = 2;
    private AlertDialog selectSourceDialog;
    private SourceListAdapter sourceListAdapter;
    private ArrayList<Integer> sourceListAdapterData = new ArrayList<Integer>();

    private boolean isFavorite = false;

    LocalMyFavoriteInfoManager mLocalLocalMyFavoriteInfo;

    private AdapterView.OnItemClickListener sourceListOnItemClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
                        int paramInt, long paramLong) {
                    if (MediaDetailActivity.this.sourceListAdapterData != null) {
                        mPreferenceSource =
                                (Integer) MediaDetailActivity.this.sourceListAdapterData.get(
                                        paramInt).intValue();
                        mMediaUrl = "";
                        // MediaDetailActivity.access$1602(MediaDetailActivity.this,
                        // ((Integer)MediaDetailActivity.this.sourceListAdapterData.get(paramInt)).intValue());
                        if (MediaDetailActivity.this.sourceListAdapter != null) {
                            MediaDetailActivity.this.sourceListAdapter
                                    .setSelectedSource(MediaDetailActivity.this.mPreferenceSource);
                            MediaDetailActivity.this.sourceListAdapter.notifyDataSetChanged();
                        }
                    }

                    MediaDetailActivity.this
                            .refreshSelectedSource(MediaDetailActivity.this.mPreferenceSource);
                    if ((MediaDetailActivity.this.selectSourceDialog != null)
                            && (MediaDetailActivity.this.selectSourceDialog.isShowing()))
                        MediaDetailActivity.this.selectSourceDialog.dismiss();
                }
            };


    private void refreshSelectedSource(int paramInt) {
        this.mPreferenceSource = paramInt;
        this.btnSelectSource.setOnClickListener(this);
        this.btnSelectSource.setVisibility(View.VISIBLE);
        // MediaUrlForPlayerUtil.getInstance(this).setPrefrenceSource(paramInt);
        if (paramInt == 8) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_qiyi);
            return;
        }
        if (paramInt == 3) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_souhu);
            return;
        }
        if (paramInt == 10) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_tencent);
            return;
        }
        if (paramInt == 20) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_youku);
            return;
        }
        if (paramInt == 24) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_fenghuang);
            return;
        }
        if (paramInt == 23) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_tudou);
            return;
        }
        if (paramInt == 17) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_lekan);
            return;
        }
        if (paramInt == 25) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_film);
            return;
        }
        if (paramInt == 32) {
            this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_letv);
            return;
        }
        this.btnSelectSource.setBackgroundResource(R.drawable.select_source_item_default);
    }

    private void refreshSelectedSource(MediaUrlInfoList paramMediaUrlInfoList) {
        // ArrayList localArrayList =
        // MediaUrlInfoListUtil.getInstance().getSourceList(paramMediaUrlInfoList);
        // ArrayList localArrayList = (ArrayList) mMediaSetInfoList.getAvailableCiList();
        ArrayList localArrayList = new ArrayList();
        if ((localArrayList != null) && (localArrayList.size() > 0)) {
            this.mPreferenceSource = ((Integer) localArrayList.get(0)).intValue();
            refreshSelectedSource(this.mPreferenceSource);
        }
    }



    private void onActivate() {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView detailName = (TextView) this.findViewById(R.id.detail_name);
        detailName.setText(this.mediaInfo.mediaName.trim());
        TextView detailSubTitle = (TextView) this.findViewById(R.id.detail_subtitle);
        detailSubTitle.setText(UIUtil.getMediaStatus(this, this.mediaInfo));

        ImageButton back = (ImageButton) this.findViewById(R.id.return_detail_back);
        back.setOnClickListener(this);
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
        // this.vBigPoster.setInAnimation(AnimationUtils.loadAnimation(this, 17432576));
        // this.vBigPoster.setOutAnimation(AnimationUtils.loadAnimation(this, 17432577));
        this.vBigPosterMask = this.findViewById(R.id.media_big_poster_mask);
        this.vBigPosterMask.setVisibility(4);
        // ActorsView.resetActorViewWidth();
        // this.vActorsView = ((ActorsView)findViewById(R.id.actors));
        // this.vActorsView.setOnActorViewClickListener(this);
        // this.btnPlay = ((Button)findViewById(R.id.btn_play));
        // this.btnPlay.setOnClickListener(this);
        // this.ivPlay = ((ImageView)findViewById(R.id.ivPlay));
        // this.ivPlay.setOnClickListener(this);
        this.btnSelectSource = ((Button) findViewById(R.id.btn_select_source));
        // this.btnSelectSource.setVisibility(View.INVISIBLE);

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
        mPagerView.setTabBackgroudResource(R.drawable.transparent);
        if (this.mediaInfo.setCount > 1 || this.mediaInfo.setNow > 1) {

            int margin = getResources().getDimensionPixelSize(R.dimen.page_margin);
            int marginTop = getResources().getDimensionPixelSize(R.dimen.home_banner_margin_top);

            View[] views = new View[2];
            views[0] = View.inflate(this, R.layout.series_gridview, null);
            views[1] = View.inflate(this, R.layout.detail_desc_view, null);
            mPagerView.setIndicatorBackgroundResource(
                    R.drawable.detail_page_indicator_arrowbar,
                    getResources().getDimensionPixelSize(
                            R.dimen.detial_page_indicator_arrowbar_width), getResources()
                            .getDimensionPixelSize(R.dimen.page_indicator_arrowbar_height));
            mPagerView.setTabs(getResources().getStringArray(R.array.series_detail_tabs));
            mSeriesGridView = (GridView) views[0].findViewById(R.id.series_gridview);
            mSeriesGridView.setAdapter(seriesAdapter);
            mSeriesGridView.setOnItemClickListener(new ItemClickListener());
            mPagerView.setPageViews(views);
            mPagerView.setCurPage(0);

            mDescTextview = (TextView) views[1].findViewById(R.id.TextView02);
        } else {
            // descEdit = (EditText)views[1].findViewById(R.id.desc_text);
            // mPagerView.setVisibility(4);
            View[] views = new View[1];
            views[0] = View.inflate(this, R.layout.detail_desc_view, null);
            mDescTextview = (TextView) views[0].findViewById(R.id.TextView02);
            mPagerView.setPageViews(views);
        }
        this.vBottomBar = View.inflate(this, R.layout.media_detail_bottom_bar, null);
        Button btn = (Button) this.findViewById(R.id.btn_play);
        btn.setOnClickListener(this);
        this.btnMyFavorite = ((Button) findViewById(R.id.btn_myfavorite));
        this.btnMyFavorite.setVisibility(View.INVISIBLE);
        this.btnMyFavorite.setOnClickListener(this);
        fillMediaInfo(this.mediaInfo);

    }

    @Override
    protected void onCreateAfterSuper(Bundle paramBundle) {
        super.onCreateAfterSuper(paramBundle);
        getActionBar().hide();
        setContentView(R.layout.media_detail_layout);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataManager = getService().getDataManager();
                mLocalLocalMyFavoriteInfo =
                        LocalMyFavoriteInfoManager.getInstance(MediaDetailActivity.this);
                isFavorite =
                        mLocalLocalMyFavoriteInfo.checkIsFavorite(MediaDetailActivity.this,
                                mediaInfo.mediaID);
                refreshMyFavorite();
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
        android.util.Log.d("XXXXXXXXXX", "download mediaID = " + mediaID);
        mDataManager.loadDetail(mediaID, new IOnloadListener<MediaDetailInfo2>() {

            @Override
            public void onLoad(MediaDetailInfo2 entity) {
                // TODO Auto-generated method stub
                if (entity == null) return;
                if (entity.mediaDetailInfo == null) return;
                if (entity.mediaSetInfoList == null) return;
                mMediaDetailInfo = entity.mediaDetailInfo;
                mMediaSetInfoList = entity.mediaSetInfoList;
                mMediaDetailInfo2 = entity;
                mHandler.sendEmptyMessage(MSG_UPDATE_DETAIL_INFO);
            }
        });
        getMediaUrl();;
    }

    private void getMediaUrl() {
        String mediaID = this.mediaInfo.mediaID + "";
        mDataManager.loadMediaUrl(mediaID, ci, new IOnloadListener<MediaUrlInfoList>() {

            @Override
            public void onLoad(MediaUrlInfoList entity) {
                // TODO Auto-generated method stub
                if (entity == null) return;
                if (entity.urlNormal == null) return;
                ci = 1;
                mPreferenceSource = entity.urlNormal[0].mediaSource;
                mMediaUrl = entity.urlNormal[0].mediaUrl;
                mMediaUrlInfoList = entity;
                mHandler.sendEmptyMessage(MSG_UPDATE_MEDIA_URL);
            }

        });
    }

    private int getSourceIDPos(int sourceId) {
        int pos = 0;
        for (int i = 0; i < sourceListAdapterData.size(); i++) {
            int temp = sourceListAdapterData.get(i);
            if (temp == sourceId) {
                pos = i;
                return pos;
            }
        }
        return pos;
    }

    private void setDetailAdapter() {
        ArrayList localArrayList = (ArrayList) this.mMediaSetInfoList.getAvailableCiList();

        seriesAdapter.setGroup(localArrayList);
        if (mDescTextview == null) return;
        mDescTextview.setText(this.mMediaDetailInfo.desc);
    }

    private void setMediaURL() {
        MediaUrlInfoList mMediaUrlInfoList2 = this.mMediaUrlInfoList;
        for (int i = 0; i < mMediaUrlInfoList2.urlNormal.length; i++) {
            MediaUrlInfo normal = mMediaUrlInfoList2.urlNormal[i];
            this.sourceListAdapterData.add(normal.mediaSource);
        }
        refreshSelectedSource(sourceListAdapterData.get(0).intValue());
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
        if (v.getId() == R.id.return_detail_back) {
            this.finish();
        }
        if (v.getId() == R.id.btn_play) {
            if (mMediaUrl == null || mPreferenceSource == -1) {
                Toast.makeText(this, "视频地址正在获取中", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mMediaUrl == "") {
                mMediaUrl = mMediaUrlInfoList.urlNormal[getSourceIDPos(mPreferenceSource)].mediaUrl;
            }
            Intent intent = new Intent(MediaDetailActivity.this, WebViewActivity.class);
            intent.putExtra("media_id", mediaInfo.mediaID);
            intent.putExtra("pageUrl", mMediaUrl);
            intent.putExtra("source", mPreferenceSource);
            intent.putExtra("meidaTitle", mediaInfo.mediaName.trim());
            intent.putExtra("available_episode_count", mediaInfo.setCount);
            intent.putExtra("current_episode", ci);
            startActivity(intent);
            android.util.Log.d("XXXXXXXXXX", "mMediaUrl = " + mMediaUrl + " mPreferenceSource = "
                    + mPreferenceSource);
        }
        if (v.getId() == R.id.btn_select_source) {
            showSelectSourceDialog();
        }
        if (v.getId() == R.id.btn_myfavorite) {
            String addDate = String.valueOf(Calendar.getInstance().getTimeInMillis());
            LocalMyFavoriteItemInfo paramLocalMyFavoriteItemInfo =
                    new LocalMyFavoriteItemInfo(mediaInfo.mediaID, this.mediaInfo, addDate);
            if (isFavorite) {
                mLocalLocalMyFavoriteInfo.deleteMyFavoriteInfo(this, paramLocalMyFavoriteItemInfo);
                Toast.makeText(this, "取消收藏成功", Toast.LENGTH_LONG).show();
                isFavorite = false;
            } else {
                mLocalLocalMyFavoriteInfo.addMyFavoriteInfo(this, paramLocalMyFavoriteItemInfo);
                isFavorite = true;
                Toast.makeText(this, "收藏成功", Toast.LENGTH_LONG).show();
            }
            refreshMyFavorite();
        }
    }

    private void refreshMyFavorite() {
        this.btnMyFavorite.setVisibility(View.VISIBLE);
        if (this.isFavorite) {
            this.btnMyFavorite.setSelected(true);
            return;
        }
        this.btnMyFavorite.setSelected(false);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_DETAIL_INFO:
                    setDetailAdapter();
                    break;
                case MSG_UPDATE_MEDIA_URL:
                    setMediaURL();
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

    private void showSelectSourceDialog() {
        View localView = LayoutInflater.from(this).inflate(R.layout.common_list_dialog, null);
        ListView localListView = (ListView) localView.findViewById(R.id.common_dialog_listview);
        if (this.sourceListAdapter == null) {
            this.sourceListAdapter = new SourceListAdapter(this);
        }

        // this.sourceListAdapterData =
        // MediaUrlInfoListUtil.getInstance().getSourceList(this.mediaUrlInfoList);
        // ArrayList<Integer> temp = new ArrayList<Integer>();
        // temp.add(3);
        // temp.add(8);
        // temp.add(23);
        // this.sourceListAdapterData = temp;
        if (this.sourceListAdapterData.size() == 0) {
            AlertMessage.show(this, R.string.hint_can_not_get_source_info);
            // getMediaUrlInfoList(this.mediaInfo.mediaID, this.ci, -1);
            return;
        }
        this.sourceListAdapter.setGroup(this.sourceListAdapterData);
        this.sourceListAdapter.setSelectedSource(this.mPreferenceSource);
        localListView.setAdapter(this.sourceListAdapter);
        localListView.setOnItemClickListener(this.sourceListOnItemClickListener);
        this.selectSourceDialog =
                new AlertDialog.Builder(this).setTitle(R.string.select_media_source)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                MediaDetailActivity.this.selectSourceDialog.dismiss();
                            }
                        }).setView(localView).create();
        this.selectSourceDialog.show();
    }

    class ItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> view, View arg1, final int arg2, long arg3) {
            // TODO Auto-generated method stub
            String mediaID = mediaInfo.mediaID + "";
            ci = arg2 + 1;
            btnPlay = ((Button) findViewById(R.id.btn_play));
            btnPlay.setText("视频地址正在获取中");
            mDataManager.loadMediaUrl(mediaID, ci, new IOnloadListener<MediaUrlInfoList>() {

                @Override
                public void onLoad(MediaUrlInfoList entity) {
                    // TODO Auto-generated method stub
                    if (entity == null) return;
                    if (entity.urlNormal == null) return;
                    // mPreferenceSource = entity.urlNormal[0].mediaSource;
                    mMediaUrl = entity.urlNormal[getSourceIDPos(mPreferenceSource)].mediaUrl;
                    if (mMediaUrl == null || mPreferenceSource == -1) {
                        Toast.makeText(MediaDetailActivity.this, "视频地址正在获取中", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    btnPlay.setText("播放");
                    Intent intent = new Intent(MediaDetailActivity.this, WebViewActivity.class);
                    intent.putExtra("media_id", mediaInfo.mediaID);
                    intent.putExtra("pageUrl", mMediaUrl);
                    intent.putExtra("source", mPreferenceSource);
                    intent.putExtra("meidaTitle", mediaInfo.mediaName.trim());
                    intent.putExtra("available_episode_count", mediaInfo.setCount);
                    intent.putExtra("current_episode", ci);
                    intent.putExtra("ci", ci);
                    startActivity(intent);
                }

            });
        }

    }
}
