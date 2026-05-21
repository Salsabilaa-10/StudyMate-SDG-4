package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamScreen(viewModel: StudyMateViewModel, onBack: () -> Unit) {
    val editingExam = viewModel.editingExam

    var subject by remember { mutableStateOf(editingExam?.subject ?: "") }
    var examType by remember { mutableStateOf(editingExam?.type ?: "Final") }
    var examDate by remember { mutableStateOf(editingExam?.date ?: "") }
    var startTime by remember { mutableStateOf(editingExam?.startTime ?: "09:00 AM") }
    var endTime by remember { mutableStateOf(editingExam?.endTime ?: "11:00 AM") }
    var venue by remember { mutableStateOf(editingExam?.venue ?: "") }
    var setReminder by remember { mutableStateOf(true) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
                        examDate = formatter.format(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                viewModel.editingExam = null
                onBack() 
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (editingExam != null) "Edit Exam" else "Add Exam",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (editingExam != null) "Update your exam details" else "Track your upcoming exams",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        LabelText("SUBJECT")
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("e.g. Mobile Programming - TM2213") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("EXAM TYPE")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExamTypeChip("Final", "📝", examType == "Final") { examType = "Final" }
            ExamTypeChip("Mid-term", "📜", examType == "Mid-term") { examType = "Mid-term" }
            ExamTypeChip("Quiz", "🧪", examType == "Quiz") { examType = "Quiz" }
            ExamTypeChip("Lab", "📂", examType == "Lab") { examType = "Lab" }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("EXAM DATE")
        OutlinedTextField(
            value = examDate,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Pick a date") },
            readOnly = true,
            enabled = false,
            leadingIcon = { Text("📅", modifier = Modifier.padding(start = 12.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("EXAM TIME")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Manual typing for start time
            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("e.g. 09:00 AM") },
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Text("→", color = MaterialTheme.colorScheme.onSurfaceVariant)
            // Manual typing for end time
            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("e.g. 11:00 AM") },
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LabelText("VENUE")
        OutlinedTextField(
            value = venue,
            onValueChange = { venue = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("e.g. Dewan Canselor, UKM") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔔", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Set reminder", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Switch(checked = setReminder, onCheckedChange = { setReminder = it })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (subject.isNotEmpty()) {
                    viewModel.addExam(
                        ExamEntity(
                            id = editingExam?.id ?: 0,
                            subject = subject,
                            type = examType,
                            date = examDate,
                            startTime = startTime,
                            endTime = endTime,
                            venue = venue
                        )
                    )
                    viewModel.editingExam = null
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("💾 Save Exam", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { 
                viewModel.editingExam = null
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

@Composable
fun ExamTypeChip(label: String, emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.height(48.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp).fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                label, 
                fontSize = 12.sp, 
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
