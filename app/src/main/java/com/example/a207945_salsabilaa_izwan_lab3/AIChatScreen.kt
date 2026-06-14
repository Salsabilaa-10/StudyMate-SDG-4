package com.example.a207945_salsabilaa_izwan_lab3

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun AIChatScreen(
    viewModel: StudyMateViewModel,
    onBack: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val isLoading by viewModel.isAILoading.collectAsState()
    
    var inputText by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val context = LocalContext.current

    val glassBg = Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)))
    val glassBorder = Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            selectedImageBase64 = uriToBase64(context, it)
        }
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Improved Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "AI Study",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            // Button Group
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // New Chat Button (Icon only to save space)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .clickable { showClearConfirm = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("New", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Text("＋", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    }
                }

                if (chatMessages.isNotEmpty()) {
                    // Flashcards Button (Icon only if space is really tight, but let's try compact row)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                            .clickable { 
                                val allContent = chatMessages.joinToString("\n") { "${it.role}: ${it.message}" }
                                viewModel.generateFlashcards("AI Chat Summary", allContent)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Cards", color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Text("⚡", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Confirmation Dialog
        if (showClearConfirm) {
            AlertDialog(
                onDismissRequest = { showClearConfirm = false },
                title = { Text("Start New Chat?") },
                text = { Text("This will start a fresh conversation.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.startNewChat()
                        showClearConfirm = false
                    }) {
                        Text("New Chat", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Chat Messages
        if (currentSessionId == null && chatMessages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👋", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Start a new conversation!", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(chatMessages) { message ->
                    ChatBubble(message)
                }
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        // Selected Image Preview
        selectedImageUri?.let {
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                val bitmap = remember(it) {
                    val inputStream = context.contentResolver.openInputStream(it)
                    BitmapFactory.decodeStream(inputStream)
                }
                bitmap?.let { b ->
                    Image(
                        bitmap = b.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                        .clickable { 
                            selectedImageUri = null
                            selectedImageBase64 = null
                        },
                    shape = CircleShape,
                    color = Color.Red,
                    contentColor = Color.White
                ) {
                    Text("×", modifier = Modifier.padding(horizontal = 6.dp), fontSize = 16.sp)
                }
            }
        }

        // Input Area - Added more bottom padding to avoid NavBar overlap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 120.dp) // Adjusted to be above the NavBar
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(glassBg)
                .border(1.2.dp, glassBorder, RoundedCornerShape(27.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach icon - improved visibility
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("📎", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                }
                
                Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    if (inputText.isEmpty()) {
                        Text(text = "Ask me anything...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(enabled = (inputText.isNotBlank() || selectedImageBase64 != null) && !isLoading) {
                            viewModel.sendMessage(inputText, selectedImageBase64)
                            inputText = ""
                            selectedImageBase64 = null
                            selectedImageUri = null
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "➤", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                message.image?.let { base64 ->
                    val bitmap = remember(base64) {
                        val decodedString = Base64.decode(base64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    }
                    bitmap?.let { b ->
                        Image(
                            bitmap = b.asImageBitmap(),
                            contentDescription = "Message Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Text(
                    text = message.message,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )
            }
        }
    }
}

fun uriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        // Resize bitmap to avoid sending too much data
        val scaledBitmap = if (bitmap.width > 800 || bitmap.height > 800) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            if (bitmap.width > bitmap.height) {
                Bitmap.createScaledBitmap(bitmap, 800, (800 / ratio).toInt(), true)
            } else {
                Bitmap.createScaledBitmap(bitmap, (800 * ratio).toInt(), 800, true)
            }
        } else bitmap
        
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val bytes = outputStream.toByteArray()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
