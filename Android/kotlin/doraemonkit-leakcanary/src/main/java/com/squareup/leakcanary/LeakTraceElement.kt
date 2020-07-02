/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.leakcanary

import java.io.Serializable
import java.util.*

/** Represents one reference in the chain of references that holds a leaking object in memory.  */
class LeakTraceElement internal constructor(
        /**
         * Information about the reference that points to the next [LeakTraceElement] in the leak
         * chain. Null if this is the last element in the leak trace, ie the leaking object.
         */
        val reference: LeakReference?, holder: Holder, classHierarchy: List<String>,
        extra: String?, exclusion: Exclusion?, leakReferences: List<LeakReference>) : Serializable {
    enum class Type {
        INSTANCE_FIELD, STATIC_FIELD, LOCAL, ARRAY_ENTRY
    }

    enum class Holder {
        OBJECT, CLASS, THREAD, ARRAY
    }

    @Deprecated("""Use {@link #reference} and {@link LeakReference#getDisplayName()} instead.
    Null if this is the last element in the leak trace, ie the leaking object.""")
    val referenceName: String?

    @Deprecated("""Use {@link #reference} and {@link LeakReference#type} instead.
    Null if this is the last element in the leak trace, ie the leaking object.""")
    val type: Type?
    val holder: Holder

    /**
     * Class hierarchy for that object. The first element is [.className]. [Object]
     * is excluded. There is always at least one element.
     */
    val classHierarchy: List<String>
    val className: String

    /** Additional information, may be null.  */
    val extra: String?

    /** If not null, there was no path that could exclude this element.  */
    val exclusion: Exclusion?

    /** List of all fields (member and static) for that object.  */
    val fieldReferences: List<LeakReference>

    @Deprecated("Use {@link #fieldReferences} instead.")
    val fields: List<String>

    /**
     * Returns the string value of the first field reference that has the provided referenceName, or
     * null if no field reference with that name was found.
     */
    fun getFieldReferenceValue(referenceName: String): String? {
        for (fieldReference in fieldReferences) {
            if (fieldReference.name == referenceName) {
                return fieldReference.value
            }
        }
        return null
    }

    /** @see .isInstanceOf
     */
    fun isInstanceOf(expectedClass: Class<*>): Boolean {
        return isInstanceOf(expectedClass.name)
    }

    /**
     * Returns true if this element is an instance of the provided class name, false otherwise.
     */
    fun isInstanceOf(expectedClassName: String): Boolean {
        for (className in classHierarchy) {
            if (className == expectedClassName) {
                return true
            }
        }
        return false
    }

    /**
     * Returns [.className] without the package.
     */
    val simpleClassName: String
        get() {
            val separator = className.lastIndexOf('.')
            return if (separator == -1) {
                className
            } else {
                className.substring(separator + 1)
            }
        }

    override fun toString(): String {
        return toString(false)
    }

    fun toString(maybeLeakCause: Boolean): String {
        var string = ""
        if (reference != null && reference.type == Type.STATIC_FIELD) {
            string += "static "
        }
        if (holder == Holder.ARRAY || holder == Holder.THREAD) {
            string += holder.name.toLowerCase(Locale.US) + " "
        }
        string += simpleClassName
        if (reference != null) {
            var referenceName = reference.displayName
            if (maybeLeakCause) {
                referenceName = "!($referenceName)!"
            }
            string += ".$referenceName"
        }
        if (extra != null) {
            string += " $extra"
        }
        if (exclusion != null) {
            string += " , matching exclusion " + exclusion.matching
        }
        return string
    }

    fun toDetailedString(): String {
        var string = "* "
        string += when (holder) {
            Holder.ARRAY -> "Array of"
            Holder.CLASS -> "Class"
            else -> "Instance of"
        }
        string += " $className\n"
        for (leakReference in fieldReferences) {
            string += "|   $leakReference\n"
        }
        return string
    }

    init {
        referenceName = reference?.displayName
        type = reference?.type
        this.holder = holder
        this.classHierarchy = Collections.unmodifiableList(ArrayList(classHierarchy))
        className = classHierarchy[0]
        this.extra = extra
        this.exclusion = exclusion
        fieldReferences = Collections.unmodifiableList(ArrayList(leakReferences))
        val stringFields: MutableList<String> = ArrayList()
        for (leakReference in leakReferences) {
            stringFields.add(leakReference.toString())
        }
        fields = Collections.unmodifiableList(stringFields)
    }
}