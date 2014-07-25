package com.infthink.itmc.v2;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;

import com.infthink.itmc.v2.LiveChannelActivity.VideoAdapter;
import com.infthink.itmc.v2.data.DataManager;
import com.infthink.itmc.v2.type.LiveChannelInfo;
import com.infthink.itmc.v2.type.LiveMediasInfo;
import com.infthink.itmc.v2.type.LiveProgramInfo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;

public class LiveProgramActivity extends CoreActivity {

    private DataManager mDataManager;
    private ListView mListView;
    private Handler mHandler;
    private String mId;
    private String mChannelName;

    @Override
    public void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);
        
        Intent intent = getIntent();
        mId = intent.getStringExtra("uuid");
        mChannelName = intent.getStringExtra("channelName");
        
        setContentView(R.layout.video_advanced);
        initActionBar();
        init();
    }
    
    @Override
    protected void onInitialized() {
        mDataManager = getService().getDataManager();
        getLiveProgram();
//        Date date = new Date();
//        date.setTime(1403020800000l);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        android.util.Log.d("XXXXXXXX", "data.getYear() = " + formatter.format(date));
    }

    private void getLiveProgram() {
        mDataManager.loadLiveProgram(mId,
                new DataManager.IOnloadListener<List<LiveProgramInfo>>() {
                    @Override
                    public void onLoad(final List<LiveProgramInfo> entity) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final VideoAdapter adapter = new VideoAdapter(
                                        getContext(), entity);
                                mListView.setAdapter(adapter);
                                mListView
                                        .setOnItemClickListener(new OnItemClickListener() {

                                            @Override
                                            public void onItemClick(
                                                    AdapterView<?> arg0,
                                                    View arg1, int position,
                                                    long id) {
                                                LiveProgramInfo info = (LiveProgramInfo) adapter.getItem(position);
                                                Intent intent = new Intent(LiveProgramActivity.this, MediaPlayerActivity.class);
                                                intent.putExtra("meidaTitle", info.programName);
                                                intent.putExtra("path", info.programUrl);
                                                intent.putExtra("live", true);
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
        textView.setText(mChannelName);
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
        private List<LiveProgramInfo> videoItems;

        public VideoAdapter(Context context, List<LiveProgramInfo> data) {
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
            holder.titleText.setText(videoItems.get(position).programName);

            return convertView;
        }

        class ViewHolder {
            ImageView thumbImage;
            TextView titleText;
        }

    }

}
