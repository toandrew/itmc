package com.infthink.itmc.v2.type;

import java.io.Serializable;

public abstract class MediaCategoryInfo implements Serializable {
    private static final long serialVersionUID = -5144938793067562452L;
    protected int localMediaCategoryType = -1;

    protected String formatLocalMediaSize(long paramLong) {
        long l1 = 1024L * 1024L;
        long l2 = l1 * 1024L;
        if (paramLong < 1024L) {
            Object[] arrayOfObject4 = new Object[1];
            arrayOfObject4[0] = Long.valueOf(paramLong);
            return String.format("%d B", arrayOfObject4);
        }
        if (paramLong < l1) {
            Object[] arrayOfObject3 = new Object[1];
            arrayOfObject3[0] = Long.valueOf(paramLong / 1024L);
            return String.format("%.2f KB", arrayOfObject3);
        }
        if (paramLong < l2) {
            Object[] arrayOfObject2 = new Object[1];
            arrayOfObject2[0] = Long.valueOf(paramLong / l1);
            return String.format("%.2f MB", arrayOfObject2);
        }
        Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = Long.valueOf(paramLong / l2);
        return String.format("%.2f GB", arrayOfObject1);
    }

    public abstract String getCategoryDesc();

    public abstract int getLocalMediaCount();

    public abstract String getLocalMediaSize();

    public abstract String getMediaCategoryTitle();

    public abstract String getMediaParentTitle();

    public int getlocalMediaCategoryType() {
        return this.localMediaCategoryType;
    }

    public abstract boolean isMyFavorite();

    public boolean isSelectable() {
        boolean flag = true;
        if ((this.localMediaCategoryType == 1)
                || (this.localMediaCategoryType == 2)
                || (this.localMediaCategoryType == 0)
                || (this.localMediaCategoryType == 6)
                || (this.localMediaCategoryType == 7))
            flag = false;
        return flag;
    }

    public abstract void setIsMyFavorite(boolean paramBoolean);
}
