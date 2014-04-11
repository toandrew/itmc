package com.infthink.itmc.adapter;

import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.HomeActivity;
import com.infthink.itmc.R;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.widget.MediaView;
import com.infthink.netcast.sdk.CastDevice;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CastDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<CastDevice> mCastDeviceList;
    
    public CastDeviceAdapter(Context context) {
        mContext = context;
    }
    
    public void setDeviceList(ArrayList<CastDevice> list) {
        mCastDeviceList = list;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        if (mCastDeviceList != null)
            return mCastDeviceList.size();
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        return mCastDeviceList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.device_info, null);
            convertView.setTag(viewHolder);
            
            viewHolder.name = ((TextView) convertView.findViewById(R.id.cast_name));
            viewHolder.status = (TextView) convertView.findViewById(R.id.cast_status);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(mCastDeviceList.get(position).getFriendlyName());
        viewHolder.status.setText(mCastDeviceList.get(position).isAp() ? "需要设置" : "准备投射");
        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public TextView status;
    }
}
