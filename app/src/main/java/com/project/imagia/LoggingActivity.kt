package com.project.imagia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.imagia.databinding.ActivityLoggingBinding

class LoggingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoggingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggingBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}