package com.didichuxing.doraemonkit.gps_mock.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.didichuxing.doraemonkit.gps_mock.R;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class PositionSelectDialogHelper implements IDialogHelper, TextWatcher, View.OnClickListener, OnGetSuggestionResultListener {
    private static final String TAG = PositionSelectDialogHelper.class.getSimpleName();
    private final Context mContext;
    private PositionSelectRecyclerAdapter.IPositionItemSelectedCallback mPositionSelectedCallback;
    private View mHeadView;
    private EditText mEdtSelectPosition;
    private RecyclerView mRvPositionList;
    private SuggestionSearch mSuggestionSearch;
    private String mLocCityName = "杭州";
    private String mCurArea = "西溪谷";
    private final List<SuggestionResult.SuggestionInfo> mPositionDataList = new ArrayList<>();
    private PositionSelectRecyclerAdapter mAdapter;

    public PositionSelectDialogHelper(Context context, PositionSelectRecyclerAdapter.IPositionItemSelectedCallback positionSelectedCallback) {
        this.mContext = context;
        this.mPositionSelectedCallback = positionSelectedCallback;
    }

    @Override
    public void init(View rootView) {
        mHeadView = rootView.findViewById(R.id.head_view);
        mEdtSelectPosition = rootView.findViewById(R.id.edt_select_position);
        mRvPositionList = rootView.findViewById(R.id.rv_position_list);
        mEdtSelectPosition.setOnClickListener(this);
        mEdtSelectPosition.addTextChangedListener(this);

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvPositionList.setLayoutManager(linearLayoutManager);
        mAdapter = new PositionSelectRecyclerAdapter(mPositionDataList);
        mRvPositionList.setAdapter(mAdapter);

        if (mPositionSelectedCallback != null) {
            mAdapter.setItemSelectedCallback(mPositionSelectedCallback);
        }

        searchSuggestPos(mCurArea);
    }

    private void searchSuggestPos(String keyWord){
        mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
            .keyword(keyWord)
            .city(mLocCityName));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edt_select_position) {

        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        LogHelper.d(TAG, "beforeTextChanged  " + charSequence + " i=" + i + " i1=" + i1 + " i2=" + i2);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        LogHelper.d(TAG, "onTextChanged  " + charSequence + " i=" + i + " i1=" + i1 + " i2=" + i2);
        if (TextUtils.isEmpty(charSequence)) {
            mPositionDataList.clear();
            mAdapter.notifyDataSetChanged();
            charSequence = "";
        }
        searchSuggestPos(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        LogHelper.d(TAG, "afterTextChanged  " + editable);
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            LogHelper.d(TAG, "search address result: result is null");
            return;
        }

        LogHelper.d(TAG, "search address result: " + ("size: " + res.getAllSuggestions().size() + res.getAllSuggestions()));
        mPositionDataList.clear();
        mPositionDataList.addAll(res.getAllSuggestions());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
    }
}

