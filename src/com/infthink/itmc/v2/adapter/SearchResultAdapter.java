package com.infthink.itmc.v2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infthink.itmc.v2.HomeActivity;
import com.infthink.itmc.v2.MediaDetailActivity;
import com.infthink.itmc.v2.R;
import com.infthink.itmc.v2.type.MediaInfo;
import com.infthink.itmc.v2.util.UIUtil;
import com.infthink.itmc.v2.util.Util;
import com.infthink.itmc.v2.widget.MediaView;
import com.infthink.itmc.v2.widget.RatingView;

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
            final MediaInfo info = (MediaInfo) data;
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
            convertView.setTag(info);
//            mediaView.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    Intent intent = new Intent(mContext, MediaDetailActivity.class);
//                    intent.putExtra("mediaInfo", info);
//                    mContext.startActivity(intent);
//                }
//            });

//             mediaView.setOnMediaClickListener(this);
        }
        return convertView;
    }

}
