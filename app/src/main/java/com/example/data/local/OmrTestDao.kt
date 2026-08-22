package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.OmrTestRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface OmrTestDao {
    @Query("SELECT * FROM omr_test_records ORDER BY timestamp DESC")
    fun getAllTestRecordsFlow(): Flow<List<OmrTestRecord>>

    @Query("SELECT * FROM omr_test_records WHERE id = :testId LIMIT 1")
    suspend fun getTestRecordById(testId: String): OmrTestRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestRecord(record: OmrTestRecord)

    @Update
    suspend fun updateTestRecord(record: OmrTestRecord)

    @Query("DELETE FROM omr_test_records WHERE id = :testId")
    suspend fun deleteTestRecord(testId: String)

    @Query("SELECT COUNT(*) FROM omr_test_records")
    suspend fun getCompletedTestCount(): Int
}
