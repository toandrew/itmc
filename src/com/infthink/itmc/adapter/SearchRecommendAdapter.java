package com.infthink.itmc.adapter;

import com.infthink.itmc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchRecommendAdapter extends BaseGroupAdapter<String> {

    public SearchRecommendAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_recommend_item, null);
            holder.searchRecommendTv = (TextView) convertView.findViewById(R.id.search_recommend_item_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.searchRecommendTv.setText(getItem(position));
        if (position >= 3) {
            holder.searchRecommendTv.setTextColor(mContext.getResources().getColor(R.color.p_80_white));
        } else {
            holder.searchRecommendTv.setTextColor(mContext.getResources().getColor(R.color.search_recommend_red));
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView searchRecommendTv;

        private ViewHolder() {
        }
    }
}
