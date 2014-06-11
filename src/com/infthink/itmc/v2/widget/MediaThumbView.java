package com.infthink.itmc.v2.widget;

import com.infthink.itmc.v2.ITApp;
import com.infthink.itmc.v2.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class MediaThumbView extends ImageView {
    private static final int THUMB_HEIGHT;
    private static final int THUMB_WIDTH;
    private boolean bShowShadowAlways;
    private Drawable defaultDrawable;
    private View vShadowView;

    static {
        Resources localResources = ITApp.getR();
        THUMB_WIDTH = localResources.getDimensionPixelSize(R.dimen.video_thumb_width);
        THUMB_HEIGHT = localResources.getDimensionPixelSize(R.dimen.video_thumb_height);
    }
    
    public MediaThumbView(Context context) {
        super(context);
    }
    
    public MediaThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MediaThumbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isAnimShow() {
        return false;
    }

    public void setDefaultDrawable(Drawable drawable) {
        this.defaultDrawable = drawable;
    }

    public void setDefaultThumb() {
        if (defaultDrawable != null) {
            super.setImageDrawable(defaultDrawable);
        } else {
            setImageResource(R.drawable.default_videothumb);
        }
        if ((!bShowShadowAlways) && (vShadowView != null))
            vShadowView.setVisibility(View.INVISIBLE);
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            setDefaultThumb();
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if ((width > THUMB_WIDTH) || (height > THUMB_HEIGHT)) {
            if (height > width) {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, width
                        * THUMB_HEIGHT / THUMB_WIDTH);
            } else {
                bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH,
                        THUMB_HEIGHT, true);
            }
        }
        setImageDrawable(new BitmapDrawable(Resources.getSystem(), bitmap));
    }

    public void setImageDrawable(Drawable drawable) {
        if (drawable == null) {
            setDefaultThumb();
        } else {
            super.setImageDrawable(drawable);
            if ((!bShowShadowAlways) && (vShadowView != null)) {
                vShadowView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setShadowView(View view) {
        this.vShadowView = view;
    }

    public void setShadowViewShowAlways(boolean always) {
        this.bShowShadowAlways = always;
    }
}
