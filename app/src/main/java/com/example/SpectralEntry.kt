package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spectral_cache")
data class SpectralEntry(
    @PrimaryKey val surahIndex: Int,
    val rank: Int,
    val trace: Double,
    val determinant: Double,
    val sparsity: Double,
    val frobeniusNorm: Double,
    val spectralEntropy: Double,
    val conditionNumber: Double
)
