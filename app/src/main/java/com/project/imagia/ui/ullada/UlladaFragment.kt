package com.project.imagia.ui.ullada

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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs




class UlladaFragment : Fragment() ,SensorEventListener{

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var linearAccelerometer: Sensor
    private var _binding: FragmentUlladaBinding? = null

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


        val ulladaViewModel =
            ViewModelProvider(this).get(UlladaViewModel::class.java)

        _binding = FragmentUlladaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sensorManager
        val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val textView: TextView = binding.textHome
        ulladaViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun onSensorChanged(event: SensorEvent){

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

    private fun detectDoubleTap(x: Float, y: Float, z: Float) {
        val currentTime = System.currentTimeMillis()
        if (x > threshold || y > threshold || z > threshold) {
            if (currentTime - lastTapTime < timeWindow) {
                binding.doubleTapInfo.text = "Double Tap Detectat"
            } else {
                binding.doubleTapInfo.text = "Esperant un segon tap"
            }
            lastTapTime = currentTime
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("tapCounterX", tapCounterX)
        outState.putInt("tapCounterY", tapCounterY)
        outState.putInt("tapCounterZ", tapCounterZ)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tapCounterX = savedInstanceState.getInt("tapCounterX", 0)
        tapCounterY = savedInstanceState.getInt("tapCounterY", 0)
        tapCounterZ = savedInstanceState.getInt("tapCounterZ", 0)
    }
}