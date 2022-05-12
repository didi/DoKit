package com.didichuxing.doraemondemo.mc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemondemo.test.ScreenRecordingTest
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.fileexplorer.ImageDetailFragment
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager
import java.io.File

/**
 * 一机多控Demo Activity
 */
class MCActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MCActivity"
    }

    lateinit var mAdapter: RVAdapter

    private val screenShotManager = ScreenShotManager("test/kk")

    private val screenRecordingTest = ScreenRecordingTest()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mc)

        findViewById<Button>(R.id.nextPage).setOnClickListener {
            startActivity(Intent(this, NetMainActivity::class.java))
        }

        findViewById<Button>(R.id.webPage).setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }

        findViewById<Button>(R.id.testPage).setOnClickListener {
            startScreenShot()
        }

        findViewById<Button>(R.id.screenPage).setOnClickListener {
            screenRecordingTest.start(this)
        }



        findViewById<SlideBar>(R.id.unlock_bar).setOnUnlockListener(object :
            SlideBar.OnUnlockListener {
            override fun onUnlock(view: View?) {
                DoKit.sendCustomEvent(
                    "un_lock",
                    view,
                    mapOf(
                        "unlock" to "custom unlock",
                        "testRecording" to "true"
                    )
                )
            }

            override fun progress(view: View?, leftMargin: Int) {
                DoKit.sendCustomEvent(
                    "lock_process",
                    view,
                    mapOf(
                        "progress" to "$leftMargin",
                        "testRecording" to "false"
                    )
                )
            }
        })

        val spinner = findViewById<Spinner>(R.id.spinner)

        val citis = resources.getStringArray(R.array.city);
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citis)
        //第三步：设置下拉列表下拉时的菜单样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.prompt = "测试"

        spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                ToastUtils.showShort("选择：" + citis[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                ToastUtils.showShort("没有选择")
            }
        })


        findViewById<View>(R.id.btn1).setOnClickListener {
            ToastUtils.showShort("正常点击")
        }


        findViewById<View>(R.id.btn2).setOnClickListener {
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
        findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { _, checkedId ->
            ToastUtils.showLong("checkedId===>${checkedId}")
        }

        initData()

    }


    private fun startScreenShot() {
        val map = screenShotManager.screenshotBitmap()
        val fileName = screenShotManager.createNextFileName()
        screenShotManager.saveBitmap(map, fileName)
        val bundle = Bundle()
        bundle.putSerializable(BundleKey.FILE_KEY, File(screenShotManager.getScreenFile(fileName)))
        DoKit.launchFullScreen(ImageDetailFragment::class.java, this, bundle, false)
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
            findViewById<LinearLayout>(R.id.ll).addView(item)
            rvDatas.add("rv item $index")
            lvDatas.add("lv item $index")
        }

        findViewById<ListView>(R.id.lv)
            .apply {
                adapter = ArrayAdapter<String>(
                    this@MCActivity,
                    android.R.layout.simple_list_item_1,
                    lvDatas
                )

                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    ToastUtils.showShort(lvDatas[position])
                }
            }



        mAdapter = RVAdapter(R.layout.item_rv, rvDatas)
        mAdapter.setOnItemClickListener { adapter, _, position ->
            ToastUtils.showShort("rv item  click ==>$position")
        }
        findViewById<RecyclerView>(R.id.rv).apply {
            layoutManager = LinearLayoutManager(this@MCActivity)
            adapter = mAdapter
        }


        val fragments = mutableListOf<Fragment>()
        fragments.add(VpFragment(0))
        fragments.add(VpFragment(1))
        fragments.add(VpFragment(2))
        fragments.add(VpFragment(3))

        findViewById<ViewPager>(R.id.vp).adapter = VPAdapter(fragments, supportFragmentManager)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        screenRecordingTest.onActivityResult(requestCode, resultCode, data)
    }
}
