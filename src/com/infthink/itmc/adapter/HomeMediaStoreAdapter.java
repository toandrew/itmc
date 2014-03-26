package com.infthink.itmc.adapter;

import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.R;
import com.infthink.itmc.type.MediaCategoryInfo;
import com.infthink.itmc.type.VideoInfo;
import com.infthink.itmc.util.LogUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeMediaStoreAdapter extends BaseGroupAdapter<MediaCategoryInfo> {

    private static final int LOW_MEDIATHUMB_TASK_STEP = 7;
    private static final String TV_TAG = "tv";
    private List<MediaCategoryInfo> allMediaCategoryInfoList;
    private boolean bSelectedMode;
    private Drawable defaultThumbDrawable;
    private SparseBooleanArray homeMediaShowHideStatus;
    private SparseBooleanArray myHomeMediaSelectedStatusArray;
    private boolean scrollToUp;
    private boolean scrolling;
//    private SparseArray<ThumbTask> thumbTaskArray;
//    private VideoThumbCacheManager videoThumbCacheManager = VideoThumbCacheManager.getInstance();
//    private VideoThumbReadyListener videoThumbReadyListener = VideoThumbReadyListener.getInstance();
    
    public HomeMediaStoreAdapter(Context context) {
        super(context);
        
//        this.videoThumbCacheManager.setCheckThumbTaskFinishListener(this);
        this.bSelectedMode = false;
        this.myHomeMediaSelectedStatusArray = new SparseBooleanArray();
//        this.thumbTaskArray = new SparseArray();
        this.defaultThumbDrawable = context.getResources().getDrawable(R.drawable.default_videothumb);
        this.homeMediaShowHideStatus = new SparseBooleanArray();
    }

    private List<MediaCategoryInfo> filterHideMedia() {
        return null;
    }

//    private VideoInfo generateVideoInfo(LocalMedia paramLocalMedia,
//            LocalPlayHistory paramLocalPlayHistory) {
//        return null;
//    }
    

//    private VideoInfo generateVideoInfo(LocalPlayHistory paramLocalPlayHistory) {
//    }

//    private VideoInfo generateVideoInfo(DLNAMediaItem paramDLNAMediaItem) {
//    }

    private void prefetchThumbanil(int paramInt) {

    }

    public void clearSelectedStatus() {
        this.myHomeMediaSelectedStatusArray.clear();
    }

    public int getSelectedCount() {
        int i = 0;
        int j = this.myHomeMediaSelectedStatusArray.size();
        for (int k = 0; k < j; k++) {
            if (!this.myHomeMediaSelectedStatusArray.get(
                    this.myHomeMediaSelectedStatusArray.keyAt(k), false))
                continue;
            i++;
        }
        return i;
    }

    public List<MediaCategoryInfo> getSelectedItem() {
        ArrayList localArrayList = new ArrayList();
        int i = this.myHomeMediaSelectedStatusArray.size();
        for (int j = 0; j < i; j++) {
            int k = this.myHomeMediaSelectedStatusArray.keyAt(j);
            if (!this.myHomeMediaSelectedStatusArray.get(k, false))
                continue;
            localArrayList.add(getItem(k));
        }
        return localArrayList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtil.dumpLog(String.valueOf(position));
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.myvideo_item, null);
            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView arrowView;
        CheckBox cbSelect;
        ImageView ivMyFavorite;
        ImageView shadowView;
        ImageView thumbTypeView;
//        MediaThumbView thumbView;
        TextView tvDesc;
        ImageView tvThumb;
        TextView tvTitle;

        private ViewHolder() {
        }
    }
}
