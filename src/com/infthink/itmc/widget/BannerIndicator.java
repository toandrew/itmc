package com.infthink.itmc.widget;

import com.infthink.itmc.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class BannerIndicator extends LinearLayout {

    public static final String TAG = BannerIndicator.class.getName();
    private Context mContext;
    private int mCurIndicatorIndex = 1;
    private int mIndicatorNum;
    private View[] mIndicators;
    
    public BannerIndicator(Context context) {
        super(context);
    }
    
    public BannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public BannerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        if (mIndicatorNum > 1) {
            if (mIndicators != null) {
                removeAllViews();
                mIndicators = null;
                mCurIndicatorIndex = 1;
            }

            mContext = getContext();
            Resources resources = mContext.getResources();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.banner_indicator_view_width),
                    resources.getDimensionPixelSize(R.dimen.banner_indicator_view_height));
            mIndicators = new View[mIndicatorNum];
            for (int i = 0; i < mIndicatorNum; i++) {
                View view = new View(mContext);
                view.setLayoutParams(lp);
                view.setBackgroundResource(R.drawable.banner_indicator_normal);
                addView(view);
                mIndicators[i] = view;
            }
        }
    }

    public void setCurIndicator(int position) {
        if (mIndicators == null || position < 0 || position >= mIndicatorNum)
            return;
        if (mCurIndicatorIndex != position) {
            mIndicators[mCurIndicatorIndex]
                    .setBackgroundResource(R.drawable.banner_indicator_normal);
            mIndicators[position]
                    .setBackgroundResource(R.drawable.banner_indicator_highlight);
            mCurIndicatorIndex = position;
        }

    }

    public void setIndicatorNum(int size) {
        mIndicatorNum = size;
        init();
    }
}
