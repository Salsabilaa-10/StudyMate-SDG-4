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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassScreen(viewModel: StudyMateViewModel, onBack: () -> Unit) {
    val editingClass = viewModel.editingClass

    var className by remember { mutableStateOf(editingClass?.className ?: "") }
    var lecturerName by remember { mutableStateOf(editingClass?.lecturerName ?: "") }
    var venue by remember { mutableStateOf(editingClass?.venue ?: "") }
    
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    
    // Map old short names to full names for editing compatibility
    val initialDay = when(editingClass?.day) {
        "M" -> "Monday"
        "T" -> "Tuesday" // Defaulting to Tuesday if it was "T"
        "W" -> "Wednesday"
        "F" -> "Friday"
        "S" -> "Saturday"
        null -> "Monday"
        else -> editingClass.day
    }
    
    var selectedDay by remember { mutableStateOf(initialDay) }
    var startTime by remember { mutableStateOf(editingClass?.startTime ?: "08:00 AM") }
    var endTime by remember { mutableStateOf(editingClass?.endTime ?: "10:00 AM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(top = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "‹ Back to Home",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { 
                viewModel.editingClass = null
                onBack() 
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (editingClass != null) "Edit Class" else "Add Class",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Set up your class schedule",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        LabelText("CLASS NAME")
        OutlinedTextField(
            value = className,
            onValueChange = { className = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("e.g. Mobile Programming") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("LECTURER NAME")
        OutlinedTextField(
            value = lecturerName,
            onValueChange = { lecturerName = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("e.g. Dr. Norfadhilah") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("ROOM / VENUE")
        OutlinedTextField(
            value = venue,
            onValueChange = { venue = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("e.g. DK3, FTSM") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("DAY")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .clickable { selectedDay = day },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.take(1),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("TIME")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Text("→", color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (className.isNotEmpty()) {
                    viewModel.addClass(
                        ClassEntity(
                            id = editingClass?.id ?: 0,
                            className = className,
                            lecturerName = lecturerName,
                            venue = venue,
                            day = selectedDay,
                            startTime = startTime,
                            endTime = endTime,
                            colorTag = editingClass?.colorTag ?: 0
                        )
                    )
                    viewModel.editingClass = null
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("💾 Save Class", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { 
                viewModel.editingClass = null
                onBack() 
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Text("✕ Cancel", color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}
