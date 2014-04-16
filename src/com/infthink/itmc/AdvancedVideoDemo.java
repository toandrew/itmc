package com.infthink.itmc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

/**
 * 本实例，我们将通过检索SDCard上的Video信息 在MediaStore中，MediaStore.Video.Media中就有Video相关信息，
 * 同时MediaStore.Video.Thumbnails中含有各个video对应的缩略图信息
 * 
 * @author Administrator
 * 
 */
public class AdvancedVideoDemo extends ListActivity {

    private Cursor cursor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_advanced);
        initActionBar();
        init();
    }

    private void initActionBar() {
        FrameLayout frameLayout = new FrameLayout(this);
        TextView textView = new TextView(this);
        textView.setText("本地视频");
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
        String[] thumbColumns =
                new String[] {MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.VIDEO_ID};

        String[] mediaColumns =
                new String[] {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE, MediaStore.Video.Media.MIME_TYPE};

        // 首先检索SDcard上所有的video
        cursor =
                this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null,
                        null, null);

        ArrayList<VideoInfo> videoList = new ArrayList<AdvancedVideoDemo.VideoInfo>();

        if (cursor.moveToFirst()) {
            do {
                VideoInfo info = new VideoInfo();

                info.filePath =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                info.mimeType =
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                info.title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));

                // // 获取当前Video对应的Id，然后根据该ID获取其Thumb
                // int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                // String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
                // String[] selectionArgs = new String[] {id + ""};
                // Cursor thumbCursor =
                // this.managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                // thumbColumns, selection, selectionArgs, null);
                // if (thumbCursor.moveToFirst()) {
                // info.thumbPath =
                // cursor.getString(cursor
                // .getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                // }

                Bitmap bitmap =
                        ThumbnailUtils.createVideoThumbnail(info.filePath, Thumbnails.MINI_KIND);
                int width = 200;
                int height = width * 3 / 4;
                Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, width, height);
                info.thumbPathBitmap = bitmap1;
                bitmap.recycle();

                // 然后将其加入到videoList
                videoList.add(info);

            } while (cursor.moveToNext());
        }

        // 然后需要设置ListView的Adapter了，使用我们自定义的Adatper
        final VideoAdapter adapter = new VideoAdapter(this, videoList);
        this.getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                    long id) {
                VideoInfo info = (VideoInfo) adapter.getItem(position);
                Intent intent = new Intent(AdvancedVideoDemo.this,
                        MediaPlayerActivity.class);
                intent.putExtra("media_id", -1);
                intent.putExtra("pageUrl", "");
                intent.putExtra("source", -1);
                intent.putExtra("meidaTitle", info.title);
                intent.putExtra("available_episode_count", -1);
                intent.putExtra("current_episode", -1);
                intent.putExtra("path", info.filePath);
                intent.putExtra("pageUrl", "");
                startActivity(intent);
            }
            
        });
    }

    static class VideoInfo {
        String filePath;
        String mimeType;
        String thumbPath;
        String title;
        Bitmap thumbPathBitmap;
    }

    /**
     * 定义一个Adapter来显示缩略图和视频title信息
     * 
     * @author Administrator
     * 
     */
    static class VideoAdapter extends BaseAdapter {

        private Context context;
        private List<VideoInfo> videoItems;

        public VideoAdapter(Context context, List<VideoInfo> data) {
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
            holder.titleText.setText(videoItems.get(position).title);
            if (videoItems.get(position).thumbPath != null) {
                holder.thumbImage.setImageURI(Uri.parse(videoItems.get(position).thumbPath));
            }
            if (videoItems.get(position).thumbPathBitmap != null){
                holder.thumbImage.setImageBitmap(videoItems.get(position).thumbPathBitmap);
            }
            

            return convertView;
        }

        class ViewHolder {
            ImageView thumbImage;
            TextView titleText;
        }

    }

}
