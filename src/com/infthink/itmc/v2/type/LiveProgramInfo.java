package com.infthink.itmc.v2.type;

import com.infthink.libs.common.utils.JSONUtils;

public class LiveProgramInfo {
    // {
    // programId: 376390,
    // programName: "新闻联播",
    // startTime: "1403089140",
    // endTime: "1403091540",
    // programUrl: "",
    // urlType: "none",
    // type: "",
    // des: "",
    // desImg: ""
    // },
    public int programId;
    public String programName;
    public String startTime;
    public String endTime;
    public String programUrl;
    public String urlType;
    public String type;
    public String des;
    public String desImg;
    
    public LiveProgramInfo(String jsonObject) {
        JSONUtils jsonUtil = JSONUtils.parse(jsonObject);
        programId = Integer.valueOf(jsonUtil.opt("programId", "0").toString());
        programName = jsonUtil.opt("programName", "").toString();
        startTime = jsonUtil.opt("startTime", "").toString();
        endTime = jsonUtil.opt("endTime", "").toString();
        programUrl = jsonUtil.opt("programUrl", "").toString();
        urlType = jsonUtil.opt("urlType", "").toString();
        type = jsonUtil.opt("type", "").toString();
        des = jsonUtil.opt("des", "").toString();
        desImg = jsonUtil.opt("desImg", "").toString();
    }
}