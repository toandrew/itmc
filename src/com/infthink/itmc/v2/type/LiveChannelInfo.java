package com.infthink.itmc.v2.type;

import com.infthink.libs.common.utils.JSONUtils;

public class LiveChannelInfo {
    // {
    // uuid: "cctv-1",
    // channelName: "CCTV-1",
    // logo: "http://tvlookbackepg.is.ysten.com:8080/logo/CCTV1.png",
    // no: "01",
    // urlid: "cctv-1",
    // usable: 1,
    // onplay: "电视剧:女人心21/35"
    // },
    public String uuid;
    public String channelName;
    public String logo;
    public String urlid;
    public int usable;
    public String onplay;

    public LiveChannelInfo(String jsonObject) {
        JSONUtils jsonUtil = JSONUtils.parse(jsonObject);
        uuid = jsonUtil.opt("uuid", "").toString();
        channelName = jsonUtil.opt("channelName", "").toString();
        logo = jsonUtil.opt("logo", "").toString();
        urlid = jsonUtil.opt("urlid", "").toString();
        usable = Integer.valueOf(jsonUtil.opt("usable", "0").toString());
        onplay = jsonUtil.opt("onplay", "").toString();
    }
}