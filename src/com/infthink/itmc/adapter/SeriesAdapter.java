package com.infthink.itmc.adapter;

import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.R;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.MediaSetInfo;

import android.content.Context;
import android.util.FloatMath;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SeriesAdapter extends BaseGroupAdapter<Object> implements View.OnClickListener {


    public SeriesAdapter(Context context) {
        super(context);
    }

    public int getCount() {
        return 0;
    }

    public Object getItem(int paramInt) {
        return null;
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
            viewHolder.seriesText = (TextView) convertView.findViewById(R.id.series_ci);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }



    public void onClick(View paramView) {}

    private class ViewHolder {
        ImageView playView;
        TextView seriesText;

        private ViewHolder() {}
    }
}
