package com.zerox.camera.util

import android.graphics.PointF
import com.otaliastudios.cameraview.*

class CameraHandler(private val LOG: CameraLogger,private val cameraResult:CameraResult) : CameraListener() {
    override fun onCameraOpened(options: CameraOptions) {
        LOG.w("onCameraOpened $options")
    }
    override fun onVideoTaken(result: VideoResult) {
        super.onVideoTaken(result)
        val videoPath = result.file.path
        LOG.w("onVideoTaken $videoPath")
        cameraResult.onVideoTaken(videoPath=videoPath)

//        findNavController().navigate(
//            R.id.openResultDialog,
//            args = Bundle().apply { putString("videoPath", videoPath) })

    }

    override fun onCameraError(exception: CameraException) {
        super.onCameraError(exception)
        LOG.e("Got CameraException #" + exception.reason)
    }

    override fun onVideoRecordingStart() {
        super.onVideoRecordingStart()
        LOG.w("onVideoRecordingStart!")
    }

    override fun onVideoRecordingEnd() {
        super.onVideoRecordingEnd()
        LOG.w("Video taken. Processing...")
    }

    override fun onExposureCorrectionChanged(
        newValue: Float,
        bounds: FloatArray,
        fingers: Array<PointF>?
    ) {
        super.onExposureCorrectionChanged(newValue, bounds, fingers)
        LOG.w("Exposure correction:$newValue")

    }

    override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
        super.onZoomChanged(newValue, bounds, fingers)
        LOG.w("Zoom:$newValue")
    }
}