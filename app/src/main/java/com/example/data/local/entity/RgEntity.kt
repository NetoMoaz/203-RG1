package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rgs",
    indices = [
        Index(value = ["codigo"], unique = true)
    ]
)
data class RgEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigo: String,
    val municipio: String = "",
    val descricao: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING_SYNC" // PENDING_SYNC, SYNCED
)
