package com.infthink.itmc.type;

import java.io.File;

import com.infthink.itmc.ITApp;

public class ITApi {
    public static final String RECOMMEND_MEDIA_FILE_NAME = "recommendmedianame.json";
    public static final String CHANNEL_LIST_FIEL_NAME = "channellist.json";
    public static final String HOME_BANNER_FILE_NAME = "banner.json";

    public static String getChannelListCachePath() {
        return ITApp.getExternalFilesDir() + File.separator + CHANNEL_LIST_FIEL_NAME;
    }
    
    public static String getRecommendMediaNameCachePath() {
        return ITApp.getExternalFilesDir() + File.separator + RECOMMEND_MEDIA_FILE_NAME;
    }
    
    public static String getHomeBannerCachePath() {
        return ITApp.getExternalFilesDir() + File.separator + HOME_BANNER_FILE_NAME;
    }
}
