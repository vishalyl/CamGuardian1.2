package com.example.camguardian12

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import java.io.File



class CameraDevicesFragment : Fragment() {

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var previewSize: Size
    private lateinit var cameraId: String
    private lateinit var btnStartRecording: Button
    private lateinit var btnStopRecording: Button
    private lateinit var chronometer: Chronometer

    private val REQUEST_PERMISSION_CODE = 101
    private val REQUEST_PERMISSIONS_CODE = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera_devices, container, false)
        val textureView: TextureView = view.findViewById(R.id.textureView)
        btnStartRecording = view.findViewById(R.id.btnStartRecording)
        btnStopRecording = view.findViewById(R.id.btnStopRecording)
        chronometer = view.findViewById(R.id.chronometer)

        btnStartRecording.setOnClickListener { startRecording() }
        btnStopRecording.setOnClickListener { stopRecording() }


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUEST_PERMISSIONS_CODE,
                REQUEST_PERMISSION_CODE
            )
        }

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                setupCamera(width, height)
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }
        }

        return view
    }

    private fun setupCamera(width: Int, height: Int) {
        val cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        for (cameraId in cameraManager.cameraIdList) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    as StreamConfigurationMap

            if (map != null) {
                previewSize = map.getOutputSizes(SurfaceTexture::class.java)[0]
                this.cameraId = cameraId
            }
        }
    }

    private fun openCamera() {
        val cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        startPreview()
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        camera.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        camera.close()
                    }

                }, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPreview() {
        val surfaceTexture = view?.findViewById<TextureView>(R.id.textureView)?.surfaceTexture!!
        surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)
        val previewSurface = Surface(surfaceTexture)

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(previewSurface)

        cameraDevice.createCaptureSession(mutableListOf(previewSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    // Handle failure
                }
            }, null)
    }

    // Add the startRecording method
    private fun startRecording() {
        // Initialize and configure MediaRecorder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaRecorder = MediaRecorder(requireContext())
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            // Additional settings like output format, encoding, etc. can be added here

            val recordingsDirectory = File(Environment.getExternalStorageDirectory(), "recordings")
            recordingsDirectory.mkdirs()
            val file = File(recordingsDirectory, "${System.currentTimeMillis()}.mp4")
            setOutputFile(file.absolutePath)

            prepare()
            start()
        }

        // Start chronometer
        chronometer.start()

        // Vibrate device
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)

        // Update button states
        btnStartRecording.isEnabled = false
        btnStopRecording.isEnabled = true
    }

    // Add the stopRecording method
    private fun stopRecording() {
        // Stop and release MediaRecorder
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }

        // Stop chronometer
        chronometer.stop()

        // Vibrate device
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)

        // Update button states
        btnStartRecording.isEnabled = true
        btnStopRecording.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
    }
}
