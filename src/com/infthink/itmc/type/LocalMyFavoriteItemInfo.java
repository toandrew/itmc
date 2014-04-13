package com.infthink.itmc.type;


public class LocalMyFavoriteItemInfo extends BaseLocalFavorite {
    private static final long serialVersionUID = 1L;
   
    public int mediaId = -1;
    public String deviceid = "";
    public String id = "";
    public MediaInfo mediaInfo;
    
    public LocalMyFavoriteItemInfo() {
        this.mediaId = -1;
        this.deviceid = "0";
        this.id = "";
        this.mediaInfo = null;
    }
    
    public LocalMyFavoriteItemInfo(int mediaId,MediaInfo mediaInfo,String addDate) {
//        this.updateTime = updateTime;
        this.mediaId = mediaId;
        this.mediaInfo = mediaInfo;
        this.addDate = addDate;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && ((obj instanceof LocalPlayHistory)))
            return ((LocalMyFavoriteItemInfo) obj).mediaId == this.mediaId;
        return super.equals(obj);
    }
}
