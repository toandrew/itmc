package com.infthink.itmc.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MediaImageView extends ImageView {
    private List<OnMediaImageReadyCallback> onMediaImageReadyCallbackList;

    public MediaImageView(Context context) {
        super(context);
    }

    public MediaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        // super.setImageBitmap(UIUtil.clipRoundCorner(DKApp.context(),
        // paramBitmap));
        if (onMediaImageReadyCallbackList != null) {
            Iterator iterator = onMediaImageReadyCallbackList.iterator();
            while (iterator.hasNext()) {
                OnMediaImageReadyCallback onMediaImageReadyCallback = (OnMediaImageReadyCallback) iterator
                        .next();
                if (onMediaImageReadyCallback != null) {
                    onMediaImageReadyCallback.onMediaImageReady(bitmap);
                }
            }
        }
    }

    public void setMediaImageReadyCallback(
            OnMediaImageReadyCallback onMediaImageReadyCallback) {
        if (onMediaImageReadyCallbackList == null)
            onMediaImageReadyCallbackList = new ArrayList();
        if (!onMediaImageReadyCallbackList.contains(onMediaImageReadyCallback))
            onMediaImageReadyCallbackList.add(onMediaImageReadyCallback);
    }

    public static abstract interface OnMediaImageReadyCallback {
        public abstract void onMediaImageReady(Bitmap paramBitmap);
    }
}
