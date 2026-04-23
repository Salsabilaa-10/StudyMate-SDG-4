package com.example.a207945_salsabilaa_izwan_lab3

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
}
