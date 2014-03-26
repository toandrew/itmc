package com.infthink.itmc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infthink.itmc.R;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.util.UIUtil;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.widget.MediaView;
import com.infthink.itmc.widget.RatingView;

public class SearchResultAdapter extends BaseGroupAdapter<MediaInfo> {

    public SearchResultAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.search_result_item,
                    null);
        }
        Object data = getItem(position);
        UIUtil.fillMediaSummary(convertView, data);
        TextView scoreView;
        if (data instanceof MediaInfo) {
            MediaInfo info = (MediaInfo) data;
            // ((RatingView)convertView.findViewById(R.id.rating)).setScore(info.score);
            scoreView = (TextView) convertView.findViewById(R.id.score);
            if (info.score > 0) {
                scoreView.setText(Util.formatScore(info.score));
            } else {
                scoreView.setText("");
            }

            MediaView mediaView = (MediaView) convertView
                    .findViewById(R.id.media_view);
            mediaView.setTag(Integer.valueOf(position));
            // mediaView.setOnMediaClickListener(this);
        }
        return convertView;
    }

}
