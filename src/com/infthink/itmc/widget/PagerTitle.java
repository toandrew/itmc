package com.infthink.itmc.widget;

import java.util.ArrayList;

import com.infthink.itmc.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.Scroller;
import android.widget.TextView;
import android.view.ViewTreeObserver;

public class PagerTitle extends FrameLayout implements
        ViewTreeObserver.OnGlobalLayoutListener, OnClickListener {

    public static final String TAG = PagerTitle.class.getName();
    private boolean mIndicatorScrollEnabled = true;
    private boolean mStartScroll;
    private Context mContext;
    private int mCurTab = 0;
    private View mIndicator = null;
    private float mLastMotionX = 0.0F;
    // public OnPageIndicatorMoveListener onPageIndicatorMoveListener;
    private OnPagerTitleListener mOnPagerTitleListener;
    // public OnTabTouchListener onTabTouchListener;
    private int mPageArrowBarHeight;
    private int mPageArrowBarWidth;
    private Scroller mScroller;
    private int mStartPixel = -1;
    private int mTabBgResId;
    private int mTabFocusColor;
    private LinearLayout mTabGroup;
    private int mTabNormalColor;
    private int mTabTextSize;
    private ArrayList<TextView> mTabViews = new ArrayList<TextView>();
    private CharSequence[] mTabs;
    private OnTabClickedListener mOnTabClickedListener;
    
    public PagerTitle(Context context) {
        super(context);
        init();
    }

    public PagerTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        
        mContext = getContext();
        mTabBgResId = R.drawable.pager_indicator_bar;
        Resources resources = mContext.getResources();
        mTabTextSize = resources.getDimensionPixelSize(R.dimen.text_size_32);
        mTabNormalColor = resources.getColor(R.color.half_white);
        mTabFocusColor = resources.getColor(R.color.white);
        mScroller = new Scroller(mContext);
        mTabGroup = new LinearLayout(mContext);
        FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.pager_title_height));
        mTabGroup.setLayoutParams(localLayoutParams1);
        addView(mTabGroup);
        mPageArrowBarWidth = resources
                .getDimensionPixelSize(R.dimen.page_indicator_arrowbar_width);
        mPageArrowBarHeight = resources
                .getDimensionPixelSize(R.dimen.page_indicator_arrowbar_height);
        mIndicator = new View(mContext);
        mIndicator.setBackgroundResource(R.drawable.transparent);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                mPageArrowBarWidth, mPageArrowBarHeight);
        lp.gravity = Gravity.BOTTOM;
        mIndicator.setLayoutParams(lp);
        addView(mIndicator);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void initTabs() {
        if (mTabs != null) {
            mTabGroup.removeAllViews();
            mTabViews.clear();
            mCurTab = 0;
            Resources resources = mContext.getResources();
            int bottomPadding = resources
                    .getDimensionPixelSize(R.dimen.pager_title_padding_bottom);
            int shadowColor = resources.getColor(R.color.text_shadow_color);
            int shadowXOffset = resources
                    .getInteger(R.integer.tab_text_shadow_dx);
            int shadowYOffset = resources
                    .getInteger(R.integer.tab_text_shadow_dy);
            int shadowRadius = resources
                    .getInteger(R.integer.tab_text_shadow_radius);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.weight = 1.0F;
            for (int i = 0; i < mTabs.length; i++) {
                TextView textView = new TextView(mContext);
                textView.setClickable(true);
                textView.setOnClickListener(this);
                // textView.setOnTouchListener(this);
                textView.setBackgroundResource(mTabBgResId);
                mTabViews.add(textView);
                mTabGroup.addView(textView);
                textView.setLayoutParams(lp);
                textView.setPadding(0, 0, 0, bottomPadding);
                textView.setGravity(Gravity.CENTER);
                textView.setText(mTabs[i]);
                textView.getPaint().setFakeBoldText(true);
                textView.setTextSize(0, mTabTextSize);
                textView.setShadowLayer(shadowRadius, shadowXOffset,
                        shadowYOffset, shadowColor);
                if (i == 0) {
                    textView.setTextColor(mTabFocusColor);
                } else {
                    textView.setTextColor(mTabNormalColor);
                }
            }
        }
    }

    public void setTabs(CharSequence[] tabs) {
        mTabs = tabs;
        initTabs();
        setCurrentTab(0);
        if(mTabs.length == 0){
            mIndicator.setBackgroundResource(R.drawable.transparent);
        } 
    }

    @Override
    public void onGlobalLayout() {
        if (mTabViews.size() > 0) {
            int indicatorCenterX = mIndicator.getLeft() + mPageArrowBarWidth
                    / 2;
            TextView curTextView = (TextView) mTabViews.get(mCurTab);
            int initialTabCenterX = curTextView.getLeft()
                    + curTextView.getWidth() / 2;
            if ((indicatorCenterX != initialTabCenterX) && (!mStartScroll))
                mIndicator.offsetLeftAndRight(initialTabCenterX
                        - indicatorCenterX);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float deltaX = mScroller.getCurrX() - mLastMotionX;
            if (Math.abs(deltaX) >= 1.0f) {
                mLastMotionX = mScroller.getCurrX();
                mIndicator.offsetLeftAndRight((int) deltaX);
            }
            invalidate();
        } else {
            mStartScroll = false;
            // if (this.onPageIndicatorMoveListener != null)
            // this.onPageIndicatorMoveListener.onPageIndicatorMoveStop(this.curTab);
            mLastMotionX = mIndicator.getLeft() + mPageArrowBarWidth / 2;
            if (mCurTab < 0 || mCurTab > mTabViews.size())
                return;
            for (int i = 0; i < mTabViews.size(); i++) {
                TextView textView = (TextView) mTabViews.get(i);
                if (i != mCurTab) {
                    textView.setTextColor(mTabNormalColor);
                } else {
                    textView.setTextColor(mTabFocusColor);
                }
            }
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            mStartPixel = -1;
            if (!mScroller.computeScrollOffset() && mCurTab >= 0
                    && mCurTab < mTabViews.size()) {
                TextView textView = (TextView) mTabViews.get(mCurTab);
                int tabViewCenterX = textView.getLeft() + textView.getWidth()
                        / 2;
                int indicatorCenterX = mIndicator.getLeft()
                        + mPageArrowBarWidth / 2;
                if (tabViewCenterX != indicatorCenterX) {
                    if (mIndicatorScrollEnabled) {
                        mStartScroll = true;
                        mLastMotionX = indicatorCenterX;
                        mScroller.startScroll(indicatorCenterX, 0,
                                tabViewCenterX - indicatorCenterX, 0);
                        invalidate();
                    } else {
                        // (this.onPageIndicatorMoveListener != null)
                        // this.onPageIndicatorMoveListener.onPageIndicatorMoveStop(this.curTab);
                        mIndicator.offsetLeftAndRight(tabViewCenterX
                                - indicatorCenterX);
                    }
                }
            }
        }
    }

    public void onPageScrolled(int tab, int offsetPixels) {
        if (!mScroller.computeScrollOffset() && tab >= 0
                && tab < mTabViews.size()) {
            if (offsetPixels != 0) {
                if (mIndicatorScrollEnabled) {
                    if (mStartPixel != -1) {
                        TextView curTextView = mTabViews.get(tab);
                        int curTabViewCenterX = curTextView.getLeft()
                                + curTextView.getWidth() / 2;
                        int oldCenterX = mIndicator.getLeft()
                                + mPageArrowBarWidth / 2;
                        int targetCenterX = oldCenterX;
                        int percent = 0;
                        if ((offsetPixels - mStartPixel) >= 0) { // 0 to 1
                            if (tab < mTabViews.size() - 1) {
                                TextView nextTextView = mTabViews.get(tab + 1);
                                int nextTabViewCenterX = nextTextView.getLeft()
                                        + nextTextView.getWidth() / 2;
                                percent = (int) ((offsetPixels - mStartPixel)
                                        * (nextTabViewCenterX - curTabViewCenterX) / getWidth());
                                targetCenterX = nextTabViewCenterX;
                            }
                        } else { // 1 to 0
                            if (tab < mTabViews.size() - 1) {
                                TextView nextTextView = mTabViews.get(tab + 1);
                                int nextTabViewCenterX = nextTextView.getLeft()
                                        + nextTextView.getWidth() / 2;
                                percent = (int) ((mStartPixel - offsetPixels)
                                        * (curTabViewCenterX - nextTabViewCenterX) / getWidth());
                                targetCenterX = curTabViewCenterX;
                            }
                        }
                        if (percent > 0) {
                            percent = Math.min(targetCenterX - oldCenterX,
                                    percent);
                        } else if (percent < 0) {
                            percent = Math.max(targetCenterX - oldCenterX,
                                    percent);
                        }
                        mIndicator.offsetLeftAndRight(percent);
                    } else {

                    }
                    mStartPixel = offsetPixels;
                }
            }

        }
    }

    public void setCurrentTab(int position) {
        if (position >= 0 && position < mTabViews.size()
                && (mCurTab != position)) {
            // if (this.onPageIndicatorMoveListener != null)
            // this.onPageIndicatorMoveListener.onPageIndicatorMoveStart(this.curTab);
            mCurTab = position;
            TextView textView = (TextView) mTabViews.get(position);
            int tabViewCenterX = textView.getLeft() + textView.getWidth() / 2;
            int indicatorCenterX = mIndicator.getLeft() + mPageArrowBarWidth
                    / 2;
            if (tabViewCenterX != indicatorCenterX) {
                if (mIndicatorScrollEnabled) {
                    mStartScroll = true;
                    mLastMotionX = indicatorCenterX;
                    mScroller.startScroll(indicatorCenterX, 0, tabViewCenterX
                            - indicatorCenterX, 0);
                    invalidate();
                } else {
                    // if (this.onPageIndicatorMoveListener != null)
                    // this.onPageIndicatorMoveListener.onPageIndicatorMoveStop(this.curTab);
                    mIndicator.offsetLeftAndRight(tabViewCenterX
                            - indicatorCenterX);
                }
            }
            if (mOnPagerTitleListener != null) {
                mOnPagerTitleListener.onPageTitleSelected(this, mCurTab);
            }
        }
    }

    public void setTabBackgroudResource(int resource) {
        mTabBgResId = resource;
    }

    public void setOnTabClickedListener(OnTabClickedListener onTabClickedListener) {
        mOnTabClickedListener = onTabClickedListener;
    }
    
    public void setOnPagerTitleListener(OnPagerTitleListener onPagerTitleListener) {
        mOnPagerTitleListener = onPagerTitleListener;
    }
    
    public void setIndicatorBackgroundResource(int paramInt)
    {
      this.mIndicator.setBackgroundResource(paramInt);
    }

    public void setIndicatorBackgroundResource(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mIndicator.setBackgroundResource(paramInt1);
      this.mPageArrowBarWidth = paramInt2;
      this.mPageArrowBarHeight = paramInt3;
      FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(this.mPageArrowBarWidth, this.mPageArrowBarHeight);
      localLayoutParams.gravity = 80;
      this.mIndicator.setLayoutParams(localLayoutParams);
      requestLayout();
    }

    @Override
    public void onClick(View view) {
        int position = mTabViews.indexOf(view);
        setCurrentTab(position);
        if (mOnTabClickedListener != null) {
            mOnTabClickedListener.onTabClicked(position);
        }
    }

    public static abstract interface OnTabClickedListener {
        public abstract void onTabClicked(int position);
    }
    
    public static abstract interface OnPagerTitleListener {
        public abstract void onPageTitleSelected(PagerTitle pagerTitle,
                int position);
    }
}
