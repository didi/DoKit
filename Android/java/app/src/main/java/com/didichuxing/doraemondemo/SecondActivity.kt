package com.didichuxing.doraemondemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.android.synthetic.main.activity_second.*
import com.didichuxing.doraemondemo.db.room.PersonDBHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {
    companion object {
        val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        tv.setOnClickListener {
            // Create Person encrypted database
            GlobalScope.launch {
                insertPersonDB()
            }
            LogHelper.i(TAG, "inner thread====>${Thread.currentThread().name}")
            ToastUtils.showShort("开始插入数据")
        }
    }


    private fun insertPersonDB() {
        LogHelper.i(TAG, "inner thread====>${Thread.currentThread().name}")
        val personDBHelper = PersonDBHelper(applicationContext)
        if (personDBHelper.count() == 0) {
            for (i in 0..99) {
                val firstName = "${PersonDBHelper.PERSON_COLUMN_FIRST_NAME}_$i"
                val lastName = "${PersonDBHelper.PERSON_COLUMN_LAST_NAME}_$i"
                val address = "${PersonDBHelper.PERSON_COLUMN_ADDRESS}_$i"
                personDBHelper.insertPerson(firstName, lastName, address)
            }
            ToastUtils.showShort("插入数据成功")
        }
    }


}