package com.zhandos.numberplatecv.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.zhandos.numberplatecv.databinding.FragmentCameraBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CameraViewModel>()
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            cameraState()
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            cameraProvider.unbindAll()

            val camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun hideAll() {
        binding.previewBanner.visibility = View.GONE
        binding.cameraPreview.visibility = View.GONE
        binding.cameraBanner.visibility = View.GONE
        binding.imageCapturedPreview.visibility = View.GONE
    }

    private fun cameraState() {
        hideAll()
        binding.cameraPreview.visibility = View.VISIBLE
        binding.cameraBanner.visibility = View.VISIBLE

        binding.capture.setOnClickListener {
            viewModel.takePhoto(requireContext(), imageCapture)
            previewState()
        }
    }

    private fun previewState() {
        hideAll()
        binding.imageCapturedPreview.visibility = View.VISIBLE
        binding.previewBanner.visibility = View.VISIBLE

        viewModel.location.observe(viewLifecycleOwner) { location ->
            binding.imageCapturedPreview.load(location)
            binding.done.setOnClickListener {
                findNavController().navigate(
                    CameraFragmentDirections.actionCameraFragmentToNumberPlateFragment(
                        location
                    )
                )
            }
        }
        binding.repeat.setOnClickListener { cameraState() }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val FILE_NAME = "COMPUTER-VISION"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS =
            mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}