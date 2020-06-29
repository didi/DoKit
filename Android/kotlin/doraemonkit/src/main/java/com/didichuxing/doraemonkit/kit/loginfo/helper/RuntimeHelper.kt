package com.didichuxing.doraemonkit.kit.loginfo.helper

import java.io.IOException

/**
 * @author lostjobs created on 2020/6/28
 */
object RuntimeHelper {
    @Throws(IOException::class)
    fun exec(args: List<String>): Process {
        return Runtime.getRuntime().exec(args.toTypedArray())
    }

    fun destroy(process: Process) {
        process.destroy()
    }
}