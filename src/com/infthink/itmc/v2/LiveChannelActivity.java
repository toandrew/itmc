package com.infthink.itmc.v2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.infthink.itmc.v2.AdvancedVideoDemo.VideoAdapter;
import com.infthink.itmc.v2.AdvancedVideoDemo.VideoInfo;
import com.infthink.itmc.v2.data.DataManager;
import com.infthink.itmc.v2.data.LocalMyFavoriteInfoManager;
import com.infthink.itmc.v2.type.LiveChannelInfo;
import com.infthink.libs.common.message.MessageManager;
import com.infthink.libs.upgrade.Upgrade;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

public class LiveChannelActivity extends CoreActivity {

    private DataManager mDataManager;
    private ListView mListView;
    private Handler mHandler;

    @Override
    public void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);
        setContentView(R.layout.video_advanced);
        initActionBar();
        init();
    }
    
    @Override
    protected void onInitialized() {
        mDataManager = getService().getDataManager();
        getLiveChannelMap();
    }

    private void getLiveChannelMap() {
        mDataManager
                .loadLiveChannel(new DataManager.IOnloadListener<List<LiveChannelInfo>>() {
                    @Override
                    public void onLoad(final List<LiveChannelInfo> infos) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final VideoAdapter adapter = new VideoAdapter(getContext(), infos);
                                mListView.setAdapter(adapter);
                                mListView.setOnItemClickListener(new OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                            long id) {
                                        LiveChannelInfo info = (LiveChannelInfo) adapter.getItem(position);
                                        Intent intent = new Intent(LiveChannelActivity.this,
                                                LiveProgramActivity.class);
                                        intent.putExtra("uuid", info.uuid);
                                        intent.putExtra("channelName", info.channelName);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                });
    }
    
    private void initActionBar() {
        FrameLayout frameLayout = new FrameLayout(this);
        TextView textView = new TextView(this);
        textView.setText("频道");
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

    private void init() {
        mHandler = new Handler();
        mListView = (ListView) findViewById(android.R.id.list);
    }
    
    /**
     * 定义一个Adapter来显示缩略图和视频title信息
     * 
     * @author Administrator
     * 
     */
    static class VideoAdapter extends BaseAdapter {

        private Context context;
        private List<LiveChannelInfo> videoItems;

        public VideoAdapter(Context context, List<LiveChannelInfo> data) {
            this.context = context;
            this.videoItems = data;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return videoItems.size();
        }

        @Override
        public Object getItem(int p) {
            // TODO Auto-generated method stub
            return videoItems.get(p);
        }

        @Override
        public long getItemId(int p) {
            // TODO Auto-generated method stub
            return p;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.video_item, null);
                holder.thumbImage = (ImageView) convertView.findViewById(R.id.thumb_image);
                holder.titleText = (TextView) convertView.findViewById(R.id.video_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 显示信息
            holder.titleText.setText(videoItems.get(position).channelName);

            return convertView;
        }

        class ViewHolder {
            ImageView thumbImage;
            TextView titleText;
        }

    }

}
