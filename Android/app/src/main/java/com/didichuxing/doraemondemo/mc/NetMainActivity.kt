package com.didichuxing.doraemondemo.mc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.didichuxing.doraemondemo.R
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

class NetMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_main)

    }

    override fun onResume() {
        super.onResume()
        request()
    }

    fun request() {
        val httpRequest = Request.Builder()
            .header("User-Agent", "mc-test")
            .url("https://www.tianqiapi.com/free/week?appid=68852321&appsecret=BgGLDVc7")
            .build()

        val client = OkHttpClient.Builder().build()
        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                GlobalScope.launch(Dispatchers.Main) {
                    val view = findViewById<TextView>(R.id.text)
                    view.text = e.message
                }
            }

            override fun onResponse(call: Call, response: Response) {
                GlobalScope.launch(Dispatchers.Main) {
                    val text: String? = response.body()?.string()
                    val view = findViewById<TextView>(R.id.text)
                    view.text = text
                }
            }
        })
    }
}
