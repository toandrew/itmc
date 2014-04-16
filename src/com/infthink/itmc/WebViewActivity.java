package com.infthink.itmc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.infthink.itmc.data.DataManager;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.RecommendChannel;
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
    // private Hashtable<String, MediaUrlMetaInfo> mediaUrlMetaInfoCache = new
    // Hashtable();
    // private Toast mNoVolumeToast;
    // private OpenMediaStatisticInfo openMediaStatisticInfo = null;
    private List<String> pageUrlList = new ArrayList();
    private int mCi = 1;
    private int mSource = -1;
    private View mTitleView;
    private View mBottomView;
    private String mUrl;
    private boolean mUrlLoadFinish = false;
    private List<String> mUrlLoadingList = new ArrayList();
    private ViewGroup mWebViewWrap;
    private HashMap<Integer, String> mSourcesMap;
    private Html5PlayUrlRetriever mRetriever;
    private String mPlayUrl;
    private String mPageUrl;
    private String mMediaTitle;
    private int mMediaCount;
    private int mMediaId;
    private DataManager mDataManager;
    private boolean mIsPageFinished = false;
    private boolean mIsLoadedUrl = false;

    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);

        Intent intent = getIntent();
        
        mMediaId = intent.getIntExtra("media_id", -1);
        mMediaTitle = intent.getStringExtra("meidaTitle");
        mMediaCount = intent.getIntExtra("available_episode_count", 0);
        mCi = intent.getIntExtra("current_episode", 0);
        mSource = intent.getIntExtra("source", -1);
        mPageUrl = Util.replaceString(intent.getStringExtra("pageUrl"), "\\",
                "").trim();
        android.util.Log.d("XXXXXXXXX", "mPageUrl = " + mPageUrl
                + "; mSource = " + mSource + "; mMediaTitle = " + mMediaTitle
                + "; mMediaCount = " + mMediaCount + "; mCi = " + mCi);
        initUI();
        FrameLayout contentView = new FrameLayout(this);

        contentView.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(contentView);

        mWebView.loadUrl(mPageUrl);

        mRetriever = new Html5PlayUrlRetriever(mWebView, mSource);
        mRetriever.setPlayUrlListener(this);
    }
    
    @Override
    protected void onInitialized() {
        mDataManager = getService().getDataManager();
        mDataManager.loadMediaPlayUrl(mMediaId, mCi, mSource, new DataManager.IOnloadListener<String>() {
            @Override
            public void onLoad(String entity) {
                mIsLoadedUrl = true;
//                if (entity != null && entity.length() > 0) {
//                    startPlayer(entity);
//                } else {
                    startRetriever();
//                }
            }
        });
    }

    private void initUI() {
        initWebView();
        // mWebView.setWebChromeClient(mChromeClient);

        // this.webView.setWebChromeClient(this.chromeClient);
        // this.webViewWrap = ((ViewGroup)findViewById(2131165431));
        // this.bottomView = findViewById(2131165429);
        // this.bottomView.setVisibility(4);
        // this.btnBottomFullScreen =
        // ((Button)this.bottomView.findViewById(2131165430));
        // this.btnBottomFullScreen.setEnabled(false);
        // this.btnBottomFullScreen.setOnClickListener(this);
        // this.titleView = findViewById(2131165434);
        // this.webViewWrap.getViewTreeObserver().addOnGlobalLayoutListener(new
        // ViewTreeObserver.OnGlobalLayoutListener()
        // {
        // public void onGlobalLayout()
        // {
        // int i = WebMediaActivity.this.titleView.getHeight();
        // if (WebMediaActivity.this.barsIsShow)
        // {
        // WebMediaActivity.this.webViewWrap.setTranslationY(i);
        // return;
        // }
        // WebMediaActivity.this.webViewWrap.offsetTopAndBottom(-i);
        // }
        // });
        // this.btnTopFullScreen =
        // ((Button)this.titleView.findViewById(2131165430));
        // this.btnTopFullScreen.setEnabled(false);
        // this.btnTopFullScreen.setOnClickListener(this);
    }

    private void initSourceMap() {
        mSourcesMap = new HashMap<Integer, String>();
        mSourcesMap.put(32, "http://m.letv.com/vplay_1934457.html?ref=xiaomi");
        mSourcesMap.put(33, "http://v.pps.tv/play_35EKJB.html?from_xiaomi");
        mSourcesMap
                .put(34,
                        "http://m.funshion.com/subject?mediaid=112875&number=20140213&malliance=1660");
        mSourcesMap.put(3,
                "http://m.tv.sohu.com/20140131/n394431306.shtml?src=10000001");
        mSourcesMap
                .put(8,
                        "http://m.iqiyi.com/play.html?tvid=430013&vid=bf2e094d29a346ee9af2d7feb24214dc&msrc=3_69_145");
        mSourcesMap
                .put(10,
                        "http://3g.v.qq.com/android/player.html?type=1&cid=9q8a0ll3ibmduvz&vid=u0012qr5vr1&protype=10&version=xiaomi1.0.0&hidemp4=1");
        mSourcesMap.put(17,
                "http://kids.lekan.com/external/xiaomi/play/free/134162/1/1");
        mSourcesMap
                .put(20,
                        "http://v.youku.com/v_show/id_XNjUyOTE4NTMy.html?tpa=dW5pb25faWQ9MTAzNDcyXzEwMDAwMV8wMV8wMQ?tpa=dW5pb25faWQ9MTAzNDcyXzEwMDAwMV8wMV8wMQ");
        mSourcesMap
                .put(23,
                        "http://www.tudou.com/albumplay/eCRSK2T7WYk/50y3WYA20z4.html?tpa=dW5pb25faWQ9MTAzNDcyXzEwMDAwMV8wMV8wMQ");
        mSourcesMap
                .put(24,
                        "http://v.ifeng.com/documentary/discovery/201205/4f31e712-9188-4ed8-9410-efc1dcaef552.shtml#_vapp_xiaomi");
        mSourcesMap
                .put(25,
                        "http://m1905.cn/Index/index/__SID/2e9ad1fbdfd08ffeaf94074b53f86018");
        mSourcesMap.put(31,
                "http://m.bestv.com.cn/wap/xmyl/by/index.jsp?c=600003031");
    }

    @Override
    public void onUrlUpdate(String pageUrl, final String playUrl) {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                startPlayer(playUrl);
            }
        });
    }

    protected void onPageFinish(WebView webView, String url) {
        mIsPageFinished = true;
        startRetriever();
    }
    
    private void startRetriever() {
        if (mIsPageFinished && mIsLoadedUrl) {
            mRetriever.start();
        }
    }
    
    private void startPlayer(String url) {
        mPlayUrl = url;
        mUrlLoadFinish = true;
        Intent intent = new Intent(WebViewActivity.this,
                MediaPlayerActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra("path", mPlayUrl);
        intent.putExtra("pageUrl", mPageUrl);
        startActivity(intent);
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

    private float mLastY = -1;

    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mLastY = -1;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mLastY == -1) {
                mLastY = event.getY();
            }
            if ((event.getY() - mLastY > 0) && mUrlLoadFinish) {
//                showBarsWithAnimation();
            } else {
//                hideBarsWithAnimation();
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mLastY = -1;
            break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean mBarsIsAnimating = false;
    private boolean mBarsIsShow = false;
    private int ANIM_DURATION = 300;

    private void showBarsWithAnimation() {
        if (mBarsIsAnimating || mBarsIsShow) {
            return;
        }
        mBarsIsShow = true;
        mBarsIsAnimating = true;
        int titleHeight = mTitleView.getHeight();
        final float scale = Math.round(mBottomView.getHeight() / titleHeight);
        ValueAnimator animatior = new ValueAnimator();
        int[] titleViewValues = new int[2];
        titleViewValues[0] = -titleHeight;
        titleViewValues[1] = 0;
        animatior.setIntValues(titleViewValues);
        animatior.setDuration(ANIM_DURATION);
        animatior.setInterpolator(new DecelerateInterpolator(2.5f));
        animatior.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                mTitleView.setTranslationY(value);
                mWebViewWrap.offsetTopAndBottom(value - mWebViewWrap.getTop());
                mBottomView.setTranslationY(-(Math.round(value * scale)));
            }
        });

        animatior.addListener(new AnimatorListener() {

            @Override
            public void onAnimationCancel(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                mBarsIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationStart(Animator arg0) {

            }
        });
        animatior.start();
    }

    private void hideBarsWithAnimation() {
        if (mBarsIsAnimating || !mBarsIsShow) {
            return;
        }
        mBarsIsShow = false;
        mBarsIsAnimating = true;
        final int titleViewHeight = mTitleView.getHeight();
        final float scale = Math.round(mBottomView.getHeight()
                / titleViewHeight);
        ValueAnimator animator = new ValueAnimator();
        int[] values = new int[2];
        values[0] = 0;
        values[1] = -titleViewHeight;
        animator.setIntValues(values);
        animator.setDuration(ANIM_DURATION);
        animator.setInterpolator(new DecelerateInterpolator(2.5f));
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                mTitleView.setTranslationY(value);
                mWebViewWrap.offsetTopAndBottom(value - mWebViewWrap.getTop());
                mBottomView.setTranslationY(-(Math.round(value * scale)));
            }

        });
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBarsIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

        });
        animator.start();
    }

    //
    // @Override
    // protected void onCreateAfterSuper(Bundle bundle) {
    // super.onCreateAfterSuper(bundle);
    // if (bundle != null) {
    // // isActivityKilledAndNewCreate = true;
    // }
    // Window window = getWindow();
    // // window.setBackgroundDrawableResource(2131230741);
    // window.addFlags(128);
    // // setWindowFullScreen(getResources().getConfiguration().orientation);
    // // setContentView(2130903140);
    // Intent intent = getIntent();
    // mMediaInfo = ((MediaInfo) intent.getSerializableExtra(KEY_MEDIA_INFO));
    // mCi = intent.getIntExtra(KEY_CI, mCi);
    // // this.localPlayHistoryInfo = LocalPlayHistoryInfo.getInstance();
    // mSource = intent.getIntExtra(KEY_SOURCE, mSource);
    // mUrl = intent.getStringExtra(KEY_URL);
    // mOpenMediaStatisticInfo = ((OpenMediaStatisticInfo) intent
    // .getSerializableExtra(KEY_ENTER_PATH_INFO));
    // onActivate();
    // adjustLayout(getResources().getConfiguration().orientation);
    // isActivityNewCreate = true;
    // }
    //
    // private void onActivate() {
    // getSettingsPrefrence();
    // mAudioService = ((AudioManager)getSystemService("audio"));
    // initWebView();
    // mWebView.setWebChromeClient(mChromeClient);
    // mWebView.addJavascriptInterface(new WebPageObject(), "WebPage");
    // mBarsIsShow = true;
    // this.barsIsAnimating = false;
    // this.btnFullscreen = null;
    // initUI();
    // super.setTitleSize(getResources().getDimensionPixelSize(101318729));
    // String str = this.mediaInfo.mediaName;
    // if (this.mediaInfo.isMultiSetType())
    // {
    // if (getString(2131427362).equals(this.mediaInfo.category))
    // {
    // super.setTitle(getTitle(str, ""));
    // return;
    // }
    // super.setTitle(getTitle(str, this.ci));
    // return;
    // }
    // super.setTitle(str);
    // }
    //
    // private void adjustLayout(int orientation) {
    // if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
    // this.btnTopFullScreen.setVisibility(0);
    // this.titleView.setBackgroundResource(2130837797);
    // this.bottomView.setVisibility(4);
    // if (this.btnFullscreen != null)
    // this.btnTopFullScreen.setEnabled(this.btnFullscreen.isEnabled());
    // this.btnFullscreen = this.btnTopFullScreen;
    // } else {
    // this.btnTopFullScreen.setVisibility(8);
    // this.titleView.setBackgroundResource(100794956);
    // this.bottomView.setVisibility(0);
    // if (this.btnFullscreen != null)
    // this.btnBottomFullScreen.setEnabled(this.btnFullscreen.isEnabled());
    // this.btnFullscreen = this.btnBottomFullScreen;
    // this.btnFullscreen.setBackgroundResource(100794605);
    // }
    //
    // }
    //
    // @Override
    // public void onConfigurationChanged(Configuration configuration) {
    // super.onConfigurationChanged(configuration);
    // int orientation = configuration.orientation;
    // setWindowFullScreen(orientation);
    // adjustLayout(orientation);
    // }
    //
    // @Override
    // protected void onDestroy() {
    // super.onDestroy();
    // mWebViewWrap.removeView(mWebView);
    // mWebView.removeAllViews();
    // if (mUrlRetriever != null)
    // mUrlRetriever.release();
    // mHandler.postDelayed(new Runnable() {
    // public void run() {
    // mWebView.destroy();
    // }
    // }, 500);
    // }
    //
    // @Override
    // public void onUrlUpdate(String pageUrl, String playUrl) {
    //
    // }

}
