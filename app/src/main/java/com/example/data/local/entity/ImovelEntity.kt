package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "imoveis",
    foreignKeys = [
        ForeignKey(
            entity = QuarteiraoEntity::class,
            parentColumns = ["id"],
            childColumns = ["quarteiraoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["quarteiraoId"]),
        Index(value = ["rgId"])
    ]
)
data class ImovelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quarteiraoId: Long,
    val localidadeId: Long,
    val rgId: Long,
    val endereco: String,
    val numero: String, // String para suportar "123", "S/N", "12-A"
    val tipo: String = "Residencial", // Residencial, Comercial, Terreno Baldio, Ponto Estratégico, Outro
    val habitacoes: Int = 1,
    val habitantes: Int = 0,
    val caes: Int = 0,
    val gatos: Int = 0,
    val observacoes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING_SYNC" // PENDING_SYNC, SYNCED
)
