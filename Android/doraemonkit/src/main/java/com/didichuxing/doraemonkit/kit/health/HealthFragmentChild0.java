package com.didichuxing.doraemonkit.kit.health;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.GlobalConfig;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.dialog.DialogListener;
import com.didichuxing.doraemonkit.ui.dialog.DialogProvider;
import com.didichuxing.doraemonkit.ui.dialog.UniversalDialogFragment;
import com.didichuxing.doraemonkit.util.DokitUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

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
        if (DokitConstant.APP_HEALTH_RUNNING) {
            mTitle.setVisibility(View.VISIBLE);
            mController.setImageResource(R.drawable.dk_health_stop);
        } else {
            mTitle.setVisibility(View.INVISIBLE);
            mController.setImageResource(R.drawable.dk_health_start);
        }
        mUserInfoDialogProvider = new UserInfoDialogProvider(null, new DialogListener() {
            @Override
            public boolean onPositive() {
                if (mUserInfoDialogProvider != null) {
                    //上传健康体检数据
                    boolean isCheck = mUserInfoDialogProvider.uploadAppHealthInfo(new UploadAppHealthCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            LogHelper.i(TAG, "上传成功===>" + response.body());
                            ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_upload_successed));
                            //重置状态
                            GlobalConfig.setAppHealth(DoraemonKit.APPLICATION, false);
                            DokitConstant.APP_HEALTH_RUNNING = false;
                            mTitle.setVisibility(View.INVISIBLE);
                            mController.setImageResource(R.drawable.dk_health_start);
                            //关闭健康体检监控
                            AppHealthInfoUtil.getInstance().stop();
                            AppHealthInfoUtil.getInstance().release();
                        }

                        @Override
                        public void onError(Response<String> response) {
                            LogHelper.e(TAG, "error response===>" + response.body());
                            ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_upload_failed));
                        }
                    });

                    return isCheck;
                }

                return true;


            }

            @Override
            public boolean onNegative() {
                return true;
            }

            @Override
            public void onCancel() {
                ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_upload_droped));
                //重置状态
                GlobalConfig.setAppHealth(DoraemonKit.APPLICATION, false);
                DokitConstant.APP_HEALTH_RUNNING = false;
                mTitle.setVisibility(View.INVISIBLE);
                mController.setImageResource(R.drawable.dk_health_start);
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
                if (DokitConstant.APP_HEALTH_RUNNING) {
                    if (mUserInfoDialogProvider != null) {
                        showDialog(mUserInfoDialogProvider);
                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(DokitUtil.getString(R.string.dk_health_upload_title))
                            .setMessage(DokitUtil.getString(R.string.dk_health_upload_message))
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (mController != null) {
                                        ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_funcation_start));
                                        GlobalConfig.setAppHealth(DoraemonKit.APPLICATION, true);
                                        DokitConstant.APP_HEALTH_RUNNING = true;
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
