package com.infthink.itmc.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.infthink.itmc.ITApp;
import com.infthink.itmc.R;
import com.infthink.itmc.service.CoreService;
import com.infthink.netcast.sdk.ApplicationMetadata;
import com.infthink.netcast.sdk.ApplicationSession;
import com.infthink.netcast.sdk.CastContext;
import com.infthink.netcast.sdk.CastDevice;
import com.infthink.netcast.sdk.MediaProtocolMessageStream;
import com.infthink.netcast.sdk.MediaStatus;
import com.infthink.netcast.sdk.ServerSearcher;
import com.infthink.netcast.sdk.SessionError;
import com.infthink.netcast.sdk.ServerSearcher.IOnSearchResultListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

public class NetcastManager {
    private final static String TAG = NetcastManager.class.getSimpleName();
    private final static String APP_UUID = "Remote Player"; // same with cast
                                                            // app name
    private final static String PROTO_TYPE = "ramp";

    private CastStatusUpdateListener mCastStatusUpdateListener;
    private CastSearchListener mCastSearchListener;
    private CastSessionListener mCastSessionListener;
    private ITApp mApplication;
    private CoreService mService;
    private ApplicationSession mSession;
    private HashMap<String, CastDevice> mCastDeviceMap;
    private List<CastDevice> mCastDeviceList;
    private CastDevice mCurrentDevice;
    private ServerSearcher mServerSearcher;
    private CastContext mCastContext = null;
    private Handler mHandler;
    private Thread mThread;
    private QueryRunnable mRunnable;
    private boolean mIsSessionEstablished;
    private int mCurVolume;
    private int mLastVolume;
    private boolean mSearching = false;
    
    public NetcastManager(Context context) {
        mApplication = ITApp.getInstance();
        mCastContext = new CastContext(context);
        mServerSearcher = new ServerSearcher();
        mCastDeviceMap = new HashMap<String, CastDevice>();
        mCastDeviceList = new ArrayList<CastDevice>();
        mHandler = new Handler();
    }

    private void startSession(String appname) {
        mSession = new ApplicationSession(mCastContext, mCurrentDevice);
        mSession.setLisetner(mSessionListener);
        try {
            mSession.startSession(appname);
            // mSession.startSession(appname, new MimeData("Do what you want",
            // MimeData.TYPE_TEXT));
        } catch (IllegalStateException e) {
            Log.d(TAG, "Can not start session\nSession already created.");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "no way");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IO");
        }
    }

    public boolean connectDevice(CastDevice device) {
        if (device.isAp()) {
            return false;
        } else {
            mCurrentDevice = device;
            if (mSession == null) {
                startSession(APP_UUID);
            }
        }
        return true;
    }
    
    public void playVideo(String videoUrl, String videoName) {
        mRampStream.loadMedia(videoUrl, videoName);
    }
    
    public void seekTo(int seconed) {
        mRampStream.selectMediaTracks(seconed);
    }
    
    public void pause() {
        mRampStream.pause();
    }
    
    public void start() {
        mRampStream.play();
    }

    private void addDevice(CastDevice device) {
        if (device != null) {
            mCastDeviceMap.put(device.getFriendlyName(), device);
            mCastDeviceList.add(device);
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
    
    public boolean isConnectedDevice() {
        return !(mCurrentDevice == null);
    }
    
    public String getCurrentDeviceName() {
        return mCurrentDevice.getFriendlyName();
    }
    
    public void disconnectDevice() {
        setCurrentDevice(null);
        mSession.endSession(false);
    }

    public void setCastStatusUpdateListener(CastStatusUpdateListener castStatusUpdateListener) {
        mCastStatusUpdateListener = castStatusUpdateListener;
    }

    public void startSearch() {
        if (!mSearching) {
            mCastDeviceMap.clear();
            mCastDeviceList.clear();
    
            mServerSearcher.startSearch(mCastContext, mSearchListener);
        }
    }

    private void startQuery() {
        mRunnable = new QueryRunnable();
        mThread = new Thread(mRunnable);
        mThread.start();
    }

    public void setCastSearchListener(CastSearchListener castSearchListener) {
        mCastSearchListener = castSearchListener;
    }
    
    public void setCastSessionListener(CastSessionListener castSessionListener) {
        mCastSessionListener = castSessionListener;
    }
    
    public class QueryRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (mSession == null || mRampStream == null) {
                    Thread.interrupted();
                    break;
                }

                if (mSession.hasStarted() && mRampStream.hasAttached()) {
                    mRampStream.getPlayerState();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void cleanSession() {
        mIsSessionEstablished = false;
        mSession = null;
        mApplication.setCastSession(null);
        mApplication.setCastAppName(null);
        mApplication.setCastDevice(null);
    }
    
    public boolean isSessionEstablished() {
        return mIsSessionEstablished;
    }

    public void setVolumeDown() {
        if (!mRampStream.hasAttached() || mLastVolume == mCurVolume) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRampStream.hasAttached()) {
                    if (mCurVolume > 0) {
                        mRampStream.setVolume(-1);
                        mLastVolume = mCurVolume;
                        mRampStream.getPlayerState();
                    }
                }
            }
        });
    }

    public void setVolumeUp() {
        if (!mRampStream.hasAttached() || mLastVolume == mCurVolume) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRampStream.hasAttached()) {
                    if (mCurVolume < 100) {
                        mRampStream.setVolume(1);
                        mLastVolume = mCurVolume;
                        mRampStream.getPlayerState();
                    }
                }
            }
        });

    }

    private IOnSearchResultListener mSearchListener = new ServerSearcher.IOnSearchResultListener() {

        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
            mSearching = true;
            if (mCastSearchListener != null) {
                mCastSearchListener.onSearchStart();
            }
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
            mSearching = false;
            if (mCastSearchListener != null) {
                mCastSearchListener.onSearchFinish();
            }
        }

        @Override
        public void onResult(final CastDevice device) {
            mHandler.post(new Runnable() {
                public void run() {
                    if (mCastDeviceMap.get(device.getFriendlyName()) == null) {
                        addDevice(device);
                        if (mCastSearchListener != null) {
                            mCastSearchListener.onCastDevicesUpdate();
                        }
                    }
                }
            });
        }

        @Override
        public void onResultList(ArrayList<CastDevice> devices) {
        }
    };

    private ApplicationSession.Listener mSessionListener = new ApplicationSession.Listener() {
        @Override
        public void onSessionStarted(ApplicationMetadata applicationmetadata) {
            mIsSessionEstablished = true;
            mApplication.setCastSession(mSession);
            mApplication.setCastDevice(mCurrentDevice);
            mApplication.setCastAppName(APP_UUID);
            mSession.getChannel().attachMessageStream(mRampStream);
            startQuery();
            mHandler.post(new Runnable() {
                public void run() {
                    if (mCastSessionListener != null) {
                        mCastSessionListener.onSessionStarted();
                    }
                }
            });
        }

        @Override
        public void onSessionStartFailed(SessionError sessionerror) {
            cleanSession();
            if (mCastSessionListener != null) {
                mCastSessionListener.onSessionFailed();
            }
        }

        @Override
        public void onSessionEnded(SessionError sessionerror) {
            cleanSession();
            if (mCastSessionListener != null) {
                mCastSessionListener.onSessionEnded();
            }
            
        }

        @Override
        public void onAppDownloading(int percent) {
        }
    };

    public void destroy() {
        if (mThread != null) {
            mThread.interrupt();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mSession != null) {
            mSession.setLisetner(null);
        }

        if (mRampStream != null && mSession != null) {
            if (mSession.getChannel() != null) {
                mSession.getChannel().detachMessageStream(mRampStream);
            }
        }
    }
    
    private MediaProtocolMessageStream mRampStream = new MediaProtocolMessageStream(
            PROTO_TYPE) {
        @Override
        public void updateMediaStatus(final MediaStatus status) {
            if (mHandler == null) return;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCurVolume = status.getVolume();
                    if (mCastStatusUpdateListener != null) {
                        mCastStatusUpdateListener.updateStatus(status);
                    }
                }
            });
        }
    };
    
    public static abstract interface CastSessionListener {
        public abstract void onSessionStarted();
        public abstract void onSessionFailed();
        public abstract void onSessionEnded();
    }

    public static abstract interface CastSearchListener {
        public abstract void onSearchStart();
        public abstract void onSearchFinish();
        public abstract void onCastDevicesUpdate();
    }

    public static abstract interface CastStatusUpdateListener {
        public abstract void updateStatus(MediaStatus status);
    }
}
