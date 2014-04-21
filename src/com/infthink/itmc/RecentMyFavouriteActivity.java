package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.List;

import com.infthink.itmc.adapter.MyFavouriteAdapter;
import com.infthink.itmc.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.data.LocalPlayHistoryInfoManager;
import com.infthink.itmc.type.LocalMyFavoriteItemInfo;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.widget.LoadingListView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;


public class RecentMyFavouriteActivity extends CoreActivity {
    private Button mClearBtn;
    private LoadingListView mLoadingListView;
    private ListView mPlayHistoryListView;
    private MyFavouriteAdapter mMyfavouriteAdapter;
    private List<LocalMyFavoriteItemInfo> mCurLocalMyFavList;

    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);
        setContentView(R.layout.playhistory_activity);
        onActivate();
//        loadFav();
    }
    @Override
    public void onResume() {
        loadFav();
        super.onResume();
     }

    private void layoutActionBar(ActionBar bar) {
        try {
            Class<?> actionBarImpl = Class
                    .forName("com.android.internal.app.ActionBarImpl");
            Class<?> actionBarView = Class
                    .forName("com.android.internal.widget.ActionBarView");

            Field actionView = actionBarImpl.getDeclaredField("mActionView");
            actionView.setAccessible(true);
            Object objActionView = actionView.get(bar);

            Field fHomeLayout = actionBarView.getDeclaredField("mHomeLayout");
            fHomeLayout.setAccessible(true);
            FrameLayout objHomeLayout = (FrameLayout) fHomeLayout
                    .get(objActionView);
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
        textView.setText("收藏");
        FrameLayout.LayoutParams textLayout = new FrameLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT
                        | Gravity.CENTER);
        frameLayout.addView(textView, textLayout);
//        mClearBtn = new Button(this);
//        mClearBtn.setBackgroundResource(R.drawable.btn_clearhistory);
//        mClearBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                if (mCurLocalMyFavList != null) {
//                    LocalPlayHistoryInfoManager.getInstance(getContext()).clearHistory(getContext());
//                    mCurLocalMyFavList.clear();
//                    mMyfavouriteAdapter.setGroup(mCurLocalMyFavList);
//                    showNoPlayHistoryTip(true);
//                }
//            }
//        });
//        FrameLayout.LayoutParams imageLayout = new FrameLayout.LayoutParams(
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
//                        | Gravity.CENTER);
//        frameLayout.addView(mClearBtn, imageLayout);
        ActionBar actionBar = getActionBar();
        layoutActionBar(actionBar);
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

        mLoadingListView = ((LoadingListView) findViewById(R.id.lv_playhistory));
        mPlayHistoryListView = mLoadingListView.getListView();
        mPlayHistoryListView.setSelector(R.drawable.clickable_item_bg_part);
        // mPlayHistoryListView.setOnScrollListener(this);
        mPlayHistoryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                    long id) {
                LocalMyFavoriteItemInfo history = mCurLocalMyFavList.get(position);
                Intent intent = new Intent(RecentMyFavouriteActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo = history.mediaInfo;
                intent.putExtra("mediaInfo", mediaInfo);
                startActivity(intent);
//                intent.putExtra("media_id", history.mediaId);
//                intent.putExtra("pageUrl", history.html5Page);
//                intent.putExtra("source", history.mediaSource);
//                intent.putExtra("meidaTitle", history.videoName);
//                intent.putExtra("available_episode_count", history.mediaCi);
//                intent.putExtra("current_episode", history.mediaCi);
//                startActivity(intent);
            }
         });

        // VideoThumbCacheManager.getInstance().clearPendingVideoThumbTask(false);
        // this.mTvInfoSparseArray = new SparseArray();
        // this.mTvPlayManager = new
        // TelevisionPlayManager(getApplicationContext());
        // this.mTvUpdateManager = TelevisionUpdateManager.getInstance();
        // this.mTvUpdateManager.registerListeners(this);
        mMyfavouriteAdapter = new MyFavouriteAdapter(this);
        mPlayHistoryListView.setAdapter(mMyfavouriteAdapter);
    }

    private void loadFav() {
        LocalMyFavoriteInfoManager mLocalLocalMyFavoriteInfo = LocalMyFavoriteInfoManager.getInstance(this);
        mCurLocalMyFavList = mLocalLocalMyFavoriteInfo.getFavVideos(this);
        if (mCurLocalMyFavList != null && mCurLocalMyFavList.size() > 0) {
            mMyfavouriteAdapter.setGroup(mCurLocalMyFavList);
            showNoPlayHistoryTip(false);
        } else {
            showNoPlayHistoryTip(true);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Intent intent = new Intent(this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNoPlayHistoryTip(boolean show) {
        TextView view = (TextView) findViewById(R.id.tv_noplayhistorytip);
        if (show) {
//            mClearBtn.setVisibility(4);
            view.setText("收藏记录为空");
            view.setVisibility(0);
            return;
        }
//        mClearBtn.setVisibility(0);
        view.setVisibility(4);
    }
}
