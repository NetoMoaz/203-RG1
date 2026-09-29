package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ImovelEntity
import kotlinx.coroutines.flow.Flow

data class GlobalTerritoryStats(
    val totalRgs: Int,
    val totalQuarteiroes: Int,
    val totalImoveis: Int,
    val totalHabitantes: Int,
    val totalHabitacoes: Int,
    val totalCaes: Int,
    val totalGatos: Int,
    val totalPendentes: Int
)

@Dao
interface ImovelDao {
    @Query("SELECT * FROM imoveis WHERE quarteiraoId = :quarteiraoId ORDER BY endereco ASC, numero ASC")
    fun getImoveisByQuarteirao(quarteiraoId: Long): Flow<List<ImovelEntity>>

    @Query("SELECT * FROM imoveis WHERE id = :id")
    suspend fun getImovelById(id: Long): ImovelEntity?

    @Query("SELECT * FROM imoveis WHERE id = :id")
    fun getImovelByIdFlow(id: Long): Flow<ImovelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImovel(imovel: ImovelEntity): Long

    @Update
    suspend fun updateImovel(imovel: ImovelEntity)

    @Delete
    suspend fun deleteImovel(imovel: ImovelEntity)

    @Query("SELECT COUNT(*) FROM imoveis")
    fun getTotalImoveisCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(habitantes), 0) FROM imoveis")
    fun getTotalHabitantesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM imoveis WHERE syncStatus = 'PENDING_SYNC'")
    fun getPendingImoveisCount(): Flow<Int>

    @Query("""
        SELECT 
            (SELECT COUNT(*) FROM rgs) AS totalRgs,
            (SELECT COUNT(*) FROM quarteiroes) AS totalQuarteiroes,
            (SELECT COUNT(*) FROM imoveis) AS totalImoveis,
            (SELECT COALESCE(SUM(habitantes), 0) FROM imoveis) AS totalHabitantes,
            (SELECT COALESCE(SUM(habitacoes), 0) FROM imoveis) AS totalHabitacoes,
            (SELECT COALESCE(SUM(caes), 0) FROM imoveis) AS totalCaes,
            (SELECT COALESCE(SUM(gatos), 0) FROM imoveis) AS totalGatos,
            ((SELECT COUNT(*) FROM rgs WHERE syncStatus = 'PENDING_SYNC') +
             (SELECT COUNT(*) FROM localidades WHERE syncStatus = 'PENDING_SYNC') +
             (SELECT COUNT(*) FROM quarteiroes WHERE syncStatus = 'PENDING_SYNC') +
             (SELECT COUNT(*) FROM imoveis WHERE syncStatus = 'PENDING_SYNC')) AS totalPendentes
    """)
    fun getGlobalStats(): Flow<GlobalTerritoryStats>
}
