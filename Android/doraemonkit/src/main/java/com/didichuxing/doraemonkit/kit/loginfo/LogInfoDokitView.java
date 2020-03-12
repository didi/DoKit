package com.didichuxing.doraemonkit.kit.loginfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.dialog.DialogProvider;
import com.didichuxing.doraemonkit.ui.dialog.UniversalDialogFragment;
import com.didichuxing.doraemonkit.ui.fileexplorer.FileExplorerChooseDialog;
import com.didichuxing.doraemonkit.ui.loginfo.LogItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */
public class LogInfoDokitView extends AbsDokitView implements LogInfoManager.OnLogCatchListener {
    private static final String TAG = "LogInfoFloatPage";

    private static final int MAX_LOG_LINE_NUM = 10000;

    private RecyclerView mLogRv;
    private LogItemAdapter mLogItemAdapter;
    private EditText mLogFilter;
    private RadioGroup mRadioGroup;
    /**
     * 单行的log
     */
    private TextView mLogHint;
    private RelativeLayout mLogRvWrap;


    private boolean mIsLoaded;

    @Override
    public void onCreate(Context context) {
        LogInfoManager.getInstance().registerListener(this);
    }


    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_log_info, null);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        initView();
    }

    public void initView() {
        mLogHint = findViewById(R.id.log_hint);
        mLogRvWrap = findViewById(R.id.log_page);
        mLogRv = findViewById(R.id.log_list);
        mLogRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mLogItemAdapter = new LogItemAdapter(getContext());
        mLogRv.setAdapter(mLogItemAdapter);
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
        FrameLayout mTitleBar = findViewById(R.id.dokit_title_bar);
        if (mTitleBar instanceof TitleBar) {
            ((TitleBar) mTitleBar).setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
                @Override
                public void onLeftClick() {
                    minimize();
                }

                @Override
                public void onRightClick() {

                }
            });
        }

        mLogHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maximize();
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
        mLogRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // if the bottom of the list isn't visible anymore, then stop autoscrolling
                mAutoScrollToBottom = (layoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1);
            }
        });

        mRadioGroup.check(R.id.verbose);
        Button mBtnTop = findViewById(R.id.btn_top);
        Button mBtnBottom = findViewById(R.id.btn_bottom);
        Button mBtnClean = findViewById(R.id.btn_clean);
        Button mBtnExport = findViewById(R.id.btn_export);
        mBtnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogItemAdapter == null || mLogItemAdapter.getItemCount() == 0) {
                    return;
                }
                mLogRv.scrollToPosition(0);
            }
        });
        mBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogItemAdapter == null || mLogItemAdapter.getItemCount() == 0) {
                    return;
                }
                mLogRv.scrollToPosition(mLogItemAdapter.getItemCount() - 1);
            }
        });

        mBtnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogItemAdapter == null || mLogItemAdapter.getItemCount() == 0) {
                    ToastUtils.showShort("暂无日志信息可以导出");
                    return;
                }

                LogExportDialog logExportDialog = new LogExportDialog(new Object(), null);
                logExportDialog.setOnButtonClickListener(new LogExportDialog.OnButtonClickListener() {
                    @Override
                    public void onSaveClick(LogExportDialog dialog) {
                        export2File(100);
                        dialog.dismiss();

                    }

                    @Override
                    public void onShareClick(LogExportDialog dialog) {
                        export2File(101);
                        dialog.dismiss();

                    }
                });
                showDialog(logExportDialog);

            }
        });

        mBtnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogItemAdapter == null || mLogItemAdapter.getItemCount() == 0) {
                    return;
                }
                counter = 0;
                mLogItemAdapter.clearLog();
            }
        });
    }


    private void showDialog(DialogProvider provider) {
        if (getActivity() == null || !(getActivity() instanceof FragmentActivity)) {
            return;
        }
        UniversalDialogFragment dialog = new UniversalDialogFragment();
        provider.setHost(dialog);
        dialog.setProvider(provider);
        provider.show(((FragmentActivity) getActivity()).getSupportFragmentManager());
    }

    /**
     * 将日志信息保存到文件
     *
     * @param operateType 100 保存到本地  101 保存到本地并分享
     */
    private void export2File(final int operateType) {
        ToastUtils.showShort("日志保存中,请稍后...");
        final String logPath = PathUtils.getInternalAppFilesPath() + File.separator + AppUtils.getAppName() + "_" + TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")) + ".log";
        final File logFile = new File(logPath);

        ThreadUtils.executeByCpu(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                try {
                    List<LogLine> logLines = new ArrayList<>(mLogItemAdapter.getTrueValues());
                    for (LogLine logLine : logLines) {
                        String strLog = logLine.getProcessId() + "   " + "   " + logLine.getTimestamp() + "   " + logLine.getTag() + "   " + logLine.getLogLevelText() + "   " + logLine.getLogOutput() + "\n";
                        FileIOUtils.writeFileFromString(logFile, strLog, true);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    ToastUtils.showShort("文件保存在:" + logPath);
                    //分享
                    if (operateType == 101) {
                        FileUtil.systemShare(DoraemonKit.APPLICATION, logFile);
                    }
                }
            }

            @Override
            public void onCancel() {
                if (logFile.exists()) {
                    FileUtils.delete(logFile);
                }
                ToastUtils.showShort("日志保存失败");
            }

            @Override
            public void onFail(Throwable t) {
                if (logFile.exists()) {
                    FileUtils.delete(logFile);
                }
                ToastUtils.showShort("日志保存失败");
            }
        });

    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.width = DokitViewLayoutParams.MATCH_PARENT;
        params.height = DokitViewLayoutParams.MATCH_PARENT;
    }

    private int counter = 0;
    private static final int UPDATE_CHECK_INTERVAL = 200;
    private boolean mAutoScrollToBottom = true;

    @Override
    public void onLogCatch(List<LogLine> logLines) {
        if (mLogRv == null || mLogItemAdapter == null) {
            return;
        }
        if (!mIsLoaded) {
            mIsLoaded = true;
            findViewById(R.id.ll_loading).setVisibility(View.GONE);
            mLogRv.setVisibility(View.VISIBLE);
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
            //LogHelper.d(TAG, "truncating %d lines from log list to avoid out of memory errors:" + numItemsToRemove);
        }
        if (mAutoScrollToBottom) {
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        mLogRv.scrollToPosition(mLogItemAdapter.getItemCount() - 1);
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

    /**
     * 最小化
     */
    private void minimize() {
        isMaximize = false;
        if (isNormalMode()) {
            mLogHint.setVisibility(View.VISIBLE);
            mLogRvWrap.setVisibility(View.GONE);
            FrameLayout.LayoutParams layoutParams = getNormalLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            getRootView().setLayoutParams(layoutParams);
        } else {
            mLogHint.setVisibility(View.VISIBLE);
            mLogRvWrap.setVisibility(View.GONE);

            WindowManager.LayoutParams layoutParams = getSystemLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            mWindowManager.updateViewLayout(getRootView(), layoutParams);
        }

    }

    /**
     * 是否最大化
     */
    private boolean isMaximize = true;

    private void maximize() {
        isMaximize = true;
        if (isNormalMode()) {
            mLogHint.setVisibility(View.GONE);
            mLogRvWrap.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams layoutParams = getNormalLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            getRootView().setLayoutParams(layoutParams);
        } else {
            mLogHint.setVisibility(View.GONE);
            mLogRvWrap.setVisibility(View.VISIBLE);
            WindowManager.LayoutParams layoutParams = getSystemLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            mWindowManager.updateViewLayout(getRootView(), layoutParams);
        }

    }

    @Override
    public boolean onBackPressed() {
        if (isMaximize) {
            minimize();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && !getActivity().getClass().getSimpleName().equals(UniversalActivity.class.getSimpleName())) {
            minimize();
        }
        LogInfoManager.getInstance().registerListener(this);
    }

    @Override
    public boolean shouldDealBackKey() {
        return true;
    }

    @Override
    public boolean canDrag() {
        return false;
    }
}