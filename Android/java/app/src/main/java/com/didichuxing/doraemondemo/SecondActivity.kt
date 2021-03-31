package com.didichuxing.doraemondemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemondemo.db.room.PersonDBHelper
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.*

class SecondActivity : AppCompatActivity() {
    companion object {
        val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        tv.setOnClickListener {
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