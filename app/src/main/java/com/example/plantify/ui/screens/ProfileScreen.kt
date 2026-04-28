package com.example.plantify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.R
import com.example.plantify.ui.theme.*

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        ProfileHeader()

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp)
        ) {
            StatsCard()

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = stringResource(R.string.section_settings))
            SettingsSection()

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = stringResource(R.string.section_about))
            AboutSection()

            Spacer(modifier = Modifier.height(24.dp))

            PromoCard()

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                color = PlantifyMediumGreen,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(R.string.profile_name),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = stringResource(R.string.member_since),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun StatsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = "3", label = stringResource(R.string.stat_plants))
            VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFF1F1F1))
            StatItem(value = "15", label = stringResource(R.string.stat_days_active))
            VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFF1F1F1))
            StatItem(value = "24", label = stringResource(R.string.stat_tasks_done))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun SettingsSection() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column {
            ProfileMenuItem(
                iconRes = R.drawable.ic_notifications,
                title = stringResource(R.string.menu_notifications),
                iconBgColor = PlantifyIconBlueBg,
                iconTint = PlantifyIconBlue
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F1F1))
            ProfileMenuItem(
                iconRes = R.drawable.ic_language,
                title = stringResource(R.string.menu_language),
                value = stringResource(R.string.menu_language_value),
                iconBgColor = PlantifyIconOrangeBg,
                iconTint = PlantifyIconOrange
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F1F1))
            DarkModeToggle()
        }
    }
}

@Composable
private fun AboutSection() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column {
            ProfileMenuItem(
                iconRes = R.drawable.ic_help,
                title = stringResource(R.string.menu_help),
                iconBgColor = PlantifyIconGreenBg,
                iconTint = PlantifyIconGreen
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F1F1))
            ProfileMenuItem(
                iconRes = R.drawable.ic_info,
                title = stringResource(R.string.menu_about),
                value = stringResource(R.string.menu_about_version),
                iconBgColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFF9C27B0)
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    iconRes: Int,
    title: String,
    iconBgColor: Color,
    iconTint: Color,
    value: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 14.sp
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DarkModeToggle() {
    var isDark by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE8EAF6), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dark_mode),
                contentDescription = null,
                tint = Color(0xFF3F51B5),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.menu_dark_mode),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDark,
            onCheckedChange = { isDark = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PlantifyMediumGreen
            )
        )
    }
}

@Composable
private fun PromoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8F5E9).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color(0xFFC8E6C9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eco),
                    contentDescription = null,
                    tint = PlantifyMediumGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.promo_title),
                    fontWeight = FontWeight.Bold,
                    color = PlantifyDarkGreen,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.promo_description),
                style = MaterialTheme.typography.bodyMedium,
                color = PlantifyMediumGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.promo_learn_more),
                fontWeight = FontWeight.Bold,
                color = PlantifyMediumGreen,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    PlantifyTheme {
        ProfileScreen()
    }
}