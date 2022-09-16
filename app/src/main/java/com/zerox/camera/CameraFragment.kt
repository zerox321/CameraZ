package com.zerox.camera

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.zerox.camera.databinding.FragmentCameraBinding
import com.zerox.camera.util.CameraHandler
import com.zerox.camera.util.CameraResult
import com.zerox.camera.util.Permission
import com.zerox.camera.util.Permission.launchTaskWithPermissions
import java.io.File
import java.util.concurrent.TimeUnit


class CameraFragment : Fragment(), CameraResult {
    private val LOG = CameraLogger.create("CameraZ")

    private val videoDuration = 60
    private val setSize = TimeUnit.SECONDS.toMillis(1L)
    private val max = TimeUnit.SECONDS.toMillis(videoDuration.toLong())

    private val timer: CountDownTimer = object : CountDownTimer(max, setSize) {
        override fun onTick(counter: Long) {
            val remain = TimeUnit.MILLISECONDS.toSeconds(counter)
            binding.captureVideo.setImageResource(R.drawable.ic__record_stop)
            binding.timerTv.visibility = View.VISIBLE
            binding.timerTv.text = "00:$remain"
        }

        override fun onFinish() {
            binding.captureVideo.setImageResource(R.drawable.ic_record_start)
            binding.timerTv.visibility = View.GONE
            camera.stopVideo()
        }

    }
    private val handler = CameraHandler(LOG = LOG, cameraResult = this)
    private fun message(content: String) {
        LOG.w(content)
    }


    private fun captureVideo() {
        if (camera.mode == Mode.PICTURE) return run {
            message("Can't record HQ videos while in PICTURE mode.")
        }

        if (camera.isTakingPicture || camera.isTakingVideo) {

            timer.onFinish()
            timer.cancel()

            return
        }

        timer.start()

        message("Recording for 60 seconds...")
        camera.takeVideo(File(requireContext().filesDir, "video.mp4"))
    }


    private fun toggleCamera() {
        if (camera.isTakingPicture || camera.isTakingVideo) return
        when (camera.toggleFacing()) {
            Facing.BACK -> message("Switched to back camera!")
            Facing.FRONT -> message("Switched to front camera!")
        }
    }


    private val camera: CameraView get() = binding.camera

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleCamera.setOnClickListener { toggleCamera() }
        binding.captureVideo.setOnClickListener { captureVideo() }

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        camera.setLifecycleOwner(this.viewLifecycleOwner)
        camera.addCameraListener(handler)

        context?.launchTaskWithPermissions(
            task = { openCamera() },
            permissions = Permission.cameraPermissions,
            permissionsRequest = { cameraPermissionsRequest.launch(Permission.cameraPermissions) })
    }

    private val cameraPermissionsRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.all { permission -> permission.value }
            if (isGranted) openCamera()
        }

    private fun openCamera() {
        if (!camera.isOpened) {
            camera.open()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onVideoTaken(videoPath: String) {
        findNavController().navigate(
            R.id.openResultDialog,
            args = Bundle().apply { putString("videoPath", videoPath) })
    }
}