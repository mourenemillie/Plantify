package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyLightGreen

@Composable
fun AddPlantScreen(
    onBackClick: () -> Unit = {},
    onUseAiSchedule: () -> Unit = {},
    onCustomizeManually: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(24.dp)
        ) {
            Text(
                text = "Step 1 of 2",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add New Plant",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            InputField(label = "Select plant", value = "Tomato (60-80 days)")
            Spacer(modifier = Modifier.height(16.dp))
            InputField(label = "Planting date", value = "April 20, 2026")
            Spacer(modifier = Modifier.height(16.dp))
            InputField(label = "Location / pot name", value = "", placeholder = "e.g. Balcony pot #1")

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = PlantifyLightGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Smart Care Advisor",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D674E),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Based on Tomato + your location weather (32°C, sunny), I recommend watering every day at 07:00, fertilizing every 14 days. Tap \"Use AI Schedule\" to apply.",
                            fontSize = 13.sp,
                            color = Color(0xFF0D674E),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onUseAiSchedule,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Use AI Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCustomizeManually,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Customize manually instead", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, placeholder: String = "") {
    Column {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = PlantifyLightGreen.copy(alpha = 0.3f),
                focusedContainerColor = PlantifyLightGreen.copy(alpha = 0.3f),
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = PlantifyMediumGreen
            ),
            readOnly = true
        )
    }
}
