package com.didichuxing.doraemonkit.weex.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.didichuxing.doraemonkit.kit.core.BaseActivity
import com.didichuxing.doraemonkit.kit.core.BaseFragment

/**
 * Transformed by alvince on 2020/6/30
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class DKCommonActivity : BaseActivity() {

    companion object {
        private const val CLASSNAME = "className"

        @JvmStatic
        fun startWith(context: Context, clazz: Class<out BaseFragment>) {
            val intent = Intent(context, DKCommonActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(CLASSNAME, clazz)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras
            ?: run {
                finish()
                return
            }

        (intent.getSerializableExtra(CLASSNAME) as? Class<BaseFragment>)
            ?.also { showContent(it) }
    }

}