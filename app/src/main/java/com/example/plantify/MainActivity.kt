package com.example.plantify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.plantify.ui.PlantifyApp
import com.example.plantify.ui.theme.PlantifyTheme

//perlu diingat! file utama hanya untuk navigasi!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantifyTheme {
                PlantifyApp()
            }
        }
    }
}
