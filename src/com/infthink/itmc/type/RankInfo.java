package com.infthink.itmc.type;

import java.io.Serializable;

public class RankInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public int channelID;
    public String channelName;
    public MediaInfo[] mediaInfos;
    public int totalCount;
}
