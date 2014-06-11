package com.infthink.itmc.v2;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;

import com.firefly.sample.castcompanionlibrary.cast.VideoCastManager;
import com.fireflycast.cast.Cast;
import com.infthink.itmc.v2.data.ChromeCastManager;
import com.infthink.libs.base.BaseApplication;
import com.infthink.libs.common.message.MessageManager;

import android.content.Context;
import android.content.res.Resources;
import android.net.http.HttpResponseCache;

public class ITApp extends BaseApplication {
    public static final int MODE_UNDEFINED = -1;
    public static final int MODE_COMMON = 0;
    public static final int MODE_MP4 = 1;
    public static final int MODE_FLV = 2;
    
    private static Context sContext;
    private static ITApp sInstance;
    private static Resources sResources;
    private static String sInternalCachePath;
    private static String sExternalCachePath;
    private static HashMap<Integer, String> sChannelMap;
    private String mCastAppName;
//    private static NetcastManager sNetcastManager;
    private int mMode = MODE_UNDEFINED;
    private static ChromeCastManager sCastManager;
    private static VideoCastManager mCastMgr = null;

    public ITApp() {
        sInstance = this;
    }

    public static ITApp getInstance() {
        return sInstance;
    }
    
    public void setMode(int mode) {
        mMode = mode;
    }
    
    public int getMode() {
        return mMode;
    }
    
    public static final Context context() {
        return sContext;
    }

    public static Resources getR() {
        return sResources;
    }
    
    public static HashMap<Integer, String> getChannelMap() {
        return sChannelMap;
    }
    
    public static void setChannelMap(HashMap<Integer, String> map) {
        if (sChannelMap == null) {
            sChannelMap = map;
        }
    }
    
//    public static NetcastManager getNetcastManager() {
//        return sNetcastManager;
//    }

    private static String APPLICATION_ID;
    @Override
    public void onCreate() {
        super.onCreate();
        
        //TODO: id
        APPLICATION_ID = Cast.CastApi.makeApplicationId("http://castapp.infthink.com/mediaplayer/index.html");

        sContext = getApplicationContext();
        sResources = getResources();
//        sNetcastManager = new NetcastManager(sContext);

        try {
            File externalCacheDir = getExternalCacheDir();
            if (externalCacheDir != null) {
                File httpCacheDir = new File(externalCacheDir, "http");
                long httpCacheSize = 30 * 1024 * 1024; // 30 MiB
                HttpResponseCache.install(httpCacheDir, httpCacheSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

//        MessageManager.init(this);

    }

    public static VideoCastManager getCastManager(Context context) {
        if (null == mCastMgr) {
            mCastMgr = VideoCastManager.initialize(context, APPLICATION_ID,
                    null, null);
            mCastMgr.enableFeatures(
                    VideoCastManager.FEATURE_NOTIFICATION |
                            VideoCastManager.FEATURE_LOCKSCREEN |
                            VideoCastManager.FEATURE_DEBUGGING);

        }
        mCastMgr.setContext(context);
        mCastMgr.setStopOnDisconnect(false);
        return mCastMgr;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        MessageManager.close();
        
//        sNetcastManager.destroy();
    }
    
    public static String getInternalFilesDir() {
        if (sInternalCachePath == null) {
            File localFile = context().getCacheDir();
            if (localFile != null)
                sInternalCachePath = localFile.getAbsolutePath();
        }
        return sInternalCachePath;
    }
    
    public static String getExternalFilesDir() {
        if (sExternalCachePath == null) {
            File localFile = context().getExternalCacheDir();
            if (localFile != null)
                sExternalCachePath = localFile.getAbsolutePath();
        }
        return sExternalCachePath;
    }

    public static int getStatusBarHeight() {
        Resources localResources = ITApp.getR();
        try {
            float height = localResources.getDimension(localResources.getIdentifier(
                    "status_bar_height", "dimen", "android"));
            return (int) height;
        } catch (Exception localException) {
        }
        return 0;
    }

    public String getCastAppName() {
        return mCastAppName;
    }

    public void setCastAppName(String appname) {
        mCastAppName = appname;
    }
}
