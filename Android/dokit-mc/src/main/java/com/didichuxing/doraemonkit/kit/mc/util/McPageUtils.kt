package com.didichuxing.doraemonkit.kit.mc.util

import android.content.Intent
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.all.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.util.ActivityUtils

object McPageUtils {

    fun startFragment(wsMode: WSMode) {
        val activity = ActivityUtils.getTopActivity()

        if (activity is DoKitMcActivity){
            activity.changeFragment(wsMode)
            return
        }
        val intent = Intent(activity, DoKitMcActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("WS_MODE_ORDINAL", wsMode.name)
        activity.startActivity(intent)
    }
}
