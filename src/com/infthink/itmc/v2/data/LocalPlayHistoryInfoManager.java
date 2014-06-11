package com.infthink.itmc.v2.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.infthink.itmc.v2.type.LocalPlayHistory;
import com.infthink.itmc.v2.util.SharedPreferencesUtil;

public class LocalPlayHistoryInfoManager {
    private static LocalPlayHistoryInfoManager sLocalPlayHistoryInfoManger;
    private List<LocalPlayHistory> mLocalPlayHistorys;

    private LocalPlayHistoryInfoManager(Context context) {
        mLocalPlayHistorys = getHistoryVideos(context);
    }

    public static LocalPlayHistoryInfoManager getInstance(Context context) {
        if (sLocalPlayHistoryInfoManger == null) {
            sLocalPlayHistoryInfoManger = new LocalPlayHistoryInfoManager(context);
        }
        return sLocalPlayHistoryInfoManger;
    }
    
    public List<LocalPlayHistory> getHistoryVideos(Context context) {
        return SharedPreferencesUtil.getHistoryVideos(context);
    }
    
    public void saveHistory(Context context, String mediaId, int meidaCi, String playSeconds, String playDate, int mediaSource, String videoName, String mediaUrl, String html5Page, String imageUrl) {
        String value = SharedPreferencesUtil.createHistoryValue(mediaId, meidaCi, playSeconds, playDate, mediaSource, videoName, mediaUrl, html5Page, imageUrl);
        SharedPreferencesUtil.recordHistory(context, mediaId, value);
        mLocalPlayHistorys = getHistoryVideos(context);
    }

    public void clearHistory(Context context) {
        mLocalPlayHistorys.clear();
        SharedPreferencesUtil.clearHistory(context);
    }
    
    public LocalPlayHistory getHistoryById(String mediaId) {
        if (mLocalPlayHistorys != null) {
            for (LocalPlayHistory history : mLocalPlayHistorys) {
                if (history.mediaId.equals(mediaId)) {
                    return history;
                }
            }
        }
        return null;
    }
}
