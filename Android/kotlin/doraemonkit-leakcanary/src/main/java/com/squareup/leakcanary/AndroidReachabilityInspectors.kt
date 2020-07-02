/*
 * Copyright (C) 2018 Square, Inc.
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

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.Fragment
import android.os.MessageQueue
import android.view.View
import java.util.*

/**
 * A set of default [Reachability.Inspector]s that knows about common AOSP and library
 * classes.
 *
 * These are heuristics based on our experience and knownledge of AOSP and various library
 * internals. We only make a reachability decision if we're reasonably sure such reachability is
 * unlikely to be the result of a programmer mistake.
 *
 * For example, no matter how many mistakes we make in our code, the value of Activity.mDestroy
 * will not be influenced by those mistakes.
 */
enum class AndroidReachabilityInspectors(//
        private val inspectorClass: Class<out Reachability.Inspector>) {
    VIEW(ViewInspector::class.java), ACTIVITY(ActivityInspector::class.java), DIALOG(DialogInspector::class.java), APPLICATION(ApplicationInspector::class.java), FRAGMENT(FragmentInspector::class.java), SUPPORT_FRAGMENT(SupportFragmentInspector::class.java), MESSAGE_QUEUE(MessageQueueInspector::class.java), MORTAR_PRESENTER(MortarPresenterInspector::class.java), VIEW_ROOT_IMPL(ViewImplInspector::class.java), MAIN_THEAD(MainThreadInspector::class.java), WINDOW(WindowInspector::class.java);

    class ViewInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf(View::class.java)) {
                return Reachability.UNKNOWN
            }
            val mAttachInfo = element.getFieldReferenceValue("mAttachInfo") ?: return Reachability.UNKNOWN
            return if (mAttachInfo == "null") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    class ActivityInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf(Activity::class.java)) {
                return Reachability.UNKNOWN
            }
            val mDestroyed = element.getFieldReferenceValue("mDestroyed") ?: return Reachability.UNKNOWN
            return if (mDestroyed == "true") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    class DialogInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf(Dialog::class.java)) {
                return Reachability.UNKNOWN
            }
            val mDecor = element.getFieldReferenceValue("mDecor") ?: return Reachability.UNKNOWN
            return if (mDecor == "null") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    class ApplicationInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            return if (element.isInstanceOf(Application::class.java)) {
                Reachability.REACHABLE
            } else Reachability.UNKNOWN
        }
    }

    class FragmentInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf(Fragment::class.java)) {
                return Reachability.UNKNOWN
            }
            val mDetached = element.getFieldReferenceValue("mDetached") ?: return Reachability.UNKNOWN
            return if (mDetached == "true") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    class SupportFragmentInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf("androidx.fragment.app.Fragment")) {
                return Reachability.UNKNOWN
            }
            val mDetached = element.getFieldReferenceValue("mDetached") ?: return Reachability.UNKNOWN
            return if (mDetached == "true") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    class MessageQueueInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf(MessageQueue::class.java)) {
                return Reachability.UNKNOWN
            }
            val mQuitting = element.getFieldReferenceValue("mQuitting")
            // If the queue is not quitting, maybe it should actually have been, we don't know.
            // However, if it's quitting, it is very likely that's not a bug.
            return if ("true" == mQuitting) {
                Reachability.UNREACHABLE
            } else Reachability.UNKNOWN
        }
    }

    class MortarPresenterInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf("mortar.Presenter")) {
                return Reachability.UNKNOWN
            }
            val view = element.getFieldReferenceValue("view")

            // Bugs in view code tends to cause Mortar presenters to still have a view when they actually
            // should be a unreachable, so in that case we don't know their reachability status. However,
            // when the view is null, we're pretty sure they should be unreachable.
            return if ("null" == view) {
                Reachability.UNREACHABLE
            } else Reachability.UNKNOWN
        }
    }

    class ViewImplInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf("android.view.ViewRootImpl")) {
                return Reachability.UNKNOWN
            }
            val mView = element.getFieldReferenceValue("mView") ?: return Reachability.UNKNOWN
            return if (mView == "null") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    class MainThreadInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf(Thread::class.java)) {
                return Reachability.UNKNOWN
            }
            val name = element.getFieldReferenceValue("name")
            return if ("main" == name) {
                Reachability.REACHABLE
            } else Reachability.UNKNOWN
        }
    }

    class WindowInspector : Reachability.Inspector {
        override fun expectedReachability(element: LeakTraceElement): Reachability {
            if (!element.isInstanceOf("android.view.Window")) {
                return Reachability.UNKNOWN
            }
            val mDestroyed = element.getFieldReferenceValue("mDestroyed") ?: return Reachability.UNKNOWN
            return if (mDestroyed == "true") Reachability.UNREACHABLE else Reachability.REACHABLE
        }
    }

    companion object {
        @JvmStatic
        fun defaultAndroidInspectors(): List<Class<out Reachability.Inspector>> {
            val inspectorClasses: MutableList<Class<out Reachability.Inspector>> = ArrayList()
            for (enumValue in values()) {
                inspectorClasses.add(enumValue.inspectorClass)
            }
            return inspectorClasses
        }
    }

}