/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.leakcanary.internal

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.text.format.Formatter
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.squareup.leakcanary.*
import com.squareup.leakcanary.AnalyzedHeap.Companion.load
import com.squareup.leakcanary.LeakCanary.Companion.leakInfo
import com.squareup.leakcanary.internal.DisplayLeakActivity
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.getLeakDirectoryProvider
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.newSingleThreadExecutor
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.setEnabledBlocking
import java.io.FilenameFilter
import java.util.*

class DisplayLeakActivity : AppCompatActivity() {
    // null until it's been first loaded.
    var leaks: List<AnalyzedHeap>? = null
    var visibleLeakRefKey: String? = null
    private var listView: ListView? = null
    private var failureView: TextView? = null
    private var actionButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            visibleLeakRefKey = savedInstanceState.getString("visibleLeakRefKey")
        } else {
            val intent = intent
            if (intent.hasExtra(SHOW_LEAK_EXTRA)) {
                visibleLeakRefKey = intent.getStringExtra(SHOW_LEAK_EXTRA)
            }
        }
        leaks = lastNonConfigurationInstance as List<AnalyzedHeap>?
        setContentView(R.layout.leak_canary_display_leak)
        listView = findViewById(R.id.leak_canary_display_leak_list)
        failureView = findViewById(R.id.leak_canary_display_leak_failure)
        actionButton = findViewById(R.id.leak_canary_action)
        updateUi()
    }


    override fun onRetainCustomNonConfigurationInstance(): Any? {
        return leaks!!
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("visibleLeakRefKey", visibleLeakRefKey)
    }

    override fun onResume() {
        super.onResume()
        LoadLeaks.load(this, getLeakDirectoryProvider(this))
    }

    override fun setTheme(resid: Int) {
        // We don't want this to be called with an incompatible theme.
        // This could happen if you implement runtime switching of themes
        // using ActivityLifecycleCallbacks.
        if (resid != R.style.leak_canary_LeakCanary_Base) {
            return
        }
        super.setTheme(resid)
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadLeaks.forgetActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val visibleLeak = visibleLeak
        if (visibleLeak != null) {
            menu.add(R.string.leak_canary_share_leak)
                    .setOnMenuItemClickListener {
                        shareLeak()
                        true
                    }
            if (visibleLeak.heapDumpFileExists) {
                menu.add(R.string.leak_canary_share_heap_dump)
                        .setOnMenuItemClickListener {
                            shareHeapDump()
                            true
                        }
            }
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            visibleLeakRefKey = null
            updateUi()
        }
        return true
    }

    override fun onBackPressed() {
        if (visibleLeakRefKey != null) {
            visibleLeakRefKey = null
            updateUi()
        } else {
            super.onBackPressed()
        }
    }

    fun shareLeak() {
        val visibleLeak = visibleLeak
        val leakInfo = leakInfo(this, visibleLeak!!.heapDump, visibleLeak.result, true)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, leakInfo)
        startActivity(Intent.createChooser(intent, getString(R.string.leak_canary_share_with)))
    }

    @SuppressLint("SetWorldReadable")
    fun shareHeapDump() {
        val visibleLeak = visibleLeak
        val heapDumpFile = visibleLeak!!.heapDump.heapDumpFile
        AsyncTask.SERIAL_EXECUTOR.execute {
            heapDumpFile.setReadable(true, false)
            val heapDumpUri = FileProvider.getUriForFile(baseContext,
                    "com.squareup.leakcanary.fileprovider." + application.packageName,
                    heapDumpFile)
            runOnUiThread { startShareIntentChooser(heapDumpUri) }
        }
    }

    private fun startShareIntentChooser(heapDumpUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/octet-stream"
        intent.putExtra(Intent.EXTRA_STREAM, heapDumpUri)
        startActivity(Intent.createChooser(intent, getString(R.string.leak_canary_share_with)))
    }

    fun deleteVisibleLeak() {
        val visibleLeak = visibleLeak
        AsyncTask.SERIAL_EXECUTOR.execute {
            val heapDumpFile = visibleLeak!!.heapDump.heapDumpFile
            val resultFile = visibleLeak.selfFile
            val resultDeleted = resultFile.delete()
            if (!resultDeleted) {
                CanaryLog.d("Could not delete result file %s", resultFile.path)
            }
            val heapDumpDeleted = heapDumpFile.delete()
            if (!heapDumpDeleted) {
                CanaryLog.d("Could not delete heap dump file %s", heapDumpFile.path)
            }
        }
        visibleLeakRefKey = null
        (leaks as MutableList).remove(visibleLeak)
        updateUi()
    }

    fun deleteAllLeaks() {
        val leakDirectoryProvider = getLeakDirectoryProvider(this)
        AsyncTask.SERIAL_EXECUTOR.execute { leakDirectoryProvider.clearLeakDirectory() }
        leaks = emptyList()
        updateUi()
    }

    fun updateUi() {
        if (leaks == null) {
            title = "Loading leaks..."
            return
        }
        if (leaks!!.isEmpty()) {
            visibleLeakRefKey = null
        }
        val visibleLeak = visibleLeak
        if (visibleLeak == null) {
            visibleLeakRefKey = null
        }
        val listAdapter = listView!!.adapter
        // Reset to defaults
        listView!!.visibility = View.VISIBLE
        failureView!!.visibility = View.GONE
        if (visibleLeak != null) {
            val result = visibleLeak.result
            actionButton!!.visibility = View.VISIBLE
            actionButton!!.setText(R.string.leak_canary_delete)
            actionButton!!.setOnClickListener { deleteVisibleLeak() }
            invalidateOptionsMenu()
            setDisplayHomeAsUpEnabled(true)
            if (result.leakFound) {
                val adapter = DisplayLeakAdapter(resources)
                listView!!.adapter = adapter
                listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> adapter.toggleRow(position) }
                val heapDump = visibleLeak.heapDump
                adapter.update(result.leakTrace!!, heapDump.referenceKey, heapDump.referenceName)
                title = if (result.retainedHeapSize == AnalysisResult.RETAINED_HEAP_SKIPPED) {
                    val className = classSimpleName(result.className)
                    getString(R.string.leak_canary_class_has_leaked, className)
                } else {
                    val size = Formatter.formatShortFileSize(this, result.retainedHeapSize)
                    val className = classSimpleName(result.className)
                    getString(R.string.leak_canary_class_has_leaked_retaining, className, size)
                }
            } else {
                listView!!.visibility = View.GONE
                failureView!!.visibility = View.VISIBLE
                listView!!.adapter = null
                var failureMessage: String
                if (result.failure != null) {
                    setTitle(R.string.leak_canary_analysis_failed)
                    failureMessage = """${getString(R.string.leak_canary_failure_report)}${BuildConfig.LEAKCANARY_LIBRARY_VERSION} ${BuildConfig.GIT_SHA}
${Log.getStackTraceString(result.failure)}"""
                } else {
                    val className = classSimpleName(result.className)
                    title = getString(R.string.leak_canary_class_no_leak, className)
                    failureMessage = getString(R.string.leak_canary_no_leak_details)
                }
                val path = visibleLeak.heapDump.heapDumpFile.absolutePath
                failureMessage += """
                    
                    
                    ${getString(R.string.leak_canary_download_dump, path)}
                    """.trimIndent()
                failureView!!.text = failureMessage
            }
        } else {
            if (listAdapter is LeakListAdapter) {
                listAdapter.notifyDataSetChanged()
            } else {
                val adapter = LeakListAdapter()
                listView!!.adapter = adapter
                listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    visibleLeakRefKey = leaks!![position].heapDump.referenceKey
                    updateUi()
                }
                invalidateOptionsMenu()
                title = getString(R.string.leak_canary_leak_list_title, packageName)
                setDisplayHomeAsUpEnabled(false)
                actionButton!!.setText(R.string.leak_canary_delete_all)
                actionButton!!.setOnClickListener {
                    AlertDialog.Builder(this@DisplayLeakActivity).setIcon(
                            android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.leak_canary_delete_all)
                            .setMessage(R.string.leak_canary_delete_all_leaks_title)
                            .setPositiveButton(android.R.string.ok) { dialog, which -> deleteAllLeaks() }
                            .setNegativeButton(android.R.string.cancel, null)
                            .show()
                }
            }
            actionButton!!.visibility = if (leaks!!.size == 0) View.GONE else View.VISIBLE
        }
    }

    private fun setDisplayHomeAsUpEnabled(enabled: Boolean) {
        val actionBar = actionBar
                ?: // https://github.com/square/leakcanary/issues/967
                return
        actionBar.setDisplayHomeAsUpEnabled(enabled)
    }

    val visibleLeak: AnalyzedHeap?
        get() {
            if (leaks == null) {
                return null
            }
            for (leak in leaks!!) {
                if (leak.heapDump.referenceKey == visibleLeakRefKey) {
                    return leak
                }
            }
            return null
        }

    internal inner class LeakListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return leaks!!.size
        }

        override fun getItem(position: Int): AnalyzedHeap {
            return leaks!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = LayoutInflater.from(this@DisplayLeakActivity)
                        .inflate(R.layout.leak_canary_leak_row, parent, false)
            }
            val titleView = convertView.findViewById<TextView>(R.id.leak_canary_row_text)
            val timeView = convertView.findViewById<TextView>(R.id.leak_canary_row_time)
            val leak = getItem(position)
            val index = (leaks!!.size - position).toString() + ". "
            var title: String
            if (leak.result.failure != null) {
                title = (index
                        + leak.result.failure.javaClass.simpleName
                        + " "
                        + leak.result.failure.message)
            } else {
                val className = classSimpleName(leak.result.className)
                if (leak.result.leakFound) {
                    title = if (leak.result.retainedHeapSize == AnalysisResult.RETAINED_HEAP_SKIPPED) {
                        getString(R.string.leak_canary_class_has_leaked, className)
                    } else {
                        val size = Formatter.formatShortFileSize(this@DisplayLeakActivity,
                                leak.result.retainedHeapSize)
                        getString(R.string.leak_canary_class_has_leaked_retaining, className, size)
                    }
                    if (leak.result.excludedLeak) {
                        title = getString(R.string.leak_canary_excluded_row, title)
                    }
                    title = index + title
                } else {
                    title = index + getString(R.string.leak_canary_class_no_leak, className)
                }
            }
            titleView.text = title
            val time = DateUtils.formatDateTime(this@DisplayLeakActivity, leak.selfLastModified,
                    DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE)
            timeView.text = time
            return convertView
        }
    }

    internal class LoadLeaks(var activityOrNull: DisplayLeakActivity?, private val leakDirectoryProvider: LeakDirectoryProvider) : Runnable {
        private val mainHandler: Handler
        override fun run() {
            val leaks: MutableList<AnalyzedHeap> = ArrayList()
            val files = leakDirectoryProvider.listFiles(FilenameFilter { dir, filename -> filename.endsWith(".result") })
            for (resultFile in files) {
                val leak = load(resultFile!!)
                if (leak != null) {
                    leaks.add(leak)
                }
            }
            Collections.sort(leaks) { lhs, rhs ->
                java.lang.Long.valueOf(rhs.selfFile.lastModified())
                        .compareTo(lhs.selfFile.lastModified())
            }
            mainHandler.post {
                inFlight.remove(this@LoadLeaks)
                if (activityOrNull != null) {
                    activityOrNull!!.leaks = leaks
                    activityOrNull!!.updateUi()
                }
            }
        }

        companion object {
            val inFlight: MutableList<LoadLeaks> = ArrayList()
            val backgroundExecutor = newSingleThreadExecutor("LoadLeaks")
            fun load(activity: DisplayLeakActivity?, leakDirectoryProvider: LeakDirectoryProvider) {
                val loadLeaks = LoadLeaks(activity, leakDirectoryProvider)
                inFlight.add(loadLeaks)
                backgroundExecutor.execute(loadLeaks)
            }

            fun forgetActivity() {
                for (loadLeaks in inFlight) {
                    loadLeaks.activityOrNull = null
                }
                inFlight.clear()
            }
        }

        init {
            mainHandler = Handler(Looper.getMainLooper())
        }
    }

    companion object {
        private const val SHOW_LEAK_EXTRA = "show_latest"

        // Public API.
        @JvmOverloads
        fun createPendingIntent(context: Context?, referenceKey: String? = null): PendingIntent {
            setEnabledBlocking(context!!, DisplayLeakActivity::class.java, true)
            val intent = Intent(context, DisplayLeakActivity::class.java)
            intent.putExtra(SHOW_LEAK_EXTRA, referenceKey)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun classSimpleName(className: String?): String? {
            val separator = className!!.lastIndexOf('.')
            return if (separator == -1) {
                className
            } else {
                className.substring(separator + 1)
            }
        }
    }
}