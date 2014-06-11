package com.infthink.itmc.v2.widget;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.v2.R;
import com.infthink.itmc.v2.adapter.ViewPagerAdapter;
import com.infthink.itmc.v2.util.BitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PagerView extends LinearLayout implements OnPageChangeListener,
        PagerTitle.OnTabClickedListener, PagerTitle.OnPagerTitleListener {

    private Context mContext;
    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;
    private int mCurPage = 0;
    private PagerTitle mPagerTitle;

    public PagerView(Context context) {
        super(context);
        init();
    }

    public PagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mContext = this.getContext();
        setOrientation(LinearLayout.VERTICAL);

        mPagerTitle = new PagerTitle(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = mContext.getResources().getDimensionPixelSize(
                R.dimen.page_margin);
        mPagerTitle.setLayoutParams(lp);
        mPagerTitle.setOnTabClickedListener(this);
        mPagerTitle.setOnPagerTitleListener(this);
        addView(mPagerTitle);

        mViewPager = new ViewPager(mContext);
        // mViewPager.setBackgroundResource(2130837800);
        mViewPager.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mViewPager.setVerticalScrollBarEnabled(false);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setPageMargin(margin);
        addView(mViewPager);
        mPagerAdapter = new ViewPagerAdapter(mContext);
        mViewPager.setAdapter(mPagerAdapter);
    }

    public void setCurPage(int position) {
        if (mCurPage != position) {
            mCurPage = position;
            // this.pagerTitle.setCurrentTab(paramInt);
            mViewPager.setCurrentItem(position, true);
        }
        // if (this.onPageChangedListener != null)
        // this.onPageChangedListener.onPageSelected(paramInt);
    }

    public void setPageViews(List<View> list) {
        mPagerAdapter.setPages(list);
    }

    public void setPageViews(View[] views) {
        mPagerAdapter.setPages(views);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        mPagerTitle.onPageScrollStateChanged(arg0);
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        mPagerTitle.onPageScrolled(arg0, arg2);
    }

    @Override
    public void onPageTitleSelected(PagerTitle pagerTitle, int position) {
        setCurPage(position);
    }

    @Override
    public void onPageSelected(int arg0) {
        mPagerTitle.setCurrentTab(arg0);
        // mCurPage = arg0;
    }

    public void setTabs(CharSequence[] tabs) {
        mPagerTitle.setTabs(tabs);
    }

    public void setTabBackgroudResource(int resource) {
        mPagerTitle.setTabBackgroudResource(resource);
    }

    @Override
    public void onTabClicked(int position) {
        setCurPage(position);
    }

    public void setIndicatorBackgroundResource(int paramInt) {
        this.mPagerTitle.setIndicatorBackgroundResource(paramInt);
    }

    public void setIndicatorBackgroundResource(int paramInt1, int paramInt2,
            int paramInt3) {
        this.mPagerTitle.setIndicatorBackgroundResource(paramInt1, paramInt2,
                paramInt3);
    }
}
