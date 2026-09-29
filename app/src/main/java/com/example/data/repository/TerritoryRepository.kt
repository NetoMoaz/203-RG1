package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.dao.GlobalTerritoryStats
import com.example.data.local.dao.LocalidadeWithCounts
import com.example.data.local.dao.QuarteiraoWithSummary
import com.example.data.local.dao.RgWithCounts
import com.example.data.local.entity.ImovelEntity
import com.example.data.local.entity.LocalidadeEntity
import com.example.data.local.entity.QuarteiraoEntity
import com.example.data.local.entity.RgEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TerritoryRepository(private val database: AppDatabase) {

    private val rgDao = database.rgDao()
    private val localidadeDao = database.localidadeDao()
    private val quarteiraoDao = database.quarteiraoDao()
    private val imovelDao = database.imovelDao()

    // --- Global Stats ---
    val globalStats: Flow<GlobalTerritoryStats> = imovelDao.getGlobalStats()

    // --- RG Operations ---
    fun getRgsWithCounts(): Flow<List<RgWithCounts>> = rgDao.getRgsWithCounts()
    fun getRgByIdFlow(id: Long): Flow<RgEntity?> = rgDao.getRgByIdFlow(id)

    suspend fun getRgById(id: Long): RgEntity? = withContext(Dispatchers.IO) {
        rgDao.getRgById(id)
    }

    suspend fun isRgCodigoExists(codigo: String, excludeId: Long = 0L): Boolean = withContext(Dispatchers.IO) {
        val existing = rgDao.getRgByCodigo(codigo.trim())
        existing != null && existing.id != excludeId
    }

    suspend fun saveRg(rg: RgEntity): Long = withContext(Dispatchers.IO) {
        val trimmedCodigo = rg.codigo.trim()
        val existing = rgDao.getRgByCodigo(trimmedCodigo)
        if (existing != null && existing.id != rg.id) {
            throw IllegalArgumentException("Já existe um RG com o código '$trimmedCodigo'.")
        }

        val now = System.currentTimeMillis()
        if (rg.id == 0L) {
            rgDao.insertRg(rg.copy(codigo = trimmedCodigo, createdAt = now, updatedAt = now, syncStatus = "PENDING_SYNC"))
        } else {
            rgDao.updateRg(rg.copy(codigo = trimmedCodigo, updatedAt = now, syncStatus = "PENDING_SYNC"))
            rg.id
        }
    }

    suspend fun deleteRg(rg: RgEntity) = withContext(Dispatchers.IO) {
        rgDao.deleteRg(rg)
    }

    // --- Localidade Operations ---
    fun getLocalidadesWithCounts(rgId: Long): Flow<List<LocalidadeWithCounts>> =
        localidadeDao.getLocalidadesWithCountsByRg(rgId)

    fun getLocalidadeByIdFlow(id: Long): Flow<LocalidadeEntity?> =
        localidadeDao.getLocalidadeByIdFlow(id)

    suspend fun getLocalidadeById(id: Long): LocalidadeEntity? = withContext(Dispatchers.IO) {
        localidadeDao.getLocalidadeById(id)
    }

    suspend fun isLocalidadeNomeExists(rgId: Long, nome: String, excludeId: Long = 0L): Boolean = withContext(Dispatchers.IO) {
        val existing = localidadeDao.getLocalidadeByNome(rgId, nome.trim())
        existing != null && existing.id != excludeId
    }

    suspend fun saveLocalidade(localidade: LocalidadeEntity): Long = withContext(Dispatchers.IO) {
        val trimmedNome = localidade.nome.trim()
        val existing = localidadeDao.getLocalidadeByNome(localidade.rgId, trimmedNome)
        if (existing != null && existing.id != localidade.id) {
            throw IllegalArgumentException("Já existe a localidade/bairro '$trimmedNome' neste RG.")
        }

        val now = System.currentTimeMillis()
        if (localidade.id == 0L) {
            localidadeDao.insertLocalidade(
                localidade.copy(nome = trimmedNome, createdAt = now, updatedAt = now, syncStatus = "PENDING_SYNC")
            )
        } else {
            localidadeDao.updateLocalidade(
                localidade.copy(nome = trimmedNome, updatedAt = now, syncStatus = "PENDING_SYNC")
            )
            localidade.id
        }
    }

    suspend fun deleteLocalidade(localidade: LocalidadeEntity) = withContext(Dispatchers.IO) {
        localidadeDao.deleteLocalidade(localidade)
    }

    // --- Quarteirão Operations ---
    fun getQuarteiroesWithSummary(localidadeId: Long): Flow<List<QuarteiraoWithSummary>> =
        quarteiraoDao.getQuarteiroesWithSummaryByLocalidade(localidadeId)

    fun getQuarteiraoSummary(quarteiraoId: Long): Flow<QuarteiraoWithSummary?> =
        quarteiraoDao.getQuarteiraoSummary(quarteiraoId)

    fun getQuarteiraoByIdFlow(id: Long): Flow<QuarteiraoEntity?> =
        quarteiraoDao.getQuarteiraoByIdFlow(id)

    suspend fun getQuarteiraoById(id: Long): QuarteiraoEntity? = withContext(Dispatchers.IO) {
        quarteiraoDao.getQuarteiraoById(id)
    }

    suspend fun isQuarteiraoNumeroExists(localidadeId: Long, numero: String, excludeId: Long = 0L): Boolean = withContext(Dispatchers.IO) {
        val existing = quarteiraoDao.getQuarteiraoByNumero(localidadeId, numero.trim())
        existing != null && existing.id != excludeId
    }

    suspend fun saveQuarteirao(quarteirao: QuarteiraoEntity): Long = withContext(Dispatchers.IO) {
        val trimmedNumero = quarteirao.numero.trim()
        val existing = quarteiraoDao.getQuarteiraoByNumero(quarteirao.localidadeId, trimmedNumero)
        if (existing != null && existing.id != quarteirao.id) {
            throw IllegalArgumentException("Já existe um quarteirão '$trimmedNumero' nesta localidade.")
        }

        val now = System.currentTimeMillis()
        if (quarteirao.id == 0L) {
            quarteiraoDao.insertQuarteirao(
                quarteirao.copy(numero = trimmedNumero, createdAt = now, updatedAt = now, syncStatus = "PENDING_SYNC")
            )
        } else {
            quarteiraoDao.updateQuarteirao(
                quarteirao.copy(numero = trimmedNumero, updatedAt = now, syncStatus = "PENDING_SYNC")
            )
            quarteirao.id
        }
    }

    suspend fun deleteQuarteirao(quarteirao: QuarteiraoEntity) = withContext(Dispatchers.IO) {
        quarteiraoDao.deleteQuarteirao(quarteirao)
    }

    // --- Imóvel Operations ---
    fun getImoveisByQuarteirao(quarteiraoId: Long): Flow<List<ImovelEntity>> =
        imovelDao.getImoveisByQuarteirao(quarteiraoId)

    fun getImovelByIdFlow(id: Long): Flow<ImovelEntity?> =
        imovelDao.getImovelByIdFlow(id)

    suspend fun getImovelById(id: Long): ImovelEntity? = withContext(Dispatchers.IO) {
        imovelDao.getImovelById(id)
    }

    suspend fun saveImovel(imovel: ImovelEntity): Long = withContext(Dispatchers.IO) {
        val trimmedEndereco = imovel.endereco.trim()
        val trimmedNumero = imovel.numero.trim()
        require(trimmedEndereco.isNotBlank()) { "O endereço/rua é obrigatório." }
        require(trimmedNumero.isNotBlank()) { "O número é obrigatório." }
        require(imovel.quarteiraoId > 0) { "O imóvel deve pertencer a um quarteirão." }
        require(imovel.habitacoes >= 0) { "A quantidade de habitações não pode ser negativa." }
        require(imovel.habitantes >= 0) { "A quantidade de habitantes não pode ser negativa." }
        require(imovel.caes >= 0) { "A quantidade de cães não pode ser negativa." }
        require(imovel.gatos >= 0) { "A quantidade de gatos não pode ser negativa." }

        val now = System.currentTimeMillis()
        if (imovel.id == 0L) {
            imovelDao.insertImovel(
                imovel.copy(
                    endereco = trimmedEndereco,
                    numero = trimmedNumero,
                    createdAt = now,
                    updatedAt = now,
                    syncStatus = "PENDING_SYNC"
                )
            )
        } else {
            imovelDao.updateImovel(
                imovel.copy(
                    endereco = trimmedEndereco,
                    numero = trimmedNumero,
                    updatedAt = now,
                    syncStatus = "PENDING_SYNC"
                )
            )
            imovel.id
        }
    }

    suspend fun deleteImovel(imovel: ImovelEntity) = withContext(Dispatchers.IO) {
        imovelDao.deleteImovel(imovel)
    }
}
