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

package  com.didichuxing.doraemonkit.viewbind.impl

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.didichuxing.doraemonkit.viewbind.GetBindMethod
import com.didichuxing.doraemonkit.viewbind.ensureMainThread
import com.didichuxing.doraemonkit.viewbind.log
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Used to implement [ViewBinding] property delegate for [Fragment].
 *
 * @param fragment the [Fragment] which owns this delegated property.
 * @param viewBindingBind a lambda function that creates a [ViewBinding] instance from [Fragment]'s root view, eg: `T::bind` static method can be used.
 * @param viewBindingClazz if viewBindingBind is not provided, Kotlin Reflection will be used to get `T::bind` static method.
 */
public class FragmentViewBindingDelegate<T : ViewBinding> private constructor(
  private val fragment: Fragment,
  viewBindingBind: ((View) -> T)? = null,
  viewBindingClazz: Class<T>? = null,
  private var onDestroyView: (T.() -> Unit)?
) : ReadOnlyProperty<Fragment, T> {

  private var binding: T? = null
  private val bind = viewBindingBind ?: { view: View ->
    @Suppress("UNCHECKED_CAST")
    GetBindMethod(viewBindingClazz!!)(null, view) as T
  }

  init {
    ensureMainThread()
    require(viewBindingBind != null || viewBindingClazz != null) {
      "Both viewBindingBind and viewBindingClazz are null. Please provide at least one."
    }

    fragment.lifecycle.addObserver(FragmentLifecycleObserver())
  }

  override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
    binding?.let {
      if (it.root === thisRef.view) {
        return it
      } else {
        binding = null
      }
    }

    check(fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
      "Attempt to get view binding when fragment view is destroyed"
    }

    return bind(thisRef.requireView()).also { binding = it }
  }

  private inner class FragmentLifecycleObserver : DefaultLifecycleObserver {
    val observer = fun(viewLifecycleOwner: LifecycleOwner?) {
      viewLifecycleOwner ?: return

      var onDestroyViewActual = onDestroyView

      val viewLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
          viewLifecycleOwner.lifecycle.removeObserver(this)

          onDestroyViewActual?.invoke(binding!!)
          onDestroyViewActual = null
          binding = null

          log { "$fragment::onDestroyView" }
        }
      }
      viewLifecycleOwner.lifecycle.addObserver(viewLifecycleObserver)
    }

    override fun onCreate(owner: LifecycleOwner) {
      fragment.viewLifecycleOwnerLiveData.observeForever(observer)

      log { "$fragment::onCreate" }
    }

    override fun onDestroy(owner: LifecycleOwner) {
      fragment.viewLifecycleOwnerLiveData.removeObserver(observer)
      fragment.lifecycle.removeObserver(this)

      binding = null
      onDestroyView = null

      log { "$fragment::onDestroy" }
    }
  }

  public companion object Factory {
    /**
     * Create [FragmentViewBindingDelegate] from [viewBindingBind] lambda function.
     *
     * @param fragment the [Fragment] which owns this delegated property.
     * @param viewBindingBind a lambda function that creates a [ViewBinding] instance from [Fragment]'s root view, eg: `T::bind` static method can be used.
     */
    public fun <T : ViewBinding> from(
      fragment: Fragment,
      viewBindingBind: (View) -> T,
      onDestroyView: (T.() -> Unit)?
    ): FragmentViewBindingDelegate<T> = FragmentViewBindingDelegate(
      fragment = fragment,
      viewBindingBind = viewBindingBind,
      onDestroyView = onDestroyView
    )

    /**
     * Create [FragmentViewBindingDelegate] from [ViewBinding] class.
     *
     * @param fragment the [Fragment] which owns this delegated property.
     * @param viewBindingClazz Kotlin Reflection will be used to get `T::bind` static method from this class.
     */
    public fun <T : ViewBinding> from(
      fragment: Fragment,
      viewBindingClazz: Class<T>,
      onDestroyView: (T.() -> Unit)?
    ): FragmentViewBindingDelegate<T> = FragmentViewBindingDelegate(
      fragment = fragment,
      viewBindingClazz = viewBindingClazz,
      onDestroyView = onDestroyView
    )
  }
}
