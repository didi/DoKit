package com.didichuxing.doraemonkit.kit.sysinfo

/**
 * @author lostjobs created on 2020/6/9
 */
class SysInfoItem(
  val type: Int = SysInfoItemAdapter.TYPE_SECTION,
  val title: String,
  val value: String? = null,
  var isPermission: Boolean = false
)