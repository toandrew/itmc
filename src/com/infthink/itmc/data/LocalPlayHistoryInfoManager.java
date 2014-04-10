package com.infthink.itmc.data;

import java.util.List;

import android.content.Context;

import com.infthink.itmc.type.LocalPlayHistory;
import com.infthink.itmc.util.SharedPreferencesUtil;

public class LocalPlayHistoryInfoManager {
    private static LocalPlayHistoryInfoManager sLocalPlayHistoryInfoManger;

    private LocalPlayHistoryInfoManager() {
    }
    
    public static LocalPlayHistoryInfoManager getInstance() {
        if (sLocalPlayHistoryInfoManger == null)
            sLocalPlayHistoryInfoManger = new LocalPlayHistoryInfoManager();
        return sLocalPlayHistoryInfoManger;
    }
    
    public List<LocalPlayHistory> getHistoryVideos(Context context) {
        return SharedPreferencesUtil.getHistoryVideos(context);
    }
}
