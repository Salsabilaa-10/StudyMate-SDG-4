package com.example.a207945_salsabilaa_izwan_lab3

import kotlinx.coroutines.flow.Flow

class AssignmentRepository(private val assignmentDao: AssignmentDao) {
    val allAssignments: Flow<List<AssignmentEntity>> = assignmentDao.getAll()
    val allExams: Flow<List<ExamEntity>> = assignmentDao.getAllExams()

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
}
