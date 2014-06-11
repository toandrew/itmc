package com.infthink.itmc.v2.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseGroupAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mGroup = new ArrayList();

    public BaseGroupAdapter(Context context) {
        mContext = context;
    }

    public void addGroup(List<T> list) {
        if (list != null) {
            Iterator<T> iterator = list.iterator();
            while (iterator.hasNext()) {
                T obj = iterator.next();
                mGroup.add(obj);
            }
            notifyDataSetChanged();
        }
    }

    public void addGroup(T[] t) {
        if (t != null) {
            int i = t.length;
            for (T obj : t) {
                mGroup.add(obj);
            }
            notifyDataSetChanged();
        }
    }
    
    public void clear() {
        mGroup = new ArrayList();
        notifyDataSetChanged();
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public List<T> getGroup() {
        return mGroup;
    }

    @Override
    public int getCount() {
        return mGroup.size();
    }

    @Override
    public T getItem(int position) {
        if ((position < 0) || (position >= mGroup.size()))
            return null;
        return mGroup.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isEmpty() {
        return mGroup.isEmpty();
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public final void refresh() {
        notifyDataSetChanged();
    }

    public void setGroup(List<T> list) {
        if (list != null) {
            mGroup = list;
            notifyDataSetChanged();
        }
    }

    public void setGroup(T[] t) {
        if (t != null) {
            mGroup.clear();
            for (T obj : t) {
                mGroup.add(obj);
            }

            notifyDataSetChanged();
        }
    }

}
