package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.RgEntity
import kotlinx.coroutines.flow.Flow

data class RgWithCounts(
    val id: Long,
    val codigo: String,
    val municipio: String,
    val descricao: String,
    val localidadesCount: Int,
    val quarteiroesCount: Int,
    val imoveisCount: Int,
    val syncStatus: String
)

@Dao
interface RgDao {
    @Query("SELECT * FROM rgs ORDER BY codigo ASC")
    fun getAllRgs(): Flow<List<RgEntity>>

    @Query("SELECT * FROM rgs WHERE id = :id")
    suspend fun getRgById(id: Long): RgEntity?

    @Query("SELECT * FROM rgs WHERE codigo = :codigo LIMIT 1")
    suspend fun getRgByCodigo(codigo: String): RgEntity?

    @Query("SELECT * FROM rgs WHERE id = :id")
    fun getRgByIdFlow(id: Long): Flow<RgEntity?>

    @Query("""
        SELECT 
            r.id,
            r.codigo,
            r.municipio,
            r.descricao,
            r.syncStatus,
            (SELECT COUNT(*) FROM localidades l WHERE l.rgId = r.id) AS localidadesCount,
            (SELECT COUNT(*) FROM quarteiroes q WHERE q.rgId = r.id) AS quarteiroesCount,
            (SELECT COUNT(*) FROM imoveis i WHERE i.rgId = r.id) AS imoveisCount
        FROM rgs r
        ORDER BY r.codigo ASC
    """)
    fun getRgsWithCounts(): Flow<List<RgWithCounts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRg(rg: RgEntity): Long

    @Update
    suspend fun updateRg(rg: RgEntity)

    @Delete
    suspend fun deleteRg(rg: RgEntity)

    @Query("SELECT COUNT(*) FROM rgs")
    fun getRgCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM rgs WHERE syncStatus = 'PENDING_SYNC'")
    fun getPendingRgCount(): Flow<Int>
}
