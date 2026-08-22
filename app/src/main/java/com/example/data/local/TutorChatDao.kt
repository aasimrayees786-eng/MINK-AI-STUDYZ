package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TutorMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorChatDao {
    @Query("SELECT * FROM tutor_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<TutorMessage>>

    @Query("SELECT * FROM tutor_messages WHERE chapterNoteId = :chapterId ORDER BY timestamp ASC")
    fun getMessagesForChapter(chapterId: Long): Flow<List<TutorMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: TutorMessage): Long

    @Query("DELETE FROM tutor_messages")
    suspend fun clearAllMessages()
}
