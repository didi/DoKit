package com.didichuxing.doraemonkit.kit.colorpick

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.didichuxing.doraemonkit.util.UIUtils

/**
 * 捕获当前应用页面类
 * @author Donald Yan
 * @date 2020/6/9
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class ImageCapturerImpl constructor(private val context: Context, bundle: Bundle): ImageCapturer {

    private lateinit var mMediaProjection: MediaProjection

    private lateinit var mImageReader: ImageReader

    private var isCapturing = false

    private lateinit var mBitmap: Bitmap

    private  var serviceConnection: ServiceConnection?  = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context.packageManager.getApplicationInfo(context.packageName, 0).targetSdkVersion >= 29) {
            serviceConnection = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {
                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    initImageRead(context, bundle)
                }
            }
            ScreenRecorderService.bindService(context, serviceConnection!!)
        }else {
            initImageRead(context, bundle)
        }
    }

    private fun initImageRead(context: Context, bundle: Bundle) {
        val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mMediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, bundle.getParcelable("data")!!)
        val width = UIUtils.widthPixels
        val height = UIUtils.realHeightPixels
        val dpi = UIUtils.densityDpi
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        mMediaProjection.createVirtualDisplay("ScreenCapture", width, height, dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.surface, null, null)
    }

    override fun destroy() {
        mImageReader.close()
        mMediaProjection.stop()
        if (this::mBitmap.isInitialized) {
            mBitmap.recycle()
        }
        if (serviceConnection != null) {
            ScreenRecorderService.unbindService(context, serviceConnection!!)
        }
    }

    override fun capture() {
        if (isCapturing) {
            return
        }
        isCapturing = true
        val image = mImageReader.acquireLatestImage() ?: return
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPaddingStride = rowStride - pixelStride * width
        val rowPadding = rowPaddingStride / pixelStride
        val recordBitmap = Bitmap.createBitmap(width + rowPadding, height, Bitmap.Config.ARGB_8888)
        recordBitmap.copyPixelsFromBuffer(buffer)
        mBitmap = Bitmap.createBitmap(recordBitmap, 0, 0, width, height)
        image.close()
        isCapturing = false
    }

    override fun getPartBitmap(x: Int, y: Int, width: Int, height: Int): Bitmap? {
        if (!this::mBitmap.isInitialized || mBitmap.isRecycled) {
            return null
        }
        var x = if (x < 0) 0 else x
        if (x + width > mBitmap.width) {
            x = mBitmap.width - width
        }
        var y = if (y < 0) 0 else y
        if (y + height > mBitmap.height) {
            y = mBitmap.height - height
        }
        return Bitmap.createBitmap(mBitmap, x, y, width, height)
    }

}