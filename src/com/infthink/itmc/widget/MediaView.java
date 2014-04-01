package com.infthink.itmc.widget;

import java.net.URI;

import com.infthink.itmc.CoreActivity;
import com.infthink.itmc.ITApp;
import com.infthink.itmc.R;
import com.infthink.itmc.type.AlbumInfo;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.PersonInfo;
import com.infthink.itmc.util.BitmapUtil;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.widget.MediaView.OnMediaClickListener;
import com.infthink.libs.cache.simple.BitmapCachePool;
import com.infthink.libs.cache.simple.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class MediaView extends LinearLayout {
    public static final int BANNER_TYPE = 1;
    public static final int COVER_TYPE = 0;
    // public static final int TV_TYPE = 2;
    private AlbumInfo mAlbumInfo;
    private ImageView mBorderImage;
    // private View clickView;
    private Context mContext;
    // private ImageManager imageManager;
    private View infoView;
    private ImageView mMaskImage;
    private MediaInfo mediaInfo;
    // private int mediaSetType;
    private int mediaType = 0;
    private TextView nameExView;
    private TextView nameView;
    private OnMediaClickListener onMediaClickListener;
    private PersonInfo mPersonInfo;
    private MediaImageView mPosterImage;
    // private boolean showMask = true;
    // private boolean showText = true;
    private TextView statusView;

    // private TelevisionInfo televisionInfo;

    public MediaView(Context context, int mediaType) {
        super(context);
        this.mediaType = mediaType;
        init();
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mContext = getContext();
        setOrientation(LinearLayout.VERTICAL);
        FrameLayout frameLayout = new FrameLayout(mContext);
        int posterWidth = 0;
        int posterHeight = 0;
        if (mediaType == COVER_TYPE) {
            posterWidth = getResources().getDimensionPixelSize(R.dimen.poster_width);
            posterHeight = getResources().getDimensionPixelSize(R.dimen.poster_height);
        } else if (mediaType == BANNER_TYPE) {
            posterWidth = FrameLayout.LayoutParams.MATCH_PARENT;
            posterHeight = getResources().getDimensionPixelSize(R.dimen.banner_height);
        }

        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(posterWidth, posterHeight));

        mPosterImage = new MediaImageView(mContext);
//         mPosterImage.setMediaImageReadyCallback(this);
        FrameLayout.LayoutParams lp1 =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);

        lp1.gravity = Gravity.CENTER;
        int posterMargin = mContext.getResources().getDimensionPixelSize(R.dimen.poster_margin);
        lp1.bottomMargin = posterMargin;
        lp1.topMargin = posterMargin;
        lp1.rightMargin = posterMargin;
        lp1.leftMargin = posterMargin;
        mPosterImage.setLayoutParams(lp1);
        mPosterImage.setScaleType(ImageView.ScaleType.FIT_XY);

        mBorderImage = new ImageView(mContext);
        FrameLayout.LayoutParams lp2 =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        lp2.gravity = 81;
        mBorderImage.setLayoutParams(lp2);
        mBorderImage.setScaleType(ImageView.ScaleType.FIT_XY);

        mMaskImage = new ImageView(mContext);
        FrameLayout.LayoutParams lp3 =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        lp3.gravity = Gravity.CENTER;
        mMaskImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mMaskImage.setLayoutParams(lp3);
        // setMediaType(this.mediaType);
        frameLayout.addView(mBorderImage);
        frameLayout.addView(mMaskImage);
        frameLayout.addView(mPosterImage);
        addView(frameLayout);

        if ((mediaType == COVER_TYPE)) {
            this.infoView = View.inflate(mContext, R.layout.media_item_info, null);
            LinearLayout.LayoutParams localLayoutParams =
                    new LinearLayout.LayoutParams(posterWidth,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            this.infoView.setLayoutParams(localLayoutParams);
            this.nameView = ((TextView) this.infoView.findViewById(R.id.media_item_name));
            this.statusView = ((TextView) this.infoView.findViewById(R.id.media_item_status));
            this.nameExView = ((TextView) this.infoView.findViewById(R.id.media_item_nameex));
            addView(this.infoView);
        }
        setDefaultPoster();
    }

    public OnMediaClickListener getOnMediaClickListener() {
        return this.onMediaClickListener;
    }

    public void onClick(View paramView) {
        if (this.onMediaClickListener != null) {
            // if (this.mediaInfo == null)
            // break label29;
            this.onMediaClickListener.onMediaClick(this, this.mediaInfo);
        }
        // label29:
        // do
        // {
        // return;
        // if (this.personInfo != null)
        // {
        // this.onMediaClickListener.onMediaClick(this, this.personInfo);
        // return;
        // }
        // if (this.albumInfo == null)
        // continue;
        // this.onMediaClickListener.onMediaClick(this, this.albumInfo);
        // return;
        // }
        // while (this.televisionInfo == null);
        // this.onMediaClickListener.onMediaClick(this, this.televisionInfo);
    }

    public void setBannerMedia(Banner banner) {
        if (banner != null) {
            // if (mediaType == 0) {
            setMediaInfo(banner.mediaInfo);
            // } else if (mediaType == 1) {
            // setPersonInfo(banner.personInfo);
            // } else if (mediaType == 100) {
            // setAlbumInfo(banner.albumInfo);
            // }
        }
        // if (banner != null)
        // {
        // if (banner.mediaType != 0)
        // break label20;
        // setMediaInfo(banner.mediaInfo);
        // }
        // label20:
        // do
        // {
        // return;
        // if (paramBanner.mediaType != 1)
        // continue;
        // setPersonInfo(paramBanner.personInfo);
        // return;
        // }
        // while (paramBanner.mediaType != 100);
        // setAlbumInfo(paramBanner.albumInfo);
    }

    private String getStatusDesc(MediaInfo meidaInfo) {
        StringBuilder sb = new StringBuilder();
        if ("综艺".equals(mediaInfo.category)) {
            if (!Util.isEmpty(meidaInfo.lastIssueDate)) {
                String[] arrayOfString = meidaInfo.lastIssueDate.split("-");
                if (arrayOfString.length >= 3) {
                    sb.append(arrayOfString[1] + "月");
                    sb.append(arrayOfString[2] + "日");
                    sb.append("更新");
                }
            }
        } else if ((meidaInfo.setCount > 1) || (meidaInfo.setNow > 1)) {
            if (meidaInfo.setNow == meidaInfo.setCount) {
                sb.append(meidaInfo.setCount + "集全");
            } else {
                sb.append("更新至" + meidaInfo.setNow + "集");
            }
        }
        return sb.toString();
    }
    private BitmapCachePool mBitmapCache;
    public void setMediaInfo(MediaInfo mediaInfo) {
        if (mediaInfo != null) {
            this.mediaInfo = mediaInfo;

            String url = Util.replaceString(mediaInfo.smallImageURL.imageUrl, "\\", "").trim();
            android.util.Log.d("XXXXXXXXXX", "setMediaInfo url = "
                    + url + " mPosterImage = " + mPosterImage);
//            ((CoreActivity) mContext).getService().getBitmapCache();
            mBitmapCache = new BitmapCachePool(20 * 1024 * 1024);//--------------临时解决方案
            ImageLoader.loadImage(mBitmapCache,
                    mPosterImage, url);
            if (this.infoView != null) {
                if ((!"综艺".equals(mediaInfo.category)) && (mediaInfo.setCount <= 1)
                        && (mediaInfo.setNow <= 1)) {
                    this.nameView.setVisibility(4);
                    this.statusView.setVisibility(4);
                    this.nameExView.setVisibility(0);
                    this.nameExView.setText(mediaInfo.mediaName);
                } else {
                    this.nameView.setVisibility(0);
                    this.statusView.setVisibility(0);
                    this.nameExView.setVisibility(4);
                    this.nameView.setText(mediaInfo.mediaName);
                    String str = getStatusDesc(mediaInfo);
                    if (!Util.isEmpty(str)) {
                        this.statusView.setText(str);
                    } else {
                        this.statusView.setText("");
                    }
                }
            }
        }
    }

    public void setAlbumInfo(AlbumInfo albumInfo) {
        if (albumInfo != null) {
            mAlbumInfo = albumInfo;
            String url = Util.replaceString(albumInfo.posterUrl.imageUrl, "\\", "").trim();
            ImageLoader.loadImage(((CoreActivity) mContext).getService().getBitmapCache(),
                    mPosterImage, url);
        }
    }

    public void setPersonInfo(PersonInfo personInfo) {
        if (personInfo != null) {
            mPersonInfo = personInfo;
            String url = Util.replaceString(personInfo.bigImageUrl.imageUrl, "\\", "").trim();
            ImageLoader.loadImage(((CoreActivity) mContext).getService().getBitmapCache(),
                    mPosterImage, url);
            // nameView.setText(personInfo.getName());
        }
    }

    public void setDefaultPoster() {
        if (this.mediaType == 1) {
            mPosterImage.setImageResource(R.drawable.banner_default_cover);
        } else {
            mPosterImage.setImageResource(R.drawable.default_cover);
        }

        // mPosterImage.setImageResource(R.drawable.a);
        // ImageView localImageView;
        // if (this.mediaType == 1)
        // {
        // this.posterImage.setImageResource(2130837509);
        // localImageView = this.maskImage;
        // if (!this.showMask)
        // break label79;
        // }
        // label79: for (int i = 0; ; i = 8)
        // {
        // localImageView.setVisibility(i);
        // return;
        // if (this.mediaType == 0)
        // {
        // this.posterImage.setImageResource(2130837563);
        // break;
        // }
        // if (this.mediaType != 2)
        // break;
        // this.posterImage.setImageResource(2130837754);
        // break;
        // }
    }

    public static abstract interface OnMediaClickListener {
        public abstract void onMediaClick(MediaView paramMediaView, Object paramObject);
    }

    public void setOnMediaClickListener(OnMediaClickListener paramOnMediaClickListener) {
        // TODO Auto-generated method stub
        this.onMediaClickListener = paramOnMediaClickListener;
    }
    
    public ImageView getPosterImage()
    {
      return this.mPosterImage;
    }

}
