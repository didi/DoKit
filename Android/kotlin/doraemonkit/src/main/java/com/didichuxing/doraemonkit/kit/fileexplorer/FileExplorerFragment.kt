package com.didichuxing.doraemonkit.kit.fileexplorer

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.dbdebug.DbDebugFragment
import com.didichuxing.doraemonkit.util.FileUtil
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.io.File

/**
 * @author lostjobs created on 2020/6/14
 */
class FileExplorerFragment : BaseFragment(), TitleBar.OnTitleBarClickListener {

    companion object {
        private const val TAG = "FileExplorerFragment"
    }

    private lateinit var mTitleBar: TitleBar
    private lateinit var mFileRv: RecyclerView
    private val mFileInfoAdapter by lazy { FileInfoAdapter(requireContext()) }
    private var mCurDir: File? = null

    override fun onRequestLayout(): Int = R.layout.dk_fragment_file_explorer

    override fun onLeftClick() {
        onBackPressed()
    }

    override fun onRightClick() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTitleBar = findViewById(R.id.title_bar)
        mTitleBar.onTitleBarClickListener = this
        mFileRv = findViewById(R.id.file_list)
        with(mFileRv) {
            layoutManager = LinearLayoutManager(requireContext())
            setAdapterInfo(initRootFileInfoList(requireContext()))
            adapter = mFileInfoAdapter
            mFileInfoAdapter.doOnViewClick { _, fileInfo ->
                if (fileInfo.isFile) {
                    val bundle = createBundle(fileInfo)
                    when {
                        fileInfo.isImage -> {
                            showContent<ImageDetailFragment>(bundle)
                        }
                        fileInfo.isDB -> {
                            showContent<DbDebugFragment>()
                        }
                        fileInfo.isVideo -> {
                            showContent<VideoPlayFragment>(bundle)
                        }
                        fileInfo.isSp -> {
                            showContent<SpFragment>(bundle)
                        }
                        else -> {
                            showContent<TextDetailFragment>(bundle)
                        }
                    }
                } else {
                    mCurDir = fileInfo.file
                    mTitleBar.setTitle(mCurDir?.name)
                    setAdapterInfo(getFileInfoList(mCurDir))
                }
            }
            mFileInfoAdapter.doOnViewLongClick { _, fileInfo ->
                val fileExplorerDialog = FileExplorerChooseDialog(fileInfo)
                        .setOnDeleteClick { dialog, data ->
                            FileUtil.deleteDirectory(data.file)
                            dialog.dismiss()
                            mCurDir?.run {
                                mTitleBar.setTitle(this.name)
                                setAdapterInfo(getFileInfoList(this))
                            }
                        }
                        .setOnShareClick { dialog, data ->
                            FileUtil.systemShare(requireContext(), data.file)
                            dialog.dismiss()
                        }
                showDialog(fileExplorerDialog)
            }
        }
    }

    private fun createBundle(fileInfo: FileInfo): Bundle {
        return Bundle().apply {
            putSerializable(BundleKey.FILE_KEY, fileInfo.file)
        }
    }

    override fun onBackPressed(): Boolean {
        val currentDir = mCurDir
        if (null == currentDir) {
            finish()
            return true
        }

        return if (isRootFile(requireContext(), currentDir)) {
            mTitleBar.setTitle(R.string.dk_kit_file_explorer)
            setAdapterInfo(initRootFileInfoList(requireContext()))
            mCurDir = null
            true
        } else {
            mCurDir = currentDir.parentFile
            mTitleBar.setTitle(mCurDir?.name)
            val fileInfoList = getFileInfoList(mCurDir)
            setAdapterInfo(fileInfoList = fileInfoList)
            true
        }
    }

    private fun getFileInfoList(dir: File?): List<FileInfo> {
        return dir?.listFiles()?.map { FileInfo(it) }?.toList() ?: listOf()
    }

    private fun getRootFiles(): List<File>? {
        val rootFile = arguments?.getSerializable(BundleKey.DIR_KEY) as? File ?: return null
        if (rootFile.exists()) {
            return rootFile.listFiles()?.toList()
        }
        return null
    }

    private fun initRootFileInfoList(context: Context): List<FileInfo> {
        return getRootFiles()?.map { FileInfo(it) } ?: initDefaultRootFileList(context)
    }

    private fun initDefaultRootFileList(context: Context): List<FileInfo> {
        val initRootFileList = mutableListOf<FileInfo>()
        context.filesDir.parentFile?.run {
            initRootFileList.add(FileInfo(this))
        }
        context.externalCacheDir?.run {
            initRootFileList.add(FileInfo(this))
        }
        context.getExternalFilesDir(null)?.run {
            initRootFileList.add(FileInfo(this))
        }
        return initRootFileList
    }

    private fun setAdapterInfo(fileInfoList: List<FileInfo>) {
        if (fileInfoList.isEmpty()) {
            mFileInfoAdapter.clear()
        } else {
            mFileInfoAdapter.data = fileInfoList
        }
    }

    private fun isRootFile(context: Context, currentDir: File): Boolean {
        val rootFiles = getRootFiles()
        if (rootFiles?.firstOrNull { currentDir == it } != null) return true
        return initRootFileInfoList(context).firstOrNull { currentDir == it.file } != null
    }
}