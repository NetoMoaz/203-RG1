package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.QuarteiraoEntity
import kotlinx.coroutines.flow.Flow

data class QuarteiraoWithSummary(
    val id: Long,
    val localidadeId: Long,
    val rgId: Long,
    val numero: String,
    val descricao: String,
    val syncStatus: String,
    val totalImoveis: Int,
    val totalHabitacoes: Int,
    val totalHabitantes: Int,
    val totalCaes: Int,
    val totalGatos: Int
)

@Dao
interface QuarteiraoDao {
    @Query("SELECT * FROM quarteiroes WHERE localidadeId = :localidadeId ORDER BY numero ASC")
    fun getQuarteiroesByLocalidade(localidadeId: Long): Flow<List<QuarteiraoEntity>>

    @Query("SELECT * FROM quarteiroes WHERE id = :id")
    suspend fun getQuarteiraoById(id: Long): QuarteiraoEntity?

    @Query("SELECT * FROM quarteiroes WHERE localidadeId = :localidadeId AND numero = :numero LIMIT 1")
    suspend fun getQuarteiraoByNumero(localidadeId: Long, numero: String): QuarteiraoEntity?

    @Query("SELECT * FROM quarteiroes WHERE id = :id")
    fun getQuarteiraoByIdFlow(id: Long): Flow<QuarteiraoEntity?>

    @Query("""
        SELECT 
            q.id,
            q.localidadeId,
            q.rgId,
            q.numero,
            q.descricao,
            q.syncStatus,
            COALESCE(COUNT(i.id), 0) AS totalImoveis,
            COALESCE(SUM(i.habitacoes), 0) AS totalHabitacoes,
            COALESCE(SUM(i.habitantes), 0) AS totalHabitantes,
            COALESCE(SUM(i.caes), 0) AS totalCaes,
            COALESCE(SUM(i.gatos), 0) AS totalGatos
        FROM quarteiroes q
        LEFT JOIN imoveis i ON i.quarteiraoId = q.id
        WHERE q.localidadeId = :localidadeId
        GROUP BY q.id
        ORDER BY q.numero ASC
    """)
    fun getQuarteiroesWithSummaryByLocalidade(localidadeId: Long): Flow<List<QuarteiraoWithSummary>>

    @Query("""
        SELECT 
            q.id,
            q.localidadeId,
            q.rgId,
            q.numero,
            q.descricao,
            q.syncStatus,
            COALESCE(COUNT(i.id), 0) AS totalImoveis,
            COALESCE(SUM(i.habitacoes), 0) AS totalHabitacoes,
            COALESCE(SUM(i.habitantes), 0) AS totalHabitantes,
            COALESCE(SUM(i.caes), 0) AS totalCaes,
            COALESCE(SUM(i.gatos), 0) AS totalGatos
        FROM quarteiroes q
        LEFT JOIN imoveis i ON i.quarteiraoId = q.id
        WHERE q.id = :quarteiraoId
        GROUP BY q.id
    """)
    fun getQuarteiraoSummary(quarteiraoId: Long): Flow<QuarteiraoWithSummary?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuarteirao(quarteirao: QuarteiraoEntity): Long

    @Update
    suspend fun updateQuarteirao(quarteirao: QuarteiraoEntity)

    @Delete
    suspend fun deleteQuarteirao(quarteirao: QuarteiraoEntity)

    @Query("SELECT COUNT(*) FROM quarteiroes")
    fun getTotalQuarteiroesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM quarteiroes WHERE syncStatus = 'PENDING_SYNC'")
    fun getPendingQuarteiroesCount(): Flow<Int>
}
