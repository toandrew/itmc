package com.infthink.itmc.type;

import java.io.Serializable;

import com.infthink.libs.common.utils.JSONUtils;

public class MediaInfo extends ShowBaseInfo {
    private static final long serialVersionUID = 1L;
    public String actors = "";
    public String area = "";
    public String category = "";
    public String director = "";
    public int flag = 0;
    public String issueDate = "";
    public String lastIssueDate = "";
    public int mediaID = -1;
    public String mediaName = "";
    public int mediaSetType = 0;
    public int playCount = 0;
    public int playLength = 0;
    public int resolution = 0;
    public float score = 0.0F;
    public int scoreCount;
    public int setAvailableCount;
    public int setCount = 0;
    public int setNow = 0;
    public ImageUrlInfo smallImageURL = null;
    public String tags = "";

    public MediaInfo(String jsonObject) {
        JSONUtils jsonUtil = JSONUtils.parse(jsonObject);
        actors = jsonUtil.opt("actors", "").toString();
        area =  jsonUtil.opt("area", "").toString();
        category = jsonUtil.opt("category", "").toString();
        director = jsonUtil.opt("director", "").toString();
        flag = Integer.valueOf(jsonUtil.opt("flag", "0").toString());
        issueDate = jsonUtil.opt("issuedate", "").toString();
        lastIssueDate = jsonUtil.opt("lastissuedate", "").toString();
        mediaID = Integer.valueOf(jsonUtil.opt("mediaid", "0").toString());
        mediaName = jsonUtil.opt("medianame", "").toString();
        mediaSetType = Integer.valueOf(jsonUtil.opt("midtype", "0").toString());
        playCount = Integer.valueOf(jsonUtil.opt("playcount", "0").toString());
        playLength = Integer.valueOf(jsonUtil.opt("playlength", "0").toString());
        resolution = Integer.valueOf(jsonUtil.opt("resolution", "0").toString());
        score = Float.valueOf(jsonUtil.opt("score", "0").toString());
        scoreCount = Integer.valueOf(jsonUtil.opt("scorecount", "0").toString());
        setAvailableCount = Integer.valueOf(jsonUtil.opt("validlength", "0").toString());
        setCount = Integer.valueOf(jsonUtil.opt("setcount", "0").toString());
        setNow = Integer.valueOf(jsonUtil.opt("setnow", "0").toString());
        ImageUrlInfo imageInfo = new ImageUrlInfo();
        imageInfo.imageUrl = jsonUtil.opt("posterurl", "").toString();
        imageInfo.md5 = jsonUtil.opt("md5", "").toString();
        smallImageURL = imageInfo;
        tags = jsonUtil.opt("allcategorys", "").toString();
    }
    
    public boolean isFinished() {
        return (this.mediaSetType <= 0) || (this.setNow == this.setCount);
    }

    public boolean isMultiSetType() {
        return this.mediaSetType > 0;
    }

    public boolean isVaritey() {
        // return this.category.equals(DKApp.getR().getString(2131427362));
        return this.category.equals("综艺");
    }
}
