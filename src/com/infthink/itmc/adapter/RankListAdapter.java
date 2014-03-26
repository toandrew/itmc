package com.infthink.itmc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infthink.itmc.R;
import com.infthink.itmc.type.RankInfo;
import com.infthink.itmc.widget.MediaView;

public class RankListAdapter extends BaseGroupAdapter<RankInfo> {
    private int mMargin;
//    private MediaView.OnMediaClickListener onMediaClickListener;
//    private OnRankClickListener onRankClickListener;
    
    public RankListAdapter(Context context) {
        super(context);
        mMargin = context.getResources().getDimensionPixelSize(R.dimen.page_margin);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if ((convertView != null) && (convertView.getTag() != null) && ((convertView.getTag() instanceof ViewHolder))) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mContext, R.layout.channel_item, null);
            holder = new ViewHolder();
            holder.channelName = (TextView) convertView.findViewById(R.id.channel_name);
            holder.mediaViews = new MediaView[3];
            holder.mediaViews[0] = (MediaView) convertView.findViewById(R.id.left);
//            holder.mediaViews[0].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[0].setTag(new MediaViewMetaInfo());
            holder.mediaViews[1] = ((MediaView)convertView.findViewById(R.id.middle));
//            holder.mediaViews[1].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[1].setTag(new MediaViewMetaInfo());
            holder.mediaViews[2] = ((MediaView)convertView.findViewById(R.id.right));
//            holder.mediaViews[2].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[2].setTag(new MediaViewMetaInfo());
            holder.more = convertView.findViewById(R.id.channel_more);
            convertView.setTag(holder);
        }
        
        RankInfo rankInfo = (RankInfo) getItem(position);
        holder.channelName.setText(rankInfo.channelName);
//        holder.more.setOnClickListener(this);
        holder.more.setTag(rankInfo);
        for (int i = 0; i < 3; i++) {
            MediaViewMetaInfo meta = (MediaViewMetaInfo) holder.mediaViews[i].getTag();
            meta.position = i;
            meta.channelID = rankInfo.channelID;
            meta.channelName = rankInfo.channelName;
        }
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

        public MediaViewMetaInfo() {
        }
    }

    private class ViewHolder {
        TextView channelName;
        MediaView[] mediaViews;
        View more;

        private ViewHolder() {
        }
    }
}
