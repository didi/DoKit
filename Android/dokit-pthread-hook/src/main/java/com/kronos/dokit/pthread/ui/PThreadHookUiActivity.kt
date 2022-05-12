package com.kronos.dokit.pthread.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
class PThreadHookUiActivity : AppCompatActivity() {

    private lateinit var viewBinding :ActivityPthreadHookBinding
    private val threadAdapter by lazy {
        ThreadAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pthread_hook)
        viewBinding = ActivityPthreadHookBinding.inflate(layoutInflater)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerView.adapter = threadAdapter
        DoKit.APPLICATION.dump {
            threadAdapter.setNewData(this)
        }
    }
}
