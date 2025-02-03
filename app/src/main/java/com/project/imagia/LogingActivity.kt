package com.project.imagia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.imagia.databinding.ActivityLogingBinding

class LogingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        0.also { binding.validatebtn.visibility = it }
    }
}