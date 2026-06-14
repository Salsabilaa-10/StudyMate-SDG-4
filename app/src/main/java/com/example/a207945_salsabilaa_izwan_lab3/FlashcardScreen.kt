package com.example.a207945_salsabilaa_izwan_lab3

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun FlashcardScreen(viewModel: StudyMateViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val flashcards by viewModel.flashcards.collectAsState()
    val sharedDecks by viewModel.sharedDecks.collectAsState()
    val isCloudLoading by viewModel.isCloudLoading.collectAsState()
    
    // Topic filtering
    var selectedTopic by remember { mutableStateOf("All Topics") }
    val uniqueTopics = remember(flashcards) {
        listOf("All Topics") + flashcards.map { it.topic }.distinct()
    }

    // UI States for generation
    var topicInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("") }
    var isProcessingCamera by remember { mutableStateOf(false) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            isProcessingCamera = true
            val image = com.google.mlkit.vision.common.InputImage.fromBitmap(bitmap, 0)
            val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS)
            
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    contentInput = visionText.text
                    if (topicInput.isBlank()) topicInput = "Scanned Notes"
                    isProcessingCamera = false
                }
                .addOnFailureListener {
                    isProcessingCamera = false
                }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFileName = uri.path?.split("/")?.last() ?: "Selected File"
        }
    }

    // Filter cards based on selected topic
    val filteredFlashcards = remember(flashcards, selectedTopic) {
        if (selectedTopic == "All Topics") {
            flashcards.ifEmpty {
                listOf(
                    FlashcardEntity(1, "Data Structures", "What is the time complexity of Binary Search?", "O(log n)"),
                    FlashcardEntity(2, "Data Structures", "What is a Stack?", "LIFO data structure"),
                    FlashcardEntity(3, "Data Structures", "What is a Queue?", "FIFO structure"),
                )
            }
        } else {
            flashcards.filter { it.topic == selectedTopic }
        }
    }

    var currentIndex by remember(selectedTopic) { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(value = false) }

    // Ensure currentIndex stays within bounds if the list changes
    if (currentIndex >= filteredFlashcards.size) {
        currentIndex = (filteredFlashcards.size - 1).coerceAtLeast(0)
    }
    
    val currentFlashcard = filteredFlashcards.getOrNull(currentIndex) ?: filteredFlashcards.first()
    val progress = (currentIndex + 1).toFloat() / filteredFlashcards.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(top = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "‹ Back",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBack() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "AI Flashcard",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Topic Selector (Filter)
        Text("SELECT TOPIC TO REVISE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uniqueTopics.forEach { topic ->
                val isSelected = topic == selectedTopic
                Surface(
                    onClick = { selectedTopic = topic },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = topic,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${if (selectedTopic == "All Topics") currentFlashcard.topic else selectedTopic} — ${filteredFlashcards.size} cards",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Card ${currentIndex + 1} of ${filteredFlashcards.size}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${(progress * 100).toInt()}% done",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clickable { isFlipped = !isFlipped },
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (isFlipped) "ANSWER" else "QUESTION",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isFlipped) currentFlashcard.answer else currentFlashcard.question,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (isFlipped) "Tap card to see question 👆" else "Tap card to reveal answer 👆",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentIndex > 0) { currentIndex--; isFlipped = false } },
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            ) { Text("‹", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }

            OutlinedButton(
                onClick = { isFlipped = !isFlipped },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text("🔄 Flip card", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }

            IconButton(
                onClick = { if (currentIndex < (filteredFlashcards.size - 1)) { currentIndex++; isFlipped = false } },
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            ) { Text("›", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("CREATE NEW CARDS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = topicInput,
            onValueChange = { topicInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Topic Name") },
            placeholder = { Text("e.g. Kotlin Basics") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contentInput,
            onValueChange = { contentInput = it },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            label = { Text("Paste Content / Notes") },
            placeholder = { Text("Paste your study notes here...") },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { 
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                enabled = !isProcessingCamera
            ) { 
                if (isProcessingCamera) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                } else {
                    Text("📸 Camera", fontSize = 13.sp)
                }
            }
            
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.secondary)
            ) { 
                Text(if (selectedFileName.isEmpty()) "📁 File" else "✅ File", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = { 
                if (topicInput.isNotEmpty() && contentInput.isNotEmpty()) {
                    viewModel.generateFlashcards(topicInput, contentInput)
                    topicInput = ""
                    contentInput = ""
                    selectedFileName = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = topicInput.isNotEmpty() && contentInput.isNotEmpty()
        ) { Text("✨ Generate Flashcards", fontSize = 14.sp) }

        if (selectedFileName.isNotEmpty()) {
            Text(
                text = "Attached: $selectedFileName",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("ALL CARDS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        
        if (filteredFlashcards.isNotEmpty()) {
            Button(
                onClick = { viewModel.shareFlashcardsToCloud(selectedTopic.ifBlank { filteredFlashcards.first().topic }, filteredFlashcards) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                enabled = !isCloudLoading
            ) {
                if (isCloudLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("☁️ Share ${if(selectedTopic == "All Topics") "All" else selectedTopic} to Cloud", fontSize = 14.sp)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                filteredFlashcards.forEachIndexed { index, card ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) { Text((index + 1).toString(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(card.question, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(card.answer, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (selectedTopic == "All Topics") {
                                Text("#${card.topic}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteFlashcard(card) }) {
                            Text("🗑️", fontSize = 14.sp)
                        }
                    }
                    if (index < filteredFlashcards.size - 1) {
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("COMMUNITY SHARED DECKS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))

        LaunchedEffect(Unit) {
            viewModel.fetchSharedDecks()
        }

        if (sharedDecks.isEmpty()) {
            Text("No shared decks found yet.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            sharedDecks.forEach { deck ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(deck.topic, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Shared by ${deck.author} (${deck.authorMatric})", fontSize = 12.sp)
                            Text("${deck.cards.size} cards", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Button(
                            onClick = { viewModel.downloadSharedDeck(deck) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Import", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}
