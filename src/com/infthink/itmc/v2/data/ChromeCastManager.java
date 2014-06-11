package com.infthink.itmc.v2.data;

import java.io.IOException;

import com.fireflycast.cast.ApplicationMetadata;
import com.fireflycast.cast.Cast;
import com.fireflycast.cast.CastMediaControlIntent;
import com.fireflycast.cast.ConnectionResult;
import com.fireflycast.cast.FireflyApiClient;
import com.fireflycast.cast.ResultCallback;
import com.fireflycast.cast.Status;
import com.fireflycast.cast.Cast.ApplicationConnectionResult;

import android.os.Bundle;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;

public class ChromeCastManager {
    private static final String TAG = ChromeCastManager.class.getSimpleName();

    private MyMediaRouterCallback mMyMediarouterCallback = new MyMediaRouterCallback();
    
    public ChromeCastManager() {
//        mMediaRouter = MediaRouter.getInstance(this);
//        mMediaRouteSelector = new MediaRouteSelector.Builder()
//        .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
//        .build();
//        
//        mMediaRouterCallback = new MyMediaRouterCallback();
//        mCastListener = new CastListener();
//        mConnectionCallbacks = new ConnectionCallbacks();
//        mConnectionFailedListener = new ConnectionFailedListener();
    }
    
    public MyMediaRouterCallback getMediaRouterCallback() {
        return mMyMediarouterCallback;
    }
    
    class CastListener extends Cast.Listener {
        @Override
        public void onVolumeChanged() {
//            refreshDeviceVolume(Cast.CastApi.getVolume(mApiClient),
//                    Cast.CastApi.isMute(mApiClient));
        }

        @Override
        public void onApplicationStatusChanged() {
//            if (mApiClient.isConnected()) {
//                String status = Cast.CastApi.getApplicationStatus(mApiClient);
//                Log.d(TAG, "onApplicationStatusChanged; status=" + status);
//            }
        }

        @Override
        public void onApplicationDisconnected(int statusCode) {
//            Log.d(TAG, "onApplicationDisconnected: statusCode=" + statusCode);
//            mAppMetadata = null;
//            detachMediaPlayer();
//            clearMediaState();
//            updateButtonStates();
////            if (statusCode != CastStatusCodes.SUCCESS) {
////                // This is an unexpected disconnect.
////                setApplicationStatus(getString(R.string.status_app_disconnected));
////            }
        }
    }

    class MyMediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo route) {
            Log.d(TAG, "onRouteSelected: route=" + route);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo route) {
            Log.d(TAG, "onRouteUnselected: route=" + route);
        }
    }

    class ApplicationConnectionResultCallback implements
            ResultCallback<Cast.ApplicationConnectionResult> {
        private final String mClassTag;

        public ApplicationConnectionResultCallback(String suffix) {
            mClassTag = TAG + "_" + suffix;
        }

        @Override
        public void onResult(ApplicationConnectionResult result) {
            // Status status = result.getStatus();
            // alert("Launch app: " + status.isSuccess());
            // if (status.isSuccess()) {
            // ApplicationMetadata applicationMetadata = result
            // .getApplicationMetadata();
            // String sessionId = result.getSessionId();
            // String applicationStatus = result.getApplicationStatus();
            // boolean wasLaunched = result.getWasLaunched();
            // Log.d(mClassTag,
            // "application name: " + applicationMetadata.getName()
            // + ", status: " + applicationStatus
            // + ", sessionId: " + sessionId
            // + ", wasLaunched: " + wasLaunched);
            // attachMediaPlayer();
            // mAppMetadata = applicationMetadata;
            //
            // updateButtonStates();
            // requestMediaStatus();
            // mBtnPlay.setClickable(true);
            // } else {
            // }
        }
    }
    
    class ConnectionCallbacks implements FireflyApiClient.ConnectionCallbacks {
        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "ConnectionCallbacks.onConnectionSuspended");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    // TODO: need to disable all controls, and possibly display a
//                    // "reconnecting..." dialog or overlay
//                    mWaitingForReconnect = true;
//                    cancelRefreshTimer();
//                    detachMediaPlayer();
//                    updateButtonStates();
//                }
//            });
        }

        @Override
        public void onConnected(final Bundle connectionHint) {
            Log.d(TAG, "ConnectionCallbacks.onConnected");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    alert("Connection success");
//                    if (!mApiClient.isConnected()) {
//                        // We got disconnected while this runnable was pending execution.
//                        return;
//                    }
//                    try {
//                        Cast.CastApi.requestStatus(mApiClient);
//                    } catch (IOException e) {
//                        Log.d(TAG, "error requesting status", e);
//                    }
//                    android.util.Log.d(TAG, "start launchApplication");
//                    
//                    
////                    attachMediaPlayer();
//
////                    setDeviceVolumeControlsEnabled(true);
////                    mLaunchAppButton.setEnabled(true);
////                    mJoinAppButton.setEnabled(true);
////
//                    if (mWaitingForReconnect) {
//                        mWaitingForReconnect = false;
//                        if ((connectionHint != null)
//                                && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
//                            Log.d(TAG, "App  is no longer running");
//                            detachMediaPlayer();
//                            mAppMetadata = null;
//                            clearMediaState();
//                            updateButtonStates();
//                        } else {
//                            attachMediaPlayer();
//                            requestMediaStatus();
//                            startRefreshTimer();
//                        }
//                    } else {
//                        Cast.CastApi
//                        .launchApplication(
//                                mApiClient,
//                                CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID,
//                                false).setResultCallback(new ApplicationConnectionResultCallback("LaunchApp"));
//                    }
//                }
//            });
        }
    }
    
    class ConnectionFailedListener implements FireflyApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.d(TAG, "onConnectionFailed");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    updateButtonStates();
//                    clearMediaState();
//                    cancelRefreshTimer();
//                }
//            });
        }
    }
}
