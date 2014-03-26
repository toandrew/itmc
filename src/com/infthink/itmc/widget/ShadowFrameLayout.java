package com.infthink.itmc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ShadowFrameLayout extends FrameLayout {

    public ShadowFrameLayout(Context context) {
        super(context);
    }
    
    public ShadowFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ShadowFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int layer = canvas.saveLayer(0.0F, 0.0F, width, height, null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(layer);
    }

    
}
