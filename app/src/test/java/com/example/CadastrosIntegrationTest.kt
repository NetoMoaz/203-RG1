package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
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
class CadastrosIntegrationTest {

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
    fun `inicializacao sem dados deve comecar com totais zerados na Home`() = runBlocking {
        val stats = repository.globalStats.first()
        assertEquals("Total de RGs deve iniciar em 0", 0, stats.totalRgs)
        assertEquals("Total de Quarteirões deve iniciar em 0", 0, stats.totalQuarteiroes)
        assertEquals("Total de Imóveis deve iniciar em 0", 0, stats.totalImoveis)
        assertEquals("Total de Habitantes deve iniciar em 0", 0, stats.totalHabitantes)
    }

    @Test
    fun `criar RG 01 e verificar persistencia como texto e atualizacao da Home`() = runBlocking {
        val rgId = repository.saveRg(
            RgEntity(codigo = "01", municipio = "Cidade Polo", descricao = "Região Central")
        )
        assertTrue(rgId > 0)

        // Verificar que o código permaneceu exatamente "01" (texto preservado)
        val loaded = repository.getRgById(rgId)
        assertNotNull(loaded)
        assertEquals("01", loaded?.codigo)

        // Verificar que o card da Home reflete 1 RG
        val stats = repository.globalStats.first()
        assertEquals(1, stats.totalRgs)
    }

    @Test
    fun `impedir criacao de RG duplicado`() = runBlocking {
        repository.saveRg(RgEntity(codigo = "01", municipio = "Cidade A"))

        try {
            repository.saveRg(RgEntity(codigo = "01", municipio = "Cidade B"))
            fail("Deveria lançar exceção ao cadastrar RG com mesmo código '01'")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Já existe") == true)
        }
    }

    @Test
    fun `criar localidade Centro dentro do RG 01`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01", municipio = "Sede"))
        val locId = repository.saveLocalidade(
            LocalidadeEntity(rgId = rgId, nome = "Centro", tipo = "Bairro")
        )
        assertTrue(locId > 0)

        val locs = repository.getLocalidadesWithCounts(rgId).first()
        assertEquals(1, locs.size)
        assertEquals("Centro", locs.first().nome)
    }

    @Test
    fun `criar quarteiroes 01 e 01_1 e verificar coexistencia como texto distintos`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01", municipio = "Sede"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))

        // Salvar Quarteirão "01"
        val q1Id = repository.saveQuarteirao(
            QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01", descricao = "Quarteirão principal")
        )
        // Salvar Quarteirão "01/1"
        val q11Id = repository.saveQuarteirao(
            QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01/1", descricao = "Desdobro norte")
        )

        assertTrue(q1Id > 0)
        assertTrue(q11Id > 0)

        val q1 = repository.getQuarteiraoById(q1Id)
        val q11 = repository.getQuarteiraoById(q11Id)

        // Garantir que ambos existem e mantiveram o texto original
        assertNotNull(q1)
        assertNotNull(q11)
        assertEquals("01", q1?.numero)
        assertEquals("01/1", q11?.numero)

        // Verificar total de quarteirões na Home
        val stats = repository.globalStats.first()
        assertEquals(2, stats.totalQuarteiroes)

        // Verificar listagem da localidade
        val list = repository.getQuarteiroesWithSummary(locId).first()
        assertEquals(2, list.size)
        val numeros = list.map { it.numero }
        assertTrue(numeros.contains("01"))
        assertTrue(numeros.contains("01/1"))
    }

    @Test
    fun `impedir quarteirao duplicado dentro da mesma localidade`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))

        repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))

        try {
            repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))
            fail("Deveria lançar exceção ao cadastrar quarteirão '01' duplicado na mesma localidade")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Já existe") == true)
        }
    }

    @Test
    fun `editar cadastro e confirmar persistencia sem afetar outros registros`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01", municipio = "Nome Original"))
        val loaded = repository.getRgById(rgId)
        assertNotNull(loaded)

        // Editar
        repository.saveRg(loaded!!.copy(municipio = "Nome Atualizado", descricao = "Nova descrição"))

        val updated = repository.getRgById(rgId)
        assertEquals("Nome Atualizado", updated?.municipio)
        assertEquals("Nova descrição", updated?.descricao)
        assertEquals("01", updated?.codigo)
    }

    @Test
    fun `exclusao em cascata remove entidades filhas e atualiza os totais da Home`() = runBlocking {
        val rgId = repository.saveRg(RgEntity(codigo = "01"))
        val locId = repository.saveLocalidade(LocalidadeEntity(rgId = rgId, nome = "Centro"))
        repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01"))
        repository.saveQuarteirao(QuarteiraoEntity(localidadeId = locId, rgId = rgId, numero = "01/1"))

        var stats = repository.globalStats.first()
        assertEquals(1, stats.totalRgs)
        assertEquals(2, stats.totalQuarteiroes)

        // Excluir RG
        val rg = repository.getRgById(rgId)
        assertNotNull(rg)
        repository.deleteRg(rg!!)

        // Verificar que RG e quarteirões foram removidos e Home voltou para 0
        assertNull(repository.getRgById(rgId))
        stats = repository.globalStats.first()
        assertEquals(0, stats.totalRgs)
        assertEquals(0, stats.totalQuarteiroes)
    }
}
