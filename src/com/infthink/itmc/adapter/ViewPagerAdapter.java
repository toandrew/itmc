package com.infthink.itmc.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {

    protected Context mContext;
    private List<View> mPagesList = new ArrayList<View>();

    public ViewPagerAdapter(Context content) {
        mContext = content;
    }

    public List<View> getPages() {
        return mPagesList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mPagesList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return -2;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView((View) mPagesList.get(position));
        return mPagesList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        boolean flag = false;
        if (object != null && object instanceof View) {
            View localView = (View)object;
            if (view == localView)
                flag = true;
        }
        return flag;
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    public void setPages(List<View> list) {
        if (list != null) {
            mPagesList = list;
            refresh();
        }
    }

    public void setPages(View[] views) {
        if (views != null) {
            mPagesList.clear();
            for (int i = 0; i < views.length; i++)
                mPagesList.add(views[i]);
            refresh();
        }
    }
}
