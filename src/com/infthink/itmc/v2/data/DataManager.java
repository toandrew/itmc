package com.infthink.itmc.v2.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.infthink.itmc.v2.ITApp;
import com.infthink.itmc.v2.service.CoreService;
import com.infthink.itmc.v2.type.Banner;
import com.infthink.itmc.v2.type.Channel;
import com.infthink.itmc.v2.type.LiveChannelInfo;
import com.infthink.itmc.v2.type.LiveMediasInfo;
import com.infthink.itmc.v2.type.LiveProgramInfo;
import com.infthink.itmc.v2.type.LocalMyFavoriteItemInfo;
import com.infthink.itmc.v2.type.MediaDetailInfo;
import com.infthink.itmc.v2.type.MediaDetailInfo2;
import com.infthink.itmc.v2.type.MediaInfo;
import com.infthink.itmc.v2.type.MediaSetInfo;
import com.infthink.itmc.v2.type.MediaSetInfoList;
import com.infthink.itmc.v2.type.MediaUrlInfo;
import com.infthink.itmc.v2.type.MediaUrlInfoList;
import com.infthink.itmc.v2.type.RankInfo;
import com.infthink.itmc.v2.type.RankInfoList;
import com.infthink.itmc.v2.type.RecommendChannel;
import com.infthink.itmc.v2.type.ShowBaseInfo;
import com.infthink.itmc.v2.util.UIUtil;
import com.infthink.itmc.v2.util.Util;
import com.infthink.libs.cache.simple.BitmapLoader;
import com.infthink.libs.cache.simple.BitmapLoader.SimpleBitmapLoadListener;
import com.infthink.libs.cache.simple.TextLoader;
import com.infthink.libs.cache.simple.TextLoader.SimpleTextLoadListener;
import com.infthink.libs.common.utils.Collections;
import com.infthink.libs.common.utils.JSONUtils;

public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();
    // 获得频道集合, 用来建立 id-name 列表
    private static final String URL_GET_CHANNEL_MAP = "http://demo.bibifa.com/getchannellist";
    // 获得推荐频道, 无参数则表示获得首页推荐
    private static final String URL_GET_RECOMMEND_CHANNEL =
            "http://demo.bibifa.com/getchannelrecommendmedia"; // ?channelid=33554432
    // 获得banner列表, 无参数则表示获得首页banner
    private static final String URL_GET_BANNER = "http://demo.bibifa.com/getbannermedia"; // ?channelid=33554432
    // 按更新｜rank 展示影片
    private static final String URL_GET_LIST_BY_RANK = "http://demo.bibifa.com/getmedialist";// ?channelid=33554432&pageno=1&pagesize=3&orderby=7

    // 影片详情页
    private static final String URL_GET_DETAIL = "http://demo.bibifa.com/getmediadetail";// ?mediaid=1021875

    // 视频地址
    private static final String URL_GET_MEDIA = "http://demo.bibifa.com/getmediaurl";// ?mediaid=987381&ci=1

    // 搜索
    private static final String URL_SEARCH_MEIDA = "http://demo.bibifa.com/searchmedia"; // ?channelid=83886080&pageno=1&pagesize=1&orderby=1&keyword=test

    // 收藏
    private static final String URL_ADD_FAVORITE = "http://demo.bibifa.com/setbookmark"; // ?deviceid=00:16:6d:e0:2a:6c&mediaid=768160

    // 取消
    private static final String URL_DELETE_FAVORITE = "http://demo.bibifa.com/deletebookmark"; // ?deviceid=00:16:6d:e0:2a:6c&mediaid=768160

    // 获得收藏
    private static final String URL_GET_FAVORITE = "http://demo.bibifa.com/getbookmark"; // ?deviceid=00:16:6d:e0:2a:6c

    // 获得直播频道
    private static final String URL_GET_LIVE_CHANNEL = "http://tvlookbackepg.is.ysten.com:8080//ysten-replay/getChannelList.jsp?sortorder=desc";
    
    private static final String URL_GET_LIVE_PROGRAM = "http://tvlookbackepg.is.ysten.com:8080//ysten-replay/getProgramAlltimeByUuid.jsp"; //?uuid=cctv-1&sortorder=desc
    
    private CoreService mService;
    private ConcurrentLinkedQueue<Object> mRefCollection;
    private String mCommonArgs;

    public DataManager(CoreService service) {
        mService = service;
        mCommonArgs = Collections.deepToString(mService.getCommonArgs().asNameValuePair());
        mRefCollection = new ConcurrentLinkedQueue<Object>();
    }

    public void loadBitmap(String imageUrl, final IOnloadListener<Bitmap> listener) {
        SimpleBitmapLoadListener bitmapListener = new SimpleBitmapLoadListener() {
            @Override
            public void onLoad(Bitmap bitmap) {
                mRefCollection.remove(this);
                listener.onLoad(bitmap);
            }
        };
        mRefCollection.add(bitmapListener);
        BitmapLoader.loadBitmap(mService.getBitmapCache(), bitmapListener, imageUrl);
    }

    public void loadChannelMap(final IOnloadListener<HashMap<Integer, String>> listener) {
        SimpleTextLoadListener<HashMap<Integer, String>> textLoadListener =
                new SimpleTextLoadListener<HashMap<Integer, String>>() {

                    @Override
                    public HashMap<Integer, String> parseText(String text) {
                        HashMap<Integer, String> channelMap = new HashMap<Integer, String>();
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null && obj instanceof JSONArray) {
                                    JSONArray data = (JSONArray) obj;
                                    int count = data.length();
                                    for (int i = 0; i < count; i++) {
                                        JSONObject channel = data.optJSONObject(i);
                                        int id = channel.optInt("id");
                                        String name = channel.optString("name");
                                        channelMap.put(id, name);
                                    }
                                }
                            }
                        }
                        return channelMap;
                    }

                    @Override
                    public void onLoadResult(HashMap<Integer, String> object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, URL_GET_CHANNEL_MAP);
    }

    public void loadRecommendChannel(String channelId,
            final IOnloadListener<RecommendChannel> listener) {
        String args = "";
        if (!Util.isEmpty(channelId)) {
            args = "?channelid=" + channelId;
        }

        String textUrl = URL_GET_RECOMMEND_CHANNEL + args;
        android.util.Log.d(TAG, "loadRecommendChannel textUrl = " + textUrl);
        SimpleTextLoadListener<RecommendChannel> textLoadListener =
                new SimpleTextLoadListener<RecommendChannel>() {

                    @Override
                    public RecommendChannel parseText(String text) {
                        RecommendChannel recommendChannel = new RecommendChannel();
                        recommendChannel.channelList = new ArrayList<Channel>();
                        recommendChannel.recommend = new HashMap<Channel, ShowBaseInfo[]>();
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null && obj instanceof JSONArray) {
                                    JSONArray data = (JSONArray) obj;
                                    int count = data.length();
                                    for (int i = 0; i < count; i++) {
                                        JSONObject channelJson = data.optJSONObject(i);
                                        int id = channelJson.optInt("id");
                                        int type = channelJson.optInt("midtype");
                                        if (type == 200) continue; // 电视直播，忽略
                                        Channel channel = new Channel();
                                        channel.channelID = id;
                                        channel.channelName = ITApp.getChannelMap().get(id);
                                        channel.channelType = type;
                                        JSONArray subDatas = channelJson.optJSONArray("data");
                                        int subCount = subDatas.length();
                                        if (subCount >= 3) {
                                            MediaInfo[] medias = new MediaInfo[subCount];
                                            for (int j = 0; j < subCount; j++) {
                                                JSONObject subData = subDatas.optJSONObject(j);
                                                medias[j] = new MediaInfo(subData.toString());
                                            }
                                            android.util.Log.d(TAG,
                                                    "loadRecommendChannel channelID = "
                                                            + channel.channelID);
                                            recommendChannel.channelList.add(channel);
                                            recommendChannel.recommend.put(channel, medias);
                                        }
                                    }
                                }
                            }
                        }
                        return recommendChannel;
                    }

                    @Override
                    public void onLoadResult(RecommendChannel object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void loadChannelRank(final String channelId, int pageNo, int pageSize,
            final int orderBy, final IOnloadListener<RankInfoList> listener) {
        String textUrl =
                URL_GET_LIST_BY_RANK + "?channelid=" + channelId + "&pageno=" + pageNo
                        + "&pagesize=" + pageSize + "&orderby=" + orderBy;
        android.util.Log.d(TAG, "loadRecommendChannel textUrl = " + textUrl);
        SimpleTextLoadListener<RankInfoList> textLoadListener =
                new SimpleTextLoadListener<RankInfoList>() {

                    @Override
                    public RankInfoList parseText(String text) {
                        RankInfoList rankInfolist = new RankInfoList();
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null && obj instanceof JSONArray) {
                                    if (orderBy == 7) {
                                        JSONArray data = (JSONArray) obj;
                                        int count = data.length();
                                        rankInfolist.ranks = new RankInfo[count];
                                        for (int i = 0; i < count; i++) {
                                            JSONObject channelJson = data.optJSONObject(i);
                                            int id = channelJson.optInt("id");
                                            String name = channelJson.optString("name");
                                            android.util.Log.d(TAG,
                                                    "loadChannelRank name = " + name);
                                            RankInfo rankinfo = new RankInfo();
                                            rankinfo.channelID = id;
                                            rankinfo.channelName = name;
                                            JSONArray subDatas = channelJson.optJSONArray("data");
                                            int subCount = subDatas.length();
                                            MediaInfo[] medias = new MediaInfo[subCount];
                                            for (int j = 0; j < subCount; j++) {
                                                JSONObject subData = subDatas.optJSONObject(j);
                                                medias[j] = new MediaInfo(subData.toString());
                                            }
                                            rankinfo.mediaInfos = medias;
                                            rankInfolist.ranks[i] = rankinfo;
                                        }
                                    } else if (orderBy == 1) {
                                        JSONArray data = (JSONArray) obj;
                                        int count = data.length();

                                        MediaInfo[] medias = new MediaInfo[count];
                                        RankInfo rankinfo = new RankInfo();
                                        rankInfolist.ranks = new RankInfo[1];
                                        for (int i = 0; i < count; i++) {
                                            JSONObject subData = data.optJSONObject(i);
                                            medias[i] = new MediaInfo(subData.toString());
                                        }
                                        rankinfo.mediaInfos = medias;
                                        rankInfolist.ranks[0] = rankinfo;
                                    }
                                }
                            }
                        }
                        return rankInfolist;
                    }

                    @Override
                    public void onLoadResult(RankInfoList object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void loadBanner(String channelId, final IOnloadListener<Banner[]> listener) {
        String args = "";
        if (!Util.isEmpty(channelId)) {
            args = "?channelid=" + channelId;
        }
        String textUrl = URL_GET_BANNER + args;

        SimpleTextLoadListener<Banner[]> textLoadListener = new SimpleTextLoadListener<Banner[]>() {

            @Override
            public Banner[] parseText(String text) {
                Banner[] banners = null;
                if (text != null && text.length() > 0) {
                    JSONUtils jsonUtil = JSONUtils.parse(text);
                    int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                    if (status == 0) {
                        Object obj = jsonUtil.opt("data", null);
                        if (obj != null && obj instanceof JSONArray) {
                            JSONArray data = (JSONArray) obj;
                            int count = data.length();
                            banners = new Banner[count];
                            for (int i = 0; i < count; i++) {
                                JSONObject bannerJson = data.optJSONObject(i);
                                Banner banner = new Banner();
                                banner.mediaInfo = new MediaInfo(bannerJson.toString());
                                banners[i] = banner;
                            }
                        }
                    }
                }
                return banners;
            }

            @Override
            public void onLoadResult(Banner[] object) {
                mRefCollection.remove(this);
                listener.onLoad(object);
            }

        };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public interface IOnloadListener<T> {
        public void onLoad(T entity);
    }

    public void loadDetail(String mediaID, final IOnloadListener<MediaDetailInfo2> listener) {
        String args = "";
        if (!Util.isEmpty(mediaID)) {
            args = "?mediaid=" + mediaID;
        }
        String textUrl = URL_GET_DETAIL + args;
        android.util.Log.d(TAG, "loadDetail textUrl = " + textUrl);
        SimpleTextLoadListener<MediaDetailInfo2> textLoadListener =
                new SimpleTextLoadListener<MediaDetailInfo2>() {

                    @Override
                    public MediaDetailInfo2 parseText(String text) {
                        MediaDetailInfo2 mediaDetailInfo2 = null;
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null) {
                                    mediaDetailInfo2 = new MediaDetailInfo2();
                                    String desc = ((JSONObject) obj).optString("desc");
                                    String posterurl = ((JSONObject) obj).optString("posterurl");
                                    android.util.Log.d(TAG, "loadDetail desc = " + desc);
                                    MediaDetailInfo mdi = new MediaDetailInfo();
                                    mdi.desc = desc;
                                    mdi.posterurl = posterurl;
                                    JSONObject mediaciinfo =
                                            ((JSONObject) obj).optJSONObject("mediaciinfo");
                                    JSONArray videos =
                                            (JSONArray) mediaciinfo.optJSONArray("videos");
                                    int ciCount = videos.length();
                                    MediaSetInfoList mediaSetInfoList = new MediaSetInfoList();
                                    mediaSetInfoList.mediaSetInfos = new MediaSetInfo[ciCount];
                                    for (int i = 0; i < ciCount; i++) {
                                        JSONObject mediaciinfoJson = videos.optJSONObject(i);
                                        String videoname = mediaciinfoJson.optString("videoname");
                                        MediaSetInfo mediaSetInfo_temp = new MediaSetInfo();
                                        mediaSetInfo_temp.szVideoName = videoname;
                                        mediaSetInfoList.mediaSetInfos[i] = mediaSetInfo_temp;
                                    }
                                    mediaDetailInfo2.mediaDetailInfo = mdi;
                                    mediaDetailInfo2.mediaSetInfoList = mediaSetInfoList;
                                }
                            }
                        }
                        android.util.Log.d(TAG,
                                "loadDetail status = "
                                        + mediaDetailInfo2.mediaSetInfoList.getAvailableCiList()
                                                .get(0).szVideoName
                                        + " count = "
                                        + mediaDetailInfo2.mediaSetInfoList.getAvailableCiList()
                                                .size());
                        return mediaDetailInfo2;
                    }

                    @Override
                    public void onLoadResult(MediaDetailInfo2 object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void loadMediaUrl(String mediaID, int ci,
            final IOnloadListener<MediaUrlInfoList> listener) {
        String args = "";
        if (!Util.isEmpty(mediaID)) {
            args = "?mediaid=" + mediaID + "&ci=" + ci;
        }
        String textUrl = URL_GET_MEDIA + args;
        android.util.Log.d(TAG, "loadMediaUrl textUrl = " + textUrl);
        SimpleTextLoadListener<MediaUrlInfoList> textLoadListener =
                new SimpleTextLoadListener<MediaUrlInfoList>() {

                    @Override
                    public MediaUrlInfoList parseText(String text) {
                        MediaUrlInfoList mediaUrlInfoList = null;
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null) {
                                    mediaUrlInfoList = new MediaUrlInfoList();
                                    JSONObject mediaobj = (JSONObject) obj;
                                    mediaUrlInfoList.videoName = mediaobj.optString("videoname");

                                    Object normalObj = mediaobj.opt("normal");
                                    Object highObj = mediaobj.opt("high");
                                    Object superObj = mediaobj.opt("super");

                                    if (normalObj instanceof JSONArray) {
                                        JSONArray normalArray = (JSONArray) normalObj;
                                        if (normalArray.length() > 0) {
                                            mediaUrlInfoList.urlNormal =
                                                    new MediaUrlInfo[normalArray.length()];
                                            for (int i = 0; i < normalArray.length(); i++) {
                                                JSONObject normaJsonObject =
                                                        normalArray.optJSONObject(i);
                                                MediaUrlInfo normalURLInfo = new MediaUrlInfo();
                                                normalURLInfo.mediaSource =
                                                        normaJsonObject.optInt("source");
                                                normalURLInfo.mediaUrl =
                                                        normaJsonObject.optString("playurl");
                                                normalURLInfo.isHtml = normaJsonObject.optInt("isHtml");
                                                mediaUrlInfoList.urlNormal[i] = normalURLInfo;
                                            }
                                        }
                                    }
                                    if (highObj instanceof JSONArray) {
                                        MediaUrlInfo highURLInfo;
                                    }

                                    if (superObj instanceof JSONArray) {
                                        MediaUrlInfo superURLInfo;
                                    }
                                }
                            }
                        }
                        
                        return mediaUrlInfoList;
                    }

                    @Override
                    public void onLoadResult(MediaUrlInfoList object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }
    
    public void loadMediaPlayUrl(int mediaID, int ci, int source,
            final IOnloadListener<String> listener) {
        int sort = (ITApp.getInstance().getMode() == -1) ? 0 : ITApp.getInstance().getMode();
        String args = "?mediaid=" + mediaID + "&ci=" + ci + "&source=" + source + "&sort=" + sort;
        String textUrl = URL_GET_MEDIA + args;
        android.util.Log.d(TAG, "loadMediaPlayUrl textUrl = " + textUrl);
        SimpleTextLoadListener<String> textLoadListener =
                new SimpleTextLoadListener<String>() {

                    @Override
                    public String parseText(String text) {
                        String url = null;
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                url = jsonUtil.opt("url", "").toString();
                            }
                        }
                        android.util.Log.d(TAG, "url ＝ " + url);
                        return url;
                    }

                    @Override
                    public void onLoadResult(String url) {
                        mRefCollection.remove(this);
                        listener.onLoad(url);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void searchMedia(String channelId, String keyword, int pageNo, int pageSize,
            int orderby, final IOnloadListener<MediaInfo[]> listener) {
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        if (!Util.isEmpty(channelId)) {
            sb.append("channelid=" + channelId);
        }
        sb.append("&keyword=");
        sb.append(keyword);
        sb.append("&pageno=");
        sb.append(pageNo);
        sb.append("&pagesize=");
        sb.append(pageSize);
        sb.append("&orderby=");
        sb.append(orderby);
        String textUrl = URL_SEARCH_MEIDA + sb.toString();
        android.util.Log.d(TAG, "textUrl = " + textUrl);
        SimpleTextLoadListener<MediaInfo[]> textLoadListener =
                new SimpleTextLoadListener<MediaInfo[]>() {

                    @Override
                    public MediaInfo[] parseText(String text) {
                        MediaInfo[] medias = null;
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null) {
                                    JSONObject mediaobj = (JSONObject) obj;
                                    Object infos = mediaobj.opt("mediainfo");
                                    if (infos != null && infos instanceof JSONArray) {
                                        JSONArray mediaArray = (JSONArray) infos;
                                        int count = mediaArray.length();
                                        medias = new MediaInfo[count];
                                        for (int i = 0; i < count; i++) {
                                            JSONObject media = mediaArray.optJSONObject(i);
                                            medias[i] = new MediaInfo(media.toString());
                                        }
                                    }
                                }
                            }
                        }
                        return medias;
                    }

                    @Override
                    public void onLoadResult(MediaInfo[] object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void uploadFavorite(String deviceId, int mediaId, final IOnloadListener<Integer> listener) {
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        if (!Util.isEmpty(deviceId)) {
            sb.append("deviceid=" + deviceId);
        }
        if (mediaId != 0) {
            sb.append("&mediaid=" + mediaId);
        }
        String textUrl = URL_ADD_FAVORITE + sb.toString();
        android.util.Log.d(TAG, "textUrl = " + textUrl);
        SimpleTextLoadListener<Integer> textLoadListener = new SimpleTextLoadListener<Integer>() {

            @Override
            public Integer parseText(String text) {
                JSONUtils jsonUtil = JSONUtils.parse(text);
                int status = Integer.valueOf(jsonUtil.opt("status", "-1").toString());
                return status;
            }

            @Override
            public void onLoadResult(Integer object) {
                mRefCollection.remove(this);
                listener.onLoad(object);
            }

        };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void deleteFavorite(String deviceId, int mediaId, final IOnloadListener<Integer> listener) {
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        if (!Util.isEmpty(deviceId)) {
            sb.append("deviceid=" + deviceId);
        }
        if (mediaId != 0) {
            sb.append("&mediaid=" + mediaId);
        }
        String textUrl = URL_DELETE_FAVORITE + sb.toString();
        android.util.Log.d(TAG, "textUrl = " + textUrl);
        SimpleTextLoadListener<Integer> textLoadListener = new SimpleTextLoadListener<Integer>() {

            @Override
            public Integer parseText(String text) {
                JSONUtils jsonUtil = JSONUtils.parse(text);
                int status = Integer.valueOf(jsonUtil.opt("status", "-1").toString());
                return status;
            }

            @Override
            public void onLoadResult(Integer object) {
                mRefCollection.remove(this);
                listener.onLoad(object);
            }

        };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void loadFav(final String deviceId,
            final IOnloadListener<List<LocalMyFavoriteItemInfo>> listener) {
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        sb.append("deviceid=");
        sb.append(deviceId);
        String textUrl = URL_GET_FAVORITE + sb.toString();
        android.util.Log.d(TAG, "textUrl = " + textUrl);
        SimpleTextLoadListener<List<LocalMyFavoriteItemInfo>> textLoadListener =
                new SimpleTextLoadListener<List<LocalMyFavoriteItemInfo>>() {

                    @Override
                    public List<LocalMyFavoriteItemInfo> parseText(String text) {
                        List<LocalMyFavoriteItemInfo> localMyFavoriteItemInfos = new ArrayList<LocalMyFavoriteItemInfo>();
                        if (text != null && text.length() > 0) {
                            JSONUtils jsonUtil = JSONUtils.parse(text);
                            int status = Integer.valueOf(jsonUtil.opt("status", "100").toString());
                            if (status == 0) {
                                Object obj = jsonUtil.opt("data", null);
                                if (obj != null  && obj instanceof JSONArray) {
                                    JSONArray data = (JSONArray) obj;
                                    int count = data.length();
                                    for (int i = 0; i < count; i++) {
                                        JSONObject mediaObj = data.optJSONObject(i);
                                        LocalMyFavoriteItemInfo myFavoriteItemInfo = new LocalMyFavoriteItemInfo();
                                        myFavoriteItemInfo.deviceid = deviceId;
                                        myFavoriteItemInfo.mediaId = Integer.parseInt(mediaObj.opt("mediaid").toString());
                                        myFavoriteItemInfo.mediaInfo = new MediaInfo(mediaObj.toString());
                                        myFavoriteItemInfo.id = mediaObj.opt("id").toString();
                                        if(mediaObj.opt("updatetime").toString() == null){
                                            myFavoriteItemInfo.addDate = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                        }
                                        android.util.Log.d(TAG, "mediaInfo mediaName = " + myFavoriteItemInfo.mediaInfo.mediaName);
                                        localMyFavoriteItemInfos.add(myFavoriteItemInfo);
                                    }
                                }
                            }
                        }
                        return localMyFavoriteItemInfos;
                    }

                    @Override
                    public void onLoadResult(List<LocalMyFavoriteItemInfo> object) {
                        mRefCollection.remove(this);
                        listener.onLoad(object);
                    }

                };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }
    

    public void loadLiveChannel(final IOnloadListener<List<LiveChannelInfo>> listener) {
        String textUrl = URL_GET_LIVE_CHANNEL;

        SimpleTextLoadListener<List<LiveChannelInfo>> textLoadListener = new SimpleTextLoadListener<List<LiveChannelInfo>>() {

            @Override
            public List<LiveChannelInfo> parseText(String text) {
                List<LiveChannelInfo> infos = new ArrayList<LiveChannelInfo>();
                if (text != null && text.length() > 0) {
                    try {
                        JSONArray data = new JSONArray(text);
                        int count = data.length();
                        for (int i = 0; i < count; i++) {
                            JSONObject json = data.optJSONObject(i);
                            LiveChannelInfo info = new LiveChannelInfo(json.toString());
                            if (info.usable > 0) {
                                infos.add(info);
                            }
                        }
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return infos;
            }

            @Override
            public void onLoadResult(List<LiveChannelInfo> object) {
                mRefCollection.remove(this);
                listener.onLoad(object);
            }

        };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }

    public void loadLiveProgram(final String uuid, final IOnloadListener<List<LiveProgramInfo>> listener) {
        String textUrl = URL_GET_LIVE_PROGRAM + "?uuid=" + uuid + "&sortorder=desc";

        SimpleTextLoadListener<List<LiveProgramInfo>> textLoadListener = new SimpleTextLoadListener<List<LiveProgramInfo>>() {

            @Override
            public List<LiveProgramInfo> parseText(String text) {
                List<LiveProgramInfo> infos = new ArrayList<LiveProgramInfo>();
                if (text != null && text.length() > 0) {
                    try {
                        JSONArray data = new JSONArray(text);
                        int count = data.length();
                        for (int i = 0; i < count; i++) {
                            JSONUtils jsonUtil = JSONUtils.parse(data.getString(i));
                            LiveMediasInfo info = new LiveMediasInfo();
                            info.playDate = jsonUtil.opt("jsonUtil", "").toString();
                            info.programs = new ArrayList<LiveProgramInfo>();
                            Object obj = jsonUtil.opt("programs", null);
                            if (obj != null  && obj instanceof JSONArray) {
                                JSONArray programs = (JSONArray) obj;
                                int length = programs.length();
                                for (int j = 0; j < length; j++) {
                                    JSONObject program = programs.optJSONObject(j);
                                    LiveProgramInfo liveProgramInfo = new LiveProgramInfo(program.toString());
                                    if ((liveProgramInfo.urlType.equals("replay") || liveProgramInfo.urlType.equals("play")) && liveProgramInfo.programUrl.length() > 0)
//                                        info.programs.add(liveProgramInfo);
                                        infos.add(liveProgramInfo);
                                }
                            }
//                            infos.add(info);
                        }
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return infos;
            }

            @Override
            public void onLoadResult(List<LiveProgramInfo> object) {
                mRefCollection.remove(this);
                listener.onLoad(object);
            }

        };
        mRefCollection.add(textLoadListener);
        TextLoader.loadText(mService.getTextCache(), textLoadListener, textUrl);
    }
}
