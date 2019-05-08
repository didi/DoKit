package com.didichuxing.doraemonkit.kit.logInfo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.List;

/**
 * Created by wanglikun on 2018/10/9.
 */

public class LogInfoFloatPage extends BaseFloatPage implements LogInfoManager.OnLogCatchListener {
    private static final String TAG = "LogInfoFloatPage";

    private static final int MAX_LOG_LINE_NUM = 10000;

    private RecyclerView mLogList;
    private LogItemAdapter mLogItemAdapter;
    private EditText mLogFilter;
    private RadioGroup mRadioGroup;
    private TitleBar mTitleBar;

    private WindowManager mWindowManager;
    private TextView mLogHint;
    private View mLogPage;

    private boolean mIsLoaded;

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
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_log_info, null);
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
                mLogItemAdapter.getFilter().filter(s);
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
                    mLogItemAdapter.setLogLevelLimit(Log.VERBOSE);
                } else if (checkedId == R.id.debug) {
                    mLogItemAdapter.setLogLevelLimit(Log.DEBUG);
                } else if (checkedId == R.id.info) {
                    mLogItemAdapter.setLogLevelLimit(Log.INFO);
                } else if (checkedId == R.id.warn) {
                    mLogItemAdapter.setLogLevelLimit(Log.WARN);
                } else if (checkedId == R.id.error) {
                    mLogItemAdapter.setLogLevelLimit(Log.ERROR);
                }
                mLogItemAdapter.getFilter().filter(mLogFilter.getText());
            }
        });
        mLogList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // if the bottom of the list isn't visible anymore, then stop autoscrolling
                mAutoscrollToBottom = (layoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1);
            }
        });

        mRadioGroup.check(R.id.verbose);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
    }

    private int counter = 0;
    private static final int UPDATE_CHECK_INTERVAL = 200;
    private boolean mAutoscrollToBottom = true;

    @Override
    public void onLogCatch(List<LogLine> logLines) {
        if (mLogList == null || mLogItemAdapter == null) {
            return;
        }
        if (!mIsLoaded) {
            mIsLoaded = true;
            findViewById(R.id.ll_loading).setVisibility(View.GONE);
            mLogList.setVisibility(View.VISIBLE);
        }
        if (logLines.size() == 1) {
            mLogItemAdapter.addWithFilter(logLines.get(0), mLogFilter.getText(), true);
        } else {
            for (LogLine line : logLines) {
                mLogItemAdapter.addWithFilter(line, mLogFilter.getText(), false);
            }
            mLogItemAdapter.notifyDataSetChanged();
        }
        if (logLines.size() > 0) {
            LogLine line = logLines.get(logLines.size() - 1);
            mLogHint.setText(line.getTag() + ":" + line.getLogOutput());
        }
        if (++counter % UPDATE_CHECK_INTERVAL == 0
                && mLogItemAdapter.getTrueValues().size() > MAX_LOG_LINE_NUM) {
            int numItemsToRemove = mLogItemAdapter.getTrueValues().size() - MAX_LOG_LINE_NUM;
            mLogItemAdapter.removeFirst(numItemsToRemove);
            LogHelper.d(TAG, "truncating %d lines from log list to avoid out of memory errors:" + numItemsToRemove);
        }
        if (mAutoscrollToBottom) {
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        mLogList.scrollToPosition(mLogItemAdapter.getItemCount() - 1);
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