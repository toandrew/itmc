package com.infthink.itmc.v2.type;

import java.io.Serializable;

public class BaseLocalFavorite implements Comparable<BaseLocalFavorite>,
        Serializable {
    private static final String TAG = BaseLocalFavorite.class
            .getSimpleName();
    private static final long serialVersionUID = 1L;
//    public int mType = PlayHistoryType.PLAYHISTORY_TYPE_UNKNOWN;
    public String addDate;

    @Override
    public int compareTo(BaseLocalFavorite fav) {
        if (fav == null)
            return -1;
        if (Long.parseLong(addDate) < Long.parseLong(fav.addDate)) {
            return 1;
        }
        return 1;
    }

//    public String getPlayHistoryDate() {
//        return playDate;
//    }
//
//    public int getPlayHistoryType() {
//        return mType;
//    }
//
//    public boolean isMediaPlayHistory() {
//        return (mType == PlayHistoryType.PLAYHISTORY_TYPE_MEDIA);
//    }
//
//    public static final class PlayHistoryType {
//        public static final int PLAYHISTORY_TYPE_MEDIA = 0;
//        public static final int PLAYHISTORY_TYPE_UNKNOWN = -1;
//    }
}
