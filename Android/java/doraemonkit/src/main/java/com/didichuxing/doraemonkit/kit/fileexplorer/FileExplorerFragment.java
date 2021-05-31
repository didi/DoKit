package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.DoKitFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
                    if (DoKitFileUtil.isImage(fileInfo.file)) {
                        showContent(ImageDetailFragment.class, bundle);
                    } else if (DoKitFileUtil.isDB(fileInfo.file)) {
                        showContent(DatabaseDetailFragment.class, bundle);
                    } else if (DoKitFileUtil.isVideo(fileInfo.file)) {
                        showContent(VideoPlayFragment.class, bundle);
                    } else if (DoKitFileUtil.isSp(fileInfo.file)) {
                        showContent(SpFragment.class, bundle);
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
            public boolean onViewLongClick(View v, final FileInfo fileInfo) {

                FileExplorerChooseDialog dialog = new FileExplorerChooseDialog(fileInfo.file, null);
                dialog.setOnButtonClickListener(new FileExplorerChooseDialog.OnButtonClickListener() {
                    @Override
                    public void onDeleteClick(FileExplorerChooseDialog dialog) {
                        DoKitFileUtil.deleteDirectory(fileInfo.file);
                        dialog.dismiss();

                        if (mCurDir != null) {
                            mTitleBar.setTitle(mCurDir.getName());
                            setAdapterData(getFileInfos(mCurDir));
                        }

                    }

                    @Override
                    public void onShareClick(FileExplorerChooseDialog dialog) {
                        DoKitFileUtil.systemShare(getContext(), fileInfo.file);
                        dialog.dismiss();

                    }
                });
                showDialog(dialog);
                return true;
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
        if (dir.listFiles() == null) {
            return fileInfos;
        }
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
    public boolean onBackPressed() {
        if (mCurDir == null) {
            finish();
            return true;
        }
        if (isRootFile(getContext(), mCurDir)) {
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
        List<File> rootFiles = getRootFiles();
        if (rootFiles != null) {
            List<FileInfo> fileInfos = new ArrayList<>();
            for (File file : rootFiles) {
                fileInfos.add(new FileInfo(file));
            }
            return fileInfos;
        }
        return initDefaultRootFileInfos(context);
    }

    private List<File> getRootFiles() {
        if (getArguments() != null) {
            File dir = (File) getArguments().getSerializable(BundleKey.DIR_KEY);
            if (dir != null && dir.exists()) {
                return Arrays.asList(dir.listFiles());
            }
        }
        return null;
    }

    private List<FileInfo> initDefaultRootFileInfos(Context context) {
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
        List<File> rootFiles = getRootFiles();
        if (rootFiles != null) {
            for (File rootFile : rootFiles) {
                return file.equals(rootFile);
            }
        }
        return file.equals(context.getExternalCacheDir())
                || file.equals(context.getExternalFilesDir(null))
                || file.equals(context.getFilesDir().getParentFile());
    }
}