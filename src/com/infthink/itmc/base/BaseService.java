package com.infthink.itmc.base;

import java.io.File;
import java.io.InputStream;

import org.json.JSONArray;

import android.util.Log;

import com.infthink.itmc.upgrade.Upgrade;
import com.infthink.itmc.upgrade.Upgrade.IInfoParser;
import com.infthink.itmc.upgrade.Upgrade.Info;
import com.infthink.libs.cache.simple.BitmapCachePool;
import com.infthink.libs.cache.simple.TextCachePool;
import com.infthink.libs.common.utils.IOUtils;
import com.infthink.libs.common.utils.JSONUtils;

/**
 * 基本服务
 */
public abstract class BaseService extends com.infthink.libs.base.BaseService {

    private TextCachePool mTextCache;
    private BitmapCachePool mBitmapCache;
    private Upgrade mUpgrade;

    @Override
    protected void onInit() {
        super.onInit();
        mUpgrade = Upgrade.init(this);
        if(mUpgrade == null) return;
        mUpgrade.setInfoParser(new IInfoParser() {
            public Info parse(InputStream is) {
                String str = IOUtils.readString(is);
                if (str == null) {
                    return null;
                }
                JSONUtils jsonUtils = JSONUtils.parse(str);
                Info info = new Info();
                int versionCode = -1;
                String upgradeApkUrl = null;
                int status = 0;
                try {
                    status = Integer.valueOf(jsonUtils.opt("status", "0").toString());
                   
                    switch (status) {
                        case -1:
                        case 0: 
                            break;
                        case 1:
                            String string = jsonUtils.opt("code", "0").toString();
                            float fStr = Float.parseFloat(string);
                            versionCode = (int) fStr;
                            upgradeApkUrl = jsonUtils.opt("url", upgradeApkUrl).toString();
                            break;
                        default:
                            break;
                    }
                    Log.e("XXXX", String.format("升级 versionCode=\"%s\"", versionCode));
                    Log.e("XXXX", String.format("升级 upgradeApkUrl=\"%s\"", upgradeApkUrl));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
                
                info.setVersionCode(versionCode);
                info.setUpgradeApkUrl(upgradeApkUrl);
                return info;
            }
        });
        try {
            File upgradeApkPath = new File(getExternalCacheDir(), "upgrade/itmc.apk");
            mUpgrade.setUpgradeApkPath(upgradeApkPath.getAbsolutePath());
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }

        mBitmapCache = new BitmapCachePool(20 * 1024 * 1024);
        mTextCache = new TextCachePool(10 * 1024 * 1024);

//        ImcManager.getInstance().scan();
    }

    public Upgrade getUpgrade() {
        return mUpgrade;
    }

    @Override
    protected void onTaskBackground() {
        super.onTaskBackground();
    }

    /**
     * @return 文本缓存
     */
    public TextCachePool getTextCache() {
        return mTextCache;
    }

    /**
     * @return 缩略图缓存
     */
    public BitmapCachePool getBitmapCache() {
        return mBitmapCache;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBitmapCache.clearAllCache();
        mTextCache.clearAllCache();
    }

}
