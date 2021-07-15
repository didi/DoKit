package com.didichuxing.doraemondemo.comm

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import com.didichuxing.doraemondemo.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/11/21-15:14
 * 描    述：
 * 修订历史：
 * ================================================
 */
class CommFragmentActivity : AppCompatActivity() {
    var fragmentClass: Class<out CommBaseFragment>? = null

    private val viewModel: CommViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comm)
        initTitleBar()
        initFragment()
    }

    private fun initFragment() {
        val bundle = intent.extras
        bundle?.let {
            if (bundle.get(CommLauncher.FRAGMENT_CLASS) != null) {
                fragmentClass = bundle[CommLauncher.FRAGMENT_CLASS] as Class<out CommBaseFragment>?
                fragmentClass?.let {
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.container_view, it.newInstance())
                        .commit()
                }
            }
        }


    }

    private fun initTitleBar() {
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        viewModel.getTitle().observe(this,
            Observer<String> {
                findViewById<TextView>(R.id.tv_title).text = it
            })
    }


}