package com.infthink.itmc.base;

import java.io.File;
import java.io.InputStream;

import com.infthink.libs.cache.simple.BitmapCachePool;
import com.infthink.libs.cache.simple.TextCachePool;
import com.infthink.libs.common.utils.IOUtils;
import com.infthink.libs.common.utils.JSONUtils;
import com.infthink.libs.upgrade.Upgrade;
import com.infthink.libs.upgrade.Upgrade.IInfoParser;
import com.infthink.libs.upgrade.Upgrade.Info;

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
//        mUpgrade = Upgrade.init(this);
//        mUpgrade.setInfoParser(new IInfoParser() {
//            public Info parse(InputStream is) {
//                String str = IOUtils.readString(is);
//                if (str == null) {
//                    return null;
//                }
//                JSONUtils jsonUtils = JSONUtils.parse(str);
//                Info info = new Info();
//                int versionCode = -1;
//                String upgradeApkUrl = null;
//                try {
//                    versionCode = Integer.valueOf(jsonUtils.opt("data.vcode", versionCode).toString());
//                    upgradeApkUrl = jsonUtils.opt("data.url", upgradeApkUrl).toString();
//                } catch (Exception e) {
//                    if (DEBUG)
//                        e.printStackTrace();
//                }
//                info.setVersionCode(versionCode);
//                info.setUpgradeApkUrl(upgradeApkUrl);
//                return info;
//            }
//        });
//        try {
//            File upgradeApkPath = new File(getExternalCacheDir(), "upgrade/infthink.apk");
//            mUpgrade.setUpgradeApkPath(upgradeApkPath.getAbsolutePath());
//        } catch (Exception e) {
//            if (DEBUG)
//                e.printStackTrace();
//        }

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
