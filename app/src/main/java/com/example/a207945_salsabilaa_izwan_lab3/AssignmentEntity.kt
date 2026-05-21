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

