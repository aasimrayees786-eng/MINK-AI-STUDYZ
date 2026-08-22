package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChapterNote
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterNoteDao {
    @Query("SELECT * FROM chapter_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<ChapterNote>>

    @Query("SELECT * FROM chapter_notes WHERE id = :id")
    fun getNoteById(id: Long): Flow<ChapterNote?>

    @Query("SELECT * FROM chapter_notes WHERE id = :id")
    suspend fun getNoteByIdDirect(id: Long): ChapterNote?

    @Query("SELECT * FROM chapter_notes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteNotes(): Flow<List<ChapterNote>>

    @Query("SELECT * FROM chapter_notes WHERE subject = :subject ORDER BY createdAt DESC")
    fun getNotesBySubject(subject: String): Flow<List<ChapterNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ChapterNote): Long

    @Update
    suspend fun updateNote(note: ChapterNote)

    @Delete
    suspend fun deleteNote(note: ChapterNote)

    @Query("UPDATE chapter_notes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE chapter_notes SET masteryScore = :score, quizCorrectCount = :lastScore, quizTotalQuestions = :total WHERE id = :id")
    suspend fun updateQuizMastery(id: Long, score: Int, lastScore: Int, total: Int)
}
