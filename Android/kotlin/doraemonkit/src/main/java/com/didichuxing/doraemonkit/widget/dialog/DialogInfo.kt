package com.didichuxing.doraemonkit.widget.dialog

/**
 * Created by wanglikun on 2019/4/12
 */
data class DialogInfo(var listener: DialogListener? = null,
                      var title: CharSequence? = null,
                      var desc: CharSequence? = null)