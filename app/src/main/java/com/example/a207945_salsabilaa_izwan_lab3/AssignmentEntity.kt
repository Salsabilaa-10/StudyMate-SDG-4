package com.example.a207945_salsabilaa_izwan_lab3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val dueDate: String,
    val dueTime: String
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val type: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val venue: String
)

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val lecturerName: String,
    val venue: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val colorTag: Int
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val question: String,
    val answer: String
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val role: String,
    val message: String,
    val image: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)


