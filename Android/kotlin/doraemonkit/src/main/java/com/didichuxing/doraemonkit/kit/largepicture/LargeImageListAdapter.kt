package com.didichuxing.doraemonkit.kit.largepicture

import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.picasso.DokitPicasso
import com.didichuxing.doraemonkit.picasso.MemoryPolicy
import com.didichuxing.doraemonkit.util.ClipboardUtils
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import java.text.DecimalFormat

/**
 * @author:  maple
 * @time:  2020/6/8 - 15:40
 * @desc: 大图列表
 */
class LargeImageListAdapter( ) : BaseQuickAdapter<LargeImageInfo, BaseViewHolder>(R.layout.dk_item_large_img_list, mutableListOf<LargeImageInfo>()) {
    private val mDecimalFormat = DecimalFormat("#0.00")
    override fun convert(holder: BaseViewHolder, largeImageInfo: LargeImageInfo) {
        try {
            val resourceUrl = largeImageInfo.getUrl()?.toInt() ?: throw RuntimeException("url 转换异常")
            DokitPicasso.with(DoraemonKit.APPLICATION)
                    .load(resourceUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .resize(ConvertUtils.dp2px(100F), ConvertUtils.dp2px(100F))
                    .centerCrop()
                    .into(holder.getView<ImageView>(R.id.iv))
        } catch (e: Exception) {
            DokitPicasso.with(DoraemonKit.APPLICATION)
                    .load(largeImageInfo.getUrl())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .resize(ConvertUtils.dp2px(100F), ConvertUtils.dp2px(100F))
                    .centerCrop()
                    .into(holder.getView<ImageView>(R.id.iv))
        }
        if (largeImageInfo.getMemorySize() == 0.toDouble()) {
            holder.getView<TextView>(R.id.tv_framework).text = String.format("framework:%s", "network")
            holder.getView<TextView>(R.id.tv_memory_size).visibility = View.GONE
            holder.getView<TextView>(R.id.tv_size).visibility = View.GONE
        } else {
            holder.getView<TextView>(R.id.tv_framework).text = String.format("framework:%s", largeImageInfo.getFramework())
            holder.getView<TextView>(R.id.tv_memory_size).visibility = View.VISIBLE
            holder.getView<TextView>(R.id.tv_size).visibility = View.VISIBLE
        }
        if (largeImageInfo.getFileSize() == 0.toDouble()) {
            holder.getView<TextView>(R.id.tv_file_size).visibility = View.GONE
        } else {
            holder.getView<TextView>(R.id.tv_file_size).visibility = View.VISIBLE
        }
        holder.getView<TextView>(R.id.tv_file_size).text = String.format("fileSize:%s", mDecimalFormat.format(largeImageInfo.getFileSize()) + "KB")
        holder.getView<TextView>(R.id.tv_memory_size).text = String.format("memorySize:%s", mDecimalFormat.format(largeImageInfo.getMemorySize()) + "MB")
        holder.getView<TextView>(R.id.tv_size).text = String.format("width:%s   height:%s", largeImageInfo.getWidth(), largeImageInfo.getHeight())
        holder.getView<Button>(R.id.btn_copy).setOnClickListener(View.OnClickListener {
            ClipboardUtils.copyUri(Uri.parse(largeImageInfo.getUrl()))
            ToastUtils.showShort("image url  has copied")
        })
    }
}