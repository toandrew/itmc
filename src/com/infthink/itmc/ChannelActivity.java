package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.infthink.itmc.adapter.HomeChannelAdapter;
import com.infthink.itmc.adapter.PosterListAdapter;
import com.infthink.itmc.adapter.RankListAdapter;
import com.infthink.itmc.adapter.ScrollBannerAdapter;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.RankInfo;
import com.infthink.itmc.type.RankInfoList;
import com.infthink.itmc.type.RecommendChannel;
import com.infthink.itmc.type.ShowBaseInfo;
import com.infthink.itmc.widget.BannerIndicator;
import com.infthink.itmc.widget.LoadingListView;
import com.infthink.itmc.widget.MediaView;
import com.infthink.itmc.widget.PagerView;
import com.infthink.itmc.widget.MediaView.OnMediaClickListener;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelActivity extends CoreActivity implements OnPageChangeListener, OnClickListener {
    public static final int PAGE_COUNT = 3;
    public static final int PAGE_HOT = 0;
    public static final int PAGE_LATEST = 2;
    public static final int PAGE_RANK = 1;
    public static final int MSG_UPDATE_CHANNEL_BANNER = 1;
    public static final int MSG_UPDATE_CHANNEL_RANK = 2;
    public static final int MSG_UPDATE_CHANNEL = 3;
    public static final int MSG_UPDATE_CHANNEL_NEW = 4;

    private PagerView mPagerView;
    private int[] mPageNo = new int[3];
    private ArrayList<int[]> mFilterChoices = new ArrayList();
    private boolean mIsManual = false; // 精选
    private RankListAdapter mRankAdapter;
    private ListView[] mListView = new ListView[3];
    private LoadingListView[] mLoadingLv = new LoadingListView[3];
    private View mHeaderView;
    private ViewPager mBannerView;
    private ScrollBannerAdapter mBannerAdapter;
    private BannerIndicator mBannerIndicator;
    private boolean mBannerCountChanged;
    private int mBannerMediaCount;
    private int mBannerViewIndex = -1;
    private Banner[] mBannerMediaList = null;
    private PosterListAdapter[] mPosterAdapter = new PosterListAdapter[3];
    private Channel mChannel;
    private DataManager mDataManager;
    private ArrayList<RankInfo> mRankInfoList = new ArrayList<RankInfo>();
    private ArrayList<Channel> mChannelList = new ArrayList<Channel>();
    private ArrayList<RankInfo> mNewInfoList = new ArrayList<RankInfo>();
    private HashMap<Channel, ShowBaseInfo[]> mRecommendationOfChannels = new HashMap();

    @Override
    protected void onCreateAfterSuper(Bundle paramBundle) {
        super.onCreateAfterSuper(paramBundle);
        setContentView(R.layout.channel_activity);

        mChannel = ((Channel) getIntent().getSerializableExtra("channel"));
        android.util.Log.d("XXXXXXXXXX", "channel = " + mChannel.channelID);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataManager = getService().getDataManager();
                download();
            }
        }, 1000);

        // this.isManual = getIntent().getBooleanExtra("isManual",
        // this.isManual);
        // if (this.channel != null)
        // {
        loadChannelFilter();
        onActivate();
        // }
    }

    private void loadChannelFilter() {
        mIsManual = true;
    }

    private void layoutActionBar(ActionBar bar) {
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
        textView.setText(mChannel.channelName);
        FrameLayout.LayoutParams textLayout =
                new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER);
        frameLayout.addView(textView, textLayout);
        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams imageLayout =
                new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER);
        frameLayout.addView(imageView, imageLayout);
        imageView.setImageResource(R.drawable.clickable_icon_search);
        ActionBar actionBar = getActionBar();
        layoutActionBar(actionBar);
        actionBar.setBackgroundDrawable(null);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBar.LayoutParams lp =
                new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, 21);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(frameLayout, lp);
    }

    private void onActivate() {
        initActionBar();

        View bottomView = View.inflate(this, R.layout.channel_bottom_item, null);
        int statusBarHeight = ITApp.getStatusBarHeight();
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        FrameLayout.LayoutParams fp =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        fp.setMargins(0, statusBarHeight, 0, 0);
        decorView.addView(bottomView, 0, fp);

        mPagerView = ((PagerView) findViewById(R.id.pager_view));
        mPagerView.setTabBackgroudResource(R.drawable.transparent);
        mPagerView.setIndicatorBackgroundResource(R.drawable.page_indicator_arrowbar);
        // mPagerView.setIndicatorBackgroundResource(R.drawable.channel_indicator_arrowbar);
        // mPagerView.setTabTextSize(R.dimen.text_size_32);
        // mPagerView.setOnPageChangedListener(this);
        // mPagerView.getPager().setOnTouchInterceptor(this);
        View[] views = new View[3];
        int margin = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int marginTop = getResources().getDimensionPixelSize(R.dimen.home_banner_margin_top);
        for (int i = 0; i < PAGE_COUNT; i++) {
            mPageNo[i] = i; // or mPageNo[i] = 0;
            mFilterChoices.add(null);
            if (i == PAGE_RANK) {
                mRankAdapter = new RankListAdapter(this);
                // mRankAdapter.setOnRankClickListener(this);
                // mRankAdapter.setOnMediaClickListener(this);
                LoadingListView loadingListView = new LoadingListView(this);
                loadingListView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                loadingListView.setLoadingView(View.inflate(this, R.layout.load_view, null));
                ((TextView) loadingListView.getLoadingView().findViewById(R.id.hint_text))
                        .setText(R.string.loading_video);
                // RetryLoadingView localRetryLoadingView1 = new RetryLoadingView(this);
                // 2 local2 = new RetryLoadingView.OnRetryLoadListener()
                // {
                // public void OnRetryLoad(View paramView)
                // {
                // ChannelActivity.this.loadingLv[1].setShowLoadingResult(false);
                // ChannelActivity.this.request[1] = null;
                // ChannelActivity.this.getRankList();
                // }
                // };
                // localRetryLoadingView1.setOnRetryLoadListener(local2);
                // localLoadingListView1.setLoadingResultView(localRetryLoadingView1);
                ListView listView = loadingListView.getListView();
                listView.setSelector(R.drawable.transparent);
                listView.setDivider(null);
                mLoadingLv[i] = loadingListView;
                // this.retryLoadingView[m] = localRetryLoadingView1;
                mListView[i] = listView;
                listView.setAdapter(mRankAdapter);
                views[i] = loadingListView;
            } else {
                LoadingListView loadingListView =
                        (LoadingListView) View.inflate(this, R.layout.channel_posters_panel, null);
                View loadView = View.inflate(this, R.layout.load_view, null);
                loadingListView.setLoadingView(loadView);
                ((TextView) loadView.findViewById(R.id.hint_text)).setText(R.string.loading_video);


                views[i] = loadingListView;
                ListView listView = loadingListView.getListView();
                mLoadingLv[i] = loadingListView;
                mListView[i] = listView;
                // this.retryLoadingView[m] = localRetryLoadingView2;
                // listView.setLoadMoreView(UIUtil.createMediaLoadMoreView(this));
                // listView.setCanLoadMore(true);
                // listView.setOnLoadMoreListener(this);

                if ((PAGE_HOT == i) && (mIsManual)) {
                    mHeaderView = View.inflate(this, R.layout.banner_view, null);
                    mHeaderView.setPadding(margin, marginTop, margin, margin);
                    mBannerView = ((ViewPager) mHeaderView.findViewById(R.id.banner));
                    // BannerViewPageChangeListener
                    // localBannerViewPageChangeListener = new
                    // BannerViewPageChangeListener();
                    // localBannerViewPageChangeListener.proguardStub();
                    mBannerView.setOnPageChangeListener(this);
                    mBannerAdapter = new ScrollBannerAdapter(this); // , this);
                    // mBannerAdapter.setOnMediaClickListener(this);
                    mBannerView.setAdapter(mBannerAdapter);
                    mBannerIndicator =
                            ((BannerIndicator) mHeaderView.findViewById(R.id.bannerIndicator));
                    mBannerCountChanged = false;
                    mBannerMediaCount = 1; // 0;
                    mBannerViewIndex = -1;
                    if (mBannerMediaList != null) mBannerMediaCount = mBannerMediaList.length;
                    mBannerIndicator.setIndicatorNum(mBannerMediaCount);
                    if (mBannerMediaCount > 0) listView.addHeaderView(mHeaderView);
                    // listView.setOnTouchInterceptor(this);
                }
                PosterListAdapter posterAdapter = new PosterListAdapter(this);
                mPosterAdapter[i] = posterAdapter;
                // mPosterAdapter[i].setOnMediaClickListener(this);
                listView.setAdapter(mPosterAdapter[i]);
            }
        }

        mPagerView.setPageViews(views);
        if (mIsManual) {
            mPagerView.setTabs(getResources().getTextArray(R.array.channel_featured_tabs));
        } else {
            mPagerView.setTabs(getResources().getTextArray(R.array.channel_tabs));
        }
        // Views.ComposedPageChangeListener localComposedPageChangeListener = new
        // Views.ComposedPageChangeListener();
        // localComposedPageChangeListener.add(PageScrollEffects.makePageChangeAdapter(this.pagerView.getPager(),
        // newPageEffectFactory()));
        // mPagerView.setComposedPageChangeListener(localComposedPageChangeListener);
        setCurrentPage(0);
        setAdapter();
    }

    private void setAdapter() {
        // Banner[] banners = new Banner[3];
        // banners[0] = new Banner();
        // banners[1] = new Banner();
        // banners[2] = new Banner();
        // mBannerAdapter.setBannerList(banners);

        // ArrayList localArrayList = new ArrayList();
        // for (int i = 0; i < 5; i++) {
        // RankInfo c = new RankInfo();
        // c.channelID = i;
        // c.channelName = "0";
        // localArrayList.add(c);
        // }
        // mRankAdapter.setGroup(localArrayList);

        // ArrayList a = new ArrayList();
        // for (int i = 0; i < 20; i++) {
        // Object c = new Object();
        // a.add(c);
        // }
        // mPosterAdapter[0].setGroup(a);

        // ArrayList b = new ArrayList();
        // for (int i = 0; i < 50; i++) {
        // Object c = new Object();
        // b.add(c);
        // }
        // mPosterAdapter[2].setGroup(b);
    }

    private void setBannerAdapter() {
        mBannerAdapter.setBannerList(mBannerMediaList);
        mBannerView.setCurrentItem(1);
        mBannerMediaCount = mBannerMediaList.length;
        mBannerIndicator.setIndicatorNum(mBannerMediaCount);
        mBannerIndicator.setCurIndicator(0);
        startBannerScrollTimer();
        mBannerAdapter.setOnMediaClickListener(new OnMediaClickListener() {

            @Override
            public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ChannelActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo = paramMediaView.getMediaInfo();
                intent.putExtra("mediaInfo", mediaInfo);
                startActivity(intent);
            }
        });
    }

    private void setRankAdapter() {
        mRankAdapter.setmRankInfoList(mRankInfoList);
        mRankAdapter.setGroup(mRankInfoList);
        mRankAdapter.setOnMediaClickListener(new OnMediaClickListener() {

            @Override
            public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ChannelActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo = paramMediaView.getMediaInfo();
                intent.putExtra("mediaInfo", mediaInfo);
                startActivity(intent);
            }
        });
    }

    private void setRecommendAdapter() {
        MediaInfo[] medias = (MediaInfo[]) mRecommendationOfChannels.get(mChannelList.get(0));
        mPosterAdapter[0].setGroup(medias);
        mPosterAdapter[0].setOnMediaClickListener(new OnMediaClickListener() {

            @Override
            public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ChannelActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo = paramMediaView.getMediaInfo();
                intent.putExtra("mediaInfo", mediaInfo);
                startActivity(intent);
            }
        });
        // mRankAdapter.setmRankInfoList(mRankInfoList);
        // mRankAdapter.setGroup(mRankInfoList);
    }

    private void setNewChannelAdapter() {
        // for (int i = 0; i < mNewInfoList.size(); i++){
        RankInfo rankInfo = mNewInfoList.get(0);
        MediaInfo[] medias = rankInfo.mediaInfos;
        mPosterAdapter[2].setGroup(medias);
        mPosterAdapter[2].setOnMediaClickListener(new OnMediaClickListener() {

            @Override
            public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ChannelActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo = paramMediaView.getMediaInfo();
                intent.putExtra("mediaInfo", mediaInfo);
                startActivity(intent);
            }
        });
        // }

    }

    // private void getChannelMapByChannelId() {
    // mDataManager.loadChannelMap(new DataManager.IOnloadListener<HashMap<Integer, String>>() {
    // @Override
    // public void onLoad(HashMap<Integer, String> channelMap) {
    // ITApp.setChannelMap(channelMap);
    // download();
    // }
    // });
    // }

    private void download() {
        String channelId = mChannel.channelID + "";
        mDataManager.loadBanner(channelId, new DataManager.IOnloadListener<Banner[]>() {

            @Override
            public void onLoad(Banner[] entity) {
                mBannerMediaList = entity;
                mHandler.sendEmptyMessage(MSG_UPDATE_CHANNEL_BANNER);
            }
        });

        mDataManager.loadRecommendChannel(channelId,
                new DataManager.IOnloadListener<RecommendChannel>() {
                    @Override
                    public void onLoad(RecommendChannel entity) {
                        mChannelList = (ArrayList<Channel>) entity.channelList;
                        mRecommendationOfChannels = entity.recommend;
                        mHandler.sendEmptyMessage(MSG_UPDATE_CHANNEL);
                    }
                });

        mDataManager.loadChannelRank(channelId, 1, 3, 7,
                new DataManager.IOnloadListener<RankInfoList>() {

                    @Override
                    public void onLoad(RankInfoList entity) {
                        // TODO Auto-generated method stub
                        android.util.Log.d("XXXXXXXXXX", "download loadChannelRank = "
                                + entity.ranks[0].mediaInfos[0].mediaName);
                        RankInfo[] ranks = entity.ranks;
                        ArrayList<RankInfo> localArrayList = new ArrayList<RankInfo>();
                        if (ranks != null) {
                            for (int i = 0; i < ranks.length; i++) {
                                localArrayList.add(ranks[i]);
                            }
                        }
                        mRankInfoList = localArrayList;
                        mHandler.sendEmptyMessage(MSG_UPDATE_CHANNEL_RANK);
                    };

                });

        mDataManager.loadChannelRank(channelId, 1, 60, 1,
                new DataManager.IOnloadListener<RankInfoList>() {

                    @Override
                    public void onLoad(RankInfoList entity) {
                        // TODO Auto-generated method stub
                        RankInfo[] ranks = entity.ranks;
                        ArrayList<RankInfo> localArrayList = new ArrayList<RankInfo>();
                        if (ranks != null) {
                            for (int i = 0; i < ranks.length; i++) {
                                localArrayList.add(ranks[i]);
                            }
                        }
                        mNewInfoList = localArrayList;
                        mHandler.sendEmptyMessage(MSG_UPDATE_CHANNEL_NEW);
                    };

                });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_CHANNEL_BANNER:
                    setBannerAdapter();
                    break;
                case MSG_UPDATE_CHANNEL_RANK:
                    setRankAdapter();
                    break;
                case MSG_UPDATE_CHANNEL:
                    setRecommendAdapter();
                    break;
                case MSG_UPDATE_CHANNEL_NEW:
                    setNewChannelAdapter();
                    break;
            }
        }
    };

    public void setCurrentPage(int position) {
        mPagerView.setCurPage(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }
}
