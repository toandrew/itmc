package com.infthink.itmc.v2.adapter;

import java.util.Calendar;

import com.infthink.itmc.v2.R;
import com.infthink.itmc.v2.type.BaseLocalPlayHistory;
import com.infthink.itmc.v2.type.LocalPlayHistory;
import com.infthink.itmc.v2.widget.MediaThumbView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayHistoryAdapter extends BaseGroupAdapter<LocalPlayHistory> {
    private Drawable defaultThumbDrawable;
    
    public PlayHistoryAdapter(Context context) {
        super(context);
        this.defaultThumbDrawable = context.getResources().getDrawable(R.drawable.default_videothumb);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.playhistory_item,
                    null);
            holder = new ViewHolder();
            holder.videoThumbImageView = ((MediaThumbView) convertView
                    .findViewById(R.id.video_thumb));
            holder.shadowView = ((ImageView) convertView
                    .findViewById(R.id.video_thumbshadow));
            holder.videoThumbImageView.setShadowView(holder.shadowView);
            holder.videoThumbImageView
                    .setDefaultDrawable(this.defaultThumbDrawable);
            holder.videoTitle = ((TextView) convertView
                    .findViewById(R.id.video_title));
            holder.videoPlayDate = ((TextView) convertView
                    .findViewById(R.id.video_playdate));
            holder.ivMyFavorite = ((ImageView) convertView
                    .findViewById(R.id.iv_myfavorite));
            holder.tvThumb = ((ImageView) convertView
                    .findViewById(R.id.tv_thumb));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseLocalPlayHistory history = (BaseLocalPlayHistory) super
                .getItem(position);
        holder.videoTitle.setText(((LocalPlayHistory)history).videoName);
        holder.videoPlayDate.setText(getPlayHistoryItemPlayDate(history));
        return convertView;
    }

    private String getPlayHistoryItemPlayDate(
            BaseLocalPlayHistory history) {
        if (history == null)
            return "";
        long time = Long.parseLong(history.playDate);
        Calendar historyCalendar = Calendar.getInstance();
        historyCalendar.setTimeInMillis(time);
        int historyYear = historyCalendar.get(Calendar.YEAR);
        int historyMonth = historyCalendar.get(Calendar.MONTH);
        int historyDate = historyCalendar.get(Calendar.DATE);
        int historyHour = historyCalendar.get(Calendar.HOUR_OF_DAY);
        int historyMinute = historyCalendar.get(Calendar.MINUTE);
        int historyDay = historyCalendar.get(Calendar.DAY_OF_YEAR);
        
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        
        Resources resource = mContext.getResources();
        StringBuilder sb = new StringBuilder();
        if ((historyYear == year) && (historyMonth == month) && (historyDate == date)) {
            sb.append(resource.getString(R.string.today));
        } else {
            sb.append(historyYear);
            sb.append(resource.getString(R.string.year));
            sb.append(historyMonth);
            sb.append(resource.getString(R.string.month));
            sb.append(historyDate);
            sb.append(resource.getString(R.string.day));
        }
        sb.append(historyHour);
        sb.append(":");
        if (historyMinute < 10) {
            sb.append(String.format("0%d",
                    historyMinute));
        }  else {
            sb.append(historyMinute);
        }
        return sb.toString();
    }

    private class ViewHolder {
        ImageView ivMyFavorite;
        ImageView shadowView;
        ImageView tvThumb;
        TextView videoPlayDate;
        MediaThumbView videoThumbImageView;
        TextView videoTitle;
    }
}
