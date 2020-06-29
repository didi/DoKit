package com.didichuxing.doraemonkit.kit.network.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil
import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView
import kotlinx.android.synthetic.main.dk_view_network_request.view.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @desc: 显示request和response的view
 */
class NetworkDetailView : LinearLayout {
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS")


    private var mClipboard: ClipboardManager? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.dk_view_network_request, this)
        mClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        tv_body.setOnLongClickListener(OnLongClickListener {
            val clipData = ClipData.newPlainText("Label", tv_body.getText())
            mClipboard!!.setPrimaryClip(clipData)
            Toast.makeText(getContext(), "copy success", Toast.LENGTH_SHORT).show()
            false
        })
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun bindRequest(record: NetworkRecord) {

        diver_time.setText(R.string.dk_network_detail_title_request_time)
        diver_header.setText(R.string.dk_network_detail_title_request_header)
        diver_body.setText(R.string.dk_network_detail_title_request_body)
        diver_format.visibility = View.GONE
        json_body.visibility = View.GONE
        tv_body.visibility = View.VISIBLE
        if (record.mRequest != null) {
            val request = record.mRequest
            request?.let {
                tv_url.text = it.url
                tv_method.text = it.method
                try {
                    tv_header.text = URLDecoder.decode(it.headers, "utf-8")
                } catch (e: Exception) {
                    tv_header.text = it.headers
                }
                tv_time.text = mDateFormat.format(Date(record.startTime))
                tv_data_size.text = ByteUtil.getPrintSize(record.requestLength)
                try {
                    var strBody = if (TextUtils.isEmpty(it.postData)) "NULL" else it.postData
                    strBody = URLDecoder.decode(strBody, "utf-8")
                    tv_body.text = strBody
                } catch (e: Exception) {
                    tv_body.text = if (TextUtils.isEmpty(it.postData)) "NULL" else it.postData
                }
            }
        }
    }

    fun bindResponse(record: NetworkRecord) {
        diver_time.setText(R.string.dk_network_detail_title_response_time)
        diver_header.setText(R.string.dk_network_detail_title_response_header)
        diver_body.setText(R.string.dk_network_detail_title_response_body)
        diver_format.visibility = View.VISIBLE
        diver_format.text = "unFormat"
        json_body.visibility = View.VISIBLE
        json_body.setTextSize(16.0f)
        json_body.setScaleEnable(false)
        tv_body.visibility = View.GONE
        diver_format.setOnClickListener {
            if (tv_body.visibility == View.VISIBLE) {
                //格式化
                val strBody = if (TextUtils.isEmpty(record.mResponseBody)) "NULL" else record.mResponseBody
                try {
                    JSONObject(strBody)
                    json_body.visibility = View.VISIBLE
                    tv_body.visibility = View.GONE
                    diver_format.text = "unFormat"
                } catch (e: JSONException) {
                    json_body.visibility = View.GONE
                    tv_body.visibility = View.VISIBLE
                    tv_body.text = strBody
                    diver_format.text = "format"
                    ToastUtils.showShort("format error")
                }
            } else {
                //反格式化
                val strBody = if (TextUtils.isEmpty(record.mResponseBody)) "NULL" else record.mResponseBody
                tv_body.text = strBody
                diver_format.text = "format"
                json_body.visibility = View.GONE
                tv_body.visibility = View.VISIBLE
            }
        }
        if (record.mResponse != null) {
            val response = record.mResponse
            val request = record.mRequest
            response?.let {
                tv_url.text = it.url
                tv_method.text = request?.method
                tv_header.text = it.headers
                tv_time.text = mDateFormat.format(Date(record.endTime))
                tv_data_size.text = ByteUtil.getPrintSize(record.responseLength)
            }

            val strBody = if (TextUtils.isEmpty(record.mResponseBody)) "NULL" else record.mResponseBody
            try {
                JSONObject(strBody)
                json_body.bindJson(strBody)
            } catch (e: JSONException) {
                e.printStackTrace()
                tv_body.visibility = View.VISIBLE
                json_body.visibility = View.GONE
                diver_format.text = "format"
                tv_body.text = strBody
            }
        }
    }
}