package com.didichuxing.doraemonkit.kit.fileexplorer

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.TitledBaseFragment
import com.didichuxing.doraemonkit.util.ImageUtil
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.io.File
import java.lang.ref.WeakReference

class ImageDetailFragment : TitledBaseFragment() {

    companion object {
        private const val TAG = "ImageDetailFragment"
    }

    private var mImageView: ImageView? = null

    override fun onRequestLayout(): Int = R.layout.dk_fragment_image_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TitleBar>(R.id.title_bar).onTitleBarClickListener = this
        mImageView = view.findViewById(R.id.image)
        (arguments?.getSerializable(BundleKey.FILE_KEY) as? File)?.run {
            ImageReadTask(this@ImageDetailFragment).execute(this)
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        mImageView?.setImageBitmap(bitmap)
    }


    class ImageReadTask(fragment: ImageDetailFragment) : AsyncTask<File, Void, Bitmap>() {
        private val mFragmentRef = WeakReference(fragment)

        override fun doInBackground(vararg params: File): Bitmap {
            return ImageUtil.decodeSampledBitmapFromFilePath(params[0].path, 1080, 1920)
        }

        override fun onPostExecute(result: Bitmap) {
            super.onPostExecute(result)
            mFragmentRef.get()?.setBitmap(result)
        }

    }
}
