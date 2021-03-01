package com.didichuxing.doraemondemo.mc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.R

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/29-11:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
class VpFragment(index: Int) : Fragment() {
    val mIndex: Int = index


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vp, container, false)
        val btn = view.findViewById<Button>(R.id.btn)
        btn.text = "Fragment_$mIndex"
        btn.setOnClickListener {
            ToastUtils.showShort((it as Button).text.toString())
        }
        return view
    }


}