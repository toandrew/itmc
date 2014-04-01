package com.infthink.itmc;

import java.util.ArrayList;
import java.util.HashMap;

import com.infthink.itmc.adapter.PosterListAdapter;
import com.infthink.itmc.adapter.SearchRecommendAdapter;
import com.infthink.itmc.adapter.SearchResultAdapter;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.util.SearchUtil;
import com.infthink.itmc.widget.LoadingListView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SearchActivity extends Activity {

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
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActionBar().hide();
        setContentView(R.layout.search_activity);
        onActive();
    }

    private void onActive() {
        mRecommendList = new ArrayList<String>();
        //TODO: 测试
        String[] strs = new String[6];
        strs[0] = "测试1";
        strs[1] = "测试2";
        strs[2] = "测试3";
        strs[3] = "测试4";
        strs[4] = "测试5";
        strs[5] = "测试6";
        SearchUtil.sSearchRecommend = strs;
        if (SearchUtil.sSearchRecommend != null && SearchUtil.sSearchRecommend.length > 0) {
            for(String searchRecommend : SearchUtil.sSearchRecommend) {
                mRecommendList.add(searchRecommend);
            }
        }
        mSearchingHint = getResources().getString(R.string.find_media_searching);
        mResultRecommendHint = getResources().getString(R.string.search_result_recommend_hint);
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
//        this.mRecommendListView.setOnItemClickListener(this.recommendListOnItemClickListener);
        mLoadingView = View.inflate(this, R.layout.load_view, null);
        mResultLoadingListView = ((LoadingListView)findViewById(R.id.search_result_loading_list_view));
        mResultLoadingListView.setLoadingView(mLoadingView);
        mResultLoadingListView.setShowLoading(true);
//        this.mRetryLoadingView = new RetryLoadingView(this);
//        this.mRetryLoadingView.setOnRetryLoadListener(new RetryLoadingView.OnRetryLoadListener()
//        {
//          public void OnRetryLoad(View paramView)
//          {
//            SearchActivity.this.mResultLoadingListView.setShowLoadingResult(false);
//            SearchActivity.this.mResultLoadingListView.setShowLoading(true);
//            SearchActivity.this.searchCategory(null);
//          }
//        });
//        this.mResultLoadingListView.setLoadingResultView(this.mRetryLoadingView);
        mResultListView = mResultLoadingListView.getListView();
        mResultListView.setVerticalScrollBarEnabled(false);
//        this.mResultListView.setOnItemClickListener(this.resultListOnItemClickListener);
//        mResultListView.setLoadMoreView(UIUtil.createMediaLoadMoreView(this));
//        this.mResultListView.setLoadMorePhaseFinished(true);
//        this.mResultListView.setCanLoadMore(true);
//        this.mResultListView.setOnLoadMoreListener(this);
        mResultAdapter = new SearchResultAdapter(this);
//        this.mResultAdapter.setOnClickListener(this);
        mResultListView.setAdapter(mResultAdapter);
        mResultTitleTv = ((TextView)findViewById(R.id.search_result_title_tv));
        mResultTitleBtn = ((Button)findViewById(R.id.search_result_title_btn));
//        mResultTitleBtn.setOnClickListener(this.resultTitleBtnOnClickListener);
        mResultRecommendLoadingListView = ((LoadingListView)findViewById(R.id.search_result_recommend_loading_listview));
        mResultRecommendHintTv = ((TextView)findViewById(R.id.search_result_recommend_hint));
        mResultRecommendTitleTv = ((TextView)findViewById(R.id.search_result_recommend_title));
        
        // TODO: not use
        mResultRecommendListView = mResultRecommendLoadingListView.getListView();
        mResultRecommendAdapter = new PosterListAdapter(this);
//        this.mResultRecommendAdapter.setOnMediaClickListener(this);
        mResultRecommendListView.setAdapter(mResultRecommendAdapter);

//        mSearchKey = getIntent().getStringExtra(KEY_SEARCHKEY);
        
//        if (Util.isEmpty(mSearchKey)) {
//            if (mRecommendList.size() > 0)
//                mSearchKey = ((String) mRecommendList.get(0));
//            setSearchHint(mSearchKey);
//            showSearchRecommendView();
//            return;
//        }
//        getWindow().setSoftInputMode(50);
//        showSearchResultView();
//        setSearchWord(mSearchKey);
//        searchCategory(null);
        setTitlesSearching();
        test();
    }
    
    private void test() {
        mViewFlipper.showNext();
    }
    
    private void setTitlesSearching() {
        mResultTitleTv.setText(mSearchingHint);
        mResultRecommendHintTv.setText(mSearchingHint);
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
}
