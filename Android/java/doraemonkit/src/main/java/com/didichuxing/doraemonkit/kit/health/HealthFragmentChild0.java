package com.didichuxing.doraemonkit.kit.health;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.GlobalConfig;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.widget.dialog.DialogListener;
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider;
import com.didichuxing.doraemonkit.widget.dialog.UniversalDialogFragment;

/**
 * 健康体检fragment
 */
public class HealthFragmentChild0 extends BaseFragment {
    TextView mTitle;
    ImageView mController;
    UserInfoDialogProvider mUserInfoDialogProvider;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_health_child0;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        mTitle = findViewById(R.id.tv_title);
        mController = findViewById(R.id.iv_btn);
        if (DoKitManager.APP_HEALTH_RUNNING) {
            mTitle.setVisibility(View.VISIBLE);
            mController.setImageResource(R.mipmap.dk_health_stop);
        } else {
            mTitle.setVisibility(View.INVISIBLE);
            mController.setImageResource(R.mipmap.dk_health_start);
        }
        mUserInfoDialogProvider = new UserInfoDialogProvider(null, new DialogListener() {
            @Override
            public boolean onPositive(DialogProvider<?> dialogProvider) {
                if (mUserInfoDialogProvider != null) {
                    //上传健康体检数据
                    boolean isCheck = mUserInfoDialogProvider.uploadAppHealthInfo(new UploadAppHealthCallback() {
                        @Override
                        public void onSuccess(String response) {
                            LogHelper.i(TAG, "上传成功===>" + response);
                            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_health_upload_successed));
                            //重置状态
                            GlobalConfig.setAppHealth(false);
                            DoKitManager.APP_HEALTH_RUNNING = false;
                            mTitle.setVisibility(View.INVISIBLE);
                            mController.setImageResource(R.mipmap.dk_health_start);
                            //关闭健康体检监控
                            AppHealthInfoUtil.getInstance().stop();
                            AppHealthInfoUtil.getInstance().release();
                        }

                        @Override
                        public void onError(String response) {
                            LogHelper.e(TAG, "error response===>" + response);
                            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_health_upload_failed));
                        }
                    });

                    return isCheck;
                }

                return true;


            }

            @Override
            public boolean onNegative(DialogProvider<?> dialogProvider) {
                return true;
            }

            @Override
            public void onCancel(DialogProvider<?> dialogProvider) {
                ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_health_upload_droped));
                //重置状态
                GlobalConfig.setAppHealth(false);
                DoKitManager.APP_HEALTH_RUNNING = false;
                mTitle.setVisibility(View.INVISIBLE);
                mController.setImageResource(R.mipmap.dk_health_start);
                //关闭健康体检监控
                AppHealthInfoUtil.getInstance().stop();
                AppHealthInfoUtil.getInstance().release();
            }
        });
        mController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) {
                    return;
                }
                //当前处于健康体检状态
                if (DoKitManager.APP_HEALTH_RUNNING) {
                    if (mUserInfoDialogProvider != null) {
                        showDialog(mUserInfoDialogProvider);
                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(DoKitCommUtil.getString(R.string.dk_health_upload_title))
                            .setMessage(DoKitCommUtil.getString(R.string.dk_health_upload_message))
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (mController != null) {
                                        ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_health_funcation_start));
                                        GlobalConfig.setAppHealth(true);
                                        DoKitManager.APP_HEALTH_RUNNING = true;
                                        //重启app
                                        mController.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                AppUtils.relaunchApp();
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                                System.exit(1);
                                            }
                                        }, 2000);
                                    }
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }
            }
        });
    }

    @Override
    public void showDialog(DialogProvider provider) {
        UniversalDialogFragment dialog = new UniversalDialogFragment();
        provider.setHost(dialog);
        dialog.setProvider(provider);
        provider.show(getChildFragmentManager());
    }


}
