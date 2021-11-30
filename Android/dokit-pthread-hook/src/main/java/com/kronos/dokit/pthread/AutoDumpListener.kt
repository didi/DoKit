package com.kronos.dokit.pthread

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.didichuxing.doraemonkit.DoKit

/**
 *
 *  @Author LiABao
 *  @Since 2021/11/17
 *
 */
class AutoDumpListener : Application.ActivityLifecycleCallbacks {

    init {
        DoKit.APPLICATION.registerActivityLifecycleCallbacks(this)
    }


    private fun getThreadCount(): Int {
        return getAllThreads().size
    }

    private var count = 0

    /**
     * ps -p `self` -t
     * See http://man7.org/linux/man-pages/man1/ps.1.html
     */
    private fun getAllThreads(): MutableList<Thread> {
        var group = Thread.currentThread().threadGroup
        var system: ThreadGroup?
        do {
            system = group
            group = group?.parent
        } while (group != null)
        val count = system?.activeCount() ?: 0
        val threads = arrayOfNulls<Thread>(count)
        system?.enumerate(threads)
        val list = mutableListOf<Thread>()
        threads.forEach {
            it?.let { it1 -> list.add(it1) }
        }
        return list
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if (count > 3) {
            return
        }
        if (getThreadCount() > Threshold) {
            DoKit.APPLICATION.dump {
                forEach {

                }

            }
        }
    }


    companion object {
        const val Threshold = 200
    }
}
