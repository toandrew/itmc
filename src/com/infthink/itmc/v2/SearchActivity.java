package com.infthink.itmc.v2;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.infthink.itmc.v2.adapter.PosterListAdapter;
import com.infthink.itmc.v2.adapter.SearchRecommendAdapter;
import com.infthink.itmc.v2.adapter.SearchResultAdapter;
import com.infthink.itmc.v2.data.DataManager;
import com.infthink.itmc.v2.type.MediaInfo;
import com.infthink.itmc.v2.util.SearchUtil;
import com.infthink.itmc.v2.util.Util;
import com.infthink.itmc.v2.widget.LoadingListView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewFlipper;

public class SearchActivity extends CoreActivity implements OnEditorActionListener {

    private ArrayList<String> mRecommendList;
    private ArrayList<String> mMapKeys = new ArrayList<String>();
    private HashMap<String, CategoryDetailInfo> mCategoryDetailInfoMap = new HashMap();
    private int SEARCH_MASK_ALL = 0;
    private String mCategoryAll;
    private String mCurrentCategory;
    private String mSearchingHint;
    private String mResultRecommendHint;
    private String mSearchKey;

    private SearchRecommendAdapter mRecommendAdapter;
    private ListView mRecommendListView;
    private LoadingListView mRecommendLoadingListView;

    private ViewFlipper mViewFlipper;
    private View mSearchRecommendView;
    private View mSearchResultRecommendView;
    private View mSearchResultView;
    private View mLoadingView;

    private ListView mResultListView;
    private LoadingListView mResultLoadingListView;
    private PosterListAdapter mResultRecommendAdapter;

    private LoadingListView mResultRecommendLoadingListView;
    private ListView mResultRecommendListView;

    private SearchResultAdapter mResultAdapter;
    private TextView mResultTitleTv;
    private TextView mResultRecommendHintTv;
    private TextView mResultRecommendTitleTv;
    private Button mResultTitleBtn;
    private EditText mEditSearch;
    private Drawable mClearTextButton;

    private OnItemClickListener mResultListOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
//            dismissSearchHintView();
//            dismissInputMethod();
            if (arg1.getTag() != null) {
                Intent intent = new Intent(SearchActivity.this, MediaDetailActivity.class);
                MediaInfo mediaInfo = (MediaInfo) arg1.getTag();
                intent.putExtra("mediaInfo", mediaInfo);
                startActivity(intent);
            }
        }
    };
    
    protected void dismissInputMethod() {
        InputMethodManager localInputMethodManager = (InputMethodManager) getApplicationContext()
                .getSystemService("input_method");
        if (localInputMethodManager != null)
            localInputMethodManager.toggleSoftInput(0, 2);
    }

    @Override
    protected void onCreateAfterSuper(Bundle bundle) {
        super.onCreateAfterSuper(bundle);
        getActionBar().hide();
        setContentView(R.layout.search_activity);
        onActive();
    }

    void removeClearButton() {
        mEditSearch.setCompoundDrawables(mEditSearch.getCompoundDrawables()[0],
                mEditSearch.getCompoundDrawables()[1], null,
                mEditSearch.getCompoundDrawables()[3]);
    }

    void addClearButton() {
        mEditSearch.setCompoundDrawables(mEditSearch.getCompoundDrawables()[0],
                mEditSearch.getCompoundDrawables()[1], mClearTextButton,
                mEditSearch.getCompoundDrawables()[3]);
    }

    private void onActive() {
        mEditSearch = (EditText) findViewById(R.id.input);
        mClearTextButton = mEditSearch.getCompoundDrawables()[2];
        // mEditSearch.setTextColor(localResources.getColor(2131230732));
        // mEditSearch.setTextSize(0,
        // localResources.getDimensionPixelSize(2131296410));
        mEditSearch.requestFocus();
        // mEditSearch.addTextChangedListener(this);
        mEditSearch.setOnEditorActionListener(this);
        mEditSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                if (mEditSearch.getText().toString().equals(""))
                    removeClearButton();
                else
                    addClearButton();

            }

        });
        mEditSearch.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Is there an X showing?
                if (mEditSearch.getCompoundDrawables()[2] == null)
                    return false;
                // Only do this for up touches
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                // Is touch on our clear button?
                if (event.getX() > mEditSearch.getWidth()
                        - mEditSearch.getPaddingRight()
                        - mClearTextButton.getIntrinsicWidth()) {
                    mEditSearch.setText("");
                    removeClearButton();
                }
                return false;
            }
        });
        ImageView back = (ImageView) findViewById(R.id.return_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
            
        });

        mRecommendList = new ArrayList<String>();
        // TODO: 测试
        String[] strs = new String[6];
        strs[0] = "测试1";
        strs[1] = "测试2";
        strs[2] = "测试3";
        strs[3] = "测试4";
        strs[4] = "测试5";
        strs[5] = "测试6";
        SearchUtil.sSearchRecommend = strs;
        if (SearchUtil.sSearchRecommend != null
                && SearchUtil.sSearchRecommend.length > 0) {
            for (String searchRecommend : SearchUtil.sSearchRecommend) {
                mRecommendList.add(searchRecommend);
            }
        }
        mSearchingHint = getResources()
                .getString(R.string.find_media_searching);
        mResultRecommendHint = getResources().getString(
                R.string.search_result_recommend_hint);
        mCategoryAll = getResources().getString(R.string.all);
        mCurrentCategory = mCategoryAll;
        CategoryDetailInfo info = new CategoryDetailInfo();
        mCategoryDetailInfoMap.put(mCategoryAll, info);
        mMapKeys.add(mCategoryAll);
        mViewFlipper = (ViewFlipper) findViewById(R.id.search_result_view_flipper);
        mSearchRecommendView = findViewById(R.id.search_recommend_view);
        mSearchResultView = findViewById(R.id.search_result_view);
        mSearchResultRecommendView = findViewById(R.id.search_result_recommend_view);
        mRecommendLoadingListView = (LoadingListView) findViewById(R.id.search_recommend_loading_list_view);
        mRecommendListView = mRecommendLoadingListView.getListView();
        mRecommendAdapter = new SearchRecommendAdapter(this);
        mRecommendAdapter.setGroup(mRecommendList);
        mRecommendListView.setAdapter(mRecommendAdapter);
        // this.mRecommendListView.setOnItemClickListener(this.recommendListOnItemClickListener);
        mLoadingView = View.inflate(this, R.layout.load_view, null);
        mResultLoadingListView = ((LoadingListView) findViewById(R.id.search_result_loading_list_view));
        mResultLoadingListView.setLoadingView(mLoadingView);
        mResultLoadingListView.setShowLoading(true);
        // this.mRetryLoadingView = new RetryLoadingView(this);
        // this.mRetryLoadingView.setOnRetryLoadListener(new
        // RetryLoadingView.OnRetryLoadListener()
        // {
        // public void OnRetryLoad(View paramView)
        // {
        // SearchActivity.this.mResultLoadingListView.setShowLoadingResult(false);
        // SearchActivity.this.mResultLoadingListView.setShowLoading(true);
        // SearchActivity.this.searchCategory(null);
        // }
        // });
        // this.mResultLoadingListView.setLoadingResultView(this.mRetryLoadingView);
        mResultListView = mResultLoadingListView.getListView();
        mResultListView.setVerticalScrollBarEnabled(false);
        mResultListView.setOnItemClickListener(mResultListOnItemClickListener);

        // mResultListView.setLoadMoreView(UIUtil.createMediaLoadMoreView(this));
        // this.mResultListView.setLoadMorePhaseFinished(true);
        // this.mResultListView.setCanLoadMore(true);
        // this.mResultListView.setOnLoadMoreListener(this);
        mResultAdapter = new SearchResultAdapter(this);
        // this.mResultAdapter.setOnClickListener(this);
        mResultListView.setAdapter(mResultAdapter);
        mResultTitleTv = ((TextView) findViewById(R.id.search_result_title_tv));
        mResultTitleBtn = ((Button) findViewById(R.id.search_result_title_btn));
        // mResultTitleBtn.setOnClickListener(this.resultTitleBtnOnClickListener);
        mResultRecommendLoadingListView = ((LoadingListView) findViewById(R.id.search_result_recommend_loading_listview));
        mResultRecommendHintTv = ((TextView) findViewById(R.id.search_result_recommend_hint));
        mResultRecommendTitleTv = ((TextView) findViewById(R.id.search_result_recommend_title));

        // TODO: 无相关视频时的推荐
        mResultRecommendListView = mResultRecommendLoadingListView
                .getListView();
        mResultRecommendAdapter = new PosterListAdapter(this);
        // this.mResultRecommendAdapter.setOnMediaClickListener(this);
        mResultRecommendListView.setAdapter(mResultRecommendAdapter);

        // mSearchKey = getIntent().getStringExtra(KEY_SEARCHKEY);

        // if (Util.isEmpty(mSearchKey)) {
        // if (mRecommendList.size() > 0)
        // mSearchKey = ((String) mRecommendList.get(0));
        // setSearchHint(mSearchKey);
        // showSearchRecommendView();
        // return;
        // }
        // getWindow().setSoftInputMode(50);
        // showSearchResultView();
        // setSearchWord(mSearchKey);
        // searchCategory(null);
        setTitlesSearching();
    }

    private void setTitlesSearching() {
        mResultTitleTv.setText(mSearchingHint);
        mResultTitleTv.setVisibility(View.INVISIBLE);
        mResultRecommendHintTv.setText(mResultRecommendHint);
    }

    public class CategoryDetailInfo {
        public String categoryName = mCategoryAll;
        public int mediaCount;
        public ArrayList<MediaInfo> mediaInfoList = new ArrayList();
        public int pageNo = 1;
        public int searchMask = SEARCH_MASK_ALL;

        public CategoryDetailInfo() {
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId,
            KeyEvent event) {
        if ((event == null) || (event.getAction() == KeyEvent.ACTION_DOWN)) {
            // search
            performSearch();
        }
        return true;
    }

    protected String getSearchBoxText() {
        String str = mEditSearch.getText().toString().trim();
        if (Util.isEmpty(str)) {
            CharSequence localCharSequence = mEditSearch.getHint();
            if (localCharSequence != null)
                str = localCharSequence.toString();
        }
        return str;
    }

    protected void performSearch() {
        // if (isSearchHintViewShowing())
        // dismissSearchHintView();
        String str = getSearchBoxText();
        if (!(Util.isEmpty(str))) {
            // addToHistory(str);
            // saveHistory();
            onPerformSearch(str);
        }
    }
    
    private void showSearchRecommendView()
    {
        if (mViewFlipper.getCurrentView() != mSearchRecommendView) {
            if (mViewFlipper.getCurrentView() == this.mSearchResultView) {
                mViewFlipper.showPrevious();
            }
        } else {
            if (mViewFlipper.getCurrentView() != mSearchResultRecommendView) {
                mViewFlipper.showPrevious();
                mViewFlipper.showPrevious();
            }
        }
    }

//    private void showSearchResultRecommendView()
//    {
//        
//        
//      this.mResultRecommendHintTv.setText(this.mResultRecommendHint);
//      this.mResultRecommendAdapter.setGroup(this.mResultRecommendMediaInfos);
//      if ((this.mResultRecommendMediaInfos == null) || (this.mResultRecommendMediaInfos.length == 0))
//      {
//        this.mResultRecommendTitleTv.setVisibility(4);
//        if (this.mViewFlipper.getCurrentView() != this.mSearchResultRecommendView)
//        {
//          if (this.mViewFlipper.getCurrentView() != this.mSearchRecommendView)
//            break label99;
//          this.mViewFlipper.showNext();
//          this.mViewFlipper.showNext();
//        }
//      }
//      label99: 
//      do
//        while (true)
//        {
//          return;
//          this.mResultRecommendTitleTv.setVisibility(0);
//        }
//      while (this.mViewFlipper.getCurrentView() != this.mSearchResultView;
//      this.mViewFlipper.showNext();
//    }
//
//    private void showSearchResultView()
//    {
//      CategoryDetailInfo localCategoryDetailInfo = (CategoryDetailInfo)this.mCategoryDetailInfoMap.get(this.mCurrentCategory);
//      if (localCategoryDetailInfo != null)
//      {
//        String str1 = getResources().getString(2131427527);
//        Object[] arrayOfObject = new Object[1];
//        arrayOfObject[0] = Integer.valueOf(localCategoryDetailInfo.mediaCount);
//        String str2 = String.format(str1, arrayOfObject);
//        this.mResultTitleTv.setText(str2);
//        this.mResultAdapter.setGroup(localCategoryDetailInfo.mediaInfoList);
//      }
//      if (this.mViewFlipper.getCurrentView() != this.mSearchResultView)
//      {
//        if (this.mViewFlipper.getCurrentView() != this.mSearchRecommendView)
//          break label108;
//        this.mViewFlipper.showNext();
//      }
//      label108: 
//      do
//        return;
//      while (this.mViewFlipper.getCurrentView() != this.mSearchResultRecommendView;
//      this.mViewFlipper.showPrevious();
//    }
    
    private void searchResult(MediaInfo[] medias) {
        mResultAdapter.setGroup(medias);

        if (mResultAdapter.getGroup().size() > 0) {
            while(mViewFlipper.getCurrentView() != this.mSearchResultView) {
                mViewFlipper.showNext();
            }
        } else {
            while(mViewFlipper.getCurrentView() != this.mSearchResultRecommendView) {
                mViewFlipper.showNext();
            }
        }
        mResultLoadingListView.setShowLoading(false);
    }

    public void onPerformSearch(String keyword) {
        mSearchKey = keyword;
        mCurrentCategory = mCategoryAll;
        mResultLoadingListView.setShowLoading(true);
        dismissInputMethod();
        getService().getDataManager().searchMedia("83886080", URLEncoder.encode(keyword), 1, 10, 1, new DataManager.IOnloadListener<MediaInfo[]>() {

            @Override
            public void onLoad(MediaInfo[] medias) {
                searchResult(medias);
            }
        });
//        clearData();
//        mResultListView.setCanLoadMore(true);
//        CategoryDetailInfo localCategoryDetailInfo = new CategoryDetailInfo(
//                this);
//        this.mCategoryDetailInfoMap.put(this.CATEGORY_All,
//                localCategoryDetailInfo);
//        this.mMapKeys.add(this.CATEGORY_All);
//        searchCategory(paramSearchStatisticInfo);
//        if (this.mViewFlipper.getCurrentView() == this.mSearchRecommendView)
//            showSearchResultView();
//        setTitlesSearching();
//            
    }
}
