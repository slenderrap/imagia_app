package com.project.imagia

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.project.imagia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val ullada = binding.navView.menu.getItem(0)
        val historial = binding.navView.menu.getItem(1)
        val compte = binding.navView.menu.getItem(2)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", "Default")
        val token = sharedPreferences.getInt("token", 0)
        if(nombre.equals("Default") && token==0) {
            intent

        }
        else{
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    ullada.itemId,
                    historial.itemId,
                    compte.itemId,
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

    }
}