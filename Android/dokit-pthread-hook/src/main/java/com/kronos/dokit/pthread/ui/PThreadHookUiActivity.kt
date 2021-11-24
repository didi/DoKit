package com.kronos.dokit.pthread.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didichuxing.doraemonkit.DoKit
import com.kronos.dokit.pthread.R
import com.kronos.dokit.pthread.databinding.ActivityPthreadHookBinding
import com.kronos.dokit.pthread.dump

/**
 *
 *  @Author LiABao
 *  @Since 2021/11/24
 *
 */
class PThreadHookUiActivity : AppCompatActivity(R.layout.activity_pthread_hook) {

    private val viewBinding by viewBinding(ActivityPthreadHookBinding::bind)
    private val threadAdapter by lazy {
        ThreadAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerView.adapter = threadAdapter
        DoKit.APPLICATION.dump {
            threadAdapter.setNewData(this)
        }
    }
}
