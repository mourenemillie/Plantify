package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.R
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyTheme

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // App Logo
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(10.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = PlantifyMediumGreen
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_leaf_logo),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.welcome_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A212E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.welcome_description),
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Feature List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                OnboardingFeature(
                    iconRes = R.drawable.ic_calendar,
                    iconBgColor = Color(0xFFE8F5E9),
                    iconTint = Color(0xFF4CAF50),
                    title = stringResource(R.string.feature_scheduling_title),
                    description = stringResource(R.string.feature_scheduling_desc)
                )

                OnboardingFeature(
                    iconRes = R.drawable.ic_book,
                    iconBgColor = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF2196F3),
                    title = stringResource(R.string.feature_guides_title),
                    description = stringResource(R.string.feature_guides_desc)
                )

                OnboardingFeature(
                    iconRes = R.drawable.ic_trending_up_chart,
                    iconBgColor = Color(0xFFFFF3E0),
                    iconTint = Color(0xFFFF9800),
                    title = stringResource(R.string.feature_progress_title),
                    description = stringResource(R.string.feature_progress_desc)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onTimeout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF50D67F) // Matching the bright green in the image
                )
            ) {
                Text(
                    text = stringResource(R.string.get_started),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun OnboardingFeature(
    iconRes: Int,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = iconBgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A212E)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    PlantifyTheme {
        SplashScreen(onTimeout = {})
    }
}