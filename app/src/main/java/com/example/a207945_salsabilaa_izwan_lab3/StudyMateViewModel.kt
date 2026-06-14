package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UserData(
    val name: String,
    val matricNo: String,
    val faculty: String,
    val course: String
)

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val message: String,
    val image: String? = null
)

data class SharedFlashcard(
    val question: String = "",
    val answer: String = ""
)

data class SharedFlashcardDeck(
    val topic: String = "",
    val author: String = "",
    val authorMatric: String = "",
    val cards: List<SharedFlashcard> = emptyList(),
    val timestamp: Long = 0
)

class StudyMateViewModel(private val repository: AssignmentRepository) : ViewModel() {
    private val _userData = MutableStateFlow(
        UserData(
            name = "Salsabilaa",
            matricNo = "A207945",
            faculty = "FTSM, UKM",
            course = "Software Engineering"
        )
    )
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    // App Preferences
    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null means follow system
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setDarkMode(enabled: Boolean?) {
        _isDarkMode.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun updateUserData(name: String, matricNo: String, faculty: String, course: String) {
        _userData.value = UserData(name, matricNo, faculty, course)
    }

    // AI Chat State
    private val _currentSessionId = MutableStateFlow<Int?>(null)
    val currentSessionId: StateFlow<Int?> = _currentSessionId.asStateFlow()

    val chatSessions: StateFlow<List<ChatSessionEntity>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessage>> = _currentSessionId
        .flatMapLatest { sessionId ->
            if (sessionId == null) kotlinx.coroutines.flow.flowOf(emptyList())
            else repository.getMessagesForSession(sessionId)
        }
        .map { entities ->
            entities.map { ChatMessage(it.role, it.message, it.image) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    // Firebase Firestore
    private val db = FirebaseFirestore.getInstance()
    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading: StateFlow<Boolean> = _isCloudLoading.asStateFlow()

    private val _sharedDecks = MutableStateFlow<List<SharedFlashcardDeck>>(emptyList())
    val sharedDecks: StateFlow<List<SharedFlashcardDeck>> = _sharedDecks.asStateFlow()

    // GROQ API KEY
    private val apiKey = "YOUR_API_KEY_HERE"


    fun selectSession(sessionId: Int) {
        _currentSessionId.value = sessionId
    }

    fun startNewChat() {
        _currentSessionId.value = null
    }

    fun sendMessage(prompt: String, base64Image: String? = null) {
        if (prompt.isBlank() && base64Image == null) return

        viewModelScope.launch {
            // 1. Ensure we have a session
            var sessionId = _currentSessionId.value
            if (sessionId == null) {
                val title = if (prompt.length > 20) prompt.take(20) + "..." else prompt.ifBlank { "Image Chat" }
                sessionId = repository.createSession(title).toInt()
                _currentSessionId.value = sessionId
                
                // Add initial greeting for new sessions if needed, or just proceed
            }

            // 2. Save user message to database
            repository.insertChatMessage(
                ChatMessageEntity(sessionId = sessionId, role = "user", message = prompt, image = base64Image)
            )
            repository.updateSessionTimestamp(sessionId)
            
            _isAILoading.value = true

            try {
                // 3. Call AI API
                val responseText = if (base64Image != null) {
                    callGroqVisionApi(prompt, base64Image)
                } else {
                    callGroqApi(prompt)
                }

                // 4. Save assistant response to database
                repository.insertChatMessage(
                    ChatMessageEntity(sessionId = sessionId, role = "assistant", message = responseText)
                )
                repository.updateSessionTimestamp(sessionId)
            } catch (e: Exception) {
                e.printStackTrace()
                repository.insertChatMessage(
                    ChatMessageEntity(sessionId = sessionId, role = "assistant", message = "Error: ${e.localizedMessage}")
                )
            } finally {
                _isAILoading.value = false
            }
        }
    }

    private suspend fun callGroqApi(prompt: String): String = withContext(Dispatchers.IO) {
        val url = URL("https://api.groq.com/openai/v1/chat/completions")
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $apiKey")
        connection.doOutput = true

        val requestBody = JSONObject().apply {
            put("model", "llama-3.3-70b-versatile")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }

        connection.outputStream.use { it.write(requestBody.toString().toByteArray()) }

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = connection.inputStream.bufferedReader().readText()
            val jsonResponse = JSONObject(responseString)
            jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        } else {
            val errorString = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown Error"
            "Server Error ($responseCode): $errorString"
        }
    }

    private suspend fun callGroqVisionApi(prompt: String, base64Image: String): String = withContext(Dispatchers.IO) {
        val url = URL("https://api.groq.com/openai/v1/chat/completions")
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $apiKey")
        connection.doOutput = true

        val requestBody = JSONObject().apply {
            put("model", "llama-3.2-11b-vision-preview")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", prompt.ifBlank { "What is in this image?" })
                        })
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            })
                        })
                    })
                })
            })
        }

        connection.outputStream.use { it.write(requestBody.toString().toByteArray()) }

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = connection.inputStream.bufferedReader().readText()
            val jsonResponse = JSONObject(responseString)
            jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        } else {
            val errorString = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown Error"
            "Vision Error ($responseCode): $errorString"
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            _currentSessionId.value?.let { sessionId ->
                repository.deleteSession(sessionId)
                _currentSessionId.value = null
            }
        }
    }

    fun deleteSession(sessionId: Int) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                _currentSessionId.value = null
            }
        }
    }

    // Edit states
    var editingAssignment by mutableStateOf<AssignmentEntity?>(null)
    var editingExam by mutableStateOf<ExamEntity?>(null)
    var editingClass by mutableStateOf<ClassEntity?>(null)

    // Using Room
    val tasks: StateFlow<List<AssignmentEntity>> = repository.allAssignments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val exams: StateFlow<List<ExamEntity>> = repository.allExams.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val classes: StateFlow<List<ClassEntity>> = repository.allClasses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val flashcards: StateFlow<List<FlashcardEntity>> = repository.allFlashcards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTask(assignment: AssignmentEntity) {
        viewModelScope.launch {
            if (assignment.id == 0) {
                repository.insert(assignment)
            } else {
                repository.update(assignment)
            }
        }
    }

    fun deleteTask(assignment: AssignmentEntity) {
        viewModelScope.launch {
            repository.delete(assignment)
        }
    }

    fun addExam(exam: ExamEntity) {
        viewModelScope.launch {
            if (exam.id == 0) {
                repository.insertExam(exam)
            } else {
                repository.updateExam(exam)
            }
        }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    fun addClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            if (classEntity.id == 0) {
                repository.insertClass(classEntity)
            } else {
                repository.updateClass(classEntity)
            }
        }
    }

    fun deleteClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.deleteClass(classEntity)
        }
    }

    fun addFlashcard(flashcard: FlashcardEntity) {
        viewModelScope.launch {
            repository.insertFlashcard(flashcard)
        }
    }

    fun deleteFlashcard(flashcard: FlashcardEntity) {
        viewModelScope.launch {
            repository.deleteFlashcard(flashcard)
        }
    }

    fun generateFlashcards(topic: String, content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            _isAILoading.value = true
            try {
                val prompt = """
                    Based on the following notes, generate 3-5 high-quality flashcards.
                    Topic: $topic
                    Notes: $content
                    
                    Respond ONLY with a JSON array of objects, where each object has "question" and "answer" fields.
                    Example: [{"question": "What is Kotlin?", "answer": "A modern programming language."}]
                """.trimIndent()

                val response = callGroqApi(prompt)
                val jsonArray = JSONArray(response.substringAfter("[").substringBeforeLast("]") .let { "[$it]" })
                
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val q = obj.getString("question")
                    val a = obj.getString("answer")
                    repository.insertFlashcard(FlashcardEntity(topic = topic, question = q, answer = a))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAILoading.value = false
            }
        }
    }

    // Firebase Integration
    fun shareFlashcardsToCloud(topic: String, flashcards: List<FlashcardEntity>) {
        if (flashcards.isEmpty()) return
        
        _isCloudLoading.value = true
        val deck = SharedFlashcardDeck(
            topic = topic,
            author = _userData.value.name,
            authorMatric = _userData.value.matricNo,
            cards = flashcards.map { SharedFlashcard(it.question, it.answer) },
            timestamp = System.currentTimeMillis()
        )

        db.collection("shared_flashcards")
            .add(deck)
            .addOnSuccessListener {
                _isCloudLoading.value = false
                fetchSharedDecks()
            }
            .addOnFailureListener {
                _isCloudLoading.value = false
            }
    }

    fun fetchSharedDecks() {
        _isCloudLoading.value = true
        db.collection("shared_flashcards")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val decks = result.mapNotNull { it.toObject(SharedFlashcardDeck::class.java) }
                _sharedDecks.value = decks
                _isCloudLoading.value = false
            }
            .addOnFailureListener {
                _isCloudLoading.value = false
            }
    }

    fun downloadSharedDeck(deck: SharedFlashcardDeck) {
        viewModelScope.launch {
            deck.cards.forEach { card ->
                repository.insertFlashcard(
                    FlashcardEntity(topic = deck.topic, question = card.question, answer = card.answer)
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val db = StudyDatabase.getDatabase(application)
                return StudyMateViewModel(AssignmentRepository(db.assignmentDao())) as T
            }
        }
    }
}
