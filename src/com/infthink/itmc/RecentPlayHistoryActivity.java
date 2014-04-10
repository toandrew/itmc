package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.List;

import com.infthink.itmc.adapter.PlayHistoryAdapter;
import com.infthink.itmc.data.LocalPlayHistoryInfoManager;
import com.infthink.itmc.type.BaseLocalPlayHistory;
import com.infthink.itmc.type.LocalPlayHistory;
import com.infthink.itmc.widget.LoadingListView;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class RecentPlayHistoryActivity extends CoreActivity {
    private Button mClearBtn;
    private LoadingListView mLoadingListView;
    private ListView mPlayHistoryListView;
    private PlayHistoryAdapter mPlayHistoryAdapter;
    private List<LocalPlayHistory> mCurLocalPlayHistoryList;

    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);
        setContentView(R.layout.playhistory_activity);
        onActivate();
        loadPlayHistory();
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
        textView.setText("最近播放");
        FrameLayout.LayoutParams textLayout =
                new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER);
        frameLayout.addView(textView, textLayout);
        mClearBtn = new Button(this);
        mClearBtn.setBackgroundResource(R.drawable.btn_clearhistory);
        FrameLayout.LayoutParams imageLayout =
                new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER);
        frameLayout.addView(mClearBtn, imageLayout);
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
        
        mLoadingListView = ((LoadingListView)findViewById(R.id.lv_playhistory));
        mPlayHistoryListView = mLoadingListView.getListView();
        mPlayHistoryListView.setSelector(R.drawable.clickable_item_bg_part);
//        mPlayHistoryListView.setOnScrollListener(this);
//        mPlayHistoryListView.setOnItemClickListener(this);
        
//        VideoThumbCacheManager.getInstance().clearPendingVideoThumbTask(false);
//        this.mTvInfoSparseArray = new SparseArray();
//        this.mTvPlayManager = new TelevisionPlayManager(getApplicationContext());
//        this.mTvUpdateManager = TelevisionUpdateManager.getInstance();
//        this.mTvUpdateManager.registerListeners(this);
        mPlayHistoryAdapter = new PlayHistoryAdapter(this);
        mPlayHistoryListView.setAdapter(mPlayHistoryAdapter);
    }

    private void loadPlayHistory() {
        LocalPlayHistoryInfoManager localHistory = LocalPlayHistoryInfoManager.getInstance();
        mCurLocalPlayHistoryList = localHistory.getHistoryVideos(this);
        if (mCurLocalPlayHistoryList != null) {
            mPlayHistoryAdapter.setGroup(mCurLocalPlayHistoryList);
        }
    }
}
