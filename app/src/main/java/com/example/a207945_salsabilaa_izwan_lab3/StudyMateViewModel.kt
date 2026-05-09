package com.example.a207945_salsabilaa_izwan_lab3

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Task(
    val title: String,
    val subject: String,
    val dueDate: String,
    val dueTime: String
)

data class Exam(
    val subject: String,
    val type: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val venue: String
)

data class UserData(
    val name: String,
    val matricNo: String,
    val faculty: String,
    val course: String
)

class StudyMateViewModel : ViewModel() {
    private val _userData = MutableStateFlow(
        UserData(
            name = "Salsabilaa",
            matricNo = "A207945",
            faculty = "FTSM, UKM",
            course = "Software Engineering"
        )
    )
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams.asStateFlow()

    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task
    }

    fun addExam(exam: Exam) {
        _exams.value = _exams.value + exam
    }
}
