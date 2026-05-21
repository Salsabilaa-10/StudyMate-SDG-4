package com.example.a207945_salsabilaa_izwan_lab3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserData(
    val name: String,
    val matricNo: String,
    val faculty: String,
    val course: String
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

    // Edit states
    var editingAssignment by mutableStateOf<AssignmentEntity?>(null)
    var editingExam by mutableStateOf<ExamEntity?>(null)

    // Using Room instead of memory
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

