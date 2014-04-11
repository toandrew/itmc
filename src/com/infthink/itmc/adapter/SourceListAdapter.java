package com.infthink.itmc.adapter;

import com.infthink.itmc.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SourceListAdapter extends BaseGroupAdapter<Integer> {
    private Context mContext;
    private int preferenceSource = -1;

    public SourceListAdapter(Context paramContext) {
        super(paramContext);
        this.mContext = paramContext;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder localViewHolder;
        int i;
        Resources localResources;
        if (paramView == null) {
            localViewHolder = new ViewHolder();
            paramView =
                    LayoutInflater.from(this.mContext).inflate(
                            R.layout.media_detail_select_source_item, null);
            paramView.setTag(localViewHolder);
            localViewHolder.sourceItemBar =
                    paramView.findViewById(R.id.common_dialog_list_item_bar);
            localViewHolder.sourceItemIv =
                    ((ImageView) paramView.findViewById(R.id.select_source_item_iv));
            localViewHolder.sourceItemTv =
                    ((TextView) paramView.findViewById(R.id.select_source_item_tv));
            i = ((Integer) getItem(paramInt)).intValue();
            if (i != this.preferenceSource) {
                paramView.setSelected(false);
            } else {
                paramView.setSelected(true);
            }
            localResources = this.mContext.getResources();
            if (!localViewHolder.sourceItemBar.isSelected()) {
                localViewHolder.sourceItemBar.setBackgroundColor(localResources
                    .getColor(R.color.select_source_gray));
            } else {
                localViewHolder.sourceItemBar.setBackgroundColor(localResources
                    .getColor(R.color.select_source_yellow));
            }
            switch (i) {
                case 3:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_souhu);
                    localViewHolder.sourceItemTv.setText(R.string.souhu);
                    break;
                case 8:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_qiyi);
                    localViewHolder.sourceItemTv.setText(R.string.qiyi);
                    break;
                case 10:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_tencent);
                    localViewHolder.sourceItemTv.setText(R.string.tencent);
                    break;
                case 17:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_lekan);
                    localViewHolder.sourceItemTv.setText(R.string.lekan);
                    break;
                case 20:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_youku);
                    localViewHolder.sourceItemTv.setText(R.string.youku);
                    break;
                case 23:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_tudou);
                    localViewHolder.sourceItemTv.setText(R.string.tudou);
                    break;
                case 24:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_fenghuang);
                    localViewHolder.sourceItemTv.setText(R.string.fenghuang);
                    break;
                case 25:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_film);
                    localViewHolder.sourceItemTv.setText(R.string.dianyingwang);
                    break;
                case 32:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_letv);
                    localViewHolder.sourceItemTv.setText(R.string.letv);
                    break;
                default:
                    localViewHolder.sourceItemIv
                            .setBackgroundResource(R.drawable.select_source_item_default);
                    localViewHolder.sourceItemTv.setText(R.string.unknown_source);
                    break;
            }
        }
        return paramView;

    }

    public void setSelectedSource(int paramInt) {
        this.preferenceSource = paramInt;
    }

    private class ViewHolder {
        public View sourceItemBar;
        public ImageView sourceItemIv;
        public TextView sourceItemTv;

        private ViewHolder() {}
    }
}
