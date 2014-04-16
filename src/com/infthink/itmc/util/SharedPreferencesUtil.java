package com.infthink.itmc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.infthink.itmc.type.LocalPlayHistory;

public class SharedPreferencesUtil {
    private static final String HISTORY_NAME = "history";
    private static final String VIDEO_SPLIT = "＃";
    private static final String HISTORY_SPLIT = "；";

    // value: mediaId, meidaCi, playSeconds, playDate, mediaSource, videoName, mediaUrl, html5Page
    public static String createHistoryValue(String mediaId, int meidaCi, String playSeconds, String playDate, int mediaSource, String videoName, String mediaUrl, String html5Page, String imageUrl) {
        return mediaId + VIDEO_SPLIT + meidaCi + VIDEO_SPLIT + playSeconds + VIDEO_SPLIT + playDate + VIDEO_SPLIT + mediaSource + VIDEO_SPLIT + videoName + VIDEO_SPLIT + mediaUrl + VIDEO_SPLIT + html5Page + VIDEO_SPLIT + imageUrl;
    }

    // value: mediaId, meidaCi, playSeconds, playDate, mediaSource, videoName, mediaUrl, html5Page
    public static void recordHistory(Context context, String mediaId, String value) {
        SharedPreferences sp = context.getSharedPreferences(HISTORY_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        String history = getHistory(context);

        StringBuilder sb = new StringBuilder(history);
        if (history.length() > 0) {
            String[] strs = sb.toString().split(HISTORY_SPLIT);
            for(int i = 0; i < strs.length; i++) {
                String video = strs[i];
                String[] obj = video.split(VIDEO_SPLIT);
                if (mediaId.equals(obj[0])) {
                    String newValue = history.replace(video + HISTORY_SPLIT, "");
                    sb.replace(0, sb.length(), newValue);
                    break;
                }
            }
        }

        sb.insert(0, value + HISTORY_SPLIT);

        if (sb.toString().split(HISTORY_SPLIT).length > 10) {
            if (sb.charAt(sb.length() - 1) == HISTORY_SPLIT.charAt(0)) {
                sb.deleteCharAt(sb.length() - 1);
            }
            int index = sb.lastIndexOf(HISTORY_SPLIT);
            sb.delete(index, sb.length());
        }
        editor.putString(HISTORY_NAME, sb.toString());
        editor.commit();
    }

    private static String getHistory(Context context) {
        SharedPreferences sp = context.getSharedPreferences(HISTORY_NAME, Context.MODE_PRIVATE);
        return sp.getString(HISTORY_NAME, "");
    }

    public static List<LocalPlayHistory> getHistoryVideos(Context context) {
        String history = getHistory(context);
        String[] strs = history.split(HISTORY_SPLIT);
        List<LocalPlayHistory> list = new ArrayList<LocalPlayHistory>();
        if (history != null && !history.equals("")) {
            for (int i = 0; i < strs.length; i++) {
                list.add(getHistoryVideo(strs[i]));
            }
        }
        return list;
    }

    private static LocalPlayHistory getHistoryVideo(String value) {
        String[] strs = value.split(VIDEO_SPLIT);
        LocalPlayHistory history = new LocalPlayHistory(strs[0], Integer.valueOf(strs[1]), strs[2], strs[3], Integer.valueOf(strs[4]), strs[5], strs[6], strs[7], strs[8]);
        return history;
    }

    public static void clearHistory(Context context) {
        SharedPreferences sp = context.getSharedPreferences(HISTORY_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

}
