package com.infthink.itmc.v2.type;

public class MediaUrlInfoList {
    public static final int RESOLUTION_COUNT = 3;
    public static final int RESOLUTION_HIGH = 1;
    public static final int RESOLUTION_NORMAL = 0;
    public static final int RESOLUTION_SUPER = 2;
    public MediaUrlInfo[] urlHigh;
    public MediaUrlInfo[] urlNormal;
    public MediaUrlInfo[] urlSuper;
    public String videoName;
}
