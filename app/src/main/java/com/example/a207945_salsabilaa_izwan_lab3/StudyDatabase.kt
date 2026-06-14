package com.example.a207945_salsabilaa_izwan_lab3

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AssignmentEntity::class, ExamEntity::class, ClassEntity::class, FlashcardEntity::class, ChatMessageEntity::class, ChatSessionEntity::class], version = 4, exportSchema = false)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao

    companion object {
        @Volatile
        private var INSTANCE: StudyDatabase? = null

        fun getDatabase(context: Context): StudyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "study_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
