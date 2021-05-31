/*
 * MIT License
 *
 * Copyright (c) 2020-2021 Petrus Nguyễn Thái Học
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package  com.didichuxing.doraemonkit.viewbind

import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

@PublishedApi
internal fun ensureMainThread(): Unit = check(Looper.getMainLooper() == Looper.myLooper()) {
  "Expected to be called on the main thread but was " + Thread.currentThread().name
}

private const val debug = false

internal inline fun log(crossinline message: () -> String) {
  if (debug) {
    Log.d("ViewBinding", message())
  }
}

internal object GetBindMethod {
  init {
    ensureMainThread()
  }

  private val methodSignature = View::class.java
  private val methodMap = ArrayMap<Class<out ViewBinding>, Method>()

  internal operator fun <T : ViewBinding> invoke(clazz: Class<T>): Method =
    methodMap
      .getOrPut(clazz) { clazz.getMethod("bind", methodSignature) }
      .also { log { "GetBindMethod::methodMap.size: ${methodMap.size}" } }
}

@PublishedApi
internal object GetInflateMethod {
  init {
    ensureMainThread()
  }

  private val methodMap = ArrayMap<Class<out ViewBinding>, Method>()
  private val threeParamsMethodSignature = arrayOf(
    LayoutInflater::class.java,
    ViewGroup::class.java,
    Boolean::class.java,
  )
  private val twoParamsMethodSignature = arrayOf(
    LayoutInflater::class.java,
    ViewGroup::class.java,
  )

  internal operator fun <T : ViewBinding> invoke(clazz: Class<T>): Method {
    return methodMap
      .getOrPut(clazz) {
        runCatching { clazz.getMethod("inflate", *threeParamsMethodSignature) }
          .recover { clazz.getMethod("inflate", *twoParamsMethodSignature) }
          .getOrThrow()
      }
      .also { log { "GetInflateMethod::methodMap.size: ${methodMap.size}" } }
  }
}
