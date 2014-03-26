package com.infthink.itmc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.infthink.itmc.ITApp;
import com.infthink.itmc.service.CoreService;
import com.infthink.itmc.type.Banner;
import com.infthink.itmc.type.Channel;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.RecommendChannel;
import com.infthink.itmc.type.ShowBaseInfo;
import com.infthink.itmc.util.Util;
import com.infthink.libs.cache.simple.BitmapLoader;
import com.infthink.libs.cache.simple.BitmapLoader.SimpleBitmapLoadListener;
import com.infthink.libs.cache.simple.TextLoader;
import com.infthink.libs.cache.simple.TextLoader.SimpleTextLoadListener;
import com.infthink.libs.common.utils.Collections;
import com.infthink.libs.common.utils.JSONUtils;

public class DataManager {

    // 获得频道集合, 用来建立 id-name 列表
    private static final String URL_GET_CHANNEL_MAP = "http://demo.bibifa.com/getchannellist";
    // 获得推荐频道, 无参数则表示获得首页推荐
    private static final String URL_GET_RECOMMEND_CHANNEL = "http://demo.bibifa.com/getchannelrecommendmedia"; // ?channelid=33554432
    // 获得banner列表, 无参数则表示获得首页banner
    private static final String URL_GET_BANNER = "http://demo.bibifa.com/getbannermedia"; // ?channelid=33554432
    
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
        BitmapLoader.loadBitmap(mService.getBitmapCache(), bitmapListener,
                imageUrl);
    }

    public void loadChannelMap(final IOnloadListener<HashMap<Integer, String>> listener) {
        SimpleTextLoadListener<HashMap<Integer, String>> textLoadListener = new SimpleTextLoadListener<HashMap<Integer, String>>() {

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

    public void loadRecommendChannel(String channelId, final IOnloadListener<RecommendChannel> listener) {
        String args = "";
        if (!Util.isEmpty(channelId)) {
            args = "?" + channelId;
        }
        String textUrl = URL_GET_RECOMMEND_CHANNEL + args;

        SimpleTextLoadListener<RecommendChannel> textLoadListener = new SimpleTextLoadListener<RecommendChannel>() {

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
                                recommendChannel.channelList.add(channel);
                                JSONArray subDatas = channelJson.optJSONArray("data");
                                int subCount = subDatas.length();
                                MediaInfo[] medias = new MediaInfo[subCount];
                                for (int j = 0; j < subCount; j++) {
                                    JSONObject subData = subDatas.optJSONObject(j);
                                    medias[j] = new MediaInfo(subData.toString());
                                }
                                recommendChannel.recommend.put(channel, medias);
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
    

    public void loadBanner(String channelId, final IOnloadListener<Banner[]> listener) {
        String args = "";
        if (!Util.isEmpty(channelId)) {
            args = "?" + channelId;
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
}
