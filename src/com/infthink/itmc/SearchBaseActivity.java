package com.infthink.itmc;

import java.util.ArrayList;

import com.infthink.itmc.util.Util;
import com.infthink.itmc.widget.DropDownPopupWindow;
import com.infthink.itmc.widget.LoadingListView;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

public abstract class SearchBaseActivity extends CoreActivity {
    private static final String HISTORY_SPLITTER = "%SearchActivity%";
    private static final int MAX_HISTORY_KEY_COUNT = 4;
    private static final String SEARCH_HISTORY_KEY = "history";
    private static final String SEARCH_HISTORY_PREF = "searchHistoryPref";
    public static final String TAG = SearchBaseActivity.class.getName();
    protected String KEY_POSITION = "key_position";
    protected String KEY_SEARCH = "key_search";
    protected String KEY_SOURCE = "key_source";
    private int PAGE_NO = 1;
    private int PAGE_SIZE = 3;
    private ArrayList<String> historyList = new ArrayList();
    private boolean isSetSearchWordMethodInvoked = false;
    protected boolean isShowHintView = false;
    private ArrayList<String> keyWordList = new ArrayList();
    // private DKRequest keyWordRequest;
    // private SearchHintItem.OnSearchHintActionListener
    // onSearchHintActionListener = new
    // SearchHintItem.OnSearchHintActionListener()
    // {
    // public void onDelete(String paramString)
    // {
    // SearchBaseActivity.this.removeFromHistory(paramString);
    // SearchBaseActivity.this.saveHistory();
    // SearchBaseActivity.this.showSearchHintViewHistory();
    // }
    //
    // public void onSelect(String paramString)
    // {
    // if (Util.isEmpty(SearchBaseActivity.this.getSearchBoxText()))
    // SearchBaseActivity.this.searchUserBehaveStatisticInfo.prepareSearchStatisticInfo(paramString,
    // "history", -1);
    // while (true)
    // {
    // SearchBaseActivity.this.setSearchWord(paramString);
    // SearchBaseActivity.this.performSearch(null);
    // return;
    // SearchBaseActivity.this.searchUserBehaveStatisticInfo.prepareSearchStatisticInfo(paramString,
    // "suggestion", -1);
    // }
    // }
    // };
    private SharedPreferences prefs;
    private EditText searchBox;
    // private SearchHintAdapter searchHintAdapter;
    private ArrayList<String> searchHintList = new ArrayList();
    private ListView searchHintListView;
    private LoadingListView searchHintLoadingListView;
    private PopupWindow searchHintView;
    private View searchPanel;
    // protected SearchUserBehaveStatisticInfo searchUserBehaveStatisticInfo =
    // new SearchUserBehaveStatisticInfo();

    private void addToHistory(String history) {
        if (!Util.isEmpty(history)) {
            if (historyList.size() > 0 && historyList.contains(history)) {
                historyList.remove(history);
            }
            historyList.add(0, history);
            if (historyList.size() > MAX_HISTORY_KEY_COUNT) {
                historyList.remove(historyList.size() - 1);
            }
        }
    }

    private void cancelRequests() {
        // if (this.keyWordRequest != null)
        // this.keyWordRequest.cancelRequest();
    }
    
    private void createSearchHintView()
    {
        
        
      if (searchHintView == null)
      {
          this.searchHintView = new DropDownPopupWindow(this, this.searchHintLoadingListView);
        this.searchHintView.setBackgroundDrawable(getResources().getDrawable(2130837803));
        this.searchHintView.setWidth(-1);
        this.searchHintView.setHeight(-2);
        this.searchHintView.setFocusable(false);
      }
      if (this.isShowHintView);
      try
      {
        this.searchHintView.showAsDropDown(this.searchPanel, 0, 0);
        return;
      }
      catch (Exception localException)
      {
//        DKLog.e(TAG, localException.getLocalizedMessage());
      }
    }
}
