package com.didichuxing.doraemonkit

import android.app.Activity
import android.content.res.Configuration
import android.view.OrientationEventListener
import com.didichuxing.doraemonkit.kit.main.MainIconDoKitView

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/4-14:10
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitOrientationEventListener(context: Activity) : OrientationEventListener(context) {
    val mActivity: Activity = context
    val TAG = "DoKitOrientationEventListener"

    /**
     * 上一次的屏幕方向位置
     */
    var lastOrientation = -1

    override fun onOrientationChanged(orientation: Int) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;  //手机平放时，检测不到有效的角度
        }
        var innerOrientation = 0
        //只检测是否有四个角度的改变
        if (orientation > 350 || orientation < 10) { //0度
            innerOrientation = 0
        } else if (orientation in 81..99) { //90度
            innerOrientation = 90
        } else if (orientation in 171..189) { //180度
            innerOrientation = 180
        } else if (orientation in 261..279) { //270度
            innerOrientation = 270
        } else {
            return
        }

        if (lastOrientation != innerOrientation) {
            //针对dokitView的根布局进行旋转操作
            //LogHelper.i(TAG, "innerOrientation===>$innerOrientation")
            val dokitView = DoKit.getDoKitView<MainIconDoKitView>(mActivity, MainIconDoKitView::class)


            var currentOrientation = Configuration.ORIENTATION_PORTRAIT
            //竖向
            if (innerOrientation == 0 || innerOrientation == 180) {
                currentOrientation = Configuration.ORIENTATION_PORTRAIT
            }
            //横向
            else if (innerOrientation == 90 || innerOrientation == 270) {
                currentOrientation = Configuration.ORIENTATION_LANDSCAPE
            }
//            dokitView?.let {
//                it.rootView.postDelayed({
//                    it.portraitOrLandscape(currentOrientation)
//                }, 1000)
//            }

            lastOrientation = innerOrientation
        }

    }

}
