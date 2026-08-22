package com.vastavik.computer.ui.screens.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vastavik.computer.ui.theme.VastavikColors
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)
    val isNeo by viewModel.isNeoBrutalish.collectAsState(initial = false)
    var notificationsEnabled by remember { mutableStateOf(true) }
    var fontScale by remember { mutableStateOf(1f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("profile") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Appearance section header
            Text("Appearance", fontWeight=FontWeight.Bold, fontSize=12.sp, color=VastavikColors.LightPrimary, modifier=Modifier.padding(start=4.dp, bottom=8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DarkMode, contentDescription = null, tint = VastavikColors.LightPrimary)
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) { Text("Dark Mode", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground) }
                        Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) }, colors = SwitchDefaults.colors(checkedTrackColor = VastavikColors.LightPrimary))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha=0.2f))
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Palette, contentDescription = null, tint = VastavikColors.LightPrimary)
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) { Text("NeoBrutalish", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground); Text("Thick borders & hard shadows", fontSize=11.sp, color=MaterialTheme.colorScheme.onSurfaceVariant) }
                        Switch(checked = isNeo, onCheckedChange = { viewModel.setNeoBrutalish(it) }, colors = SwitchDefaults.colors(checkedTrackColor = VastavikColors.LightPrimary))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha=0.2f))
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.TextFields, contentDescription = null, tint = VastavikColors.LightPrimary)
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) { Text("Font Scale", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground) }
                        Slider(value = fontScale, onValueChange = { fontScale = it }, valueRange = 0.8f..1.4f, modifier=Modifier.width(120.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Notifications", fontWeight=FontWeight.Bold, fontSize=12.sp, color=VastavikColors.LightPrimary, modifier=Modifier.padding(start=4.dp, bottom=8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = VastavikColors.LightPrimary)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) { Text("Notifications", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground) }
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it }, colors = SwitchDefaults.colors(checkedTrackColor = VastavikColors.LightPrimary))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("General", fontWeight=FontWeight.Bold, fontSize=12.sp, color=VastavikColors.LightPrimary, modifier=Modifier.padding(start=4.dp, bottom=8.dp))
            Card(modifier = Modifier.fillMaxWidth().clickable{ onNavigate("notifications") }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = VastavikColors.LightPrimary)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) { Text("Notification History", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground) }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth().clickable{ onNavigate("app_update") }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SystemUpdate, contentDescription = null, tint = VastavikColors.LightPrimary)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) { Text("App Update", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground); Text("Check for updates", fontSize=12.sp, color=MaterialTheme.colorScheme.onSurfaceVariant) }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth().clickable { }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = VastavikColors.LightPrimary)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) { Text("About", fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground); Text("Version 1.0.0", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
