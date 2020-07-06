package com.didichuxing.doraemonkit.kit.health

import android.os.Bundle
import android.os.Process
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.GlobalConfig.appHealth
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.okgo.model.Response
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.util.LogHelper.e
import com.didichuxing.doraemonkit.util.LogHelper.i
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider
import com.didichuxing.doraemonkit.widget.dialog.UniversalDialogFragment
import kotlin.system.exitProcess

/**
 * 健康体检 第一页
 * @author pengyushan
 */
class HealthFragmentFirst : BaseFragment() {
    var mTitle: TextView? = null
    var mController: ImageView? = null
    var mUserInfoDialogProvider: UserInfoDialogProvider? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_health_child_one
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity == null) {
            return
        }
        mTitle = findViewById(R.id.tv_title)
        mController = findViewById(R.id.iv_btn)
        if (DokitConstant.APP_HEALTH_RUNNING) {
            mTitle!!.visibility = View.VISIBLE
            mController!!.setImageResource(R.mipmap.dk_health_stop)
        } else {
            mTitle!!.visibility = View.INVISIBLE
            mController!!.setImageResource(R.mipmap.dk_health_start)
        }
        mUserInfoDialogProvider = UserInfoDialogProvider(null, object : DialogListener {
            override fun onPositive(): Boolean {
                return if (mUserInfoDialogProvider != null) {
                    //上传健康体检数据
                    mUserInfoDialogProvider!!.uploadAppHealthInfo(object : UploadAppHealthCallback {
                        override fun onSuccess(response: Response<String>?) {
                            i(TAG, "上传成功===>" + response!!.body())
                            ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_upload_successed))
                            //重置状态
                            appHealth = false
                            DokitConstant.APP_HEALTH_RUNNING = false
                            mTitle!!.visibility = View.INVISIBLE
                            mController!!.setImageResource(R.mipmap.dk_health_start)
                            //关闭健康体检监控
                            AppHealthInfoUtil.instance.stop()
                            AppHealthInfoUtil.instance.release()
                        }

                        override fun onError(response: Response<String>?) {
                            e(TAG, "error response===>" + response!!.message())
                            ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_upload_failed))
                        }
                    })
                } else true
            }

            override fun onNegative(): Boolean {
                return true
            }

            override fun onCancel(): Boolean {
                ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_upload_droped))
                //重置状态
                appHealth = false
                DokitConstant.APP_HEALTH_RUNNING = false
                mTitle!!.visibility = View.INVISIBLE
                mController!!.setImageResource(R.mipmap.dk_health_start)
                //关闭健康体检监控
                AppHealthInfoUtil.instance.stop()
                AppHealthInfoUtil.instance.release()
                return true
            }
        })
        mController!!.setOnClickListener(View.OnClickListener {
            if (activity == null) {
                return@OnClickListener
            }
            //当前处于健康体检状态
            if (DokitConstant.APP_HEALTH_RUNNING) {
                if (mUserInfoDialogProvider != null) {
                    showDialog(mUserInfoDialogProvider!!)
                }
            } else {
                AlertDialog.Builder(activity!!)
                        .setTitle(DokitUtil.getString(R.string.dk_health_upload_title))
                        .setMessage(DokitUtil.getString(R.string.dk_health_upload_message))
                        .setCancelable(false)
                        .setPositiveButton("ok") { dialog, which ->
                            dialog.dismiss()
                            if (mController != null) {
                                ToastUtils.showShort(DokitUtil.getString(R.string.dk_health_funcation_start))
                                appHealth = true
                                DokitConstant.APP_HEALTH_RUNNING = true
                                //重启app
                                mController!!.postDelayed({
                                    AppUtils.relaunchApp()
                                    Process.killProcess(Process.myPid())
                                    exitProcess(1)
                                }, 2000)
                            }
                        }
                        .setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }.show()
            }
        })
    }

    override fun showDialog(provider: DialogProvider<*>) {
        val dialog = UniversalDialogFragment(provider)
        provider.host = dialog
        provider.show(childFragmentManager)
    }
}