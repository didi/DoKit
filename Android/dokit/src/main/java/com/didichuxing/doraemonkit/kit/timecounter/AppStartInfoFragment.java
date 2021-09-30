package com.didichuxing.doraemonkit.kit.timecounter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.DoKitEnv;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.aop.method_stack.MethodStackUtil;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.DoKitFileUtil;
import com.didichuxing.doraemonkit.util.FileIOUtils;
import com.didichuxing.doraemonkit.util.FileUtils;
import com.didichuxing.doraemonkit.util.PathUtils;
import com.didichuxing.doraemonkit.util.ThreadUtils;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;

import java.io.File;

/**
 * @desc: Activity跳转耗时检测首页
 */

public class AppStartInfoFragment extends BaseFragment {
    TextView mInfo;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_app_start_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    private void initView() {
        TitleBar titleBar = findViewById(R.id.title_bar);

        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                export2File(mInfo.getText().toString());
            }
        });

        mInfo = findViewById(R.id.app_start_info);
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(MethodStackUtil.STR_APP_ON_CREATE) && TextUtils.isEmpty(MethodStackUtil.STR_APP_ATTACH_BASECONTEXT)) {
            builder.append("只有配置slowMethod的strategy=0模式下才能统计到启动函数调用栈");
        } else {
            if(!TextUtils.isEmpty(MethodStackUtil.STR_APP_ATTACH_BASECONTEXT)){
                builder.append(MethodStackUtil.STR_APP_ATTACH_BASECONTEXT);
                builder.append("\n");
            }
            builder.append(MethodStackUtil.STR_APP_ON_CREATE);
        }

        mInfo.setText(builder.toString());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 将启动信息保存到文件并分享
     */
    private void export2File(final String info) {
        if (TextUtils.isEmpty(info)) {
            ToastUtils.showShort("启动信息为空");
            return;
        }
        ToastUtils.showShort("启动信息保存中,请稍后...");
        final String logPath = PathUtils.getInternalAppFilesPath() + File.separator + AppUtils.getAppName() + "_" + "app_launch.log";
        final File logFile = new File(logPath);

        ThreadUtils.executeByCpu(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                try {

                    FileIOUtils.writeFileFromString(logFile, info, false);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    ToastUtils.showShort("启动信息文件保存在:" + logPath);
                    //分享
                    DoKitFileUtil.systemShare(DoKitEnv.requireApp(), logFile);
                }
            }

            @Override
            public void onCancel() {
                if (logFile.exists()) {
                    FileUtils.delete(logFile);
                }
                ToastUtils.showShort("启动信息保存失败");
            }

            @Override
            public void onFail(Throwable t) {
                if (logFile.exists()) {
                    FileUtils.delete(logFile);
                }
                ToastUtils.showShort("启动信息保存失败");
            }
        });

    }


}
