package com.example.a207945_salsabilaa_izwan_lab3

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assignment: AssignmentEntity)

    @Update
    suspend fun update(assignment: AssignmentEntity)

    @Delete
    suspend fun delete(assignment: AssignmentEntity)

    @Query("SELECT * FROM assignments ORDER BY id DESC")
    fun getAll(): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    @Query("SELECT * FROM exams ORDER BY id DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    // Classes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity)

    @Update
    suspend fun updateClass(classEntity: ClassEntity)

    @Delete
    suspend fun deleteClass(classEntity: ClassEntity)

    @Query("SELECT * FROM classes ORDER BY id DESC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    // Flashcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)

    @Query("SELECT * FROM flashcards ORDER BY id DESC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    // Chat Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Query("SELECT * FROM chat_sessions ORDER BY lastUpdated DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("UPDATE chat_sessions SET lastUpdated = :timestamp WHERE id = :sessionId")
    suspend fun updateSessionTimestamp(sessionId: Int, timestamp: Long)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Int)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesBySession(sessionId: Int)

    // Chat History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Int): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllChatMessages()
}

