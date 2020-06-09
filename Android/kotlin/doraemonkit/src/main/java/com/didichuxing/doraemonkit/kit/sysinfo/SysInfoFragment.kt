package com.didichuxing.doraemonkit.kit.sysinfo

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.PhoneUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.extensions.runBelowAndroidM
import com.didichuxing.doraemonkit.extensions.runGreaterThanAndroidM
import com.didichuxing.doraemonkit.extensions.runGreaterThanAndroidN
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.util.DeviceUtils
import com.didichuxing.doraemonkit.util.DeviceUtils.getRomSpace
import com.didichuxing.doraemonkit.util.DeviceUtils.getSDCardSpace
import com.didichuxing.doraemonkit.util.ExecutorUtil
import com.didichuxing.doraemonkit.util.PermissionUtil.checkCameraUnreliable
import com.didichuxing.doraemonkit.util.PermissionUtil.checkLocationUnreliable
import com.didichuxing.doraemonkit.util.PermissionUtil.checkReadContactUnreliable
import com.didichuxing.doraemonkit.util.PermissionUtil.checkReadPhoneUnreliable
import com.didichuxing.doraemonkit.util.PermissionUtil.checkRecordUnreliable
import com.didichuxing.doraemonkit.util.PermissionUtil.checkStorageUnreliable
import com.didichuxing.doraemonkit.util.PermissionUtil.hasPermissions
import com.didichuxing.doraemonkit.util.UIUtils.density
import com.didichuxing.doraemonkit.util.UIUtils.getScreenInch
import com.didichuxing.doraemonkit.util.UIUtils.realHeightPixels
import com.didichuxing.doraemonkit.util.UIUtils.widthPixels
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import java.util.ArrayList

/**
 * @author lostjobs created on 2020/6/9
 */
class SysInfoFragment : BaseFragment(), HomeTitleBar.OnTitleBarClickListener {

  private var mInfoList: RecyclerView? = null
  private val mInfoItemAdapter by lazy {
    SysInfoItemAdapter()
  }

  override fun onRequestLayout(): Int = R.layout.dk_fragment_sys_info

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    kotlin.runCatching {
      initView()
      initData()
    }
  }

  private fun initView() {
    mInfoList = view?.findViewById(R.id.info_list)
    mInfoList?.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = mInfoItemAdapter
      addItemDecoration(
        DividerItemDecoration(DividerItemDecoration.VERTICAL).setDrawable(
          ContextCompat.getDrawable(requireContext(), R.drawable.dk_divider)
        )
      )
    }

    findViewById<HomeTitleBar>(R.id.title_bar).mListener = this
  }

  private fun initData() {
    val sysInfoItems = mutableListOf<SysInfoItem>()
    addAppData(sysInfoItems)
    addDeviceData(sysInfoItems)
    runGreaterThanAndroidM {
      addPermissionData(sysInfoItems)
    }
    runBelowAndroidM {
      addPermissionDataUnreliable()
    }
    mInfoItemAdapter.setData(sysInfoItems)
  }

  private fun addPermissionDataUnreliable() {
    ExecutorUtil.execute {
      val list: MutableList<SysInfoItem> = ArrayList()
      list.add(SysInfoItem(title = getString(R.string.dk_sysinfo_permission_info_unreliable)))
      list.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_permission_location),
          value = if (checkLocationUnreliable(context!!)) "YES" else "NO",
          isPermission = true
        )
      )
      list.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_permission_sdcard),
          value = if (checkStorageUnreliable()) "YES" else "NO",
          isPermission = true
        )
      )
      list.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_permission_camera),
          value = if (checkCameraUnreliable()) "YES" else "NO",
          isPermission = true
        )
      )
      list.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_permission_record),
          value = if (checkRecordUnreliable()) "YES" else "NO",
          isPermission = true
        )
      )
      list.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_permission_read_phone),
          value = if (checkReadPhoneUnreliable(context!!)) "YES" else "NO",
          isPermission = true
        )
      )
      list.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_permission_contact),
          value = if (checkReadContactUnreliable(context!!)) "YES" else "NO",
          isPermission = true
        )
      )
      view?.post {
        if (!this@SysInfoFragment.isDetached) {
          mInfoItemAdapter.append(list)
        }
      }
    }
  }

  private fun addPermissionData(sysInfoItems: MutableList<SysInfoItem>) {
    sysInfoItems.add(SysInfoItem(title = getString(R.string.dk_sysinfo_permission_info)))
    val p1 = arrayOf(
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_permission_location),
        value = checkPermission(*p1),
        isPermission = true
      )
    )
    val p2 = arrayOf(
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_permission_sdcard),
        value = checkPermission(*p2),
        isPermission = true
      )
    )
    val p3 = arrayOf(
      Manifest.permission.CAMERA
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_permission_camera),
        value = checkPermission(*p3),
        isPermission = true
      )
    )
    val p4 = arrayOf(
      Manifest.permission.RECORD_AUDIO
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_permission_record),
        value = checkPermission(*p4),
        isPermission = true
      )
    )
    val p5 = arrayOf(
      Manifest.permission.READ_PHONE_STATE
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_permission_read_phone),
        value = checkPermission(*p5),
        isPermission = true
      )
    )
    val p6 = arrayOf(
      Manifest.permission.READ_CONTACTS
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_permission_contact),
        value = checkPermission(*p6),
        isPermission = true
      )
    )
  }

  @SuppressLint("MissingPermission")
  private fun addDeviceData(sysInfoItems: MutableList<SysInfoItem>) {
    sysInfoItems.add(SysInfoItem(title = getString(R.string.dk_sysinfo_device_info)))
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_brand_and_model),
        value = Build.MANUFACTURER + " " + Build.MODEL
      )
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_android_version),
        value = Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")"
      )
    )
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_ext_storage_free),
          value = getSDCardSpace(context!!)
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_rom_free),
          value = getRomSpace(context!!)
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_display_size),
          value = widthPixels.toString() + "x" + realHeightPixels
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_display_inch),
          value = "" + getScreenInch(activity!!)
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "ROOT",
          value = com.blankj.utilcode.util.DeviceUtils.isDeviceRooted().toString()
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "DENSITY",
          value = density.toString()
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "IP",
          value = if (TextUtils.isEmpty(NetworkUtils.getIPAddress(true))) "null" else NetworkUtils.getIPAddress(
            true
          )
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "Mac",
          value = if (TextUtils.isEmpty(com.blankj.utilcode.util.DeviceUtils.getMacAddress())) "null" else com.blankj.utilcode.util.DeviceUtils.getMacAddress()
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "IMEI",
          value = if (TextUtils.isEmpty(PhoneUtils.getIMEI())) "null" else PhoneUtils.getIMEI()
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "Sign MD5",
          value = AppUtils.getAppSignatureMD5()
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "Sign SHA1",
          value = AppUtils.getAppSignatureSHA1()
        )
      )
    }
    kotlin.runCatching {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = "Sign SHA256",
          value = AppUtils.getAppSignatureSHA256()
        )
      )
    }
  }

  @SuppressLint("NewApi")
  private fun addAppData(sysInfoItems: MutableList<SysInfoItem>) {
    val pi = DeviceUtils.getPackageInfo(requireContext())
    sysInfoItems.add(SysInfoItem(title = getString(R.string.dk_sysinfo_app_info)))
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_package_name),
        value = pi?.packageName
      )
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_package_version_name),
        value = pi?.versionName
      )
    )
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_package_version_code),
        value = pi?.versionCode.toString()
      )
    )
    runGreaterThanAndroidN {
      sysInfoItems.add(
        SysInfoItem(
          type = SysInfoItemAdapter.TYPE_INFO_ITEM,
          title = getString(R.string.dk_sysinfo_package_min_sdk),
          value = requireContext().applicationInfo.minSdkVersion.toString()
        )
      )
    }
    sysInfoItems.add(
      SysInfoItem(
        type = SysInfoItemAdapter.TYPE_INFO_ITEM,
        title = getString(R.string.dk_sysinfo_package_target_sdk),
        value = requireContext().applicationInfo.targetSdkVersion.toString()
      )
    )
  }

   private fun checkPermission(vararg perms: String): String? {
    try {
      return if (hasPermissions(requireContext(), *perms)) "YES" else "NO"
    } catch (e: NullPointerException) {
      e.printStackTrace()
    }
    return "NO"
  }

  override fun onRightClick() {
    requireActivity().finish()
  }
}