package com.infthink.itmc.v2.type;

import java.util.ArrayList;
import java.util.List;

public class MediaSetInfoList {
    public MediaSetInfo[] mediaSetInfos;
    public int nDataCount;

    public List<MediaSetInfo> getAvailableCiList() {
        ArrayList localArrayList = new ArrayList();
        if (this.mediaSetInfos == null) ;
        for (int i = 0; i < this.mediaSetInfos.length; i++) {
            localArrayList.add(this.mediaSetInfos[i]);
        }
        return localArrayList;
    }
}
