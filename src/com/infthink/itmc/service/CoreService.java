package com.infthink.itmc.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.infthink.itmc.base.BaseService;
import com.infthink.itmc.data.DataManager;
import com.infthink.libs.common.utils.SystemUtils;

public class CoreService extends BaseService {

    private static final String TAG = CoreService.class.getSimpleName();
    private CommonArgs mCommonArgs;
    private DataManager mDataManager;
//    private SearchHistory mSearchHistory;
//    private NetTest mNetTest;

//    public NetTest getNetTest() {
//        return mNetTest;
//    }

    @Override
    protected void onInit() {
        super.onInit();
        mCommonArgs = new CommonArgs(this);
        mDataManager = new DataManager(this);
//        mSearchHistory = new SearchHistory(this);
//        mNetTest = new NetTest(this);

//        if (DEBUG) {
            Log.d("XXXXXXXXX", "onInit()");
//        }
    }

//    public SearchHistory getSearchHistory() {
//        return mSearchHistory;
//    }

//    @MessageResponse
//    public void cleanCache(final CacheCleanEvent cacheCleanEvent) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                if (cacheCleanEvent.isCleanBitmapCache() && getBitmapCache() != null) {
//                    if (DEBUG) {
//                        Log.d(TAG, "删除bitmap缓存");
//                    }
//                    getBitmapCache().clearAllCache();
//                }
//                if (cacheCleanEvent.isCleanTextCache() && getTextCache() != null) {
//                    if (DEBUG) {
//                        Log.d(TAG, "删除text缓存");
//                    }
//                    getTextCache().clearAllCache();
//                }
//                return null;
//            }
//        }.execute();
//    }

    /**
     * 检查版本更新
     */
    public void checkUpdate() {
        if (DEBUG) {
            Log.d(TAG, "checkUpdate " + System.currentTimeMillis());
        }
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public CommonArgs getCommonArgs() {
        return mCommonArgs;
    }

    /**
     * Encoded Common Args
     */
    public static class CommonArgs {

        private String mBoard;
        private int mSdk;
        private String mModel;
        private String mHardware;
        private String mResolution;
        private String mMan;

        private CommonArgs(Context context) {
            try {
                mBoard = URLEncoder.encode(SystemUtils.BuildInfo.BOARD, "UTF-8");
                mSdk = SystemUtils.BuildInfo.VERSION.SDK_INT;
                mModel = URLEncoder.encode(SystemUtils.BuildInfo.MODEL, "UTF-8");
                mHardware = URLEncoder.encode(SystemUtils.BuildInfo.HARDWARE, "UTF-8");
                DisplayMetrics dm = SystemUtils.getDisplayMetrics(context);
                mResolution = dm.widthPixels + "x" + dm.heightPixels;
                mMan = URLEncoder.encode(SystemUtils.BuildInfo.MANUFACTURER, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                if (DEBUG)
                    e.printStackTrace();
            }
        }

        public List<NameValuePair> asNameValuePair() {
            List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
            nvpList.add(new BasicNameValuePair("board", mBoard));
            nvpList.add(new BasicNameValuePair("sdk", "" + mSdk));
            nvpList.add(new BasicNameValuePair("model", mModel));
            nvpList.add(new BasicNameValuePair("hardware", mHardware));
            nvpList.add(new BasicNameValuePair("resolution", mResolution));
            nvpList.add(new BasicNameValuePair("man", mMan));
            return nvpList;
        }

        @Override
        public String toString() {
            return "board=" + mBoard + "&sdk=" + mSdk + "&model=" + mModel + "&hardware=" + mHardware + "&resolution=" + mResolution + "&man=" + mMan;
        }

    }

}
