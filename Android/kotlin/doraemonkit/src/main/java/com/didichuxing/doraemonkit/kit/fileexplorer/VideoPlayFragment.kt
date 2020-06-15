package com.didichuxing.doraemonkit.kit.fileexplorer

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.videoview.DKVideoView
import java.io.File

class VideoPlayFragment : BaseFragment() {

    private var mVideoView: DKVideoView? = null

    override fun onRequestLayout(): Int = R.layout.dk_fragment_video_play

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVideoView = findViewById(R.id.video_view)
        mVideoView?.register(requireActivity())
        getVideoPath()?.let { mVideoView?.setVideoPath(it) }
    }

    private fun getVideoPath(): String? = (arguments?.getSerializable(BundleKey.FILE_KEY) as? File)?.path

    override fun onPause() {
        super.onPause()
        mVideoView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mVideoView?.onResume()
    }
}
