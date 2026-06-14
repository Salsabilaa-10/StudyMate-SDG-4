package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    liquidGlassBg: Brush, 
    liquidGlassBorder: Brush,
    viewModel: StudyMateViewModel // Added ViewModel parameter
) {
    val userData by viewModel.userData.collectAsState() // Observe ViewModel state
    val isDarkModePref by viewModel.isDarkMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val systemInDark = isSystemInDarkTheme()
    
    // Effective dark mode value
    val darkThemeEnabled = isDarkModePref ?: systemInDark

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    // Edit Profile Dialog State
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userData.name) }
    var editMatric by remember { mutableStateOf(userData.matricNo) }
    var editFaculty by remember { mutableStateOf(userData.faculty) }
    var editCourse by remember { mutableStateOf(userData.course) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") })
                    OutlinedTextField(value = editMatric, onValueChange = { editMatric = it }, label = { Text("Matric No") })
                    OutlinedTextField(value = editFaculty, onValueChange = { editFaculty = it }, label = { Text("Faculty") })
                    OutlinedTextField(value = editCourse, onValueChange = { editCourse = it }, label = { Text("Course") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateUserData(editName, editMatric, editFaculty, editCourse)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.padding(top = 60.dp)) {
        Text(text = "Profile", color = textPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        // Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            border = BorderStroke(1.2.dp, liquidGlassBorder)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userData.name.take(1), // Use initial from ViewModel
                        color = MaterialTheme.colorScheme.onPrimary, 
                        fontSize = 36.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(text = userData.name, color = textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Text(text = "${userData.course} · ${userData.faculty.split(",").last().trim()}", color = textSecondary, fontSize = 14.sp)
            }
        }

        Text(text = "MY INFO", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            border = BorderStroke(1.2.dp, liquidGlassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Full name", userData.name)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                InfoRow("Matric no.", userData.matricNo)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                InfoRow("Faculty", userData.faculty)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                InfoRow("Course", userData.course)
            }
        }

        Text(text = "SETTINGS", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
            border = BorderStroke(1.2.dp, liquidGlassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingToggleRow("Dark mode", darkThemeEnabled) { viewModel.setDarkMode(it) }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                SettingToggleRow("Notifications", notificationsEnabled) { viewModel.setNotificationsEnabled(it) }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            editName = userData.name
                            editMatric = userData.matricNo
                            editFaculty = userData.faculty
                            editCourse = userData.course
                            showEditDialog = true 
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Edit profile", color = textPrimary, fontSize = 15.sp)
                    Text(text = "›", color = textSecondary, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
