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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.CopyOnWriteArraySet

/**
 * An interface allows you to use [dialogFragmentViewBinding] and [DialogFragmentViewBindingDelegate].
 *
 * It is recommended to extend [DefaultViewBindingDialogFragment] before implementing this interface.
 * If not, you can copy/paste [DefaultViewBindingDialogFragment]'s implementation to make your own implementation.
 */
public interface ViewBindingDialogFragment {
  /**
   * A [LiveData] which allows you to observe [OnDestroyViewListeners].
   *
   * This will be set to the new [OnDestroyViewListeners] when [DialogFragment.onCreateView] called
   * and will set to null when [DialogFragment.onDestroyView] called.
   *
   * The [OnDestroyViewListeners] will be invoked and disposed when when [DialogFragment.onDestroyView] called.
   */
  public val onDestroyViewLiveData: LiveData<OnDestroyViewListeners?>

  /**
   * The class containing listeners will be invoked when [DialogFragment.onDestroyView] called.
   */
  @MainThread
  public class OnDestroyViewListeners {
    private var isDisposed = false
    private val listeners: MutableSet<() -> Unit> = CopyOnWriteArraySet()

    public operator fun plusAssign(listener: () -> Unit) {
      check(!isDisposed) { "Already disposed" }

      listeners += listener
    }

    public operator fun invoke() {
      check(!isDisposed) { "Already disposed" }
      log { "Listeners::invoke ${listeners.size}" }

      listeners.forEach { it() }
    }

    /**
     * Dispose this listeners. Should call once.
     */
    public fun dispose() {
      check(!isDisposed) { "Already disposed" }
      log { "Listeners::dispose ${listeners.size}" }

      listeners.clear()
      isDisposed = true
    }
  }
}

/**
 * Default implementation of [ViewBindingDialogFragment].
 * Extends this class to able to use [dialogFragmentViewBinding] and [DialogFragmentViewBindingDelegate]
 */
public open class DefaultViewBindingDialogFragment : DialogFragment(), ViewBindingDialogFragment {
  private lateinit var listeners: ViewBindingDialogFragment.OnDestroyViewListeners
  private val _onDestroyViewLiveData = MutableLiveData<ViewBindingDialogFragment.OnDestroyViewListeners?>()

  final override val onDestroyViewLiveData: LiveData<ViewBindingDialogFragment.OnDestroyViewListeners?> get() = _onDestroyViewLiveData

  @CallSuper
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _onDestroyViewLiveData.value = ViewBindingDialogFragment.OnDestroyViewListeners()
      .also { listeners = it }
    return null
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()

    listeners()
    listeners.dispose()
    _onDestroyViewLiveData.value = null
  }
}
