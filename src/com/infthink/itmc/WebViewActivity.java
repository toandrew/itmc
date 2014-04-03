package com.infthink.itmc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.util.Html5PlayUrlRetriever;
import com.infthink.itmc.util.Util;
import com.infthink.itmc.util.Html5PlayUrlRetriever.PlayUrlListener;

public class WebViewActivity extends BaseWebViewActivity implements
        PlayUrlListener {
    public static final String KEY_CI = "ci";
    public static final String KEY_ENTER_PATH_INFO = "enterPathInfo";
    public static final String KEY_HTML5_LOCALPLAYHISTORY_INFO = "html5LocalPlayHistory";
    public static final String KEY_MEDIA_INFO = "mediaInfo";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_URL = "url";

    private Html5PlayUrlRetriever mUrlRetriever = null;
    private MediaInfo mMediaInfo = null;
//    private Hashtable<String, MediaUrlMetaInfo> mediaUrlMetaInfoCache = new Hashtable();
//    private Toast mNoVolumeToast;
//    private OpenMediaStatisticInfo openMediaStatisticInfo = null;
    private List<String> pageUrlList = new ArrayList();
    private int mCi = 1;
    private int mSource = -1;
    private View mTitleView;
    private String mUrl;
    private boolean mUrlLoadFinish = false;
    private List<String> mUrlLoadingList = new ArrayList();
    private ViewGroup mWebViewWrap;
    private HashMap<Integer, String> mSourcesMap;
    private Html5PlayUrlRetriever mRetriever;
    private String mPlayUrl;
    private String mPageUrl;

    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);
        
        Intent intent = getIntent();
        mSource = intent.getIntExtra("source", -1);
        mPageUrl = Util.replaceString(intent.getStringExtra("pageUrl"), "\\", "").trim();
        android.util.Log.d("XXXXXXXXX", "mSource = " + mSource);
        initWebView();
        FrameLayout contentView = new FrameLayout(this);
        
        contentView.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(contentView);
        
        mSourcesMap = new HashMap<Integer, String>();
        mSourcesMap.put(32, "http://m.letv.com/vplay_1934457.html?ref=xiaomi");
        mSourcesMap.put(33, "http://v.pps.tv/play_35EKJB.html?from_xiaomi");
        mSourcesMap.put(34, "http://m.funshion.com/subject?mediaid=112875&number=20140213&malliance=1660");
        mSourcesMap.put(3, "http://m.tv.sohu.com/20140131/n394431306.shtml?src=10000001");
        mSourcesMap.put(8, "http://m.iqiyi.com/play.html?tvid=430013&vid=bf2e094d29a346ee9af2d7feb24214dc&msrc=3_69_145");
        mSourcesMap.put(10, "http://3g.v.qq.com/android/player.html?type=1&cid=9q8a0ll3ibmduvz&vid=u0012qr5vr1&protype=10&version=xiaomi1.0.0&hidemp4=1");
        mSourcesMap.put(17, "http://kids.lekan.com/external/xiaomi/play/free/134162/1/1");
        mSourcesMap.put(20, "http://v.youku.com/v_show/id_XNjUyOTE4NTMy.html?tpa=dW5pb25faWQ9MTAzNDcyXzEwMDAwMV8wMV8wMQ?tpa=dW5pb25faWQ9MTAzNDcyXzEwMDAwMV8wMV8wMQ");
        mSourcesMap.put(23, "http://www.tudou.com/albumplay/eCRSK2T7WYk/50y3WYA20z4.html?tpa=dW5pb25faWQ9MTAzNDcyXzEwMDAwMV8wMV8wMQ");
        mSourcesMap.put(24, "http://v.ifeng.com/documentary/discovery/201205/4f31e712-9188-4ed8-9410-efc1dcaef552.shtml#_vapp_xiaomi");
        mSourcesMap.put(25, "http://m1905.cn/Index/index/__SID/2e9ad1fbdfd08ffeaf94074b53f86018");
        mSourcesMap.put(31, "http://m.bestv.com.cn/wap/xmyl/by/index.jsp?c=600003031");
//        mSource = 3;
        mWebView.loadUrl(mPageUrl);

        mRetriever = new Html5PlayUrlRetriever(mWebView, mSource);
        mRetriever.setPlayUrlListener(this);
    }

    @Override
    public void onUrlUpdate(String pageUrl, final String playUrl) {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WebViewActivity.this, MediaPlayerActivity.class);
                intent.putExtra("path", playUrl);
                startActivity(intent);
            }
        });
    }
    
    protected void onPageFinish(WebView webView, String url) {
        mRetriever.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mRetriever.release();
        mWebView.destroy();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }
    
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

//    
//    @Override
//    protected void onCreateAfterSuper(Bundle bundle) {
//        super.onCreateAfterSuper(bundle);
//        if (bundle != null) {
//            // isActivityKilledAndNewCreate = true;
//        }
//        Window window = getWindow();
//        // window.setBackgroundDrawableResource(2131230741);
//        window.addFlags(128);
//        // setWindowFullScreen(getResources().getConfiguration().orientation);
//        // setContentView(2130903140);
//        Intent intent = getIntent();
//        mMediaInfo = ((MediaInfo) intent.getSerializableExtra(KEY_MEDIA_INFO));
//        mCi = intent.getIntExtra(KEY_CI, mCi);
//        // this.localPlayHistoryInfo = LocalPlayHistoryInfo.getInstance();
//        mSource = intent.getIntExtra(KEY_SOURCE, mSource);
//        mUrl = intent.getStringExtra(KEY_URL);
//        mOpenMediaStatisticInfo = ((OpenMediaStatisticInfo) intent
//                .getSerializableExtra(KEY_ENTER_PATH_INFO));
//        onActivate();
//        adjustLayout(getResources().getConfiguration().orientation);
//        isActivityNewCreate = true;
//    }
//    
//    private void onActivate() {
//        getSettingsPrefrence();
//        mAudioService = ((AudioManager)getSystemService("audio"));
//        initWebView();
//        mWebView.setWebChromeClient(mChromeClient);
//        mWebView.addJavascriptInterface(new WebPageObject(), "WebPage");
//        mBarsIsShow = true;
//        this.barsIsAnimating = false;
//        this.btnFullscreen = null;
//        initUI();
//        super.setTitleSize(getResources().getDimensionPixelSize(101318729));
//        String str = this.mediaInfo.mediaName;
//        if (this.mediaInfo.isMultiSetType())
//        {
//          if (getString(2131427362).equals(this.mediaInfo.category))
//          {
//            super.setTitle(getTitle(str, ""));
//            return;
//          }
//          super.setTitle(getTitle(str, this.ci));
//          return;
//        }
//        super.setTitle(str);
//    }
//    
//    private void adjustLayout(int orientation) {
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            this.btnTopFullScreen.setVisibility(0);
//            this.titleView.setBackgroundResource(2130837797);
//            this.bottomView.setVisibility(4);
//            if (this.btnFullscreen != null)
//              this.btnTopFullScreen.setEnabled(this.btnFullscreen.isEnabled());
//            this.btnFullscreen = this.btnTopFullScreen;
//        } else {
//            this.btnTopFullScreen.setVisibility(8);
//            this.titleView.setBackgroundResource(100794956);
//            this.bottomView.setVisibility(0);
//            if (this.btnFullscreen != null)
//              this.btnBottomFullScreen.setEnabled(this.btnFullscreen.isEnabled());
//            this.btnFullscreen = this.btnBottomFullScreen;
//            this.btnFullscreen.setBackgroundResource(100794605);
//        }
//        
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration configuration) {
//        super.onConfigurationChanged(configuration);
//        int orientation = configuration.orientation;
//        setWindowFullScreen(orientation);
//        adjustLayout(orientation);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mWebViewWrap.removeView(mWebView);
//        mWebView.removeAllViews();
//        if (mUrlRetriever != null)
//            mUrlRetriever.release();
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                mWebView.destroy();
//            }
//        }, 500);
//    }
//
//    @Override
//    public void onUrlUpdate(String pageUrl, String playUrl) {
//
//    }

}
