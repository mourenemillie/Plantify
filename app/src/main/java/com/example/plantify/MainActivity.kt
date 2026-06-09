package com.example.plantify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.plantify.ui.PlantifyApp
import com.example.plantify.ui.theme.PlantifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PlantifyTheme {
                PlantifyApp()
            }
        }
    }
}
