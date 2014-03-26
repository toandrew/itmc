package com.infthink.itmc.type;

import java.io.Serializable;

public class ChannelRecommendation implements Serializable {
    private static final long serialVersionUID = 1L;
    public int channelID;
    public int channelType;
    public int isManual;
    public MediaInfo[] mediaInfoList;
//    public TelevisionInfo[] televisionInfoList;
    public int totalCount;
}
