package com.squareup.leakcanary

import android.content.Context
import com.squareup.leakcanary.ActivityRefWatcher.Companion.install
import com.squareup.leakcanary.AndroidExcludedRefs.createAppDefaults
import com.squareup.leakcanary.AndroidReachabilityInspectors.Companion.defaultAndroidInspectors
import com.squareup.leakcanary.UploadLeakService
import com.squareup.leakcanary.internal.DisplayLeakActivity
import com.squareup.leakcanary.internal.FragmentRefWatcher
import com.squareup.leakcanary.internal.LeakCanaryInternals
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.getLeakDirectoryProvider
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.setEnabledAsync
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default.Companion

/** A [RefWatcherBuilder] with appropriate Android defaults.  */
class AndroidRefWatcherBuilder internal constructor(context: Context) : RefWatcherBuilder<AndroidRefWatcherBuilder?>() {
    private val context: Context = context.applicationContext
    private var watchActivities = true
    private var watchFragments = true
    private var enableDisplayLeakActivity = false

    /**
     * Sets a custom [AbstractAnalysisResultService] to listen to analysis results. This
     * overrides any call to [.heapDumpListener].
     */
    fun listenerServiceClass(
            listenerServiceClass: Class<out AbstractAnalysisResultService?>): AndroidRefWatcherBuilder {
        enableDisplayLeakActivity = UploadLeakService::class.java.isAssignableFrom(listenerServiceClass)
        return heapDumpListener(ServiceHeapDumpListener(context, listenerServiceClass))!!
    }

    /**
     * Sets a custom delay for how long the [RefWatcher] should wait until it checks if a
     * tracked object has been garbage collected. This overrides any call to [ ][.watchExecutor].
     */
    fun watchDelay(delay: Long, unit: TimeUnit): AndroidRefWatcherBuilder {
        return watchExecutor(AndroidWatchExecutor(unit.toMillis(delay)))!!
    }

    /**
     * Whether we should automatically watch activities when calling [.buildAndInstall].
     * Default is true.
     */
    fun watchActivities(watchActivities: Boolean): AndroidRefWatcherBuilder {
        this.watchActivities = watchActivities
        return this
    }

    /**
     * Whether we should automatically watch fragments when calling [.buildAndInstall].
     * Default is true. When true, LeakCanary watches native fragments on Android O+ and support
     * fragments if the leakcanary-support-fragment dependency is in the classpath.
     */
    fun watchFragments(watchFragments: Boolean): AndroidRefWatcherBuilder {
        this.watchFragments = watchFragments
        return this
    }

    /**
     * Sets the maximum number of heap dumps stored. This overrides any call to
     * [LeakCanary.setLeakDirectoryProvider]
     *
     * @throws IllegalArgumentException if maxStoredHeapDumps < 1.
     */
    fun maxStoredHeapDumps(maxStoredHeapDumps: Int): AndroidRefWatcherBuilder {
        val leakDirectoryProvider: LeakDirectoryProvider = DefaultLeakDirectoryProvider(context, maxStoredHeapDumps)
        LeakCanary.setLeakDirectoryProvider(leakDirectoryProvider)
        return self()!!
    }

    /**
     * Creates a [RefWatcher] instance and makes it available through [ ][LeakCanary.installedRefWatcher].
     *
     * Also starts watching activity references if [.watchActivities] was set to true.
     *
     * @throws UnsupportedOperationException if called more than once per Android process.
     */
    fun buildAndInstall(): RefWatcher {
        if (LeakCanaryInternals.installedRefWatcher != null) {
            throw UnsupportedOperationException("buildAndInstall() should only be called once.")
        }
        val refWatcher = build()
        if (refWatcher != RefWatcher.DISABLED) {
            if (enableDisplayLeakActivity) {
                setEnabledAsync(context, DisplayLeakActivity::class.java, true)
            }
            if (watchActivities) {
                install(context, refWatcher)
            }
            if (watchFragments) {
                FragmentRefWatcher.Helper.install(context, refWatcher)
            }
        }
        LeakCanaryInternals.installedRefWatcher = refWatcher
        return refWatcher
    }

    override val isDisabled: Boolean = LeakCanary.isInAnalyzerProcess(context)

    override fun defaultHeapDumper(): HeapDumper {
        val leakDirectoryProvider = getLeakDirectoryProvider(context)
        return AndroidHeapDumper(context, leakDirectoryProvider)
    }

    override fun defaultDebuggerControl(): DebuggerControl {
        return AndroidDebuggerControl()
    }

    override fun defaultHeapDumpListener(): HeapDump.Listener {
        return ServiceHeapDumpListener(context, UploadLeakService::class.java)
    }

    override fun defaultExcludedRefs(): ExcludedRefs {
        return createAppDefaults().build()
    }

    override fun defaultWatchExecutor(): WatchExecutor {
        return AndroidWatchExecutor(DEFAULT_WATCH_DELAY_MILLIS)
    }

    override fun defaultReachabilityInspectorClasses(): List<Class<out Reachability.Inspector?>> {
        return defaultAndroidInspectors()
    }

    companion object {
        private val DEFAULT_WATCH_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(5)
    }

}