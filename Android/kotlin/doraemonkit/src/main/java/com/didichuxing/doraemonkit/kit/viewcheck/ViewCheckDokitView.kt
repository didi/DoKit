package com.didichuxing.doraemonkit.kit.viewcheck

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil
import com.didichuxing.doraemonkit.util.UIUtils
import java.util.*

/**
 * @author: xuchun
 * @time: 2020/6/4 - 14:47
 * @desc: 控件检查View
 */
class ViewCheckDokitView : AbsDokitView(), LifecycleListenerUtil.LifecycleListener {
    private lateinit var mFindCheckViewRunnable: FindCheckViewRunnable
    private lateinit var mTraverHandlerThread: HandlerThread
    private lateinit var mTraverHandler: Handler
    private val mViewSelectListeners: MutableList<OnViewSelectListener> = ArrayList()
    private var mResumedActivity: Activity? = null
    override fun onCreate(context: Context?) {
        mTraverHandlerThread = HandlerThread(TAG)
        mTraverHandlerThread.start()
        mTraverHandler = Handler(mTraverHandlerThread.looper)
        mFindCheckViewRunnable = FindCheckViewRunnable()
        mResumedActivity = ActivityUtils.getTopActivity()
        LifecycleListenerUtil.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTraverHandler.removeCallbacks(mFindCheckViewRunnable)
        mTraverHandlerThread.quit()
        LifecycleListenerUtil.unRegisterListener(this)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check, null)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.x = UIUtils.widthPixels / 2
            it.y = UIUtils.heightPixels / 2
            it.height = DokitViewLayoutParams.WRAP_CONTENT
            it.width = DokitViewLayoutParams.WRAP_CONTENT
        }

    }

    override fun onUp(x: Int, y: Int) {
        super.onUp(x, y)
        preformFindCheckView()
    }

    override fun onActivityResumed(activity: Activity?) {
        mResumedActivity = activity
        preformFindCheckView()
    }

    override fun onViewCreated(rootView: FrameLayout?) {}
    override fun onActivityPaused(activity: Activity?) {}
    override fun onFragmentAttached(f: Fragment?) {}
    override fun onFragmentDetached(f: Fragment?) {}
    override fun onDown(x: Int, y: Int) {}
    fun setViewSelectListener(viewSelectListener: OnViewSelectListener) {
        mViewSelectListeners.add(viewSelectListener)
        preformFindCheckView()
    }

    fun removeViewSelectListener(viewSelectListener: OnViewSelectListener) {
        mViewSelectListeners.remove(viewSelectListener)
    }

    fun preformPreCheckView() {
        mFindCheckViewRunnable.mIndex--
        if (mFindCheckViewRunnable.mIndex < 0) {
            mFindCheckViewRunnable.mIndex += mFindCheckViewRunnable.mCheckViewList!!.size
        }
        mFindCheckViewRunnable.dispatchOnViewSelected()
    }

    fun preformNextCheckView() {
        mFindCheckViewRunnable.mIndex++
        if (mFindCheckViewRunnable.mIndex >= mFindCheckViewRunnable.mCheckViewList!!.size) {
            mFindCheckViewRunnable.mIndex -= mFindCheckViewRunnable.mCheckViewList!!.size
        }
        mFindCheckViewRunnable.dispatchOnViewSelected()
    }

    private fun preformFindCheckView() {
        val x: Int
        val y: Int
        if (isNormalMode) {
            x = normalLayoutParams.leftMargin + rootView.width / 2
            y = normalLayoutParams.topMargin + rootView.height / 2
        } else {
            x = systemLayoutParams.x + rootView.width / 2
            y = systemLayoutParams.y + rootView.height / 2
        }
        mTraverHandler.removeCallbacks(mFindCheckViewRunnable)
        mFindCheckViewRunnable.mX = x
        mFindCheckViewRunnable.mY = y
        mTraverHandler.post(mFindCheckViewRunnable)
    }

    private fun traverseViews(viewList: MutableList<View>, view: View?, x: Int, y: Int) {
        if (view == null) {
            return
        }
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.width
        val bottom = top + view.height

        // 深度优先遍历
        if (view is ViewGroup) {
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in childCount - 1 downTo 0) {
                    traverseViews(viewList, view.getChildAt(index), x, y)
                }
            }
        }
        if (x in (left + 1) until right && top < y && y < bottom) {
            viewList.add(view)
        }
    }

    private fun onViewSelected(current: View?, checkViewList: List<View>?) {
        for (listener in mViewSelectListeners) {
            listener.onViewSelected(current, checkViewList!!)
        }
    }

    interface OnViewSelectListener {
        fun onViewSelected(current: View?, checkViewList: List<View>)
    }

    internal inner class FindCheckViewRunnable : Runnable {
        var mX = 0
        var mY = 0
        var mIndex = 0
        var mCheckViewList: List<View>? = null
        override fun run() {
            val viewList: MutableList<View> = ArrayList(20)
            if (mResumedActivity != null && mResumedActivity!!.window != null) {
                if (isNormalMode) {
                    //LogHelper.d(TAG, "x: " + mX + ", y: " + mY);
                    traverseViews(viewList, UIUtils.getDokitAppContentView(mResumedActivity), mX, mY)
                } else {
                    traverseViews(viewList, mResumedActivity!!.window.decorView, mX, mY)
                }
            }
            mIndex = 0
            mCheckViewList = viewList
            dispatchOnViewSelected()
        }

        fun dispatchOnViewSelected() {
            post(Runnable { onViewSelected(currentCheckView, mCheckViewList) })
        }

        private val currentCheckView: View?
            get() {
                val size = mCheckViewList!!.size
                return if (size == 0) {
                    null
                } else mCheckViewList!![mIndex]
            }
    }

    companion object {
        private const val TAG = "ViewCheckFloatPage"
    }
}