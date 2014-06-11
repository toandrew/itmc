package com.infthink.itmc.v2;

import java.util.ArrayList;
import java.util.HashMap;

import com.firefly.sample.castcompanionlibrary.cast.VideoCastManager;
import com.firefly.sample.castcompanionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.fireflycast.cast.ApplicationMetadata;
import com.infthink.itmc.v2.adapter.HomeChannelAdapter;
import com.infthink.itmc.v2.adapter.HomeMediaStoreAdapter;
import com.infthink.itmc.v2.adapter.ScrollBannerAdapter;
import com.infthink.itmc.v2.data.DataManager;
import com.infthink.itmc.v2.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.v2.type.Banner;
import com.infthink.itmc.v2.type.Channel;
import com.infthink.itmc.v2.type.LocalMediaCategoryInfo;
import com.infthink.itmc.v2.type.MediaInfo;
import com.infthink.itmc.v2.type.RecommendChannel;
import com.infthink.itmc.v2.type.ShowBaseInfo;
import com.infthink.itmc.v2.widget.BannerIndicator;
import com.infthink.itmc.v2.widget.LoadingListView;
import com.infthink.itmc.v2.widget.MediaView;
import com.infthink.itmc.v2.widget.PagerView;
import com.infthink.itmc.v2.widget.ScrollViewPager;
import com.infthink.itmc.v2.widget.MediaView.OnMediaClickListener;
import com.infthink.libs.common.message.MessageManager;
import com.infthink.libs.common.message.MessageResponse;
import com.infthink.libs.upgrade.Upgrade;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends CoreActivity implements OnPageChangeListener,
        OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
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
    private View mBottomItem;
    private VideoCastManager mCastManager;
    
    LocalMyFavoriteInfoManager mLocalLocalMyFavoriteInfo;

    @Override
    protected void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);

        android.util.Log.d(TAG, "onCreateAfterSuper");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContentView(R.layout.activity_home);
        mBottomItem = View.inflate(this, R.layout.home_bottom_item, null);
        mOnlineBottombar = View.inflate(this,
                R.layout.home_onlinevideo_bottombar, null);
        int height = ITApp.getStatusBarHeight();
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins(0, height, 0, 0);
        decorView.addView(mBottomItem, 0, lp);
        
        if (ITApp.getInstance().getMode() == ITApp.MODE_UNDEFINED) {
            final LinearLayout content = new LinearLayout(this);
            content.setBackgroundColor(Color.BLACK);
            content.setOrientation(LinearLayout.VERTICAL);
            Button common = new Button(this);
            common.setText("common");

            common.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ITApp.getInstance().setMode(ITApp.MODE_COMMON);
                    content.setVisibility(View.GONE);
                }
            });
            content.addView(common);

            Button mp4 = new Button(this);
            mp4.setText("mp4");

            mp4.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ITApp.getInstance().setMode(ITApp.MODE_MP4);
                    content.setVisibility(View.GONE);
                }
            });
            content.addView(mp4);
            
            Button flv = new Button(this);
            flv.setText("flv");

            flv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ITApp.getInstance().setMode(ITApp.MODE_FLV);
                    content.setVisibility(View.GONE);
                }
            });
            content.addView(flv);
            content.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            decorView.addView(content, fl);
        }
        // localViewGroup.addView(this.vOnlineBottombar);
        onCreateActivate();
    }
    
    @Override
    protected void onInitialized() {
        android.util.Log.d(TAG, "onInitialized");
        mDataManager = getService().getDataManager();
        getChannelMap();
        Upgrade upgrade = getService().getUpgrade();
        upgrade.prepareUpgradeView(HomeActivity.this);
        MessageManager.sendMessage(new AppUpdateEvent(false), 1, true);

        mLocalLocalMyFavoriteInfo = LocalMyFavoriteInfoManager.getInstance(HomeActivity.this);
    }
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setBackgroundDrawable(
//                getResources().getDrawable(R.drawable.ab_transparent_democastoverlay));
    }
    private void onCreateActivate() {
        mCastManager = ITApp.getCastManager(this);
        
        setupActionBar();
        
        LinearLayout layout = new LinearLayout(this);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.clickable_icon_search);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                 Intent intent = new Intent(HomeActivity.this,
                 SearchActivity.class);
                 startActivity(intent);
                 overridePendingTransition(R.anim.appear, R.anim.stay_same);
            }
        });
        layout.addView(imageView);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(null);
        actionBar.setDisplayShowHomeEnabled(false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                        | Gravity.CENTER);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(layout, lp);

        mPagerView = ((PagerView) findViewById(R.id.home_pagerview));
        mPagerView
                .setIndicatorBackgroundResource(R.drawable.page_indicator_arrowbar);

        mPagerView.setTabBackgroudResource(R.drawable.transparent);
        mPagerView.setTabs(getResources().getStringArray(R.array.home_tabs));
        // mPagerView.setIndicatorMoveListener(this);
        // mPagerView.setTabOnTouchListener(this);
        View[] views = new View[2];
        views[0] = View.inflate(this, R.layout.home_onlinevideo_view, null);
        views[1] = View.inflate(this, R.layout.home_myvideo_view, null);
        mChannelLoadingListView = (LoadingListView) views[0];
        mMediaStoreLoadingListView = (LoadingListView) views[1];

        mPagerView.setPageViews(views);
        mPagerView.setCurPage(0);
        // mPagerView.getPager().setOnTouchInterceptor(this);

        mHeaderView = View.inflate(this, R.layout.banner_view, null);
        int marginH = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int marginTop = getResources().getDimensionPixelSize(
                R.dimen.home_banner_margin_top);
        mHeaderView.setPadding(marginH, marginTop, marginH, 0);

        mBannerView = (ScrollViewPager) mHeaderView.findViewById(R.id.banner);
        mBannerView.setOnPageChangeListener(this);
        mBannerAdapter = new ScrollBannerAdapter(this/* , this */);
        // mBannerAdapter.setOnMediaClickListener(this);
        mBannerView.setAdapter(mBannerAdapter);

        mBannerIndicator = ((BannerIndicator) mHeaderView.findViewById(R.id.bannerIndicator));

        mBannerCountChanged = false;
        mBannerMediaCount = 0;
        mBannerViewIndex = -1;
        if (mBannerMediaList != null) mBannerMediaCount = mBannerMediaList.length;
        mBannerIndicator.setIndicatorNum(mBannerMediaCount);

        mFooterView = View.inflate(this, R.layout.all_specials_view, null);
        mBtnSpecials = (Button) mOnlineBottombar.findViewById(R.id.all_special);
        // mBtnSpecials.setOnClickListener(this);

        mChannelLoadingListView.setLoadingView(View.inflate(this, R.layout.load_view, null));

        // RetryLoadingView localRetryLoadingView = new RetryLoadingView(this);
        // 2 local2 = new RetryLoadingView.OnRetryLoadListener()
        // {
        // public void OnRetryLoad(View paramView)
        // {
        // HomeActivity.this.channelLoadingListView.setShowLoadingResult(false);
        // HomeActivity.this.loadData();
        // }
        // };
        // localRetryLoadingView.setOnRetryLoadListener(local2);
        // this.channelLoadingListView.setLoadingResultView(localRetryLoadingView);

        mHomeChannelListView = mChannelLoadingListView.getListView();
        mHomeChannelListView.setVerticalScrollBarEnabled(false);
        // this.mHomeChannelListView.setOnTouchInterceptor(this);
        mHomeChannelListView.addHeaderView(mHeaderView);
        mHomeChannelListView.addFooterView(mFooterView);


        mMediaStoreLoadingListView.setLoadingView(View.inflate(this, R.layout.load_view, null));
        mHomeMediaStoreListView = mMediaStoreLoadingListView.getListView();
        View headerView = new View(this);
        headerView.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(
                        R.dimen.home_media_padding_top)));
        mHomeMediaStoreListView.addHeaderView(headerView, null, false);
        mHomeMediaStoreListView.setSelectionAfterHeaderView();
        mHomeMediaStoreListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        // mHomeMediaStoreListView.setMultiChoiceModeListener(this.mMultiChoiceController);
        mHomeMediaStoreListView.setSelector(R.drawable.clickable_item_bg_part);
        mHomeMediaStoreAdapter = new HomeMediaStoreAdapter(this);
        mHomeMediaStoreAdapter.setGroup(new ArrayList());
        mHomeMediaStoreListView.setAdapter(mHomeMediaStoreAdapter);
        // mHomeMediaStoreListView.setOnScrollListener(this);
        // mHomeMediaStoreListView.setOnItemLongClickListener(this);
        // mHomeMediaStoreListView.setOnItemClickListener(this);

        setMediaAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCastManager.decrementUiCounter();
    }

    private void setBannerAdapter() {
        if (mBannerMediaList != null && mBannerMediaList.length > 0) {
            mBannerAdapter.setBannerList(mBannerMediaList);
            mBannerView.setCurrentItem(1);
            mBannerMediaCount = mBannerMediaList.length;
            mBannerIndicator.setIndicatorNum(mBannerMediaCount);
            mBannerIndicator.setCurIndicator(0);
            mBannerAdapter.setOnMediaClickListener(new OnMediaClickListener() {
                
                @Override
                public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(HomeActivity.this, MediaDetailActivity.class);
                    MediaInfo mediaInfo = paramMediaView.getMediaInfo();
                    intent.putExtra("mediaInfo", mediaInfo);
                    startActivity(intent);
                }
            });
            startBannerScrollTimer();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCastManager = ITApp.getCastManager(this);
        mCastManager.incrementUiCounter();
    }
    
    private void getChannelMap() {
        mDataManager
                .loadChannelMap(new DataManager.IOnloadListener<HashMap<Integer, String>>() {
                    @Override
                    public void onLoad(HashMap<Integer, String> channelMap) {
                        ITApp.setChannelMap(channelMap);
                        download();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCastManager) {
            mCastManager.clearContext(this);
        }
    }

    private void download() {
        mDataManager.loadRecommendChannel(null,
                new DataManager.IOnloadListener<RecommendChannel>() {
                    @Override
                    public void onLoad(RecommendChannel entity) {
                        if (entity == null) return;
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
            // this.mHomeChannelAdapter.setRecommendationOfChannels(this.recommendationOfChannels);
            // this.mHomeChannelAdapter.setOnMediaClickListenenr(this);
            // this.mHomeChannelAdapter.setOnMoreClickListener(this);

        }

        mHomeChannelAdapter.setRecommendationOfChannels(mRecommendationOfChannels);

        mHomeChannelAdapter.setOnMoreClickListener(this);
        mHomeChannelAdapter.setOnMediaClickListenenr(new OnMediaClickListener() {
            
            @Override
            public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(HomeActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo2 = paramMediaView.getMediaInfo();
                intent.putExtra("mediaInfo", mediaInfo2);
                startActivity(intent);
            }
        });
        mHomeChannelListView.setAdapter(mHomeChannelAdapter);
        mHomeChannelAdapter.setGroup(mChannelList);
    }

    private void setMediaAdapter() {
        ArrayList localArrayList = new ArrayList();
        LocalMediaCategoryInfo c = new LocalMediaCategoryInfo();
        
        localArrayList.add(c);
        localArrayList.add(c);
        localArrayList.add(c);
        
        if (mHomeMediaStoreAdapter == null) {
            mHomeMediaStoreAdapter = new HomeMediaStoreAdapter(this);
            mHomeMediaStoreListView.setAdapter(mHomeMediaStoreAdapter);
        }
        mHomeMediaStoreAdapter.setGroup(localArrayList);
        mHomeMediaStoreListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                if (arg2 == 1){
                    Intent intent = new Intent(HomeActivity.this, RecentPlayHistoryActivity.class);
                    startActivity(intent);
                } 
                if (arg2 == 2){
                    Intent intent = new Intent(HomeActivity.this, RecentMyFavouriteActivity.class);
                    startActivity(intent);
                } 
                if (arg2 == 3){
                    Intent intent = new Intent(HomeActivity.this, AdvancedVideoDemo.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
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
        if (view.getId() == R.id.channel_more) {
            Intent intent = new Intent(HomeActivity.this, ChannelActivity.class);
            Channel channel = (Channel) view.getTag();
            intent.putExtra("channel", channel);
            startActivity(intent);

            // overridePendingTransition(R.anim.appear, R.anim.stay_same);
        }
    }

    @MessageResponse
    public final void checkUpdate(AppUpdateEvent event) {
        Upgrade upgrade = getService().getUpgrade();
        upgrade.showChecking(event.isShowChecking());
        upgrade.upgrade();
    }
}
