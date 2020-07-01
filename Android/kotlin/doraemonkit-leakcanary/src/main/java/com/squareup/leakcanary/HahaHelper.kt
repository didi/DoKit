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

import com.squareup.haha.perflib.*
import com.squareup.haha.perflib.ClassInstance.FieldValue
import com.squareup.leakcanary.Preconditions.Companion.checkNotNull
import java.lang.reflect.InvocationTargetException
import java.nio.charset.Charset
import java.util.*

class HahaHelper private constructor() {
    companion object {
        private val WRAPPER_TYPES: Set<String> = HashSet(
                Arrays.asList(Boolean::class.java.name, Char::class.java.name, Float::class.java.name,
                        Double::class.java.name, Byte::class.java.name, Short::class.java.name,
                        Int::class.java.name, Long::class.java.name))

        fun threadName(holder: Instance): String {
            val values = classInstanceValues(holder)
            val nameField = fieldValue<Any>(values, "name")
                    ?: // Sometimes we can't find the String at the expected memory address in the heap dump.
                    // See https://github.com/square/leakcanary/issues/417 .
                    return "Thread name not available"
            return asString(nameField)
        }

        fun extendsThread(clazz: ClassObj): Boolean {
            var extendsThread = false
            var parentClass = clazz
            while (parentClass.superClassObj != null) {
                if (parentClass.className == Thread::class.java.name) {
                    extendsThread = true
                    break
                }
                parentClass = parentClass.superClassObj
            }
            return extendsThread
        }

        /**
         * This returns a string representation of any object or value passed in.
         */
        fun valueAsString(value: Any?): String {
            val stringValue: String
            stringValue = if (value == null) {
                "null"
            } else if (value is ClassInstance) {
                val valueClassName = value.classObj.className
                if (valueClassName == String::class.java.name) {
                    '"'.toString() + asString(value) + '"'
                } else {
                    value.toString()
                }
            } else {
                value.toString()
            }
            return stringValue
        }

        /** Given a string instance from the heap dump, this returns its actual string value.  */
        fun asString(stringObject: Any): String {
            checkNotNull(stringObject, "stringObject")
            val instance = stringObject as Instance
            val values = classInstanceValues(instance)
            val count = fieldValue<Int>(values, "count")
            checkNotNull(count, "count")
            if (count == 0) {
                return ""
            }
            val value = fieldValue<Any>(values, "value")
            checkNotNull(value, "value")
            var offset: Int
            val array: ArrayInstance
            return if (isCharArray(value)) {
                array = value as ArrayInstance
                offset = 0
                // < API 23
                // As of Marshmallow, substrings no longer share their parent strings' char arrays
                // eliminating the need for String.offset
                // https://android-review.googlesource.com/#/c/83611/
                if (hasField(values, "offset")) {
                    offset = fieldValue(values, "offset")
                    checkNotNull(offset, "offset")
                }
                val chars = array.asCharArray(offset, count)
                String(chars)
            } else if (isByteArray(value)) {
                // In API 26, Strings are now internally represented as byte arrays.
                array = value as ArrayInstance

                // HACK - remove when HAHA's perflib is updated to https://goo.gl/Oe7ZwO.
                try {
                    val asRawByteArray = ArrayInstance::class.java.getDeclaredMethod("asRawByteArray", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                    asRawByteArray.isAccessible = true
                    val rawByteArray = asRawByteArray.invoke(array, 0, count) as ByteArray
                    String(rawByteArray, Charset.forName("UTF-8"))
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException(e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException(e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException(e)
                }
            } else {
                throw UnsupportedOperationException("Could not find char array in $instance")
            }
        }

        fun isPrimitiveWrapper(value: Any?): Boolean {
            return if (value !is ClassInstance) {
                false
            } else WRAPPER_TYPES.contains(value.classObj.className)
        }

        fun isPrimitiveOrWrapperArray(value: Any?): Boolean {
            if (value !is ArrayInstance) {
                return false
            }
            val arrayInstance = value
            return if (arrayInstance.arrayType != Type.OBJECT) {
                true
            } else WRAPPER_TYPES.contains(arrayInstance.classObj.className)
        }

        private fun isCharArray(value: Any): Boolean {
            return value is ArrayInstance && value.arrayType == Type.CHAR
        }

        private fun isByteArray(value: Any): Boolean {
            return value is ArrayInstance && value.arrayType == Type.BYTE
        }

        fun classInstanceValues(instance: Instance): List<FieldValue> {
            val classInstance = instance as ClassInstance
            return classInstance.values
        }

        fun <T> fieldValue(values: List<FieldValue>, fieldName: String): T {
            for (fieldValue in values) {
                if (fieldValue.field.name == fieldName) {
                    return fieldValue.value as T
                }
            }
            throw IllegalArgumentException("Field $fieldName does not exists")
        }

        fun hasField(values: List<FieldValue>, fieldName: String): Boolean {
            for (fieldValue in values) {
                if (fieldValue.field.name == fieldName) {
                    return true
                }
            }
            return false
        }
    }

    init {
        throw AssertionError()
    }
}