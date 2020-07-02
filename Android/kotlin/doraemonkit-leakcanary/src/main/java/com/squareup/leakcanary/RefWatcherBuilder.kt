package com.squareup.leakcanary

import com.squareup.leakcanary.ExcludedRefs.Companion.builder
import com.squareup.leakcanary.HeapDump

/**
 * Responsible for building [RefWatcher] instances. Subclasses should provide sane defaults
 * for the platform they support.
 */
open class RefWatcherBuilder<T : RefWatcherBuilder<T>?> {
    private var heapDumpListener: HeapDump.Listener? = null
    private var debuggerControl: DebuggerControl? = null
    private var heapDumper: HeapDumper? = null
    private var watchExecutor: WatchExecutor? = null
    private var gcTrigger: GcTrigger? = null
    private val heapDumpBuilder: HeapDump.Builder

    /** @see HeapDump.Listener
     */
    fun heapDumpListener(heapDumpListener: HeapDump.Listener?): T {
        this.heapDumpListener = heapDumpListener
        return self()
    }

    /** @see ExcludedRefs
     */
    fun excludedRefs(excludedRefs: ExcludedRefs?): T {
        heapDumpBuilder.excludedRefs(excludedRefs)
        return self()
    }

    /** @see HeapDumper
     */
    fun heapDumper(heapDumper: HeapDumper?): T {
        this.heapDumper = heapDumper
        return self()
    }

    /** @see DebuggerControl
     */
    fun debuggerControl(debuggerControl: DebuggerControl?): T {
        this.debuggerControl = debuggerControl
        return self()
    }

    /** @see WatchExecutor
     */
    fun watchExecutor(watchExecutor: WatchExecutor?): T {
        this.watchExecutor = watchExecutor
        return self()
    }

    /** @see GcTrigger
     */
    fun gcTrigger(gcTrigger: GcTrigger?): T {
        this.gcTrigger = gcTrigger
        return self()
    }

    /** @see Reachability.Inspector
     */
    fun stethoscopeClasses(
            stethoscopeClasses: List<Class<out Reachability.Inspector?>?>?): T {
        heapDumpBuilder.reachabilityInspectorClasses(stethoscopeClasses)
        return self()
    }

    /**
     * Whether LeakCanary should compute the retained heap size when a leak is detected. False by
     * default, because computing the retained heap size takes a long time.
     */
    fun computeRetainedHeapSize(computeRetainedHeapSize: Boolean): T {
        heapDumpBuilder.computeRetainedHeapSize(computeRetainedHeapSize)
        return self()
    }

    /** Creates a [RefWatcher].  */
    fun build(): RefWatcher {
        if (isDisabled) {
            return RefWatcher.DISABLED
        }
        if (heapDumpBuilder.excludedRefs == null) {
            heapDumpBuilder.excludedRefs(defaultExcludedRefs())
        }
        var heapDumpListener = heapDumpListener
        if (heapDumpListener == null) {
            heapDumpListener = defaultHeapDumpListener()
        }
        var debuggerControl = debuggerControl
        if (debuggerControl == null) {
            debuggerControl = defaultDebuggerControl()
        }
        var heapDumper = heapDumper
        if (heapDumper == null) {
            heapDumper = defaultHeapDumper()
        }
        var watchExecutor = watchExecutor
        if (watchExecutor == null) {
            watchExecutor = defaultWatchExecutor()
        }
        var gcTrigger = gcTrigger
        if (gcTrigger == null) {
            gcTrigger = defaultGcTrigger()
        }
        if (heapDumpBuilder.reachabilityInspectorClasses == null) {
            heapDumpBuilder.reachabilityInspectorClasses(defaultReachabilityInspectorClasses())
        }
        return RefWatcher(watchExecutor, debuggerControl, gcTrigger, heapDumper, heapDumpListener,
                heapDumpBuilder)
    }

    protected open val isDisabled: Boolean
        protected get() = false

    protected fun defaultGcTrigger(): GcTrigger {
        return GcTrigger.DEFAULT
    }

    protected open fun defaultDebuggerControl(): DebuggerControl? {
        return DebuggerControl.NONE
    }

    protected open fun defaultExcludedRefs(): ExcludedRefs? {
        return builder().build()
    }

    protected open fun defaultHeapDumper(): HeapDumper? {
        return HeapDumper.NONE
    }

    protected open fun defaultHeapDumpListener(): HeapDump.Listener? {
        return HeapDump.Listener.NONE
    }

    protected open fun defaultWatchExecutor(): WatchExecutor? {
        return WatchExecutor.NONE
    }

    protected open fun defaultReachabilityInspectorClasses(): List<Class<out Reachability.Inspector?>?>? {
        return emptyList()
    }

    protected fun self(): T {
        return this as T
    }

    init {
        heapDumpBuilder = HeapDump.Builder()
    }
}