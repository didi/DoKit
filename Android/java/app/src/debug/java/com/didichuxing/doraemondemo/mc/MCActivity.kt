package com.didichuxing.doraemondemo.mc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.R
import kotlinx.android.synthetic.main.activity_mc.*

/**
 * 一机多控Demo Activity
 */
class MCActivity : AppCompatActivity() {
    val TAG = "MCActivity"

    lateinit var adapter: RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mc)

        btn_webview.setOnClickListener {
            startActivity(Intent(this@MCActivity, WebViewActivity::class.java))
        }

        btn1.setOnClickListener {
            ToastUtils.showShort("正常点击")
        }

        btn2.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("我是弹框")
                .setPositiveButton(
                    "确定"
                ) { dialog, _ ->
                    run {
                        ToastUtils.showShort("确定")
                        dialog.dismiss()
                    }
                }.setNegativeButton("取消") { dialog, _ ->
                    run {
                        ToastUtils.showShort("取消")
                        dialog.dismiss()
                    }
                }.show()


        }
        initData()

    }

    private fun initData() {
        val rvDatas = mutableListOf<String>()
        val lvDatas = mutableListOf<String>()


        for (index in 0..100) {
            val item = layoutInflater.inflate(R.layout.item_sc, null)
            val tv = item.findViewById<TextView>(R.id.tv)
            tv.setOnClickListener {
                ToastUtils.showShort("${(it as TextView).text}")
            }
            tv.text = "sc item $index"
            ll.addView(item)
            rvDatas.add("rv item $index")
            lvDatas.add("lv item $index")
        }

        lv.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lvDatas)

        lv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            ToastUtils.showShort(lvDatas[position])
        }


        rv.layoutManager = LinearLayoutManager(this)
        adapter = RVAdapter(R.layout.item_rv, rvDatas)
        adapter.setOnItemClickListener { adapter, _, position ->
            ToastUtils.showShort("rv item  click ==>$position")
        }
        rv.adapter = adapter

        val fragments = mutableListOf<Fragment>()
        fragments.add(VpFragment(0))
        fragments.add(VpFragment(1))
        fragments.add(VpFragment(2))
        fragments.add(VpFragment(3))

        vp.adapter = VPAdapter(fragments, supportFragmentManager)

    }


}