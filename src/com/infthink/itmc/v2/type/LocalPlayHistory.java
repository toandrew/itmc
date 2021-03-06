package com.infthink.itmc.v2.type;

public class LocalPlayHistory extends BaseLocalPlayHistory {
    private static final long serialVersionUID = 2L;
    public String html5Page = "";
    public String imageMd5 = "";
    public String imageUrl = "";
    public String issueDate = "";
    public int mediaCi;
    public String mediaId;
    public int mediaSource;
    public String mediaUrl = "";
    public String playSeconds;
    public String videoName = "";

    public LocalPlayHistory() {
        this.mediaId = "";
        this.mediaCi = -1;
        this.playSeconds = "0";
        this.playDate = "";
        this.videoName = "";
        this.mediaUrl = "";
        this.html5Page = "";
        this.issueDate = "";
    }

    public LocalPlayHistory(String mediaId, int mediaCi, String playSeconds, String playDate, int mediaSource, String videoName, String mediaUrl, String html5Page, String imageUrl) {
        this.mediaId = mediaId;
        this.mediaCi = mediaCi;
        this.playSeconds = playSeconds;
        this.playDate = playDate;
        this.mediaSource = mediaSource;
        this.videoName = videoName;
        this.mediaUrl = mediaUrl;
        this.html5Page = html5Page;
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && ((obj instanceof LocalPlayHistory)))
            return ((LocalPlayHistory) obj).mediaId == this.mediaId;
        return super.equals(obj);
    }
}
