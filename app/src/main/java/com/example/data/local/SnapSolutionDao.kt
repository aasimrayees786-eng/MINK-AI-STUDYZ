package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SnapSolutionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapSolutionDao {
    @Query("SELECT * FROM snap_solutions ORDER BY timestamp DESC")
    fun getAllSolutions(): Flow<List<SnapSolutionRecord>>

    @Query("SELECT * FROM snap_solutions WHERE id = :id")
    fun getSolutionById(id: Long): Flow<SnapSolutionRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolution(record: SnapSolutionRecord): Long

    @Delete
    suspend fun deleteSolution(record: SnapSolutionRecord)

    @Query("DELETE FROM snap_solutions")
    suspend fun clearAllSolutions()
}
