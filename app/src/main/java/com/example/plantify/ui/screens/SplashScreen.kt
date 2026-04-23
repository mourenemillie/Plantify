package com.example.plantify.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.ui.theme.PlantifyDarkGreen
import com.example.plantify.ui.theme.PlantifyLightGreen
import com.example.plantify.ui.theme.PlantifyMediumGreen

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PlantifyMediumGreen, PlantifyDarkGreen)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            LeafIcon(
                modifier = Modifier.size(56.dp),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to Plantify",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PlantifyDarkGreen,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your personal urban farming assistant.\nTrack your plants, get smart reminders,\nand grow your own food at home.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        FeatureItem(
            icon = Icons.Default.DateRange,
            iconBackgroundColor = PlantifyMediumGreen,
            title = "Smart Scheduling",
            description = "Automated watering and fertilizing\nreminders"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureItem(
            icon = Icons.Default.MenuBook,
            iconBackgroundColor = Color(0xFF4A90D9),
            title = "Plant Guides",
            description = "Simple care instructions for beginners"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureItem(
            icon = Icons.Default.TrendingUp,
            iconBackgroundColor = Color(0xFFE57373),
            title = "Track Progress",
            description = "Monitor your plants' growth journey"
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onTimeout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PlantifyLightGreen
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlantifyDarkGreen
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun LeafIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        rotate(degrees = -45f, pivot = Offset(width / 2, height / 2)) {
            val leafPath = Path().apply {
                // Leaf body shape
                moveTo(width * 0.5f, height * 0.1f)
                cubicTo(
                    width * 0.85f, height * 0.1f,
                    width * 0.95f, height * 0.5f,
                    width * 0.5f, height * 0.9f
                )
                cubicTo(
                    width * 0.05f, height * 0.5f,
                    width * 0.15f, height * 0.1f,
                    width * 0.5f, height * 0.1f
                )
                close()
            }
            drawPath(leafPath, color = color, style = Fill)
        }

        val stemPath = Path().apply {
            moveTo(width * 0.25f, height * 0.75f)
            cubicTo(
                width * 0.35f, height * 0.6f,
                width * 0.45f, height * 0.5f,
                width * 0.7f, height * 0.3f
            )
        }
        drawPath(
            stemPath,
            color = if (color == Color.White) PlantifyMediumGreen else Color.White,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = width * 0.05f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}
