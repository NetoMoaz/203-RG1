package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ImovelEntity
import com.example.data.local.entity.LocalidadeEntity
import com.example.data.local.entity.QuarteiraoEntity
import com.example.data.local.entity.RgEntity
import com.example.data.repository.TerritoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ImoveisIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: TerritoryRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TerritoryRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `criar imovel basico e verificar persistencia e totais na Home e no Quarteirao`() = runBlocking {
        // Setup: RG "01" > "Centro" > Quarteirão "01"
        val rgId = repository.saveRg(RgEntity(codigo = "01", municipio = "Município"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        val qId = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))

        // Criar imóvel: Rua A, 10, Residencial, 1 hab, 4 hab, 2 caes, 1 gato
        val imovelId = repository.saveImovel(
            ImovelEntity(
                quarteiraoId = qId,
                localidadeId = locId,
                rgId = rgId,
                endereco = "Rua A",
                numero = "10",
                tipo = "Residencial",
                habitacoes = 1,
                habitantes = 4,
                caes = 2,
                gatos = 1
            )
        )
        assertTrue(imovelId > 0)

        // 1. Confirmar persistência
        val loaded = repository.getImovelById(imovelId)
        assertNotNull(loaded)
        assertEquals("Rua A", loaded?.endereco)
        assertEquals("10", loaded?.numero)
        assertEquals("Residencial", loaded?.tipo)
        assertEquals(1, loaded?.habitacoes)
        assertEquals(4, loaded?.habitantes)
        assertEquals(2, loaded?.caes)
        assertEquals(1, loaded?.gatos)

        // 2. Confirmar Home: Imóveis = 1, Habitantes = 4
        val homeStats = repository.globalStats.first()
        assertEquals("Total de Imóveis na Home", 1, homeStats.totalImoveis)
        assertEquals("Total de Habitantes na Home", 4, homeStats.totalHabitantes)

        // 3. Confirmar Quarteirão: Imóveis = 1, Habitações = 1, Habitantes = 4, Cães = 2, Gatos = 1
        val qSummary = repository.getQuarteiraoSummary(qId).first()
        assertNotNull(qSummary)
        assertEquals("Imóveis no Quarteirão", 1, qSummary?.totalImoveis)
        assertEquals("Habitações no Quarteirão", 1, qSummary?.totalHabitacoes)
        assertEquals("Habitantes no Quarteirão", 4, qSummary?.totalHabitantes)
        assertEquals("Cães no Quarteirão", 2, qSummary?.totalCaes)
        assertEquals("Gatos no Quarteirão", 1, qSummary?.totalGatos)
    }

    @Test
    fun `testar numeros de imovel como texto exato 10A e SN`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        val qId = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))

        // Imóvel com número "10A"
        val id1 = repository.saveImovel(
            ImovelEntity(
                quarteiraoId = qId,
                localidadeId = locId,
                rgId = rgId,
                endereco = "Av. Brasil",
                numero = "10A",
                tipo = "Comercial",
                habitacoes = 0,
                habitantes = 0
            )
        )

        // Imóvel com número "S/N"
        val id2 = repository.saveImovel(
            ImovelEntity(
                quarteiraoId = qId,
                localidadeId = locId,
                rgId = rgId,
                endereco = "Rua do Porto",
                numero = "S/N",
                tipo = "Terreno",
                habitacoes = 0,
                habitantes = 0
            )
        )

        val imovel1 = repository.getImovelById(id1)
        val imovel2 = repository.getImovelById(id2)

        assertNotNull(imovel1)
        assertNotNull(imovel2)
        assertEquals("10A", imovel1?.numero)
        assertEquals("S/N", imovel2?.numero)
    }

    @Test
    fun `editar habitantes de 4 para 5 e confirmar atualizacao dos totais`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        val qId = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))

        val id = repository.saveImovel(
            ImovelEntity(
                quarteiraoId = qId,
                localidadeId = locId,
                rgId = rgId,
                endereco = "Rua B",
                numero = "20",
                tipo = "Residencial",
                habitacoes = 1,
                habitantes = 4,
                caes = 0,
                gatos = 0
            )
        )

        var homeStats = repository.globalStats.first()
        assertEquals(4, homeStats.totalHabitantes)

        // Editar habitantes 4 -> 5
        val imovel = repository.getImovelById(id)
        assertNotNull(imovel)
        repository.saveImovel(imovel!!.copy(habitantes = 5))

        // Confirmar atualização dos totais na Home e no Quarteirão
        homeStats = repository.globalStats.first()
        assertEquals(5, homeStats.totalHabitantes)

        val qSummary = repository.getQuarteiraoSummary(qId).first()
        assertEquals(5, qSummary?.totalHabitantes)
    }

    @Test
    fun `excluir imovel e confirmar atualizacao imediata dos totais`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        val qId = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))

        val id = repository.saveImovel(
            ImovelEntity(
                quarteiraoId = qId,
                localidadeId = locId,
                rgId = rgId,
                endereco = "Rua C",
                numero = "30",
                tipo = "Residencial",
                habitacoes = 1,
                habitantes = 3
            )
        )

        var homeStats = repository.globalStats.first()
        assertEquals(1, homeStats.totalImoveis)
        assertEquals(3, homeStats.totalHabitantes)

        // Excluir
        val imovel = repository.getImovelById(id)
        assertNotNull(imovel)
        repository.deleteImovel(imovel!!)

        // Confirmar remoção
        assertNull(repository.getImovelById(id))
        homeStats = repository.globalStats.first()
        assertEquals(0, homeStats.totalImoveis)
        assertEquals(0, homeStats.totalHabitantes)

        val qSummary = repository.getQuarteiraoSummary(qId).first()
        assertEquals(0, qSummary?.totalImoveis)
        assertEquals(0, qSummary?.totalHabitantes)
    }

    @Test
    fun `confirmar isolamento entre imoveis de quarteiroes diferentes`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        val q1Id = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))
        val q2Id = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01/1"))

        repository.saveImovel(
            ImovelEntity(quarteiraoId = q1Id, localidadeId = locId, rgId = rgId, endereco = "Rua 1", numero = "1", habitantes = 2)
        )
        repository.saveImovel(
            ImovelEntity(quarteiraoId = q2Id, localidadeId = locId, rgId = rgId, endereco = "Rua 2", numero = "2", habitantes = 6)
        )

        val q1Imoveis = repository.getImoveisByQuarteirao(q1Id).first()
        val q2Imoveis = repository.getImoveisByQuarteirao(q2Id).first()

        assertEquals(1, q1Imoveis.size)
        assertEquals("Rua 1", q1Imoveis.first().endereco)

        assertEquals(1, q2Imoveis.size)
        assertEquals("Rua 2", q2Imoveis.first().endereco)

        val q1Summary = repository.getQuarteiraoSummary(q1Id).first()
        val q2Summary = repository.getQuarteiraoSummary(q2Id).first()

        assertEquals(2, q1Summary?.totalHabitantes)
        assertEquals(6, q2Summary?.totalHabitantes)
    }

    @Test
    fun `confirmar rejeicao de valores negativos`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        val qId = repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))

        try {
            repository.saveImovel(
                ImovelEntity(
                    quarteiraoId = qId,
                    localidadeId = locId,
                    rgId = rgId,
                    endereco = "Rua Negativa",
                    numero = "1",
                    habitacoes = -1
                )
            )
            fail("Deveria rejeitar habitações negativas")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("não pode ser negativa") == true)
        }

        try {
            repository.saveImovel(
                ImovelEntity(
                    quarteiraoId = qId,
                    localidadeId = locId,
                    rgId = rgId,
                    endereco = "Rua Negativa",
                    numero = "1",
                    habitantes = -5
                )
            )
            fail("Deveria rejeitar habitantes negativos")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("não pode ser negativa") == true)
        }
    }
}
