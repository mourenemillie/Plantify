package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlertsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notifications",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
            )
            Text(
                text = "Mark all read",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00B894)
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            item {
                NotificationItem(
                    title = "Time to water your plants",
                    desc = "Cherry Tomato and Spinach need watering",
                    time = "2 min ago",
                    icon = Icons.Default.Notifications,
                    iconBgColor = Color(0xFFE3F2FD),
                    isUnread = true
                )
            }

            item {
                NotificationItem(
                    title = "Fertilizing reminder",
                    desc = "Red Chili needs fertilizing today",
                    time = "1 hour ago",
                    icon = Icons.Default.ArrowForward,
                    iconBgColor = Color(0xFFE8F5E9),
                    isUnread = true
                )
            }

            item {
                NotificationItem(
                    title = "Plant milestone reached!",
                    desc = "Your Spinach has grown for 1 week",
                    time = "3 hours ago",
                    icon = Icons.Default.Star,
                    iconBgColor = Color(0xFFFFF3E0),
                    isUnread = false
                )
            }

            item {
                NotificationItem(
                    title = "Watering completed",
                    desc = "Great job! You watered Basil",
                    time = "Yesterday",
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = Color(0xFFE3F2FD),
                    isUnread = false
                )
            }

            item {
                NotificationItem(
                    title = "Plant care tip",
                    desc = "Tomatoes grow better with consistent watering",
                    time = "2 days ago",
                    icon = Icons.Default.Info,
                    iconBgColor = Color(0xFFF5F5F5),
                    isUnread = false
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    desc: String,
    time: String,
    icon: ImageVector,
    iconBgColor: Color,
    isUnread: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kotak Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2D3436),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2D3436)
                        )
                    )
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00B894), shape = CircleShape)
                        )
                    }
                }
                Text(
                    text = desc,
                    color = Color(0xFF636E72),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = time,
                    color = Color(0xFFB2BEC3),
                    fontSize = 12.sp
                )
            }
        }
    }
}