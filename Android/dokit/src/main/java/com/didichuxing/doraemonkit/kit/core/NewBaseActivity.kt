package com.didichuxing.doraemonkit.kit.core

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * Created by wanglikun on 2018/10/26.
 */
abstract class NewBaseActivity : AppCompatActivity() {
    private val mFragments = ArrayDeque<BaseFragment>()
    private var contentId = android.R.id.content

    @JvmOverloads
    fun showContent(target: Class<out BaseFragment>, bundle: Bundle? = null) {
        try {
            val fragment = target.newInstance()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            showContent(android.R.id.content, fragment)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun showContent(@IdRes contentId: Int, fragment: BaseFragment) {
        this.contentId = contentId
        try {
            mFragments.push(fragment)
            supportFragmentManager.beginTransaction()
                .replace(contentId, fragment)
                .commitNowAllowingStateLoss()

        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun replaceContent(@IdRes contentId: Int, fragment: BaseFragment) {
        this.contentId = contentId
        mFragments.clear()
        mFragments.push(fragment)
        supportFragmentManager.beginTransaction()
            .replace(contentId, fragment)
            .commitNowAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (!mFragments.isEmpty()) {
            val old = mFragments.pollFirst()
            if (!mFragments.isEmpty()) {
                val fragment = mFragments.peekFirst()
                supportFragmentManager.beginTransaction()
                    .replace(contentId, fragment)
                    .commitNowAllowingStateLoss()
            } else {
                finish()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun doBack(fragment: BaseFragment) {
        onBackPressed()
//        if (mFragments.contains(fragment)) {
//            mFragments.remove(fragment)
//            val fm = supportFragmentManager
//            fm.popBackStack()
//            if (mFragments.isEmpty()) {
//                finish()
//            }
//        }
    }

}
