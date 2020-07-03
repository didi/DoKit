package com.didichuxing.doraemonkit.hook

object ActivityHookManager {

    private val mListeners = mutableListOf<ActivityListener>()

    fun registerActivityListener(listener: ActivityListener) {
        mListeners.add(listener)
    }

    fun unregisterActivityListener(listener: ActivityListener) {
        mListeners.remove(listener)
    }


    interface ActivityListener {
        /**
         * activity启动前
         */
        fun onPreLaunch()

        /**
         * activity启动后
         */
        fun onLaunched()

        /**
         * activity暂停前
         */
        fun onPrePause()

        /**
         * activity暂停后
         */
        fun onPaused()
    }

    fun dispatchActivityPreLauncher() {
        mListeners.forEach {
            it.onPreLaunch()
        }
    }

    fun dispatchActivityLaunched() {
        mListeners.forEach {
            it.onLaunched()
        }
    }

    fun dispatchActivityPrePause() {
        mListeners.forEach {
            it.onPrePause()
        }
    }

    fun dispatchActivityPaused() {
        mListeners.forEach {
            it.onPaused()
        }
    }
}