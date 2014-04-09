package com.infthink.itmc.adapter;

import java.util.HashMap;

import com.infthink.itmc.HomeActivity;
import com.infthink.itmc.R;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.ShowBaseInfo;
import com.infthink.itmc.widget.MediaView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

public class HomeChannelAdapter extends BaseGroupAdapter<Channel> {
    private int margin;
    private MediaView.OnMediaClickListener onMediaClickListener;
    private View.OnClickListener onMoreClickListener;
    private HashMap<Channel, ShowBaseInfo[]> recommendationOfChannels;
    
    public HomeChannelAdapter(Context context) {
        super(context);
        margin = context.getResources().getDimensionPixelSize(R.dimen.page_margin);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        MediaView[] mediaView;
        Channel channel;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.home_channel_itemex, null);
            convertView.setTag(viewHolder);
            
            viewHolder.tvChannelName = ((TextView) convertView.findViewById(R.id.channel_name));
            viewHolder.moreView = convertView.findViewById(R.id.channel_more);
            viewHolder.moreView.setOnClickListener(onMoreClickListener);
            convertView.setPadding(margin, 0, margin, 0);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        channel = (Channel)getItem(position);
        viewHolder.moreView.setTag(channel);
        viewHolder.tvChannelName.setText(channel.channelName);
        if (viewHolder.mediaPosters == null) {
            viewHolder.mediaPosters = ((ViewStub) convertView.findViewById(R.id.media_stub)).inflate();
            viewHolder.mediaView_0 = ((MediaView)viewHolder.mediaPosters.findViewById(R.id.left));
            viewHolder.mediaView_0.setOnMediaClickListener(this.onMediaClickListener);
            viewHolder.mediaView_1 = ((MediaView)viewHolder.mediaPosters.findViewById(R.id.middle));
            viewHolder.mediaView_1.setOnMediaClickListener(this.onMediaClickListener);
            viewHolder.mediaView_2 = ((MediaView)viewHolder.mediaPosters.findViewById(R.id.right));
            viewHolder.mediaView_2.setOnMediaClickListener(this.onMediaClickListener);
        }
        mediaView = new MediaView[3];
        mediaView[0] = viewHolder.mediaView_0;
        mediaView[0].setDefaultPoster();
        mediaView[1] = viewHolder.mediaView_1;
        mediaView[1].setDefaultPoster();
        mediaView[2] = viewHolder.mediaView_2;
        mediaView[2].setDefaultPoster();
        mediaView[0].setTag(new HomeActivity.MediaViewMetaInfo(0, channel.channelID, channel.channelName));
        mediaView[1].setTag(new HomeActivity.MediaViewMetaInfo(1, channel.channelID, channel.channelName));
        mediaView[2].setTag(new HomeActivity.MediaViewMetaInfo(2, channel.channelID, channel.channelName));
        MediaInfo[] medias = (MediaInfo[]) recommendationOfChannels.get(channel);
        mediaView[0].setMediaInfo(medias[0]);
        mediaView[1].setMediaInfo(medias[1]);
        mediaView[2].setMediaInfo(medias[2]);
        return convertView;
    }

    public void setOnMoreClickListener(View.OnClickListener onclickListener) {
        onMoreClickListener = onclickListener;
    }

    public void setRecommendationOfChannels(HashMap<Channel, ShowBaseInfo[]> map) {
        recommendationOfChannels = map;
    }

    private class ViewHolder {
        public View mediaPosters;
        public MediaView mediaView_0;
        public MediaView mediaView_1;
        public MediaView mediaView_2;
        public View moreView;
        public TextView tvChannelName;

        private ViewHolder() {
        }
    }
    
    public void setOnMediaClickListenenr(MediaView.OnMediaClickListener paramOnMediaClickListener)
    {
      this.onMediaClickListener = paramOnMediaClickListener;
    }

}
