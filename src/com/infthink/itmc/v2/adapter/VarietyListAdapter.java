package com.infthink.itmc.v2.adapter;

import com.infthink.itmc.v2.R;
import com.infthink.itmc.v2.type.Variety;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VarietyListAdapter extends BaseGroupAdapter<Variety> {
    private int ci = 1;
    private boolean ciClickable = true;
    private boolean ciClicked;
    private int ciPosition = -1;

    public VarietyListAdapter(Context paramContext) {
        super(paramContext);
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder localViewHolder = null;
        if ((paramView != null) && (paramView.getTag() != null)
                && ((paramView.getTag() instanceof TextView)))
            localViewHolder = (ViewHolder) paramView.getTag();
        while (true) {
            Variety localVariety = (Variety) getItem(paramInt);
            localViewHolder.nameView.setText(localVariety.name);
            localViewHolder.dateView.setText(localVariety.date);
            if (localVariety.ci != this.ci) break;
            this.ciPosition = paramInt;
            if (this.ciClicked) {
                localViewHolder.playIcon.setVisibility(4);
                localViewHolder.playLoading.setVisibility(0);
                
                paramView = View.inflate(this.mContext, R.layout.variety_item, null);
                localViewHolder = new ViewHolder();
                localViewHolder.nameView = ((TextView) paramView.findViewById(R.id.name));
                localViewHolder.dateView = ((TextView) paramView.findViewById(R.id.date));
                localViewHolder.playIcon = ((ImageView) paramView.findViewById(R.id.icon_state));
                localViewHolder.playLoading = paramView.findViewById(2131165419);
                paramView.setTag(localViewHolder);
                return paramView;
            }
            localViewHolder.playIcon.setVisibility(0);
            localViewHolder.playLoading.setVisibility(4);
            return paramView;
        }
        localViewHolder.playIcon.setVisibility(4);
        localViewHolder.playLoading.setVisibility(4);
        return paramView;
    }

    public boolean isCurrentClickable() {
        return this.ciClickable;
    }

    public boolean isCurrentClicked() {
        return this.ciClicked;
    }

    public boolean isEnabled(int paramInt) {
        return (this.ciPosition != paramInt) || (this.ciClickable);
    }

    public void setCurrentClickable(boolean paramBoolean) {
        this.ciClickable = paramBoolean;
    }

    public void setCurrentClicked(boolean paramBoolean) {
        this.ciClicked = paramBoolean;
    }

    public void setCurrentIndex(int paramInt) {
        this.ci = paramInt;
    }

    private class ViewHolder {
        TextView dateView;
        TextView nameView;
        ImageView playIcon;
        View playLoading;

        private ViewHolder() {}
    }
}
