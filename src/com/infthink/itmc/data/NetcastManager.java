package com.infthink.itmc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.infthink.itmc.service.CoreService;
import com.infthink.libs.common.utils.Collections;
import com.infthink.netcast.sdk.ApplicationMetadata;
import com.infthink.netcast.sdk.ApplicationSession;
import com.infthink.netcast.sdk.CastContext;
import com.infthink.netcast.sdk.CastDevice;
import com.infthink.netcast.sdk.RampConstants;
import com.infthink.netcast.sdk.ServerSearcher;
import com.infthink.netcast.sdk.SessionError;
import com.infthink.netcast.sdk.ServerSearcher.IOnSearchResultListener;

import android.util.Log;
import android.widget.BaseAdapter;

public class NetcastManager {
    private static final String TAG = NetcastManager.class.getSimpleName();
    private CoreService mService;
    private ApplicationSession mSession;
    private HashMap<String, CastDevice> mCastDeviceMap;
    private List<CastDevice> mCastDeviceList;
    private CastDevice mCurrentDevice;
    private ServerSearcher mServerSearcher;
    private CastContext mCastContext = null;

    public NetcastManager(CoreService service) {
        mService = service;
        
        mCastContext = new CastContext(service.getApplicationContext());
        mServerSearcher = new ServerSearcher();
        mCastDeviceMap = new HashMap<String, CastDevice>();
        mCastDeviceList = new ArrayList<CastDevice>();
    }

    private synchronized void addDevice(CastDevice device) {
        if (device != null) {
            mCastDeviceMap.put(device.getDeviceId(), device);
        }
    }

    public List<CastDevice> getCastDeviceList() {
        return mCastDeviceList;
    }
    
    public HashMap<String, CastDevice> getCastDeviceMap() {
        return mCastDeviceMap;
    }
    
    public void setCurrentDevice(CastDevice device) {
        mCurrentDevice = device;
    }

    public void startSearch() {
        mCastDeviceMap.clear();
        mCastDeviceList.clear();

        mServerSearcher.startSearch(mCastContext, mSearchListener);
    }
    
    private IOnSearchResultListener mSearchListener = new ServerSearcher.IOnSearchResultListener() {

        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
        }

        @Override
        public void onResult(CastDevice device) {
            Log.d(TAG, "onResult: DeviceId = " + device.getDeviceId());
            addDevice(device);
        }

        @Override
        public void onResultList(ArrayList<CastDevice> devices) {
            Log.d(TAG, "onResultList: devices size is " + devices.size());
        }
    };

    private ApplicationSession.Listener mSessionListener = new ApplicationSession.Listener() {
        @Override
        public void onSessionStarted(ApplicationMetadata applicationmetadata) {
            
        }

        @Override
        public void onSessionStartFailed(SessionError sessionerror) {
            
        }

        @Override
        public void onSessionEnded(SessionError sessionerror) {
            
        }

        @Override
        public void onAppDownloading(int percent) {

        }
    };
    
    public static abstract interface CastSearchListener {
        public abstract void onStart();
        public abstract void onFinish();
    }
}
