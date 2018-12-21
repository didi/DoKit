package com.didichuxing.doraemonkit.kit.logInfo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.loginfo.LogItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/10/9.
 */

public class LogInfoFloatPage extends BaseFloatPage implements LogInfoManager.OnLogCatchListener {
    private static final String TAG = "LogInfoFloatPage";

    private static final int MAX_LOG_LINE_NUM = 50;

    private RecyclerView mLogList;
    private LogItemAdapter mLogItemAdapter;
    private EditText mLogFilter;
    private RadioGroup mRadioGroup;
    private TitleBar mTitleBar;
    private List<LogInfoItem> mLogInfoItems = new ArrayList<>();

    private WindowManager mWindowManager;
    private TextView mLogHint;
    private View mLogPage;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LogInfoManager.getInstance().registerListener(this);
        LogInfoManager.getInstance().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogInfoManager.getInstance().stop();
        LogInfoManager.getInstance().removeListener();
        mLogInfoItems.clear();
        mLogInfoItems = null;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.float_log_info, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        initView();
    }

    public void initView() {
        mLogHint = findViewById(R.id.log_hint);
        mLogPage = findViewById(R.id.log_page);
        mLogList = findViewById(R.id.log_list);
        mLogList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLogItemAdapter = new LogItemAdapter(getContext());
        mLogList.setAdapter(mLogItemAdapter);
        mLogFilter = findViewById(R.id.log_filter);
        mLogFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(mLogFilter.getText())) {
                    CharSequence filter = mLogFilter.getText();
                    List<LogInfoItem> infoItems = new ArrayList<>();
                    for (LogInfoItem item : mLogInfoItems) {
                        if (item.orginalLog.contains(filter)) {
                            infoItems.add(item);
                        }
                    }
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(infoItems);
                } else {
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(mLogInfoItems);
                }
            }
        });
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                hidePage();
            }

            @Override
            public void onRightClick() {

            }
        });
        mLogHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPage();
            }
        });
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.verbose) {
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(mLogInfoItems);
                } else if (checkedId == R.id.debug) {
                    List<LogInfoItem> infoItems = new ArrayList<>();
                    for (LogInfoItem item : mLogInfoItems) {
                        if (item.level >= Log.DEBUG) {
                            infoItems.add(item);
                        }
                    }
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(infoItems);
                } else if (checkedId == R.id.info) {
                    List<LogInfoItem> infoItems = new ArrayList<>();
                    for (LogInfoItem item : mLogInfoItems) {
                        if (item.level >= Log.INFO) {
                            infoItems.add(item);
                        }
                    }
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(infoItems);
                } else if (checkedId == R.id.warn) {
                    List<LogInfoItem> infoItems = new ArrayList<>();
                    for (LogInfoItem item : mLogInfoItems) {
                        if (item.level >= Log.WARN) {
                            infoItems.add(item);
                        }
                    }
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(infoItems);
                } else if (checkedId == R.id.error) {
                    List<LogInfoItem> infoItems = new ArrayList<>();
                    for (LogInfoItem item : mLogInfoItems) {
                        if (item.level >= Log.ERROR) {
                            infoItems.add(item);
                        }
                    }
                    mLogItemAdapter.clear();
                    mLogItemAdapter.setData(infoItems);
                }
            }
        });
        mRadioGroup.check(R.id.verbose);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
    }

    @Override
    public void onLogCatch(LogInfoItem infoItem) {
        if (mLogList == null || mLogItemAdapter == null) {
            return;
        }
        mLogInfoItems.add(infoItem);
        if (mLogInfoItems.size() == MAX_LOG_LINE_NUM) {
            mLogInfoItems.remove(0);
        }
        if (infoItem.level >= getSelectLogLevel()) {
            if (!TextUtils.isEmpty(mLogFilter.getText())) {
                CharSequence filter = mLogFilter.getText();
                if (infoItem.orginalLog.contains(filter)) {
                    mLogItemAdapter.append(infoItem);
                }
            } else {
                mLogItemAdapter.append(infoItem);
            }
            if (mLogItemAdapter.getItemCount() == MAX_LOG_LINE_NUM) {
                mLogItemAdapter.remove(0);
            }
        }
    }

    private int getSelectLogLevel() {
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.verbose) {
            return Log.VERBOSE;
        } else if (checkedId == R.id.debug) {
            return Log.DEBUG;
        } else if (checkedId == R.id.info) {
            return Log.INFO;
        } else if (checkedId == R.id.warn) {
            return Log.WARN;
        } else if (checkedId == R.id.error) {
            return Log.ERROR;
        } else {
            return Log.VERBOSE;
        }
    }

    private void hidePage() {
        final WindowManager.LayoutParams layoutParams = getLayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        mLogHint.setVisibility(View.VISIBLE);
        mLogPage.setVisibility(View.GONE);
        mWindowManager.updateViewLayout(getRootView(), layoutParams);
    }

    private void showPage() {
        final WindowManager.LayoutParams layoutParams = getLayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        mLogHint.setVisibility(View.GONE);
        mLogPage.setVisibility(View.VISIBLE);
        mWindowManager.updateViewLayout(getRootView(), layoutParams);
    }

    @Override
    protected boolean onBackPressed() {
        hidePage();
        return true;
    }

    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
        getRootView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
        getRootView().setVisibility(View.GONE);
    }
}