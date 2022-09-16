package com.zerox.camera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.zerox.camera.databinding.DialogResultBinding
import com.zerox.camera.util.HandelResult.setCameraZResult


class ResultDialog : DialogFragment() {
    private val args: ResultDialogArgs by navArgs()
    private val video: Uri? get() = Uri.parse(args.videoPath)
    private var _binding: DialogResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            sendIcon.setOnClickListener {
                this@ResultDialog.dismissAllowingStateLoss()
                setCameraZResult()
            }
            closeIv.setOnClickListener { this@ResultDialog.dismissAllowingStateLoss() }
//            videoView.setOnClickListener {
//                if (videoView.isPlaying) videoView.pause()
//                else videoView.start()
//            }
            videoView.setVideoURI(video)
            val mediaController = MediaController(view.context)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            videoView.start()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}