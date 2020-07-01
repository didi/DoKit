package com.squareup.leakcanary

import java.util.*

/**
 * An instance tracked by a [KeyedWeakReference] that hadn't been cleared when the
 * heap was dumped. May or may not point to a leaking reference.
 */
class TrackedReference(
        /** Corresponds to [KeyedWeakReference.key].  */
        val key: String,
        /** Corresponds to [KeyedWeakReference.name].  */
        val name: String,
        /** Class of the tracked instance.  */
        val className: String,
        fields: List<LeakReference>) {

    /** List of all fields (member and static) for that instance.  */
    val fields: List<LeakReference> = Collections.unmodifiableList(ArrayList(fields))

}