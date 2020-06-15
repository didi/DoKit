package com.didichuxing.doraemonkit.kit.fileexplorer

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.TitledBaseFragment
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.ref.WeakReference

class TextDetailFragment : TitledBaseFragment() {
    companion object {
        private const val TAG = "TextDetailFragment"
    }

    private var mTask: FileReadTask? = null
    private val mTextContentAdapter by lazy { TextContentAdapter(requireContext()) }

    override fun onRequestLayout(): Int = R.layout.dk_fragment_text_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        startReadFile()
    }

    private fun initViews(view: View) {
        view.findViewById<TitleBar>(R.id.title_bar)
                .onTitleBarClickListener = this
        val contentRV = view.findViewById<RecyclerView>(R.id.text_list)
        with(contentRV) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mTextContentAdapter
        }
    }

    private fun startReadFile() {
        (arguments?.getSerializable(BundleKey.FILE_KEY) as? File)?.run {
            mTask = FileReadTask(this@TextDetailFragment).apply {
                execute(this@run)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mTask?.cancel(true)
    }

    private fun append(line: String) {
        mTextContentAdapter.append(line)
    }

    class FileReadTask(textDetailFragment: TextDetailFragment) : AsyncTask<File, String, Unit>() {
        private val mFragmentRef = WeakReference(textDetailFragment)


        override fun doInBackground(vararg params: File) {
            BufferedReader(FileReader(params[0])).use {
                do {
                    val line = it.readLine()
                    publishProgress(line)
                } while (!isCancelled)
                return@use
            }
        }

        override fun onProgressUpdate(vararg values: String) {
            super.onProgressUpdate(*values)
            mFragmentRef.get()?.append(values[0])
        }

    }

}
