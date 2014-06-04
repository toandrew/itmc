package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.infthink.itmc.adapter.PosterListAdapter;
import com.infthink.itmc.adapter.RankListAdapter.OnRankClickListener;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.RankInfo;
import com.infthink.itmc.type.RankInfoList;
import com.infthink.itmc.widget.MediaView;
import com.infthink.itmc.widget.MediaView.OnMediaClickListener;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class RankMediaActivity extends CoreActivity implements OnClickListener {

    private PosterListAdapter adapter;
    private Channel channelInfo;
    private int mPageCurr = 0;

    private DataManager mDataManager;
    private RankInfo mRankInfo;
    private PosterListAdapter mPosterAdapter;

    // private ArrayList<RankInfo> mNewInfoList = new ArrayList<RankInfo>();
    public static final int MSG_UPDATE_RANK_MEDIA = 1;
    private ArrayList<RankInfo> mRankInfoList = new ArrayList<RankInfo>();
    
    private static final String TAG = RankMediaActivity.class.getSimpleName();

    @Override
    protected void onInitialized() {
        android.util.Log.d(TAG, "onInitialized");
        mDataManager = getService().getDataManager();
        download();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_RANK_MEDIA:
                    setRankAdapter();
                    break;
            }
        }
    };

    @Override
    protected void onCreateAfterSuper(Bundle paramBundle) {
        super.onCreateAfterSuper(paramBundle);
        setContentView(R.layout.rank_media_activity);
        mRankInfo = ((RankInfo) getIntent().getSerializableExtra("rankinfo"));

        android.util.Log.d(TAG, "channel.channelID = " + mRankInfo.channelID);

        onActivate();
    }

    private void download() {

        String channelId = mRankInfo.channelID + "";
        mDataManager.loadChannelRank(channelId, 1, 60, 1,
                new DataManager.IOnloadListener<RankInfoList>() {

                    @Override
                    public void onLoad(RankInfoList entity) {
                        // TODO Auto-generated method stub
                        if (entity == null) return;
                        RankInfo[] ranks = entity.ranks;
                        ArrayList<RankInfo> localArrayList = new ArrayList<RankInfo>();
                        if (ranks != null) {
                            for (int i = 0; i < ranks.length; i++) {
                                localArrayList.add(ranks[i]);
                            }
                        }
                        mRankInfoList = localArrayList;
                        mHandler.sendEmptyMessage(MSG_UPDATE_RANK_MEDIA);
                    };

                });
    }

    private void setRankAdapter() {
        android.util.Log.d(TAG, "channel.mRankInfoList = " + mRankInfoList.size());

        for (int i = 0; i < mRankInfoList.size(); i++) {
            RankInfo rankInfo = mRankInfoList.get(0);
            MediaInfo[] medias = rankInfo.mediaInfos;
            mPosterAdapter.setGroup(medias);
            mPosterAdapter.setOnMediaClickListener(new OnMediaClickListener() {

                @Override
                public void onMediaClick(MediaView paramMediaView, Object paramObject) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(RankMediaActivity.this, MediaDetailActivity.class);
                    MediaInfo mediaInfo = paramMediaView.getMediaInfo();
                    intent.putExtra("mediaInfo", mediaInfo);
                    startActivity(intent);
                }
            });
        }

    }

    private void onActivate() {
        initActionBar();
        mPosterAdapter = new PosterListAdapter(RankMediaActivity.this);
        final PullToRefreshListView mPullRefreshListView =
                (PullToRefreshListView) this.findViewById(R.id.media_list);
        mPullRefreshListView
                .setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_END);

        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                // Do work to refresh the list here.
                // new GetDataTask().execute();
                mPageCurr += 1;
                String channelId = mRankInfo.channelID + "";
                mDataManager.loadChannelRank(channelId, mPageCurr, 30, 1,
                        new DataManager.IOnloadListener<RankInfoList>() {

                            @Override
                            public void onLoad(RankInfoList entity) {
                                // TODO Auto-generated method stub
                                if (entity == null) return;
                                RankInfo[] ranks = entity.ranks;
                                ArrayList<RankInfo> localArrayList = new ArrayList<RankInfo>();
                                if (ranks != null) {
                                    for (int i = 0; i < ranks.length; i++) {
                                        localArrayList.add(ranks[i]);
                                        MediaInfo[] medias = ranks[i].mediaInfos;
                                        mPosterAdapter.addGroup(medias);
                                    }
                                }
                                mPosterAdapter.refresh();

                                // Call onRefreshComplete when the list has been refreshed.
                                mPullRefreshListView.onRefreshComplete();
                            };
                        });
            }
        });

        ListView listView = mPullRefreshListView.getRefreshableView();
        listView.setAdapter(mPosterAdapter);
    }

    private void initActionBar() {
        FrameLayout frameLayout = new FrameLayout(this);
        TextView textView = new TextView(this);
        textView.setText(mRankInfo.channelName);
        FrameLayout.LayoutParams textLayout =
                new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER);
        frameLayout.addView(textView, textLayout);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
