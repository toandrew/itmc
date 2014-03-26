package com.infthink.itmc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

public class LoadingListView extends FrameLayout {
    private boolean mLoading;
    private Context mContext;
    private ListView mInnerListView;
    private View mLoadingResultView;
    private View mLoadingView;
    
    public LoadingListView(Context context) {
        super(context);
        init();
    }
    
    public LoadingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public LoadingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
        mContext = getContext();
        mInnerListView = new ListView(mContext);
        mInnerListView.setDivider(null);
        mInnerListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mInnerListView.setFadingEdgeLength(0);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mInnerListView.setLayoutParams(fl);
        addView(mInnerListView);
    }

    public ListView getListView() {
        return mInnerListView;
    }

    public View getLoadingResultView() {
        return mLoadingResultView;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public boolean isLoading() {
        return mLoading;
    }
    
    public void setLoadingResultView(View resultView) {
        if (resultView != null && resultView != mLoadingResultView) {
            if (mLoadingResultView != null) {
                removeView(mLoadingResultView);
            }
            mLoadingResultView = resultView;
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            resultView.setLayoutParams(fl);
            resultView.setVisibility(View.INVISIBLE);
            addView(resultView);
        }
    }

    public void setLoadingView(View loadingView) {
        if (loadingView != null && loadingView != mLoadingView) {
            if (mLoadingView != null) {
                removeView(mLoadingView);
            }
            mLoadingView = loadingView;
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            loadingView.setLayoutParams(fl);
            loadingView.setVisibility(View.INVISIBLE);
            addView(loadingView);
        }
    }

    public void setShowLoading(boolean show) {
        mLoading = show;
        if (mLoadingView == null)
            return;

        mLoadingView.setVisibility(mLoading ? View.VISIBLE : View.INVISIBLE);
    }

    public void setShowLoadingResult(boolean show) {
        if (mLoadingResultView == null)
            return;

        mLoadingResultView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
