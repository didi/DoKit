package com.didichuxing.doraemonkit.kit.loginfo

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.util.FileUtil.systemShare
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import com.didichuxing.doraemonkit.widget.dialog.UniversalDialogFragment
import com.didichuxing.doraemonkit.widget.titlebar.LogTitleBar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author lostjobs created on 2020/6/28
 */
open class LogInfoDokitView : AbsDokitView(), LogInfoManager.OnLogCatchListener {

    companion object {
        private const val SAVE = 100
        private const val SHARE = 101

        private const val MAX_LOG_LINE_NUM = 10000
        private const val UPDATE_CHECK_INTERVAL = 200
    }

    /**
     * 是否最大化
     */
    private var isMaximize: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                maximize()
            } else {
                minimize()
            }
        }

    private lateinit var mLogRv: RecyclerView
    private lateinit var mLogItemAdapter: LogItemAdapter
    private lateinit var mLogFilter: EditText
    private lateinit var mRatioGroup: RadioGroup
    private lateinit var mLogHint: TextView
    private lateinit var mLogRvWrap: RelativeLayout

    private var counter: Int = 0
    private var mAutoScrollToBottom: Boolean = true

    private var isLoaded: Boolean = false

    override fun onCreate(context: Context) {
        LogInfoManager.registerListener(this)
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_log_info, null)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        initView()
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params?.width = DokitViewLayoutParams.MATCH_PARENT
        params?.height = DokitViewLayoutParams.MATCH_PARENT
    }

    private fun initView() {
        mLogHint = findViewById(R.id.log_hint)
        mLogRvWrap = findViewById(R.id.log_page)
        mLogRv = findViewById(R.id.log_list)
        mLogRv.layoutManager = LinearLayoutManager(context)
        mLogItemAdapter = LogItemAdapter(context!!)
        mLogRv.adapter = mLogItemAdapter
        mLogFilter = findViewById(R.id.log_filter)
        mLogFilter.addTextChangedListener(afterTextChanged = {
            mLogItemAdapter.filter.filter(it)
        })
        val titleBar = findViewById<LogTitleBar>(R.id.dokit_title_bar)
        titleBar.setListener(object : LogTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                LogInfoManager.stop()
                LogInfoManager.removeListener()
                detach()
            }

            override fun onLeftClick() {
                isMaximize = false
            }
        })

        mLogHint.setOnClickListener {
            isMaximize = true
        }
        mRatioGroup = findViewById(R.id.radio_group)
        mRatioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.verbose -> {
                    mLogItemAdapter.logLevelLimit = Log.VERBOSE
                }
                R.id.debug -> {
                    mLogItemAdapter.logLevelLimit = Log.DEBUG
                }
                R.id.info -> {
                    mLogItemAdapter.logLevelLimit = Log.INFO
                }
                R.id.warn -> {
                    mLogItemAdapter.logLevelLimit = Log.WARN
                }
                R.id.error -> {
                    mLogItemAdapter.logLevelLimit = Log.ERROR
                }
            }
            mLogItemAdapter.filter.filter(mLogFilter.text)
        }
        mLogRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                // if the bottom of the list isn't visible anymore, then stop autoscrolling
                mAutoScrollToBottom = layoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.adapter!!.itemCount - 1
            }
        })
        mRatioGroup.check(R.id.verbose)
        val mBtnTop = findViewById<Button>(R.id.btn_top)
        val mBtnBottom = findViewById<Button>(R.id.btn_bottom)
        val mBtnClean = findViewById<Button>(R.id.btn_clean)
        val mBtnExport = findViewById<Button>(R.id.btn_export)
        mBtnTop.setOnClickListener {
            if (mLogItemAdapter.itemCount > 0) {
                scrollToTop()
            }
        }
        mBtnBottom.setOnClickListener {
            if (mLogItemAdapter.itemCount > 0) {
                scrollToBottom()
            }
        }
        mBtnExport.setOnClickListener {
            if (mLogItemAdapter.itemCount > 0) {
                val exportDialog = LogExportDialog().also {
                    it.onButtonClickListener = object : LogExportDialog.OnButtonClickListener {
                        override fun onSaveClick(dialog: LogExportDialog) {
                            export2File(SAVE)
                            dialog.dismiss()
                        }

                        override fun onShareClick(dialog: LogExportDialog) {
                            export2File(SHARE)
                            dialog.dismiss()
                        }
                    }
                }
                showDialog(exportDialog)
            } else {
                ToastUtils.showShort("暂无日志信息可以导出")
            }
        }
        mBtnClean.setOnClickListener {
            if (mLogItemAdapter.itemCount > 0) {
                counter = 0
                mLogItemAdapter.clearLog()
            }
        }
    }

    private fun showDialog(dialogProvider: DialogProvider<*>) {
        val activity = attachActivity?.get()
        if (activity != null && activity is FragmentActivity) {
            val dialog = UniversalDialogFragment(dialogProvider)
            dialogProvider.host = dialog
            dialogProvider.show(activity.supportFragmentManager)
        }
    }

    private fun export2File(operation: Int) {
        ToastUtils.showShort("日志保存中,请稍后...")
        val logPath = PathUtils.getInternalAppFilesPath() + File.separator + AppUtils.getAppName() + "_" + TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA)) + ".log"
        val logFile = File(logPath)
        ThreadUtils.executeByIo(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                return try {
                    val logLines: List<LogLine> = ArrayList(mLogItemAdapter.getTrueValues())
                    for (logLine in logLines) {
                        val strLog = """${logLine.processId}      ${logLine.timestamp}   ${logLine.tag}   ${logLine.logLevelText}   ${logLine.logOutput}
        """
                        FileIOUtils.writeFileFromString(logFile, strLog, true)
                    }
                    true
                } catch (e: Throwable) {
                    false
                }
            }

            override fun onSuccess(result: Boolean) {
                if (result) {
                    ToastUtils.showShort("文件保存在:$logPath")
                    if (operation == SHARE) {
                        DoraemonKit.APPLICATION?.run {
                            systemShare(this, logFile)
                        }
                    }
                }
            }

            override fun onFail(t: Throwable?) {
                if (logFile.exists()) {
                    FileUtils.delete(logFile)
                }
                ToastUtils.showShort("日志保存失败")
            }

            override fun onCancel() {
                if (logFile.exists()) {
                    FileUtils.delete(logFile)
                }
                ToastUtils.showShort("日志保存失败")
            }

        })
    }


    override fun onLogCatch(logLines: List<LogLine>) {
        if (!::mLogRv.isInitialized) return
        if (!isLoaded) {
            isLoaded = true
            findViewById<LinearLayout>(R.id.ll_loading).visibility = View.GONE
            mLogRv.visibility = View.VISIBLE
        }

        if (logLines.size == 1) {
            mLogItemAdapter.addWithFilter(logLines[0], mLogFilter.text, true)
        }else {
            for (line in logLines) {
                mLogItemAdapter.addWithFilter(line, mLogFilter.text, false)
            }
            mLogItemAdapter.notifyDataSetChanged()
        }
        if (logLines.isNotEmpty()) {
            val lastLine = logLines.last()
            mLogHint.text = "${lastLine.tag}:${lastLine.logOutput}"
        }

        if (++counter % UPDATE_CHECK_INTERVAL == 0
                && mLogItemAdapter.getTrueValues().size > MAX_LOG_LINE_NUM) {
            val numItemsToRemove = mLogItemAdapter.getTrueValues().size - MAX_LOG_LINE_NUM
            mLogItemAdapter.removeFirst(numItemsToRemove)
        }

        if(mAutoScrollToBottom) {
            scrollToBottom()
        }
    }

    private fun scrollToTop() {
        mLogRv.scrollToPosition(0)
    }

    private fun scrollToBottom() {
        mLogRv.scrollToPosition(mLogItemAdapter.itemCount - 1)
    }



    private fun changeSize(isMaximize: Boolean) {
        if (isNormalMode) {
            mLogHint.visibility = if (isMaximize) View.GONE else View.VISIBLE
            mLogRvWrap.visibility = if (isMaximize) View.VISIBLE else View.GONE
            val layoutParams: FrameLayout.LayoutParams = normalLayoutParams
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = if (isMaximize) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.gravity = Gravity.START or Gravity.TOP
            rootView.layoutParams = layoutParams
        } else {
            mLogHint.visibility = if (isMaximize) View.GONE else View.VISIBLE
            mLogRvWrap.visibility = if (isMaximize) View.VISIBLE else View.GONE
            val layoutParams: WindowManager.LayoutParams = systemLayoutParams
            layoutParams.flags = if (isMaximize) WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL else WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = if (isMaximize) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.gravity = Gravity.START or Gravity.TOP
            mWindowManager.updateViewLayout(rootView, layoutParams)
        }
    }

    protected fun maximize() {
        changeSize(isMaximize)
    }

    protected fun minimize() {
        changeSize(isMaximize)
    }


    override fun onResume() {
        super.onResume()
        if (!isOnUniversalActivity) {
            isMaximize = false
        }
        LogInfoManager.registerListener(this)
    }

    override fun shouldDealBackKey(): Boolean = true

    override fun canDrag(): Boolean = false
}