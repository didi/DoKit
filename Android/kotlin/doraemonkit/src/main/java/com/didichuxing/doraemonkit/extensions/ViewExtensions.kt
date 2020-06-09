package com.didichuxing.doraemonkit.extensions

import android.view.LayoutInflater
import android.view.View

/**
 * @author lostjobs created on 2020/6/9
 */

inline val View.layoutInflater: LayoutInflater
  get() = LayoutInflater.from(context)