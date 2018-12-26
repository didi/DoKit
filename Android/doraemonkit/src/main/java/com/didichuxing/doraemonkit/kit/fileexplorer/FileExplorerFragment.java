package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.fileexplorer.FileInfo;
import com.didichuxing.doraemonkit.ui.fileexplorer.FileInfoAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangweida on 2018/6/26.
 */

public class FileExplorerFragment extends BaseFragment {
    private static final String TAG = "FileExplorerFragment";
    private FileInfoAdapter mFileInfoAdapter;
    private RecyclerView mFileList;
    private TitleBar mTitleBar;
    private File mCurDir;

    private void initFileInfoList() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
        mFileList = findViewById(R.id.file_list);
        mFileList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFileInfoAdapter = new FileInfoAdapter(getContext());
        mFileInfoAdapter.setOnViewClickListener(new FileInfoAdapter.OnViewClickListener() {
            @Override
            public void onViewClick(View v, FileInfo fileInfo) {
                if (fileInfo.file.isFile()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BundleKey.FILE_KEY, fileInfo.file);
                    if (FileUtil.isImage(fileInfo.file)) {
                        showContent(ImageDetailFragment.class, bundle);
                    } else {
                        showContent(TextDetailFragment.class, bundle);
                    }
                } else {
                    mCurDir = fileInfo.file;
                    mTitleBar.setTitle(mCurDir.getName());
                    setAdapterData(getFileInfos(mCurDir));
                }
            }
        });
        mFileInfoAdapter.setOnViewLongClickListener(new FileInfoAdapter.OnViewLongClickListener() {
            @Override
            public boolean onViewLongClick(View v, FileInfo fileInfo) {
                if (fileInfo.file.isFile()) {
                    FileUtil.systemShare(getContext(), fileInfo.file);
                    return true;
                } else {
                    return false;
                }
            }
        });
        setAdapterData(initRootFileInfos(getContext()));
        mFileList.setAdapter(mFileInfoAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurDir = null;
        initFileInfoList();
    }

    private List<FileInfo> getFileInfos(File dir) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : dir.listFiles()) {
            FileInfo fileInfo = new FileInfo(file);
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_file_explorer;
    }

    @Override
    protected boolean onBackPressed() {
        if (mCurDir == null) {
            getActivity().finish();
            return true;
        } if (isRootFile(getContext(), mCurDir)) {
            mTitleBar.setTitle(R.string.dk_kit_file_explorer);
            setAdapterData(initRootFileInfos(getContext()));
            mCurDir = null;
            return true;
        } else {
            mCurDir = mCurDir.getParentFile();
            mTitleBar.setTitle(mCurDir.getName());
            List<FileInfo> fileInfos = getFileInfos(mCurDir);
            setAdapterData(fileInfos);
            return true;
        }
    }

    private void setAdapterData(List<FileInfo> fileInfos) {
        if (fileInfos.isEmpty()) {
            mFileInfoAdapter.clear();
        } else {
            mFileInfoAdapter.setData(fileInfos);
        }
    }

    private List<FileInfo> initRootFileInfos(Context context) {
        List<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(new FileInfo(context.getFilesDir().getParentFile()));
        fileInfos.add(new FileInfo(context.getExternalCacheDir()));
        fileInfos.add(new FileInfo(context.getExternalFilesDir(null)));
        return fileInfos;
    }

    private boolean isRootFile(Context context, File file) {
        if (file == null) {
            return false;
        }
        return file.equals(context.getExternalCacheDir())
                || file.equals(context.getExternalFilesDir(null))
                || file.equals(context.getFilesDir().getParentFile());
    }
}