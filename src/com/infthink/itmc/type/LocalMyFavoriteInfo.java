package com.infthink.itmc.type;

import java.util.Hashtable;
import java.util.Vector;

import com.infthink.itmc.MediaDetailActivity;
import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.data.DataManager.IOnloadListener;
import com.infthink.itmc.service.CoreService;
import com.infthink.libs.base.BaseActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class LocalMyFavoriteInfo {
    private static LocalMyFavoriteInfo sLocalMyFavoriteInfo;
    private static DataManager mDataManager;
    private Vector<LocalMyFavoriteItemInfo> myFavoriteItemInfoVector = new Vector();
    private Hashtable<String, LocalMyFavoriteItemInfo> myFavoriteLocalMediaCache = new Hashtable();
    private Hashtable<Integer, LocalMyFavoriteItemInfo> myFavoriteNetworkMediaCache =
            new Hashtable();

    public static LocalMyFavoriteInfo getInstance(Context context) {
        try {
            if (sLocalMyFavoriteInfo == null) {
                sLocalMyFavoriteInfo = new LocalMyFavoriteInfo();
                mDataManager = ((BaseActivity<CoreService>) context).getService().getDataManager();
            }
            LocalMyFavoriteInfo localLocalMyFavoriteInfo = sLocalMyFavoriteInfo;
            return localLocalMyFavoriteInfo;
        } finally {}
    }
    
    private boolean addMyFavoriteItem(LocalMyFavoriteItemInfo paramLocalMyFavoriteItemInfo, boolean paramBoolean)
    {
      if (paramLocalMyFavoriteItemInfo == null){
          
      }
      return paramBoolean;
    }
    
    public boolean saveMyFavoriteInfo(Context paramContext, LocalMyFavoriteItemInfo paramLocalMyFavoriteItemInfo){
        WifiManager wifi = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        paramLocalMyFavoriteItemInfo.deviceid = macAddress;
//        mDataManager.
        mDataManager.loadBanner("1", new IOnloadListener<Banner[]>() {
            
            @Override
            public void onLoad(Banner[] entity) {
                // TODO Auto-generated method stub
                
            }
        });
        return false;
    }
    
}
