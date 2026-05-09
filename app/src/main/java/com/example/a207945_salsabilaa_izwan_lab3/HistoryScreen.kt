package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen(
    liquidGlassBg: Brush, 
    liquidGlassBorder: Brush,
    viewModel: StudyMateViewModel // Added ViewModel parameter
) {
    val tasks by viewModel.tasks.collectAsState() // Observe tasks
    val exams by viewModel.exams.collectAsState() // Observe exams
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    var selectedFilter by remember { mutableStateOf("All") }

    Column(modifier = Modifier.padding(top = 60.dp)) {
        Text(text = "History", color = textPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "Your recent study activity", color = textSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 24.dp))

        // Stats Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(tasks.size.toString(), "Tasks", Modifier.width(100.dp), liquidGlassBorder)
            StatCard(exams.size.toString(), "Exams", Modifier.width(100.dp), liquidGlassBorder)
            StatCard("5", "AI Chat", Modifier.width(100.dp), liquidGlassBorder)
            StatCard("12", "Flashcards", Modifier.width(100.dp), liquidGlassBorder)
            StatCard("0", "Classes", Modifier.width(100.dp), liquidGlassBorder)
        }

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "AI Flashcard", "AI Chat", "Exam", "Class", "Task").forEach { filter ->
                FilterPill(filter, isSelected = selectedFilter == filter, onClick = { selectedFilter = filter }, liquidGlassBorder)
            }
        }

        // History Items List Container - One single card for all items
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
            border = BorderStroke(1.dp, liquidGlassBorder)
        ) {
            Column {
                val combinedHistory = remember(tasks, exams, selectedFilter) {
                    val list = mutableListOf<HistoryItem>()
                    
                    // Add Tasks
                    if (selectedFilter == "All" || selectedFilter == "Task") {
                        tasks.forEach { list.add(HistoryItem(it.title, "${it.dueDate} · ${it.dueTime}", "Task")) }
                    }
                    
                    // Add Exams
                    if (selectedFilter == "All" || selectedFilter == "Exam") {
                        exams.forEach { list.add(HistoryItem(it.subject, "${it.date} · Exam added", "Exam")) }
                    }

                    // Mock data for other categories
                    if (selectedFilter == "All" || selectedFilter == "AI Flashcard") {
                        list.add(HistoryItem("Data Structures", "Today · 20 cards generated", "AI Flashcard"))
                    }
                    if (selectedFilter == "All" || selectedFilter == "AI Chat") {
                        list.add(HistoryItem("Explain Binary Search", "Yesterday · AI Chat", "AI Chat"))
                    }
                    
                    list.reversed() 
                }

                if (combinedHistory.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No items found for $selectedFilter", color = textSecondary)
                    }
                } else {
                    combinedHistory.forEachIndexed { index, item ->
                        val badgeBg = when (item.type) {
                            "Task" -> MaterialTheme.colorScheme.primaryContainer
                            "Exam" -> MaterialTheme.colorScheme.tertiaryContainer
                            "AI Flashcard" -> MaterialTheme.colorScheme.primaryContainer
                            "AI Chat" -> MaterialTheme.colorScheme.secondaryContainer
                            "Class" -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        val emoji = when (item.type) {
                            "Task" -> "📌"
                            "Exam" -> "📝"
                            "AI Flashcard" -> "📚"
                            "AI Chat" -> "💬"
                            "Class" -> "🏫"
                            else -> "📄"
                        }

                        HistoryItemRow(
                            title = item.title, 
                            subtitle = item.subtitle, 
                            type = item.type, 
                            badgeBg = badgeBg, 
                            emoji = emoji,
                            showDivider = index < combinedHistory.size - 1
                        )
                    }
                }
            }
        }
    }
}

data class HistoryItem(
    val title: String,
    val subtitle: String,
    val type: String
)

@Composable
fun HistoryItemRow(title: String, subtitle: String, type: String, badgeBg: Color, emoji: String, showDivider: Boolean) {
    Column {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(badgeBg.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeBg.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = type, 
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier, border: Brush) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, border)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FilterPill(label: String, isSelected: Boolean, onClick: () -> Unit, border: Brush) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
