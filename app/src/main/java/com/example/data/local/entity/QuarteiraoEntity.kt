package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quarteiroes",
    foreignKeys = [
        ForeignKey(
            entity = LocalidadeEntity::class,
            parentColumns = ["id"],
            childColumns = ["localidadeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["localidadeId"]),
        Index(value = ["localidadeId", "numero"], unique = true)
    ]
)
data class QuarteiraoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val localidadeId: Long,
    val rgId: Long,
    /**
     * O número/código do quarteirão DEVE ser tratado como texto.
     * Exemplo: "01", "01/1", "02-A"
     */
    val numero: String,
    val descricao: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING_SYNC"
)
