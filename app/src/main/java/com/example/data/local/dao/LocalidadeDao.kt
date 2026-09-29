package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.LocalidadeEntity
import kotlinx.coroutines.flow.Flow

data class LocalidadeWithCounts(
    val id: Long,
    val rgId: Long,
    val nome: String,
    val tipo: String,
    val quarteiroesCount: Int,
    val imoveisCount: Int,
    val syncStatus: String
)

@Dao
interface LocalidadeDao {
    @Query("SELECT * FROM localidades WHERE rgId = :rgId ORDER BY nome ASC")
    fun getLocalidadesByRg(rgId: Long): Flow<List<LocalidadeEntity>>

    @Query("SELECT * FROM localidades WHERE id = :id")
    suspend fun getLocalidadeById(id: Long): LocalidadeEntity?

    @Query("SELECT * FROM localidades WHERE rgId = :rgId AND LOWER(nome) = LOWER(:nome) LIMIT 1")
    suspend fun getLocalidadeByNome(rgId: Long, nome: String): LocalidadeEntity?

    @Query("SELECT * FROM localidades WHERE id = :id")
    fun getLocalidadeByIdFlow(id: Long): Flow<LocalidadeEntity?>

    @Query("""
        SELECT 
            l.id,
            l.rgId,
            l.nome,
            l.tipo,
            l.syncStatus,
            (SELECT COUNT(*) FROM quarteiroes q WHERE q.localidadeId = l.id) AS quarteiroesCount,
            (SELECT COUNT(*) FROM imoveis i WHERE i.localidadeId = l.id) AS imoveisCount
        FROM localidades l
        WHERE l.rgId = :rgId
        ORDER BY l.nome ASC
    """)
    fun getLocalidadesWithCountsByRg(rgId: Long): Flow<List<LocalidadeWithCounts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalidade(localidade: LocalidadeEntity): Long

    @Update
    suspend fun updateLocalidade(localidade: LocalidadeEntity)

    @Delete
    suspend fun deleteLocalidade(localidade: LocalidadeEntity)

    @Query("SELECT COUNT(*) FROM localidades")
    fun getTotalLocalidadesCount(): Flow<Int>
}
