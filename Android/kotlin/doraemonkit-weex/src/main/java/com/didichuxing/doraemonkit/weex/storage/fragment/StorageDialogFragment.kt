package com.didichuxing.doraemonkit.weex.storage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.storage.StorageInfo
import kotlinx.android.synthetic.main.dk_item_storage_dialog.*

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author alvince.zy@gmail.com
 */
class StorageDialogFragment : DialogFragment() {

    companion object {
        const val KEY_STORAGE_INFO = "key_storage_info"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dk_item_storage_dialog, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getSerializable(KEY_STORAGE_INFO)
            ?.let { it as? StorageInfo }
            ?.also { info ->
                view.apply {
                    tv_name.text = info.key
                    tv_value.text = info.value
                }
            }
    }

    override fun onStart() {
        super.onStart()
        dialog?.also {
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

}