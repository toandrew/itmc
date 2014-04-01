package com.infthink.itmc.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infthink.itmc.HomeActivity;
import com.infthink.itmc.R;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.RankInfo;
import com.infthink.itmc.type.ShowBaseInfo;
import com.infthink.itmc.widget.MediaView;

public class RankListAdapter extends BaseGroupAdapter<RankInfo> implements View.OnClickListener {
    private int mMargin;
    private ArrayList<RankInfo> mRankInfoList = new ArrayList<RankInfo>();
     private MediaView.OnMediaClickListener onMediaClickListener;
     private OnRankClickListener onRankClickListener;


    public void setmRankInfoList(ArrayList<RankInfo> mRankInfoList) {
        this.mRankInfoList = mRankInfoList;
    }

    public RankListAdapter(Context context) {
        super(context);
        mMargin = context.getResources().getDimensionPixelSize(R.dimen.page_margin);
    }
    
    public MediaView.OnMediaClickListener getOnMediaClickListener()
    {
      return this.onMediaClickListener;
    }

    public OnRankClickListener getOnRankClickListener()
    {
      return this.onRankClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        RankInfo item = getItem(position);
        if ((convertView != null) && (convertView.getTag() != null)
                && ((convertView.getTag() instanceof ViewHolder))) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mContext, R.layout.channel_item, null);
            holder = new ViewHolder();
            holder.channelName = (TextView) convertView.findViewById(R.id.channel_name);
            holder.mediaViews = new MediaView[3];
            holder.mediaViews[0] = (MediaView) convertView.findViewById(R.id.left);
             holder.mediaViews[0].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[0].setTag(new HomeActivity.MediaViewMetaInfo(0, item.channelID, item.channelName));
            holder.mediaViews[1] = ((MediaView) convertView.findViewById(R.id.middle));
             holder.mediaViews[1].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[1].setTag(new HomeActivity.MediaViewMetaInfo(0, item.channelID, item.channelName));
            holder.mediaViews[2] = ((MediaView) convertView.findViewById(R.id.right));
             holder.mediaViews[2].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[2].setTag(new HomeActivity.MediaViewMetaInfo(0, item.channelID, item.channelName));

            convertView.setTag(holder);
        }
        
        holder.more = convertView.findViewById(R.id.channel_more);
        
        MediaView[] mediaView = new MediaView[3];
        for (int j = 0; j < mRankInfoList.size(); j++){
            if(mRankInfoList.get(j).channelID == item.channelID){
                for (int i = 0; i < 3; i++) {
                    android.util.Log.d("XXXXXXXXXX", "jj = " + j);
                    RankInfo rankInfo = mRankInfoList.get(j);
                    mediaView[i] = holder.mediaViews[i];
                    mediaView[i].setDefaultPoster();
                    MediaInfo[] mediaInfos = rankInfo.mediaInfos;
                    mediaView[i].setMediaInfo(mediaInfos[i]);
                }
            }
        }

        RankInfo rankInfo = (RankInfo) getItem(position);
        holder.channelName.setText(rankInfo.channelName);
//        holder.more.setOnClickListener(this);
        holder.more.setTag(rankInfo);
//        for (int i = 0; i < 3; i++) {
//            MediaViewMetaInfo meta = (MediaViewMetaInfo) holder.mediaViews[i].getTag();
//            meta.position = i;
//            meta.channelID = rankInfo.channelID;
//            meta.channelName = rankInfo.channelName;
//        }
        // UIUtil.fillPosterViews(localViewHolder.mediaViews,
        // localRankInfo.mediaInfos);
        if (position == mGroup.size() - 1) {
            convertView.setPadding(mMargin, 0, mMargin, mMargin);
        } else {
            convertView.setPadding(mMargin, 0, mMargin, 0);
        }
        return convertView;
    }

    public class MediaViewMetaInfo {
        public int channelID;
        public String channelName;
        public int position;

        public MediaViewMetaInfo(int position, int channelId, String channelName) {
            this.position = position;
            this.channelID = channelId;
            this.channelName = channelName;
        }
    }

    private class ViewHolder {
        TextView channelName;
        MediaView[] mediaViews;
        View more;

        private ViewHolder() {}
    }
    
    public void onClick(View paramView)
    {
      if ((paramView.getTag() != null) && ((paramView.getTag() instanceof RankInfo)) && (this.onRankClickListener != null))
        this.onRankClickListener.onMoreClick(paramView, (RankInfo)paramView.getTag());
    }

    public void setOnMediaClickListener(MediaView.OnMediaClickListener paramOnMediaClickListener)
    {
      this.onMediaClickListener = paramOnMediaClickListener;
    }

    public void setOnRankClickListener(OnRankClickListener paramOnRankClickListener)
    {
      this.onRankClickListener = paramOnRankClickListener;
    }
    
    public static abstract interface OnRankClickListener
    {
      public abstract void onMoreClick(View paramView, RankInfo paramRankInfo);
    }

}
