package com.didichuxing.doraemondemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.db.PersonDBHelper
import kotlinx.coroutines.*

class EmptyActivity : AppCompatActivity() {
    companion object {
        val TAG = "EmptyActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)

    }
}
