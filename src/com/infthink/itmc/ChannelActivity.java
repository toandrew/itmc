package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.infthink.itmc.adapter.PosterListAdapter;
import com.infthink.itmc.adapter.RankListAdapter;
import com.infthink.itmc.adapter.ScrollBannerAdapter;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.RankInfo;
import com.infthink.itmc.widget.BannerIndicator;
import com.infthink.itmc.widget.LoadingListView;
import com.infthink.itmc.widget.PagerView;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelActivity extends CoreActivity {
    public static final int PAGE_COUNT = 3;
    public static final int PAGE_HOT = 0;
    public static final int PAGE_LATEST = 2;
    public static final int PAGE_RANK = 1;
    
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
    
    @Override
    protected void onCreateAfterSuper(Bundle paramBundle) {
        super.onCreateAfterSuper(paramBundle);
        setContentView(R.layout.channel_activity);
        
        mChannel = ((Channel) getIntent().getSerializableExtra("channel"));
        android.util.Log.d("XXXXXXXXXX", "channel = " + mChannel.channelID);
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
        textView.setText(mChannel.channelName);
        FrameLayout.LayoutParams textLayout = new FrameLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER);
        frameLayout.addView(textView, textLayout);
        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams imageLayout = new FrameLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER);
        frameLayout.addView(imageView, imageLayout);
        imageView.setImageResource(R.drawable.clickable_icon_search);
        ActionBar actionBar = getActionBar();
        cust(actionBar);
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

        View bottomView = View.inflate(this, R.layout.channel_bottom_item, null);
        int statusBarHeight = ITApp.getStatusBarHeight();
        ViewGroup decorView = (ViewGroup)getWindow().getDecorView();
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fp.setMargins(0, statusBarHeight, 0, 0);
        decorView.addView(bottomView, 0, fp);

        mPagerView = ((PagerView)findViewById(R.id.pager_view));
//        mPagerView.setIndicatorBackgroundResource(R.drawable.channel_indicator_arrowbar);
//        mPagerView.setTabTextSize(R.dimen.text_size_32);
//        mPagerView.setOnPageChangedListener(this);
//        mPagerView.getPager().setOnTouchInterceptor(this);
        View[] views = new View[3];
        int margin = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int marginTop = getResources().getDimensionPixelSize(R.dimen.home_banner_margin_top);
        for (int i = 0; i < PAGE_COUNT; i++) {
            mPageNo[i] = i; // or mPageNo[i] = 0;
            mFilterChoices.add(null);
            if (i == PAGE_RANK) {
                mRankAdapter = new RankListAdapter(this);
//                mRankAdapter.setOnRankClickListener(this);
//                mRankAdapter.setOnMediaClickListener(this);
                LoadingListView loadingListView = new LoadingListView(this);
                loadingListView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                loadingListView.setLoadingView(View.inflate(this, R.layout.load_view, null));
                ((TextView)loadingListView.getLoadingView().findViewById(R.id.hint_text)).setText(R.string.loading_video);
//                RetryLoadingView localRetryLoadingView1 = new RetryLoadingView(this);
//                2 local2 = new RetryLoadingView.OnRetryLoadListener()
//                {
//                  public void OnRetryLoad(View paramView)
//                  {
//                    ChannelActivity.this.loadingLv[1].setShowLoadingResult(false);
//                    ChannelActivity.this.request[1] = null;
//                    ChannelActivity.this.getRankList();
//                  }
//                };
//                localRetryLoadingView1.setOnRetryLoadListener(local2);
//                localLoadingListView1.setLoadingResultView(localRetryLoadingView1);
                ListView listView = loadingListView.getListView();
                listView.setSelector(R.drawable.transparent);
                listView.setDivider(null);
                mLoadingLv[i] = loadingListView;
//                this.retryLoadingView[m] = localRetryLoadingView1;
                mListView[i] = listView;
                listView.setAdapter(mRankAdapter);
                views[i] = loadingListView;
            } else {
                LoadingListView loadingListView = (LoadingListView)View.inflate(this, R.layout.channel_posters_panel, null);
                View loadView = View.inflate(this, R.layout.load_view, null);
                loadingListView.setLoadingView(loadView);
                ((TextView)loadView.findViewById(R.id.hint_text)).setText(R.string.loading_video);
                
                
                views[i] = loadingListView;
                ListView listView = loadingListView.getListView();
                mLoadingLv[i] = loadingListView;
                mListView[i] = listView;
//                this.retryLoadingView[m] = localRetryLoadingView2;
//                listView.setLoadMoreView(UIUtil.createMediaLoadMoreView(this));
//                listView.setCanLoadMore(true);
//                listView.setOnLoadMoreListener(this);

                if ((PAGE_HOT == i) && (mIsManual)) {
                    mHeaderView = View
                            .inflate(this, R.layout.banner_view, null);
                    mHeaderView.setPadding(margin, marginTop, margin, margin);
                    mBannerView = ((ViewPager) mHeaderView
                            .findViewById(R.id.banner));
                    // BannerViewPageChangeListener
                    // localBannerViewPageChangeListener = new
                    // BannerViewPageChangeListener();
                    // localBannerViewPageChangeListener.proguardStub();
                    // mBannerView.setOnPageChangeListener(localBannerViewPageChangeListener);
                    mBannerAdapter = new ScrollBannerAdapter(this); // , this);
                    // mBannerAdapter.setOnMediaClickListener(this);
                    mBannerView.setAdapter(mBannerAdapter);
                    mBannerIndicator = ((BannerIndicator) mHeaderView
                            .findViewById(R.id.bannerIndicator));
                    mBannerCountChanged = false;
                    mBannerMediaCount = 3; // 0;
                    mBannerViewIndex = -1;
                    if (mBannerMediaList != null)
                        mBannerMediaCount = mBannerMediaList.length;
                    mBannerIndicator.setIndicatorNum(mBannerMediaCount);
                    if (mBannerMediaCount > 0)
                        listView.addHeaderView(mHeaderView);
                    // listView.setOnTouchInterceptor(this);
                }
                PosterListAdapter posterAdapter = new PosterListAdapter(this);
                mPosterAdapter[i] = posterAdapter;
//                mPosterAdapter[i].setOnMediaClickListener(this);
                listView.setAdapter(mPosterAdapter[i]);
            }
        }
        
        mPagerView.setPageViews(views);
        if (mIsManual) {
            mPagerView.setTabs(getResources().getTextArray(R.array.channel_featured_tabs));
        } else {
            mPagerView.setTabs(getResources().getTextArray(R.array.channel_tabs));
        }
//        Views.ComposedPageChangeListener localComposedPageChangeListener = new Views.ComposedPageChangeListener();
//        localComposedPageChangeListener.add(PageScrollEffects.makePageChangeAdapter(this.pagerView.getPager(), newPageEffectFactory()));
//        mPagerView.setComposedPageChangeListener(localComposedPageChangeListener);
        setCurrentPage(0);
        setAdapter();
    }

    private void setAdapter() {
        Banner[] banners = new Banner[3];
        banners[0] = new Banner();
        banners[1] = new Banner();
        banners[2] = new Banner();
        mBannerAdapter.setBannerList(banners);
        
        ArrayList localArrayList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            RankInfo c = new RankInfo();
            c.channelID = i;
            c.channelName = "0";
            localArrayList.add(c);
        }
        mRankAdapter.setGroup(localArrayList);
        
        ArrayList a = new ArrayList();
        for (int i = 0; i < 20; i++) {
            Object c = new Object();
            a.add(c);
        }
        mPosterAdapter[0].setGroup(a);
        
        ArrayList b = new ArrayList();
        for (int i = 0; i < 50; i++) {
            Object c = new Object();
            b.add(c);
        }
        mPosterAdapter[2].setGroup(b);
    }
    
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
}
