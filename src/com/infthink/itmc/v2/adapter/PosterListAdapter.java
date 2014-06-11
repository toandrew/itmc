package com.infthink.itmc.v2.adapter;

import com.infthink.itmc.v2.R;
import com.infthink.itmc.v2.util.UIUtil;
import com.infthink.itmc.v2.widget.MediaView;

import android.content.Context;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;

public class PosterListAdapter extends BaseGroupAdapter<Object> {
    private int margin;
    private MediaView.OnMediaClickListener onMediaClickListener;
    private int topMargin;
    
    public PosterListAdapter(Context context) {
        super(context);
        margin = context.getResources().getDimensionPixelSize(R.dimen.page_margin);
        topMargin = context.getResources().getDimensionPixelSize(R.dimen.subject_posterrow_margin_top);
    }
    
    public MediaView.OnMediaClickListener getOnMediaClickListener()
    {
      return this.onMediaClickListener;
    }


    @Override
    public int getCount() {
        return (int) FloatMath.ceil(mGroup.size() / 3.0F);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if ((convertView != null) && (convertView.getTag() != null) && (convertView.getTag() instanceof ViewHolder)) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mContext, R.layout.row_of_posters, null);
            holder = new ViewHolder();
            holder.mediaViews = new MediaView[3];
            holder.mediaViews[0] = ((MediaView)convertView.findViewById(R.id.left));
            holder.mediaViews[0].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[1] = ((MediaView)convertView.findViewById(R.id.middle));
            holder.mediaViews[1].setOnMediaClickListener(this.onMediaClickListener);
            holder.mediaViews[2] = ((MediaView)convertView.findViewById(R.id.right));
            holder.mediaViews[2].setOnMediaClickListener(this.onMediaClickListener);
            convertView.setTag(holder);
        }
        Object[] objs = new Object[3];
        if (position * 3 < mGroup.size())
            objs[0] = mGroup.get(position * 3);
        if (1 + position * 3 < mGroup.size())
            objs[1] = mGroup.get(1 + position * 3);
        if (2 + position * 3 < mGroup.size())
            objs[2] = mGroup.get(2 + position * 3);
        UIUtil.fillPosterViews(holder.mediaViews, objs);
        convertView.setPadding(margin, topMargin, margin, 0);
        return convertView;
    }
    
    private static class ViewHolder {
        MediaView[] mediaViews;
    }
    
    public void setOnMediaClickListener(MediaView.OnMediaClickListener paramOnMediaClickListener)
    {
      this.onMediaClickListener = paramOnMediaClickListener;
    }


}
