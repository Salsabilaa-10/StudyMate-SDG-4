package com.example.a207945_salsabilaa_izwan_lab3

import kotlinx.coroutines.flow.Flow

class AssignmentRepository(private val assignmentDao: AssignmentDao) {
    val allAssignments: Flow<List<AssignmentEntity>> = assignmentDao.getAll()
    val allExams: Flow<List<ExamEntity>> = assignmentDao.getAllExams()
    val allClasses: Flow<List<ClassEntity>> = assignmentDao.getAllClasses()
    val allFlashcards: Flow<List<FlashcardEntity>> = assignmentDao.getAllFlashcards()
    val allSessions: Flow<List<ChatSessionEntity>> = assignmentDao.getAllSessions()

    fun getMessagesForSession(sessionId: Int): Flow<List<ChatMessageEntity>> {
        return assignmentDao.getMessagesForSession(sessionId)
    }

    suspend fun createSession(title: String): Long {
        return assignmentDao.insertSession(ChatSessionEntity(title = title))
    }

    suspend fun updateSessionTimestamp(sessionId: Int) {
        assignmentDao.updateSessionTimestamp(sessionId, System.currentTimeMillis())
    }

    suspend fun deleteSession(sessionId: Int) {
        assignmentDao.deleteMessagesBySession(sessionId)
        assignmentDao.deleteSession(sessionId)
    }

    suspend fun insert(assignment: AssignmentEntity) {
        assignmentDao.insert(assignment)
    }

    suspend fun update(assignment: AssignmentEntity) {
        assignmentDao.update(assignment)
    }

    suspend fun delete(assignment: AssignmentEntity) {
        assignmentDao.delete(assignment)
    }

    suspend fun insertExam(exam: ExamEntity) {
        assignmentDao.insertExam(exam)
    }

    suspend fun updateExam(exam: ExamEntity) {
        assignmentDao.updateExam(exam)
    }

    suspend fun deleteExam(exam: ExamEntity) {
        assignmentDao.deleteExam(exam)
    }

    suspend fun insertClass(classEntity: ClassEntity) {
        assignmentDao.insertClass(classEntity)
    }

    suspend fun updateClass(classEntity: ClassEntity) {
        assignmentDao.updateClass(classEntity)
    }

    suspend fun deleteClass(classEntity: ClassEntity) {
        assignmentDao.deleteClass(classEntity)
    }

    suspend fun insertFlashcard(flashcard: FlashcardEntity) {
        assignmentDao.insertFlashcard(flashcard)
    }

    suspend fun deleteFlashcard(flashcard: FlashcardEntity) {
        assignmentDao.deleteFlashcard(flashcard)
    }

    suspend fun insertChatMessage(message: ChatMessageEntity) {
        assignmentDao.insertChatMessage(message)
    }

    suspend fun clearChatHistory() {
        assignmentDao.deleteAllChatMessages()
    }
}
