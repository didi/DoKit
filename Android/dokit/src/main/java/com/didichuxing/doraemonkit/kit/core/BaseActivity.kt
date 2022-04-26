package com.didichuxing.doraemonkit.kit.core

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * Created by wanglikun on 2018/10/26.
 */
abstract class BaseActivity : AppCompatActivity() {
    private val mFragments = ArrayDeque<BaseFragment>()

    @JvmOverloads
    fun showContent(target: Class<out BaseFragment>, bundle: Bundle? = null) {
        try {

            val fragment = target.newInstance()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            val fm = supportFragmentManager
            val fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.add(R.id.content, fragment)
            mFragments.push(fragment)
            fragmentTransaction.addToBackStack("")
            fragmentTransaction.commit()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (!mFragments.isEmpty()) {
            val fragment = mFragments.first
            if (!fragment.onBackPressed()) {
                mFragments.removeFirst()
                super.onBackPressed()
                if (mFragments.isEmpty()) {
                    finish()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    fun doBack(fragment: BaseFragment) {
        if (mFragments.contains(fragment)) {
            mFragments.remove(fragment)
            val fm = supportFragmentManager
            fm.popBackStack()
            if (mFragments.isEmpty()) {
                finish()
            }
        }
    }

}
