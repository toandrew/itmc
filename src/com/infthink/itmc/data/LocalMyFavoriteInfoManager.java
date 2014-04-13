package com.infthink.itmc.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.data.DataManager.IOnloadListener;
import com.infthink.itmc.service.CoreService;
import com.infthink.itmc.type.LocalMyFavoriteItemInfo;
import com.infthink.itmc.type.LocalPlayHistory;
import com.infthink.itmc.util.SharedPreferencesUtil;
import com.infthink.libs.base.BaseActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

public class LocalMyFavoriteInfoManager {
    private static LocalMyFavoriteInfoManager sLocalMyFavoriteInfo;
    private static DataManager mDataManager;

    private Vector<Integer> myFavoriteAddItemInfoVector = new Vector<Integer>();
    private Vector<Integer> myFavoriteRemoveItemInfoVector = new Vector<Integer>();

    // private Hashtable<Integer, LocalMyFavoriteItemInfo> myFavoriteLocalMediaCache =
    // new Hashtable<Integer, LocalMyFavoriteItemInfo>();
    private Hashtable<Integer, LocalMyFavoriteItemInfo> myFavoriteNetworkMediaCache =
            new Hashtable<Integer, LocalMyFavoriteItemInfo>();
    private List<LocalMyFavoriteItemInfo> myFavoriteLocalMediaCache =
            new ArrayList<LocalMyFavoriteItemInfo>();

    public static final int MSG_ADD_FAVORITE = 1;
    public static final int MSG_REMOVE_FAVORITE = 2;
    public static final int MSG_GET_FAVORITE = 3;

    String macAddress;

    private LocalMyFavoriteInfoManager(Context context) {
        // myFavoriteLocalMediaCache = getFavoriteVideos(context);
        mHandler.sendEmptyMessageDelayed(MSG_ADD_FAVORITE, 60 * 1000);
        mHandler.sendEmptyMessageDelayed(MSG_GET_FAVORITE, 10 * 1000);
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifi.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();
    }
    
    public List<LocalMyFavoriteItemInfo> getFavVideos(Context context) {
        return myFavoriteLocalMediaCache;
    }


    private Hashtable<Integer, LocalMyFavoriteItemInfo> getFavoriteVideos(Context context) {
        // TODO Auto-generated method stub
        return null;
    }


    public static LocalMyFavoriteInfoManager getInstance(Context context) {
        try {
            if (sLocalMyFavoriteInfo == null) {
                sLocalMyFavoriteInfo = new LocalMyFavoriteInfoManager(context);
                mDataManager = ((BaseActivity<CoreService>) context).getService().getDataManager();
            }
            LocalMyFavoriteInfoManager localLocalMyFavoriteInfo = sLocalMyFavoriteInfo;
            return localLocalMyFavoriteInfo;
        } finally {}
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD_FAVORITE:
                    uploadFav();
                    break;
                case MSG_REMOVE_FAVORITE:
                    deleteFav();
                case MSG_GET_FAVORITE:
                    getFavList();
                    break;
            }
        }
    };

    private void getFavList() {
        mDataManager.loadFav(macAddress, new IOnloadListener<List<LocalMyFavoriteItemInfo>>() {

            @Override
            public void onLoad(List<LocalMyFavoriteItemInfo> entity) {
                // TODO Auto-generated method stub
                myFavoriteLocalMediaCache = entity;
            }
        });
        mHandler.sendEmptyMessageDelayed(MSG_GET_FAVORITE, 10 * 60 * 1000);
    }

    private void uploadFav() {
        android.util.Log.d("XXXXXXXXXX", "uploadFav");
        // Collections.reverse(myFavoriteAddItemInfoVector);
        final Vector myFavoriteAddItemInfoVectorTemp = new Vector(myFavoriteAddItemInfoVector);
        myFavoriteAddItemInfoVector.clear();
        for (int i = 0; i < myFavoriteAddItemInfoVectorTemp.size(); i++) {
            final Integer mediaID = (Integer) myFavoriteAddItemInfoVectorTemp.get(i);
            String deviceid = macAddress;
            android.util.Log.d("XXXXXXXXXX", "uploadFav mediaID  = " + mediaID);
            mDataManager.uploadFavorite(deviceid, mediaID, new IOnloadListener<Integer>() {
                @Override
                public void onLoad(Integer entity) {
                    // TODO Auto-generated method stub
                    if (entity == 0) {
                        android.util.Log.d("XXXXXXXXXX", "uploadFav  = " + mediaID);
                        myFavoriteAddItemInfoVectorTemp.removeElement(mediaID);
                    } else {
                        myFavoriteAddItemInfoVector.add(mediaID);
                        mHandler.sendEmptyMessageDelayed(MSG_ADD_FAVORITE, 60 * 1000);
                    }
                }
            });
        }
        for (int i = 0; i < myFavoriteAddItemInfoVector.size(); i++) {
            Integer mediaID = myFavoriteAddItemInfoVector.get(i);
            android.util.Log.d("XXXXXXXXXX", "myFavoriteAddItemInfoVector －－－uploadFav  = "
                    + mediaID);
        }
        mHandler.sendEmptyMessageDelayed(MSG_ADD_FAVORITE, 60 * 1000);
    }

    private void deleteFav() {
        android.util.Log.d("XXXXXXXXXX", "deleteFav");
        // Collections.reverse(myFavoriteRemoveItemInfoVector);
        final Vector myFavoriteRemoveItemInfoVectorTemp =
                new Vector(myFavoriteRemoveItemInfoVector);
        myFavoriteRemoveItemInfoVector.removeAllElements();
        for (int i = 0; i < myFavoriteRemoveItemInfoVectorTemp.size(); i++) {
            final Integer mediaID = (Integer) myFavoriteRemoveItemInfoVectorTemp.get(i);

            String deviceid = macAddress;
            mDataManager.deleteFavorite(deviceid, mediaID, new IOnloadListener<Integer>() {
                @Override
                public void onLoad(Integer entity) {
                    // TODO Auto-generated method stub
                    if (entity == 0) {
                        android.util.Log.d("XXXXXXXXXX", "deleteFav  = " + mediaID);
                        myFavoriteRemoveItemInfoVectorTemp.removeElement(mediaID);
                    } else {
                        myFavoriteRemoveItemInfoVector.add(mediaID);
                        mHandler.sendEmptyMessageDelayed(MSG_REMOVE_FAVORITE, 60 * 1000);
                    }
                }
            });
        }
        for (int i = 0; i < myFavoriteRemoveItemInfoVector.size(); i++) {
            Integer mediaID = myFavoriteRemoveItemInfoVector.get(i);
            android.util.Log.d("XXXXXXXXXX", "myFavoriteRemoveItemInfoVector －－－mediaID  = "
                    + mediaID);
        }

    }


    public boolean addMyFavoriteInfo(Context paramContext,
            LocalMyFavoriteItemInfo paramLocalMyFavoriteItemInfo) {
        WifiManager wifi = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        paramLocalMyFavoriteItemInfo.deviceid = macAddress;
        for (int i = 0; i < myFavoriteLocalMediaCache.size(); i++) {
            LocalMyFavoriteItemInfo localMyFavoriteItemInfo = myFavoriteLocalMediaCache.get(i);
            if (localMyFavoriteItemInfo.mediaId == paramLocalMyFavoriteItemInfo.mediaId) {
                myFavoriteLocalMediaCache.remove(i);
            }
        }
        myFavoriteLocalMediaCache.add(0, paramLocalMyFavoriteItemInfo);
        // myFavoriteLocalMediaCache.add(paramLocalMyFavoriteItemInfo);
        myFavoriteRemoveItemInfoVector.removeElement(paramLocalMyFavoriteItemInfo.mediaId);
        if (!myFavoriteAddItemInfoVector.contains(paramLocalMyFavoriteItemInfo.mediaId)) {
            myFavoriteAddItemInfoVector.add(paramLocalMyFavoriteItemInfo.mediaId);
        }
        // mHandler.removeMessages(MSG_ADD_FAVORITE);
        // mHandler.sendEmptyMessageDelayed(MSG_ADD_FAVORITE, 10 * 1000);
        // Collections.reverse(myFavoriteLocalMediaCache);
        for (int i = 0; i < myFavoriteLocalMediaCache.size(); i++) {
            LocalMyFavoriteItemInfo localMyFavoriteItemInfo = myFavoriteLocalMediaCache.get(i);
            android.util.Log.d("XXXXXXXXXX", "myFavoriteLocalMediaCache －－－mediaName  = "
                    + localMyFavoriteItemInfo.mediaInfo.mediaName);
        }
        return true;
    }

    public boolean checkIsFavorite(Context paramContext, int mediaId) {
        Iterator<LocalMyFavoriteItemInfo> iterator = myFavoriteLocalMediaCache.iterator();
        while (iterator.hasNext()) {
            LocalMyFavoriteItemInfo localMyFavoriteItemInfo = iterator.next();
            android.util.Log.d("XXXXXXXXX", "checkIsFavorite mediaName = " + localMyFavoriteItemInfo.mediaInfo.mediaName);
            if(mediaId == localMyFavoriteItemInfo.mediaInfo.mediaID){
                return true;
            }
           
        }
        return false;
    }

    public boolean deleteMyFavoriteInfo(Context paramContext,
            LocalMyFavoriteItemInfo paramLocalMyFavoriteItemInfo) {
        WifiManager wifi = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        paramLocalMyFavoriteItemInfo.deviceid = macAddress;

        myFavoriteAddItemInfoVector.removeElement(paramLocalMyFavoriteItemInfo.mediaId);
        if (!myFavoriteRemoveItemInfoVector.contains(paramLocalMyFavoriteItemInfo.mediaId)) {
            myFavoriteRemoveItemInfoVector.add(paramLocalMyFavoriteItemInfo.mediaId);
        }
        for (int i = 0; i < myFavoriteLocalMediaCache.size(); i++) {
            LocalMyFavoriteItemInfo localMyFavoriteItemInfo = myFavoriteLocalMediaCache.get(i);
            if (localMyFavoriteItemInfo.mediaId == paramLocalMyFavoriteItemInfo.mediaId) {
                myFavoriteLocalMediaCache.remove(i);
            }
        }
        mHandler.removeMessages(MSG_REMOVE_FAVORITE);
        mHandler.sendEmptyMessageDelayed(MSG_REMOVE_FAVORITE, 60 * 1000);

        return true;
    }
}
