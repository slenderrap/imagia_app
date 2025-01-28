package com.project.imagia.ui.ullada

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.imagia.databinding.FragmentUlladaBinding
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs


typealias LumaListener = (luma: Double) -> Unit

class UlladaFragment : Fragment() ,SensorEventListener{

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var linearAccelerometer: Sensor
    private var _binding: FragmentUlladaBinding? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var tapCounterX = 0
    private var tapCounterY = 0
    private var tapCounterZ = 0
    private val threshold = 2.0
    private val timeWindow = 300L
    private var lastTapTime = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }


        val ulladaViewModel =
            ViewModelProvider(this).get(UlladaViewModel::class.java)

        _binding = FragmentUlladaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Set up the listeners for take photo and video capture buttons
        _binding!!.button.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()
        val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val textView: TextView = binding.textHome
        ulladaViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return binding.root
    }
    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }


        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this.requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
     override fun onSensorChanged(event: SensorEvent){

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

//    private fun detectDoubleTap(x: Float, y: Float, z: Float) {
//        val currentTime = System.currentTimeMillis()
//        if (x > threshold || y > threshold || z > threshold) {
//            if (currentTime - lastTapTime < timeWindow) {
//                binding.doubleTapInfo.text = "Double Tap Detectat"
//            } else {
//                binding.doubleTapInfo.text = "Esperant un segon tap"
//            }
//            lastTapTime = currentTime
//        }
//    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("tapCounterX", tapCounterX)
        outState.putInt("tapCounterY", tapCounterY)
        outState.putInt("tapCounterZ", tapCounterZ)
    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
            }.toTypedArray()
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Obtén el proveedor de cámara
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Selecciona la cámara trasera como predeterminada
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                // Unbind all use cases antes de volver a adjuntar
                cameraProvider.unbindAll()


                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(requireActivity().windowManager.defaultDisplay.rotation)
                    .build()
                // Vincula el caso de vista previa
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.previewFinder.surfaceProvider)
                }
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Error al vincular la cámara", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }




}