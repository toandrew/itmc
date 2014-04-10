package com.infthink.itmc.type;

import java.io.Serializable;

public class BaseLocalPlayHistory implements Comparable<BaseLocalPlayHistory>,
        Serializable {
    private static final String TAG = BaseLocalPlayHistory.class
            .getSimpleName();
    private static final long serialVersionUID = 1L;
    public int mType = PlayHistoryType.PLAYHISTORY_TYPE_UNKNOWN;
    public String playDate;

    @Override
    public int compareTo(BaseLocalPlayHistory history) {
        if (history == null)
            return -1;
        if (Long.parseLong(playDate) < Long.parseLong(history.playDate)) {
            return 1;
        }
        return 0;
    }

    public String getPlayHistoryDate() {
        return playDate;
    }

    public int getPlayHistoryType() {
        return mType;
    }

    public boolean isMediaPlayHistory() {
        return (mType == PlayHistoryType.PLAYHISTORY_TYPE_MEDIA);
    }

    public static final class PlayHistoryType {
        public static final int PLAYHISTORY_TYPE_MEDIA = 0;
        public static final int PLAYHISTORY_TYPE_UNKNOWN = -1;
    }
}
