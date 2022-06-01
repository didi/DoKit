package com.didichuxing.doraemondemo.module.bigbitmap

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import coil.imageLoader
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.didichuxing.doraemondemo.BaseStatusBarActivity
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemondemo.databinding.ActivityBigBitmapMockBinding
import com.facebook.drawee.backends.pipeline.Fresco
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


/**
 * didi Create on 2022/5/27 .
 *
 * Copyright (c) 2022/5/27 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/27 6:00 下午
 * @Description 用一句话说明文件功能
 */

class BigBitmapActivity : BaseStatusBarActivity() {

    //Glide 加载
    val picassoImgUrl =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F2c.zol-img.com.cn%2Fproduct%2F124_500x2000%2F748%2FceZOdKgDAFsq2.jpg&refer=http%3A%2F%2F2c.zol-img.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=850e537c70eaa7753e892bc8b4d05f57"
    val glideImageUrl =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F1812.img.pp.sohu.com.cn%2Fimages%2Fblog%2F2009%2F11%2F18%2F18%2F8%2F125b6560a6ag214.jpg&refer=http%3A%2F%2F1812.img.pp.sohu.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=76d35d35d9c510f1c24a422e9e02fd46"
    val frescoImageUrl =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F035%2F063%2F726%2F3ea4031f045945e1843ae5156749d64c.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=7150aaa2071d512cf2f6b556e126dd66"
    val imageLoaderImageUrl =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F004%2F531%2F381%2F4339f96900344574a0c8ca272a7b8f27.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621652979&t=b7e83ecc987c64cc31079469d292eb56"
    val coilImageUrl =
        "https://cdn.nlark.com/yuque/0/2020/png/252337/1587091196083-assets/web-upload/62122ab5-986b-4662-be88-d3007a5e31c5.png"

    private var _binding: ActivityBigBitmapMockBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "大图检测"

        //初始化
        val config = ImageLoaderConfiguration.Builder(this)
            .build()
        ImageLoader.getInstance().init(config)

        _binding = ActivityBigBitmapMockBinding.inflate(layoutInflater).also { it ->
            setContentView(it.root)

            it.btnLoadImg.setOnClickListener { view ->
                onClick(view)
            }
        }

    }


     fun onClick(v: View) {
        when (v.id) {

            R.id.btn_load_img -> {

                Picasso.get().load(picassoImgUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.mipmap.cat)
                    .error(R.mipmap.cat)
                    .intoOrCancel(_binding?.ivPicasso)

                Glide.with(this)
                    .asBitmap()
                    .load(glideImageUrl)
                    .placeholder(R.mipmap.cat)
                    .error(R.mipmap.cat)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(CircleCrop())
                    .intoOrCancel(_binding?.ivGlide)

                //coil
                _binding?.ivCoil?.apply {
                    val request = coil.request.ImageRequest.Builder(this.context)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .transformations(CircleCropTransformation())
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .data(coilImageUrl)
                        .target(this)
                        .build()
                    imageLoader.enqueue(request)
                }

                //connect
                run {
                    requestImage(coilImageUrl)
                }

                //imageLoader
                val imageLoader = ImageLoader.getInstance()
                imageLoader.displayImageOrNot(imageLoaderImageUrl, _binding?.ivImageloader)
                //fresco
                _binding?.ivFresco?.setImageURI(Uri.parse(frescoImageUrl))
                val imagePipeline = Fresco.getImagePipeline()
                // combines above two lines
                imagePipeline.clearCaches()
            }

            else -> {
            }
        }
    }

    private fun requestImage(urlStr: String) {
        try {
            //
            val url = URL(urlStr)
            // http    https
            // ftp
            val urlConnection = url.openConnection() as HttpURLConnection
            //http get post
            urlConnection.requestMethod = "GET"
            urlConnection.connectTimeout = 5000
            urlConnection.readTimeout = 5000
            val responseCode = urlConnection.responseCode
            if (responseCode == 200) {
                val bitmap = BitmapFactory.decodeStream(urlConnection.inputStream)

                runOnUiThread {
                    _binding?.ivConnect?.setImageBitmap(bitmap)
                }
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun RequestCreator.intoOrCancel(target: ImageView?) {
        target?.also { into(it) }
    }

    private fun RequestBuilder<*>.intoOrCancel(target: ImageView?) {
        target?.also { into(it) }
    }

    private fun ImageLoader.displayImageOrNot(url: String, target: ImageView?) {
        target?.also { displayImage(url, it) }
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}
