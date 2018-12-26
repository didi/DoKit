package com.didichuxing.doraemonkit.kit.webdoor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.webdoor.WebDoorHistoryAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

import java.util.List;

/**
 * Created by wanglikun on 2018/10/10.
 */

public class WebDoorFragment extends BaseFragment {
    private EditText mWebAddressInput;
    private TextView mUrlExplore;
    private RecyclerView mHistoryList;
    private WebDoorHistoryAdapter mWebDoorHistoryAdapter;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_web_door;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        mWebAddressInput = findViewById(R.id.web_address_input);
        mWebAddressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput()) {
                    mUrlExplore.setEnabled(true);
                } else {
                    mUrlExplore.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mUrlExplore = findViewById(R.id.url_explore);
        mUrlExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch(mWebAddressInput.getText().toString());
            }
        });
        mHistoryList = findViewById(R.id.history_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mHistoryList.setLayoutManager(layoutManager);
        List<String> historyItems = WebDoorManager.getInstance().getHistory(getContext());

        mWebDoorHistoryAdapter = new WebDoorHistoryAdapter(getContext());
        mWebDoorHistoryAdapter.setData(historyItems);
        mWebDoorHistoryAdapter.setOnItemClickListener(new WebDoorHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                doSearch(data);
            }
        });
        mHistoryList.setAdapter(mWebDoorHistoryAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mHistoryList.addItemDecoration(decoration);
    }

    private void doSearch(String url) {
        if (!WebDoorManager.getInstance().isWebDoorEnable()) {
            return;
        }
        WebDoorManager.getInstance().saveHistory(getContext(), url);
        WebDoorManager.getInstance().getWebDoorCallback().overrideUrlLoading(url);
        mWebDoorHistoryAdapter.setData(WebDoorManager.getInstance().getHistory(getContext()));
    }

    private boolean checkInput() {
        return !TextUtils.isEmpty(mWebAddressInput.getText());
    }
}
