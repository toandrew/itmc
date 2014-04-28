package com.infthink.itmc;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;

import com.infthink.itmc.data.NetcastManager;
import com.infthink.libs.base.BaseApplication;
import com.infthink.libs.cache.simple.BitmapCachePool;
import com.infthink.libs.common.message.MessageManager;
import com.infthink.netcast.sdk.ApplicationSession;
import com.infthink.netcast.sdk.CastDevice;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.http.HttpResponseCache;
import android.util.Log;

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
    private ApplicationSession mSession;
    private CastDevice mDevice;
    private String mCastAppName;
    private static NetcastManager sNetcastManager;
    private int mMode = MODE_UNDEFINED;
    
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
    
    public static NetcastManager getNetcastManager() {
        return sNetcastManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        sResources = getResources();
        sNetcastManager = new NetcastManager(sContext);

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
        
        sNetcastManager.destroy();
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

    public ApplicationSession getCastSession() {
        return mSession;
    }

    public void setCastSession(ApplicationSession session) {
        mSession = session;
    }

    public CastDevice getCastDevice() {
        return mDevice;
    }

    public void setCastDevice(CastDevice device) {
        mDevice = device;
    }

    public String getCastAppName() {
        return mCastAppName;
    }

    public void setCastAppName(String appname) {
        mCastAppName = appname;
    }
}
