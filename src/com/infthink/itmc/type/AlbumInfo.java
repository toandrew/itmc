package com.infthink.itmc.type;

import java.io.Serializable;

public class AlbumInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public int albumID;
    public String desc;
    public String name;
    public ImageUrlInfo posterUrl;
}
