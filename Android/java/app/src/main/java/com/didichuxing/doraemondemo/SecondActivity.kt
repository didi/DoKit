package com.didichuxing.doraemondemo

import android.os.Bundle
import android.util.Xml
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.db.room.PersonDBHelper
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.android.synthetic.main.activity_second.*
import org.xml.sax.helpers.DefaultHandler
import org.xmlpull.v1.XmlPullParser
import javax.xml.parsers.SAXParserFactory

class SecondActivity : AppCompatActivity() {
    companion object {
        val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        tv.setOnClickListener {
            // Create Person encrypted database
            val personDBHelper = PersonDBHelper(applicationContext)
            if (personDBHelper.count() == 0) {
                for (i in 0..99) {
                    val firstName = "${PersonDBHelper.PERSON_COLUMN_FIRST_NAME}_$i"
                    val lastName = "${PersonDBHelper.PERSON_COLUMN_LAST_NAME}_$i"
                    val address = "${PersonDBHelper.PERSON_COLUMN_ADDRESS}_$i"
                    personDBHelper.insertPerson(firstName, lastName, address)
                }
            }
            ToastUtils.showShort("aaa")
        }
    }


}