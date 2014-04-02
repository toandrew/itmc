package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.infthink.itmc.adapter.HomeChannelAdapter;
import com.infthink.itmc.adapter.HomeMediaStoreAdapter;
import com.infthink.itmc.adapter.ScrollBannerAdapter;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.LocalMediaCategoryInfo;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.RecommendChannel;
import com.infthink.itmc.type.ShowBaseInfo;
import com.infthink.itmc.widget.BannerIndicator;
import com.infthink.itmc.widget.LoadingListView;
import com.infthink.itmc.widget.PagerView;
import com.infthink.itmc.widget.ScrollViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends CoreActivity implements OnPageChangeListener, OnClickListener {
    private static final int MSG_UPDATE_HOME_CHANNEL = 0;
    private static final int MSG_UPDATE_HOME_BANNER = 1;
    
    private PagerView mPagerView;
    private View mHeaderView;
    private View mFooterView;
    private ScrollViewPager mBannerView;
    private ScrollBannerAdapter mBannerAdapter;
    private BannerIndicator mBannerIndicator;
    private int mBannerMediaCount;
    private Banner[] mBannerMediaList = null;
    private boolean mBannerCountChanged;
    private int mBannerViewIndex = -1;
    private Button mBtnSpecials;
    private View mOnlineBottombar;
    private LoadingListView mChannelLoadingListView;
    private LoadingListView mMediaStoreLoadingListView;
    private ListView mHomeChannelListView;
    private ListView mHomeMediaStoreListView;
    private HomeChannelAdapter mHomeChannelAdapter;
    private HomeMediaStoreAdapter mHomeMediaStoreAdapter;
    private HashMap<Channel, ShowBaseInfo[]> mRecommendationOfChannels = new HashMap();
    private ArrayList<Channel> mChannelList = new ArrayList<Channel>();
    private long mTimeLastBackPressed;
    private DataManager mDataManager;

    @Override
    protected void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataManager = getService().getDataManager();
                getChannelMap();
            }
        }, 1000);
       
        
        setContentView(R.layout.activity_home);

        mOnlineBottombar = View.inflate(this, R.layout.home_onlinevideo_bottombar, null);
        onCreateActivate();
    }
    

    private void onCreateActivate() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.clickable_icon_search);
        ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(null);
        actionBar.setDisplayShowHomeEnabled(false);
        
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER);
        
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(imageView, lp);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.appear, R.anim.stay_same);
            }
        });

        mPagerView = ((PagerView)findViewById(R.id.home_pagerview));
        mPagerView.setIndicatorBackgroundResource(R.drawable.page_indicator_arrowbar);
        mPagerView.setTabBackgroudResource(R.drawable.transparent);
        mPagerView.setTabs(getResources().getStringArray(R.array.home_tabs));
//        mPagerView.setIndicatorMoveListener(this);
//        mPagerView.setTabOnTouchListener(this);
        View[] views = new View[2];
        views[0] = View.inflate(this, R.layout.home_onlinevideo_view, null);
        views[1] = View.inflate(this, R.layout.home_myvideo_view, null);
        mChannelLoadingListView = (LoadingListView) views[0];
        mMediaStoreLoadingListView = (LoadingListView) views[1];
        
        mPagerView.setPageViews(views);
        mPagerView.setCurPage(0);
//        mPagerView.getPager().setOnTouchInterceptor(this);
        
        mHeaderView = View.inflate(this, R.layout.banner_view, null);
        int marginH = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int marginTop = getResources().getDimensionPixelSize(R.dimen.home_banner_margin_top);
        mHeaderView.setPadding(marginH, marginTop, marginH, 0);

        mBannerView = (ScrollViewPager) mHeaderView.findViewById(R.id.banner);
        mBannerView.setOnPageChangeListener(this);
        mBannerAdapter = new ScrollBannerAdapter(this/*, this*/);
//        mBannerAdapter.setOnMediaClickListener(this);
        mBannerView.setAdapter(mBannerAdapter);

        mBannerIndicator = ((BannerIndicator)mHeaderView.findViewById(R.id.bannerIndicator));
        mBannerCountChanged = false;
        mBannerMediaCount = 0;
        mBannerViewIndex = -1;
        if (mBannerMediaList != null)
            mBannerMediaCount = mBannerMediaList.length;
        mBannerIndicator.setIndicatorNum(mBannerMediaCount);
        
        mFooterView = View.inflate(this, R.layout.all_specials_view, null);
        mBtnSpecials = (Button) mOnlineBottombar.findViewById(R.id.all_special);
//        mBtnSpecials.setOnClickListener(this);
        
        mChannelLoadingListView.setLoadingView(View.inflate(this, R.layout.load_view, null));
//        RetryLoadingView localRetryLoadingView = new RetryLoadingView(this);
//        2 local2 = new RetryLoadingView.OnRetryLoadListener()
//        {
//          public void OnRetryLoad(View paramView)
//          {
//            HomeActivity.this.channelLoadingListView.setShowLoadingResult(false);
//            HomeActivity.this.loadData();
//          }
//        };
//        localRetryLoadingView.setOnRetryLoadListener(local2);
//        this.channelLoadingListView.setLoadingResultView(localRetryLoadingView);
        
        mHomeChannelListView = mChannelLoadingListView.getListView();
        mHomeChannelListView.setVerticalScrollBarEnabled(false);
//        this.mHomeChannelListView.setOnTouchInterceptor(this);
        mHomeChannelListView.addHeaderView(mHeaderView);
        mHomeChannelListView.addFooterView(mFooterView);
        
        mMediaStoreLoadingListView.setLoadingView(View.inflate(this, R.layout.load_view, null));
        mHomeMediaStoreListView = mMediaStoreLoadingListView.getListView();
        View headerView = new View(this);
        headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.home_media_padding_top)));
        mHomeMediaStoreListView.addHeaderView(headerView, null, false);
        mHomeMediaStoreListView.setSelectionAfterHeaderView();
        mHomeMediaStoreListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        mHomeMediaStoreListView.setMultiChoiceModeListener(this.mMultiChoiceController);
        mHomeMediaStoreListView.setSelector(R.drawable.clickable_item_bg_part);
        mHomeMediaStoreAdapter = new HomeMediaStoreAdapter(this);
        mHomeMediaStoreAdapter.setGroup(new ArrayList());
        mHomeMediaStoreListView.setAdapter(mHomeMediaStoreAdapter);
//        mHomeMediaStoreListView.setOnScrollListener(this);
//        mHomeMediaStoreListView.setOnItemLongClickListener(this);
//        mHomeMediaStoreListView.setOnItemClickListener(this);

        setMediaAdapter();
    }
    
    private void setBannerAdapter() {
        mBannerAdapter.setBannerList(mBannerMediaList);
        mBannerView.setCurrentItem(1);
        mBannerMediaCount = mBannerMediaList.length;
        mBannerIndicator.setIndicatorNum(mBannerMediaCount);
        mBannerIndicator.setCurIndicator(0);
        startBannerScrollTimer();
    }

    private void getChannelMap() {
        mDataManager.loadChannelMap(new DataManager.IOnloadListener<HashMap<Integer, String>>() {
            @Override
            public void onLoad(HashMap<Integer, String> channelMap) {
                ITApp.setChannelMap(channelMap);
                download();
            }
        });
    }

    private void download() {
        mDataManager.loadRecommendChannel(null, new DataManager.IOnloadListener<RecommendChannel>() {
            @Override
            public void onLoad(RecommendChannel entity) {
                mChannelList = (ArrayList<Channel>) entity.channelList;
                mRecommendationOfChannels = entity.recommend;
                mHandler.sendEmptyMessage(MSG_UPDATE_HOME_CHANNEL);
            }
        });
        mDataManager.loadBanner(null, new DataManager.IOnloadListener<Banner[]>() {

            @Override
            public void onLoad(Banner[] entity) {
                mBannerMediaList = entity;
                mHandler.sendEmptyMessage(MSG_UPDATE_HOME_BANNER);
            }
        });
    }
    
   private void setHomeChannelAdapter() {
       if (mHomeChannelAdapter == null) {
           mHomeChannelAdapter = new HomeChannelAdapter(this);
//            this.mHomeChannelAdapter.setRecommendationOfChannels(this.recommendationOfChannels);
           // this.mHomeChannelAdapter.setOnMediaClickListenenr(this);
           // this.mHomeChannelAdapter.setOnMoreClickListener(this);

       }
       mHomeChannelAdapter.setRecommendationOfChannels(mRecommendationOfChannels);
       mHomeChannelAdapter.setOnMoreClickListener(this);
       mHomeChannelListView.setAdapter(mHomeChannelAdapter);
       mHomeChannelAdapter.setGroup(mChannelList);
   }
    
    private void setMediaAdapter() {
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            LocalMediaCategoryInfo c = new LocalMediaCategoryInfo();
            localArrayList.add(c);
        }
        if (mHomeMediaStoreAdapter == null) {
            mHomeMediaStoreAdapter = new HomeMediaStoreAdapter(this);
            // this.mHomeChannelAdapter.setRecommendationOfChannels(this.recommendationOfChannels);
            // this.mHomeChannelAdapter.setOnMediaClickListenenr(this);
            // this.mHomeChannelAdapter.setOnMoreClickListener(this);
            mHomeMediaStoreListView.setAdapter(mHomeMediaStoreAdapter);
            mHomeMediaStoreAdapter.setGroup(localArrayList);
        } else {
            mHomeMediaStoreAdapter.setGroup(localArrayList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - mTimeLastBackPressed < 3000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "再按一次返回退出", Toast.LENGTH_SHORT).show();
            mTimeLastBackPressed = now;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_HOME_CHANNEL:
                setHomeChannelAdapter();
                break;
            case MSG_UPDATE_HOME_BANNER:
                setBannerAdapter();
                break;
            }
        }
    };

    public static class MediaViewMetaInfo {
        public int channelId;
        public String channelName;
        public int position;

        public MediaViewMetaInfo(int position, int channelId, String channelName) {
            this.position = position;
            this.channelId = channelId;
            this.channelName = channelName;
        }
    }

    private void callBack() {
        mHandler.postDelayed(mRunnable, 5000);
    }

    private void stopBannerScrollTimer() {
        mHandler.removeCallbacks(mRunnable);
    }
    

    public void startBannerScrollTimer() {
        stopBannerScrollTimer();
        callBack();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mBannerView.setCurrentItem(mBannerView.getCurrentItem() + 1, true);
            callBack();
        }
    };
    
    private boolean mUserDraggingBanner = false;
    
    @Override
    public void onPageScrollStateChanged(int status) {
        switch (status) {
        case 1:// 手势滑动
            if (!mUserDraggingBanner) {
                mUserDraggingBanner = true;
                stopBannerScrollTimer();
            }
            break;
        case 2:// 界面切换
            break;
        case 0:// 滑动结束

            // 当前为最后一张，此时从右向左滑，则切换到第一张
            int pageIndex = mBannerView.getCurrentItem();
            if (pageIndex == 0) {
                pageIndex = mBannerAdapter.getCount() - 2;
                mBannerView.setCurrentItem(pageIndex, false);
            } else if (pageIndex == (mBannerAdapter.getCount() - 1)) {
                pageIndex = 1;
                mBannerView.setCurrentItem(pageIndex, false);
            }
            mBannerIndicator.setCurIndicator(pageIndex - 1);
            if (mUserDraggingBanner) {
                mUserDraggingBanner = false;
                startBannerScrollTimer();
            }
            break;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        
    }

    @Override
    public void onPageSelected(int arg0) {
        
    }

    @Override
    public void onClick(View view) {
        android.util.Log.d("XXXXXXXX", "onClick ");
        if (view.getId() == R.id.channel_more) {
            Intent intent = new Intent(HomeActivity.this, MediaDetailActivity.class);
            Channel channel = (Channel) view.getTag();
            MediaInfo[] mediainfo = (MediaInfo[]) mRecommendationOfChannels.get(channel);
            android.util.Log.d("XXXXXXXXXX", "mediainfo = " + mediainfo[0].actors);
            intent.putExtra("mediaInfo", mediainfo[0]);
            startActivity(intent);
            
//            overridePendingTransition(R.anim.appear, R.anim.stay_same);
        }
    }
}
