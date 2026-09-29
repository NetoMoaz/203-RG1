package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "localidades",
    foreignKeys = [
        ForeignKey(
            entity = RgEntity::class,
            parentColumns = ["id"],
            childColumns = ["rgId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["rgId"]),
        Index(value = ["rgId", "nome"], unique = true)
    ]
)
data class LocalidadeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rgId: Long,
    val nome: String,
    val tipo: String = "Bairro", // Bairro, Localidade, Distrito, Povoado
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING_SYNC"
)
