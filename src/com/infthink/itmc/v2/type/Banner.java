package com.infthink.itmc.v2.type;

import java.io.Serializable;

public class Banner implements Serializable {
    private static final long serialVersionUID = 1;
    public AlbumInfo albumInfo;
    public MediaInfo mediaInfo;
    public int mediaType;
    public PersonInfo personInfo;
}
