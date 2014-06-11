package com.infthink.itmc.v2.adapter;

import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.v2.R;
import com.infthink.itmc.v2.type.MediaInfo;
import com.infthink.itmc.v2.type.MediaSetInfo;

import android.content.Context;
import android.util.FloatMath;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SeriesAdapter extends BaseGroupAdapter<Object> implements View.OnClickListener {


    public SeriesAdapter(Context context) {
        super(context);
    }



    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View convertView, ViewGroup paramViewGroup) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.series_gridview_item, null);
            convertView.setTag(viewHolder);
            viewHolder.playView = (ImageView) convertView.findViewById(R.id.icon_play_image);
            viewHolder.seriesText = (Button) convertView.findViewById(R.id.series_ci);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.seriesText.setText("第" + (paramInt + 1) + "集");
        return convertView;
    }



    public void onClick(View paramView) {}

    private class ViewHolder {
        ImageView playView;
        Button seriesText;

        private ViewHolder() {}
    }
}
