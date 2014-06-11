package com.infthink.itmc.v2.adapter;

import java.util.ArrayList;

import com.infthink.itmc.v2.type.Banner;
import com.infthink.itmc.v2.widget.MediaView;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ScrollBannerAdapter extends PagerAdapter {

    private ArrayList<Banner> mBannerList = new ArrayList();
    private Context mContext;
    private MediaView.OnMediaClickListener onMediaClickListener;
    // private MediaView.OnMediaClickListener onMediaClickListener;
    // private OnScrollBannerAdapterOberver primaryItemObserver;
    // private ArrayList<TelevisionInfo> tvList = new ArrayList();

    public ScrollBannerAdapter(Context context) {
        mContext = context;
    }

    // public ScrollBannerApdater(Context paramContext,
    // OnScrollBannerAdapterOberver paramOnScrollBannerAdapterOberver)
    // {
    // this.context = paramContext;
    // this.primaryItemObserver = paramOnScrollBannerAdapterOberver;
    // }

    public void destroyItem(ViewGroup container, int position, Object obj) {
        container.removeView((View) obj);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public int getItemPosition(Object obj) {
        return super.getItemPosition(obj);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return mBannerList.size();
    }

    public Object instantiateItem(ViewGroup convertView, int position) {
        MediaView mediaView = new MediaView(mContext, 1);
        mediaView.setBannerMedia(mBannerList.get(position));
        mediaView.setTag(position);
        mediaView.setDefaultPoster();
         mediaView.setOnMediaClickListener(this.onMediaClickListener);
        // mediaView.setShowText(false);
        convertView.addView(mediaView);
        return mediaView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        boolean flag = false;
        if (object != null && object instanceof View) {
            View localView = (View) object;
            if (view == localView)
                flag = true;
        }
        return flag;
    }

    public void setBannerList(Banner[] banners) {
        setBannerMediaList(banners);
    }

    public void setBannerMediaList(Banner[] banners) {
        if (banners != null) {
            mBannerList.clear();
            for (Banner banner : banners) {
                mBannerList.add(banner);
            }
            if (banners.length > 1) {
                mBannerList.add(0, banners[banners.length - 1]);
                mBannerList.add(mBannerList.size(), banners[0]);
            }
            notifyDataSetChanged();
        }
    }

    public void setOnMediaClickListener(MediaView.OnMediaClickListener paramOnMediaClickListener) {
        // TODO Auto-generated method stub
        this.onMediaClickListener = paramOnMediaClickListener;
    }
}
