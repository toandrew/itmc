package com.infthink.itmc.v2.widget;

import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.v2.util.Util;

import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.text.TextUtils;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class ActorsView extends LinearLayout
        implements
            View.OnClickListener,
            ViewTreeObserver.OnGlobalLayoutListener {
    private static int ACTOR_VIEW_WIDTH = 0;
    private static final int MAX_ACTORS_LINE = 3;
    private static final int MAX_ROW_ACTORS_COUNT = 4;
    private List<ActorMetaInfo> actorMetaInfoList;
    private int actorNamePaddingH = 0;
    private List<LinearLayout> actorRowLayoutList;
    private boolean bLayoutFinished = false;
    private Context context;
    private int maxActorsLine = 3;
    private OnActorViewClickListener onActorViewClickListener;

    public ActorsView(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public ActorsView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public ActorsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private void init(Context paramContext) {
        this.context = paramContext;
        Resources localResources = paramContext.getResources();
        this.actorNamePaddingH = localResources.getDimensionPixelSize(2131296496);
        int i = localResources.getDimensionPixelSize(2131296518);
        int j = localResources.getDimensionPixelSize(2131296519);
        int k = localResources.getDimensionPixelSize(2131296520);
        int m = localResources.getDimensionPixelSize(2131296521);
        int n = localResources.getInteger(2131361806);
        int i1 = localResources.getInteger(2131361807);
        int i2 = localResources.getInteger(2131361808);
        int i3 = localResources.getColor(2131230735);
        int i4 = localResources.getColor(2131230720);
        float f = localResources.getDimensionPixelSize(2131296415);
        this.actorMetaInfoList = new ArrayList();
        this.actorRowLayoutList = new ArrayList();
        for (int i5 = 0; i5 < 3; i5++) {
            LinearLayout localLinearLayout = new LinearLayout(paramContext);
            localLinearLayout.setOrientation(0);
            LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(-1, -1);
            localLinearLayout.setLayoutParams(localLayoutParams1);
            if ((i5 == 1) || (i5 == 2)) localLayoutParams1.topMargin = i;
            localLayoutParams1.leftMargin = j;
            for (int i6 = 0; i6 < 4; i6++) {
                Button localButton = new Button(paramContext);
                localButton.setFocusable(false);
                localButton.setFocusableInTouchMode(false);
                localButton.setOnClickListener(this);
                localButton.setEllipsize(TextUtils.TruncateAt.END);
                localButton.setBackgroundResource(2130837504);
                localButton.setGravity(1);
                localButton.setPadding(0, k, 0, 0);
                LinearLayout.LayoutParams localLayoutParams2 =
                        new LinearLayout.LayoutParams(-2, -2);
                if (i6 != 0) localLayoutParams2.leftMargin = m;
                localButton.setLayoutParams(localLayoutParams2);
                localButton.setTextSize(0, f);
                localButton.setTextColor(i4);
                localButton.setShadowLayer(i2, n, i1, i3);
                localLinearLayout.addView(localButton, i6);
            }
            addView(localLinearLayout, i5);
            this.actorRowLayoutList.add(localLinearLayout);
        }
        setOrientation(1);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void layoutActorViews() {
        for (int i = 0; i < 3; i++) {
            LinearLayout localLinearLayout2 = (LinearLayout) this.actorRowLayoutList.get(i);
            for (int i3 = 0; i3 < 4; i3++)
                localLinearLayout2.getChildAt(i3).setVisibility(8);
            localLinearLayout2.setVisibility(8);
        }
        int j = this.actorMetaInfoList.size();
        if (j == 0) return;
        int k = 0;
        int m = 0;
        int n = 0;
        LinearLayout localLinearLayout1 = null;
        int i1 = 0;
        label91: if ((i1 < j) && (m < this.maxActorsLine)) {
            ActorMetaInfo localActorMetaInfo = (ActorMetaInfo) this.actorMetaInfoList.get(i1);
            int i2 = localActorMetaInfo.nameWidth + 2 * this.actorNamePaddingH;
            k += i2;
            if (k > ACTOR_VIEW_WIDTH) k -= this.actorNamePaddingH;
//            if ((k > ACTOR_VIEW_WIDTH) && (n != 0)) break label259;
            if (localLinearLayout1 == null) {
                localLinearLayout1 = (LinearLayout) this.actorRowLayoutList.get(m);
                localLinearLayout1.setVisibility(0);
            }
            Button localButton = (Button) localLinearLayout1.getChildAt(n);
            if (localButton != null) {
                localButton.setVisibility(0);
                localButton.setText(localActorMetaInfo.actorName);
                localButton.setTag(localActorMetaInfo.actorName);
                ((LinearLayout.LayoutParams) localButton.getLayoutParams()).width = i2;
                n++;
            }
        }
        while (true) {
//            i1++;
//            break label91;
//            break;
//            label259: m++;
//            n = 0;
//            localLinearLayout1 = null;
//            k = 0;
        }
    }

    public static void resetActorViewWidth() {
        ACTOR_VIEW_WIDTH = 0;
    }

    public void onClick(View paramView) {
        if (this.onActorViewClickListener != null)
            this.onActorViewClickListener.onActorViewClick(paramView.getTag().toString());
    }

    public void onGlobalLayout() {
        if (this.bLayoutFinished) return;
        ACTOR_VIEW_WIDTH = getWidth();
        layoutActorViews();
        this.bLayoutFinished = true;
    }

    public void setActorViewClickable(boolean paramBoolean) {
        int i = 0;
        if (i < 3) {
            LinearLayout localLinearLayout = (LinearLayout) this.actorRowLayoutList.get(i);
            if (!localLinearLayout.isShown()) ;
            while (true) {
//                i++;
//                break;
//                for (int j = 0; j < 4; j++) {
//                    Button localButton = (Button) localLinearLayout.getChildAt(j);
//                    if (!localButton.isShown()) continue;
//                    localButton.setEnabled(paramBoolean);
//                }
            }
        }
    }

    public void setActors(String paramString) {
        this.actorMetaInfoList.clear();
        String[] arrayOfString = paramString.split(" ");
        int i = arrayOfString.length;
        Button localButton = new Button(this.context);
        localButton.setTextSize(0, this.context.getResources().getDimensionPixelSize(2131296415));
        TextPaint localTextPaint = localButton.getPaint();
        int j = 0;
        if (j < i) {
            if (Util.isEmpty(arrayOfString[j])) ;
            while (true) {
                j++;
                break;
//                ActorMetaInfo localActorMetaInfo = new ActorMetaInfo(null);
//                localActorMetaInfo.actorName = arrayOfString[j];
//                localActorMetaInfo.nameWidth = (int) localTextPaint.measureText(arrayOfString[j]);
//                this.actorMetaInfoList.add(localActorMetaInfo);
            }
        }
        if (ACTOR_VIEW_WIDTH != 0) {
            layoutActorViews();
            return;
        }
        this.bLayoutFinished = false;
        requestLayout();
    }

    public void setOnActorViewClickListener(OnActorViewClickListener paramOnActorViewClickListener) {
        this.onActorViewClickListener = paramOnActorViewClickListener;
    }

    private class ActorMetaInfo {
        public String actorName;
        public int nameWidth;

        private ActorMetaInfo() {}
    }

    public static abstract interface OnActorViewClickListener {
        public abstract void onActorViewClick(String paramString);
    }
}
