package com.didichuxing.doraemondemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.db.PersonDBHelper
import kotlinx.coroutines.*

class DataBaseActivity : AppCompatActivity() {
    companion object {
        val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        findViewById<View>(R.id.tv).setOnClickListener {
            // Create Person encrypted database
            CoroutineScope(Dispatchers.Main).launch {
                val job = async(Dispatchers.IO) { insertPersonDB() }
                val success = job.await()
                ToastUtils.showShort("插入数据成功")

            }
            ToastUtils.showShort("开始插入数据")
        }
    }


    /**
     * 只非ui编程中执行操作
     */
    private fun insertPersonDB(): Boolean {
        val personDBHelper = PersonDBHelper(applicationContext)
        if (personDBHelper.count() == 0) {
            for (i in 0..99) {
                val firstName = "${PersonDBHelper.PERSON_COLUMN_FIRST_NAME}_$i"
                val lastName = "${PersonDBHelper.PERSON_COLUMN_LAST_NAME}_$i"
                val address = "${PersonDBHelper.PERSON_COLUMN_ADDRESS}_$i"
                personDBHelper.insertPerson(firstName, lastName, address)
            }
        }

        return true
    }

}
