package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    liquidGlassBg: Brush, 
    liquidGlassBorder: Brush, 
    viewModel: StudyMateViewModel // Added ViewModel parameter
) {
    val userData by viewModel.userData.collectAsState() // Observe ViewModel state

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    var searchQuery by remember { mutableStateOf("") }
    var displayedMessage by remember { mutableStateOf("") }

    val examAccent = MaterialTheme.colorScheme.primary
    val classAccent = MaterialTheme.colorScheme.secondary
    val assignmentAccent = MaterialTheme.colorScheme.tertiary

    Column {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 60.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Welcome back,", color = textSecondary, fontSize = 14.sp)
                // Use data from ViewModel
                Text(text = userData.name, color = textPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.padding(end = 12.dp).clip(RoundedCornerShape(15.dp))
                        .background(liquidGlassBg).border(1.2.dp, liquidGlassBorder, RoundedCornerShape(15.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(text = "☀️ 🌙", fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userData.name.take(1), // Use initial from ViewModel
                        color = MaterialTheme.colorScheme.primary, 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Date
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Thursday, Apr 9", color = textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(liquidGlassBg)
                    .border(1.2.dp, liquidGlassBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(text = "Today", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Calendar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val days = listOf("M" to "6", "T" to "7", "W" to "8", "T" to "9", "F" to "10", "S" to "11", "S" to "12")
            days.forEach { (day, date) ->
                Box(
                    modifier = Modifier.width(42.dp).height(65.dp).clip(RoundedCornerShape(12.dp))
                        .background(liquidGlassBg).border(1.2.dp, liquidGlassBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = day, color = textSecondary, fontSize = 12.sp)
                        Text(text = date, color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Info Cards
        InfoCardItem("Upcoming Exam", "No exam yet", examAccent, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f), liquidGlassBorder)
        InfoCardItem("Today's Clases", "No clases today", classAccent, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f), liquidGlassBorder)
        InfoCardItem("Upcoming Assignments", "No assignments yet", assignmentAccent, MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.75f), liquidGlassBorder)

        Text(
            text = "Quick Actions", color = textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        // Action Boxes
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            ActionBox("📚", "AI Flashcard", MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f), liquidGlassBorder, Modifier.weight(1f).padding(end = 8.dp))
            ActionBox("📝", "Add Exam", MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f), liquidGlassBorder, Modifier.weight(1f).padding(start = 8.dp))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            ActionBox("🏫", "Add Class", MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f), liquidGlassBorder, Modifier.weight(1f).padding(end = 8.dp))
            ActionBox("📌", "Add Task", MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f), liquidGlassBorder, Modifier.weight(1f).padding(start = 8.dp))
        }

        // Search Box
        Box(
            modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(27.dp))
                .background(liquidGlassBg).border(1.2.dp, liquidGlassBorder, RoundedCornerShape(27.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(text = "Ask me anything...", color = textSecondary, fontSize = 15.sp)
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(color = textPrimary, fontSize = 15.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (searchQuery.isNotEmpty()) {
                                displayedMessage = searchQuery
                                searchQuery = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "➤", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                }
            }
        }
        if (displayedMessage.isNotEmpty()) {
            Text(
                text = "Recent query: $displayedMessage",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, start = 12.dp)
            )
        }
    }
}

@Composable
fun InfoCardItem(title: String, subtitle: String, accent: Color, bg: Color, border: Brush) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(1.2.dp, border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(6.dp).height(45.dp).clip(RoundedCornerShape(3.dp)).background(accent))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = title, color = accent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
            if (expanded) {
                Text(
                    text = "More details for $title: No pending tasks.",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ActionBox(emoji: String, label: String, bg: Color, border: Brush, modifier: Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(1.2.dp, border)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = emoji, fontSize = 26.sp)
            Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
