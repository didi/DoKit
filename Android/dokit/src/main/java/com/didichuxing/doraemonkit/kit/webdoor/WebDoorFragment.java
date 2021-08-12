package com.didichuxing.doraemonkit.kit.webdoor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wanglikun on 2018/10/10.
 */

public class WebDoorFragment extends BaseFragment {
    private EditText mWebAddressInput;
    private TextView mUrlExplore;
    private RecyclerView mHistoryList;
    private WebDoorHistoryAdapter mWebDoorHistoryAdapter;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_QR_CODE = 3;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA};


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
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebDoorManager.getInstance().clearHistory();
                mWebDoorHistoryAdapter.clear();
            }
        });
        findViewById(R.id.qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCode();
            }
        });
        mUrlExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch(mWebAddressInput.getText().toString());
            }
        });
        mHistoryList = findViewById(R.id.history_list);
        mHistoryList.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mHistoryList.setLayoutManager(layoutManager);
        List<String> historyItems = WebDoorManager.getInstance().getHistory();

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
        WebDoorManager.getInstance().saveHistory(url);
        WebDoorManager.getInstance().getWebDoorCallback().overrideUrlLoading(getContext(), url);
        mWebDoorHistoryAdapter.setData(WebDoorManager.getInstance().getHistory());
    }

    private boolean checkInput() {
        return !TextUtils.isEmpty(mWebAddressInput.getText());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_QR_CODE) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            if (!TextUtils.isEmpty(result)) {
                doSearch(result);
            }
        }
    }

    private void qrCode() {
        if (!ownPermissionCheck()) {
            requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
            return;
        }
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_QR_CODE);
    }

    private boolean ownPermissionCheck() {
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) {
                    ToastUtils.showShort(R.string.dk_error_tips_permissions_less);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
