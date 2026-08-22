package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChapterNote
import com.example.data.model.TutorMessage

@Database(
    entities = [
        ChapterNote::class,
        TutorMessage::class,
        com.example.data.model.SnapSolutionRecord::class,
        com.example.data.model.OmrTestRecord::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chapterNoteDao(): ChapterNoteDao
    abstract fun tutorChatDao(): TutorChatDao
    abstract fun snapSolutionDao(): SnapSolutionDao
    abstract fun omrTestDao(): OmrTestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chapter_ai_tutor.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
