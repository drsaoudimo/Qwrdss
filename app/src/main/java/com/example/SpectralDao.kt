package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SpectralDao {
    @Query("SELECT * FROM spectral_cache WHERE surahIndex = :index")
    suspend fun getEntry(index: Int): SpectralEntry?

    @Query("SELECT * FROM spectral_cache")
    suspend fun getAll(): List<SpectralEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: SpectralEntry)
}
