package com.didichuxing.doraemonkit.widget.videoview

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.util.UIUtils.dp2px
import com.didichuxing.doraemonkit.util.UIUtils.realHeightPixels
import com.didichuxing.doraemonkit.util.UIUtils.widthPixels
import com.didichuxing.doraemonkit.widget.videoview.CustomVideoView.StateListener

/**
 * Created by wanglikun on 2019/4/16
 */
class DKVideoView : RelativeLayout {
    private var videoView: CustomVideoView? = null
    private var seekbarProgress: SeekBar? = null
    private var btnController: ImageView? = null
    private var tvCurrentProgress: TextView? = null
    private var tvTotalProgress: TextView? = null
    private var ivVolume: ImageView? = null
    private var seekbarVolume: SeekBar? = null
    private var btnScreen: ImageView? = null
    private var flVolume: FrameLayout? = null
    private var flLight: FrameLayout? = null
    private var llyController: LinearLayout? = null
    private var rlContainer: RelativeLayout? = null
    private var mAudioManager: AudioManager? = null
    private var screenWidth = 0
    private var screenHeight = 0
    private var mContext: Context? = null
    private var videoLayout: View? = null
    private var mActivity: Activity? = null
    private var videoPos = 0
    private var state = 0
    private var mVideoPath: String? = null
    private var isVerticalScreen = true

    constructor(context: Context?) : super(context, null) {}

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        mContext = context
        init()
        initView()
        initData()
        initListener()
    }

    fun register(activity: Activity?) {
        mActivity = activity
    }

    fun setVideoPath(path: String) {
        mVideoPath = path
        if (path.startsWith("http") || path.startsWith("https")) {
            videoView!!.setVideoURI(Uri.parse(path))
        } else {
            videoView!!.setVideoPath(mVideoPath)
        }
    }

    private fun init() {
        screenWidth = widthPixels
        screenHeight = realHeightPixels
        mAudioManager = mContext!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun initView() {
        val videoLayout = LayoutInflater.from(mContext).inflate(R.layout.dk_video_layout, this, true)
        flVolume = videoLayout.findViewById(R.id.fl_volume)
        flLight = videoLayout.findViewById(R.id.fl_light)
        videoView = videoLayout.findViewById(R.id.videoView)
        seekbarProgress = videoLayout.findViewById(R.id.seekbar_progress)
        seekbarVolume = videoLayout.findViewById(R.id.seekbar_volume)
        btnController = videoLayout.findViewById(R.id.btn_controller)
        btnScreen = videoLayout.findViewById(R.id.btn_screen)
        tvCurrentProgress = videoLayout.findViewById(R.id.tv_currentProgress)
        tvTotalProgress = videoLayout.findViewById(R.id.tv_totalProgress)
        ivVolume = videoLayout.findViewById(R.id.iv_volume)
        llyController = videoLayout.findViewById(R.id.lly_controller)
        rlContainer = videoLayout.findViewById(R.id.rl_container)
        this.videoLayout = videoLayout
    }

    private fun initData() {
        val currentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        seekbarVolume!!.progress = currentVolume
    }

    private fun initListener() {
        btnScreen!!.setOnClickListener {
            if (isVerticalScreen) {
                mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        btnController!!.setOnClickListener {
            if (videoView!!.isPlaying) {
                btnController!!.setImageResource(R.drawable.dk_btn_play_style)
                videoView!!.pause()
                mHandler.removeMessages(UPDATE_PROGRESS)
            } else {
                btnController!!.setImageResource(R.drawable.dk_btn_pause_style)
                videoView!!.start()
                mHandler.sendEmptyMessage(UPDATE_PROGRESS)
                if (state == 0) state = 1
            }
        }
        videoView!!.setStateListener(object : StateListener {
            override fun changeVolume(detlaY: Float) {
                if (flVolume!!.visibility == View.GONE) {
                    flVolume!!.visibility = View.VISIBLE
                }
                val maxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val currentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
                val index = (detlaY / screenHeight * maxVolume * 3).toInt()
                val volume = Math.max(0, currentVolume - index)
                mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                seekbarVolume!!.progress = volume
            }

            override fun changeBrightness(detlaY: Float) {
                if (flLight!!.visibility == View.GONE) {
                    flLight!!.visibility = View.VISIBLE
                }
                val wml = mActivity!!.window.attributes
                var screenBrightness = wml.screenBrightness
                val index = -detlaY / screenHeight / 5
                screenBrightness += index
                if (screenBrightness > 1.0f) {
                    screenBrightness = 1.0f
                } else if (screenBrightness < 0.01f) {
                    screenBrightness = 0.01f
                }
                wml.screenBrightness = screenBrightness
                mActivity!!.window.attributes = wml
            }

            override fun hideHint() {
                if (flLight!!.visibility == View.VISIBLE) {
                    flLight!!.visibility = View.GONE
                }
                if (flVolume!!.visibility == View.VISIBLE) {
                    flVolume!!.visibility = View.GONE
                }
            }
        })
        seekbarVolume!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        seekbarProgress!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewFormat(tvCurrentProgress, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // 暂停刷新
                mHandler.removeMessages(UPDATE_PROGRESS)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (state != 0) {
                    mHandler.sendEmptyMessage(UPDATE_PROGRESS)
                }
                videoView!!.seekTo(seekBar.progress)
            }
        })
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            isVerticalScreen = true
            ivVolume!!.visibility = View.GONE
            seekbarVolume!!.visibility = View.GONE
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(290f))
            mActivity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            mActivity!!.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        } else {
            isVerticalScreen = false
            ivVolume!!.visibility = View.VISIBLE
            seekbarVolume!!.visibility = View.VISIBLE
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            // 移除半屏状态
            mActivity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            mActivity!!.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun setVideoViewScale(width: Int, height: Int) {
        val videoViewLayoutParams = videoView!!.layoutParams
        videoViewLayoutParams.width = width
        videoViewLayoutParams.height = height
        videoView!!.layoutParams = videoViewLayoutParams
        val rlContainerLayoutParams = rlContainer!!.layoutParams as LayoutParams
        rlContainerLayoutParams.width = width
        rlContainerLayoutParams.height = height
        rlContainer!!.layoutParams = rlContainerLayoutParams
    }

    private fun updateTextViewFormat(tv: TextView?, m: Int) {
        val result: String
        // 毫秒转成秒
        val second = m / 1000
        val hour = second / 3600
        val minute = second % 3600 / 60
        val ss = second % 60
        result = if (hour != 0) {
            String.format("%02d:%02d:%02d", hour, minute, ss)
        } else {
            String.format("%02d:%02d", minute, ss)
        }
        tv!!.text = result
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == UPDATE_PROGRESS) {

                // 获取当前时间
                val currentTime = videoView!!.currentPosition
                // 获取总时间
                val totalTime = videoView!!.duration - 100
                if (currentTime >= totalTime) {
                    videoView!!.pause()
                    videoView!!.seekTo(0)
                    seekbarProgress!!.progress = 0
                    btnController!!.setImageResource(R.drawable.dk_btn_play_style)
                    updateTextViewFormat(tvCurrentProgress, 0)
                    this.removeMessages(UPDATE_PROGRESS)
                } else {
                    seekbarProgress!!.max = totalTime
                    seekbarProgress!!.progress = currentTime
                    updateTextViewFormat(tvCurrentProgress, currentTime)
                    updateTextViewFormat(tvTotalProgress, totalTime)
                    sendEmptyMessageDelayed(UPDATE_PROGRESS, 100)
                }
            }
        }
    }

    fun onPause() {
        videoPos = videoView!!.currentPosition
        videoView!!.stopPlayback()
        mHandler.removeMessages(UPDATE_PROGRESS)
    }

    fun onResume() {
        videoView!!.seekTo(videoPos)
        videoView!!.resume()
    }

    fun setProgressBg(drawable: Drawable?) {
        seekbarProgress!!.progressDrawable = drawable
    }

    fun setVolumeBg(drawable: Drawable?) {
        seekbarVolume!!.progressDrawable = drawable
    }

    companion object {
        private const val UPDATE_PROGRESS = 1
    }
}