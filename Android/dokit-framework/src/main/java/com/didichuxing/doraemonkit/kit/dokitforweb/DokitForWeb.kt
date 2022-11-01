package com.didichuxing.doraemonkit.kit.dokitforweb

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.util.SPUtils

object DokitForWeb {


    const val DOKIT_H5_MC_INJECT_JS = "dokit_h5_mc_inject_js"
    const val NAME_DOKIIT_MC_CONFIGALL = "dokit-for-web-config-all"

    const val DOKIT_H5_MC_INJECT_MODE = "dokit_h5_mc_inject_mode"
    const val DOKIT_H5_MC_INJECT_URL = "dokit_h5_mc_inject_url"
    const val DOKIT_H5_MC_INJECT_URL_DEFAULT = "http://120.55.183.20/dokit/mc/dokit.js"

    var sp: SPUtils = SPUtils.getInstance(NAME_DOKIIT_MC_CONFIGALL)


    fun loadConfig() {
        DoKitManager.H5_DOKIT_MC_INJECT = sp.getBoolean(DOKIT_H5_MC_INJECT_JS)

        DoKitManager.H5_MC_JS_INJECT_MODE = sp.getString(DOKIT_H5_MC_INJECT_MODE, "file")
        DoKitManager.H5_MC_JS_INJECT_URL = sp.getString(DOKIT_H5_MC_INJECT_URL, DOKIT_H5_MC_INJECT_URL_DEFAULT)
    }

    fun saveMcH5Inject(switch: Boolean) {
        DoKitManager.H5_DOKIT_MC_INJECT = switch
        sp.put(DOKIT_H5_MC_INJECT_JS, switch)
    }

    fun saveMcInjectMode(mode: String) {
        DoKitManager.H5_MC_JS_INJECT_MODE = mode
        sp.put(DOKIT_H5_MC_INJECT_MODE, mode)
    }

    fun saveMcInjectUrl(url: String) {
        DoKitManager.H5_MC_JS_INJECT_URL = url
        sp.put(DOKIT_H5_MC_INJECT_URL, url)
    }

}
