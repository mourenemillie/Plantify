package com.example.plantify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.ui.PlantifyApp
import com.example.plantify.ui.theme.PlantifyTheme
import com.example.plantify.ui.viewmodel.ProfileViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val profileViewModel: ProfileViewModel = viewModel()
            val isDarkMode by profileViewModel.isDarkMode.collectAsState()

            PlantifyTheme(darkTheme = isDarkMode) {
                PlantifyApp(profileViewModel = profileViewModel)
            }
        }
    }
}
