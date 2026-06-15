package com.example.plantify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.R
import com.example.plantify.ui.theme.*
import com.example.plantify.ui.viewmodel.ProfileViewModel
import androidx.appcompat.app.AppCompatDelegate
import com.example.plantify.ui.theme.ThemeManager
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    onNotificationClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Mengambil data pengguna dari database
    val userName by viewModel.userName.collectAsState()
    val memberSince by viewModel.memberSince.collectAsState()
    val plantsCount by viewModel.plantsCount.collectAsState()
    val daysActive by viewModel.daysActive.collectAsState()
    val tasksDone by viewModel.tasksDone.collectAsState()
    val isDarkMode by ThemeManager.isDarkMode.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        ProfileHeader(userName, memberSince)

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp)
        ) {
            StatsCard(plantsCount, daysActive, tasksDone)

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = stringResource(R.string.section_settings))
            SettingsSection(
                isDarkMode = isDarkMode, 
                onDarkModeChange = { ThemeManager.setDarkMode(context, it) },
                onLanguageClick = { showLanguageDialog = true },
                onNotificationClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = stringResource(R.string.section_about))
            AboutSection(
                onHelpClick = { showHelpDialog = true },
                onAboutClick = { showAboutDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PromoCard()

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.choose_language), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            ThemeManager.setLanguage(context, "en")
                            showLanguageDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🇬🇧  " + stringResource(R.string.english), fontSize = 16.sp)
                    }
                    TextButton(
                        onClick = {
                            ThemeManager.setLanguage(context, "id")
                            showLanguageDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🇮🇩  " + stringResource(R.string.indonesian), fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(stringResource(R.string.menu_help)) },
            text = { Text("Contact support at support@plantify.com") },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.menu_about)) },
            text = { Text("Plantify version 1.0.0\nYour personal urban farming assistant.") },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ProfileHeader(userName: String, memberSince: String) {
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
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = memberSince,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun StatsCard(plantsCount: Int, daysActive: Int, tasksDone: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = plantsCount.toString(), label = stringResource(R.string.stat_plants))
            VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outlineVariant)
            StatItem(value = daysActive.toString(), label = stringResource(R.string.stat_days_active))
            VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outlineVariant)
            StatItem(value = tasksDone.toString(), label = stringResource(R.string.stat_tasks_done))
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun SettingsSection(
    isDarkMode: Boolean, 
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            ProfileMenuItem(
                iconRes = R.drawable.ic_notifications,
                title = stringResource(R.string.menu_notifications),
                iconBgColor = PlantifyIconBlueBg,
                iconTint = PlantifyIconBlue,
                onClick = onNotificationClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            ProfileMenuItem(
                iconRes = R.drawable.ic_language,
                title = stringResource(R.string.menu_language),
                value = stringResource(R.string.menu_language_value),
                iconBgColor = PlantifyIconOrangeBg,
                iconTint = PlantifyIconOrange,
                onClick = onLanguageClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            DarkModeToggle(isDarkMode, onDarkModeChange)
        }
    }
}

@Composable
private fun AboutSection(
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            ProfileMenuItem(
                iconRes = R.drawable.ic_help,
                title = stringResource(R.string.menu_help),
                iconBgColor = PlantifyIconGreenBg,
                iconTint = PlantifyIconGreen,
                onClick = onHelpClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            ProfileMenuItem(
                iconRes = R.drawable.ic_info,
                title = stringResource(R.string.menu_about),
                value = stringResource(R.string.menu_about_version),
                iconBgColor = PlantifyPurpleBg,
                iconTint = PlantifyPurple,
                onClick = onAboutClick
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
    value: String = "",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 14.sp
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DarkModeToggle(isDark: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PlantifyIndigoBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dark_mode),
                contentDescription = null,
                tint = PlantifyIndigo,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.menu_dark_mode),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDark,
            onCheckedChange = onCheckedChange,
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
        color = MaterialTheme.colorScheme.surface,
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