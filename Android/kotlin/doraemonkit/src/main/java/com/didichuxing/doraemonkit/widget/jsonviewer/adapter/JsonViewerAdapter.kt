package com.didichuxing.doraemonkit.widget.jsonviewer.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.widget.jsonviewer.adapter.JsonViewerAdapter.JsonItemViewHolder
import com.didichuxing.doraemonkit.widget.jsonviewer.utils.Utils.getHierarchyStr
import com.didichuxing.doraemonkit.widget.jsonviewer.utils.Utils.isUrl
import com.didichuxing.doraemonkit.widget.jsonviewer.view.JsonItemView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by yuyuhang on 2017/11/29.
 */
class JsonViewerAdapter : BaseJsonViewerAdapter<JsonItemViewHolder?> {
    private var jsonStr: String? = null
    private var mJSONObject: JSONObject? = null
    private var mJSONArray: JSONArray? = null

    constructor(jsonStr: String?) {
        this.jsonStr = jsonStr
        var `object`: Any? = null
        try {
            `object` = JSONTokener(jsonStr).nextValue()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (`object` != null && `object` is JSONObject) {
            mJSONObject = `object`
        } else if (`object` != null && `object` is JSONArray) {
            mJSONArray = `object`
        } else {
            throw IllegalArgumentException("jsonStr is illegal.")
        }
    }

    constructor(jsonObject: JSONObject?) {
        mJSONObject = jsonObject
        requireNotNull(mJSONObject) { "jsonObject can not be null." }
    }

    constructor(jsonArray: JSONArray?) {
        mJSONArray = jsonArray
        requireNotNull(mJSONArray) { "jsonArray can not be null." }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JsonItemViewHolder {
        return JsonItemViewHolder(JsonItemView(parent.context))
    }

    override fun onBindViewHolder(holder: JsonItemViewHolder, position: Int) {
        val itemView: JsonItemView = holder.rootView
        itemView.setTextSize(BaseJsonViewerAdapter.Companion.TEXT_SIZE_DP)
        itemView.setRightColor(BaseJsonViewerAdapter.Companion.BRACES_COLOR)
        if (mJSONObject != null) {
            if (position == 0) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("{")
                return
            } else if (position == itemCount - 1) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("}")
                return
            } else if (mJSONObject!!.names() == null) {
                return
            }
            // 遍历key
            val key = mJSONObject!!.names().optString(position - 1)
            val value = mJSONObject!!.opt(key)
            if (position < itemCount - 2) {
                handleJsonObject(key, value, itemView, true, 1)
            } else {
                handleJsonObject(key, value, itemView, false, 1) // 最后一组，结尾不需要逗号
            }
        }
        if (mJSONArray != null) {
            if (position == 0) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("[")
                return
            } else if (position == itemCount - 1) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("]")
                return
            }
            val value = mJSONArray!!.opt(position - 1) // 遍历array
            if (position < itemCount - 2) {
                handleJsonArray(value, itemView, true, 1)
            } else {
                handleJsonArray(value, itemView, false, 1) // 最后一组，结尾不需要逗号
            }
        }
    }

    override fun getItemCount(): Int {
        if (mJSONObject != null) {
            return if (mJSONObject!!.names() != null) {
                mJSONObject!!.names().length() + 2
            } else {
                2
            }
        }
        return if (mJSONArray != null) {
            mJSONArray!!.length() + 2
        } else 0
    }

    /**
     * 处理 value 上级为 JsonObject 的情况，value有key
     *
     * @param value
     * @param key
     * @param itemView
     * @param appendComma
     * @param hierarchy
     */
    private fun handleJsonObject(key: String, value: Any, itemView: JsonItemView, appendComma: Boolean, hierarchy: Int) {
        val keyBuilder = SpannableStringBuilder(getHierarchyStr(hierarchy))
        keyBuilder.append("\"").append(key).append("\"").append(":")
        keyBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.KEY_COLOR), 0, keyBuilder.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        keyBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.BRACES_COLOR), keyBuilder.length - 1, keyBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemView.showLeft(keyBuilder)
        handleValue(value, itemView, appendComma, hierarchy)
    }

    /**
     * 处理 value 上级为 JsonArray 的情况，value无key
     *
     * @param value
     * @param itemView
     * @param appendComma 结尾是否需要逗号(最后一组 value 不需要逗号)
     * @param hierarchy   缩进层级
     */
    private fun handleJsonArray(value: Any, itemView: JsonItemView, appendComma: Boolean, hierarchy: Int) {
        itemView.showLeft(SpannableStringBuilder(getHierarchyStr(hierarchy)))
        handleValue(value, itemView, appendComma, hierarchy)
    }

    /**
     * @param value
     * @param itemView
     * @param appendComma 结尾是否需要逗号(最后一组 key:value 不需要逗号)
     * @param hierarchy   缩进层级
     */
    private fun handleValue(value: Any?, itemView: JsonItemView, appendComma: Boolean, hierarchy: Int) {
        val valueBuilder = SpannableStringBuilder()
        if (value is Number) {
            valueBuilder.append(value.toString())
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.NUMBER_COLOR), 0, valueBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else if (value is Boolean) {
            valueBuilder.append(value.toString())
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.BOOLEAN_COLOR), 0, valueBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else if (value is JSONObject) {
            itemView.showIcon(true)
            valueBuilder.append("Object{...}")
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.BRACES_COLOR), 0, valueBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            itemView.setIconClickListener(JsonItemClickListener(value, itemView, appendComma, hierarchy + 1))
        } else if (value is JSONArray) {
            itemView.showIcon(true)
            valueBuilder.append("Array[").append(value.length().toString()).append("]")
            val len = valueBuilder.length
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.BRACES_COLOR), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.NUMBER_COLOR), 6, len - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.BRACES_COLOR), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            itemView.setIconClickListener(JsonItemClickListener(value, itemView, appendComma, hierarchy + 1))
        } else if (value is String) {
            itemView.hideIcon()
            valueBuilder.append("\"").append(value.toString()).append("\"")
            if (isUrl(value.toString())) {
                valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.TEXT_COLOR), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.URL_COLOR), 1, valueBuilder.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.TEXT_COLOR), valueBuilder.length - 1, valueBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.TEXT_COLOR), 0, valueBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else if (valueBuilder.length == 0 || value == null) {
            itemView.hideIcon()
            valueBuilder.append("null")
            valueBuilder.setSpan(ForegroundColorSpan(BaseJsonViewerAdapter.Companion.NULL_COLOR), 0, valueBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (appendComma) {
            valueBuilder.append(",")
        }
        itemView.showRight(valueBuilder)
    }

    internal inner class JsonItemClickListener(private val value: Any?, private val itemView: JsonItemView, private val appendComma: Boolean, private val hierarchy: Int) : View.OnClickListener {
        private var isCollapsed = true
        private val isJsonArray: Boolean
        override fun onClick(view: View) {
            if (itemView.childCount == 1) { // 初始（折叠） --> 展开""
                isCollapsed = false
                itemView.showIcon(false)
                itemView.tag = itemView.rightText
                itemView.showRight(if (isJsonArray) "[" else "{")
                val array = if (isJsonArray) value as JSONArray? else (value as JSONObject?)!!.names()
                var i = 0
                while (array != null && i < array.length()) {
                    val childItemView = JsonItemView(itemView.context)
                    childItemView.setTextSize(BaseJsonViewerAdapter.Companion.TEXT_SIZE_DP)
                    childItemView.setRightColor(BaseJsonViewerAdapter.Companion.BRACES_COLOR)
                    val childValue = array.opt(i)
                    if (isJsonArray) {
                        handleJsonArray(childValue, childItemView, i < array.length() - 1, hierarchy)
                    } else {
                        handleJsonObject(childValue as String, (value as JSONObject?)!!.opt(childValue), childItemView, i < array.length() - 1, hierarchy)
                    }
                    itemView.addViewNoInvalidate(childItemView)
                    i++
                }
                val childItemView = JsonItemView(itemView.context)
                childItemView.setTextSize(BaseJsonViewerAdapter.Companion.TEXT_SIZE_DP)
                childItemView.setRightColor(BaseJsonViewerAdapter.Companion.BRACES_COLOR)
                val builder = StringBuilder(getHierarchyStr(hierarchy - 1))
                builder.append(if (isJsonArray) "]" else "}").append(if (appendComma) "," else "")
                childItemView.showRight(builder)
                itemView.addViewNoInvalidate(childItemView)
                itemView.requestLayout()
                itemView.invalidate()
            } else {                            // 折叠 <--> 展开
                val temp = itemView.rightText
                itemView.showRight(itemView.tag as CharSequence)
                itemView.tag = temp
                itemView.showIcon(!isCollapsed)
                for (i in 1 until itemView.childCount) {
                    itemView.getChildAt(i).visibility = if (isCollapsed) View.VISIBLE else View.GONE
                }
                isCollapsed = !isCollapsed
            }
        }

        init {
            isJsonArray = value != null && value is JSONArray
        }
    }

    inner class JsonItemViewHolder(rootView: JsonItemView) : RecyclerView.ViewHolder(rootView) {
        lateinit var rootView: JsonItemView

        init {
            setIsRecyclable(false)
            this.rootView = rootView
        }
    }
}