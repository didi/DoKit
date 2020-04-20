package com.didichuxing.doraemondemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.DoraemonKit
import kotlinx.android.synthetic.main.activity_second.*
import me.imid.swipebacklayout.lib.app.SwipeBackActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tv.setOnClickListener {
            DoraemonKit.show();
        }
    }
}