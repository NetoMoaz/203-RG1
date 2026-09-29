package com.example

import com.example.integration.rg203.Rg203Constants
import com.example.integration.rg203.Rg203FileValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Rg203InfrastructureTest {

    private val validator = Rg203FileValidator()

    @Test
    fun `validar extensao oficial rg203 com sucesso`() {
        assertTrue(validator.validateFileName("base_desktop_campo.rg203"))
        assertTrue(validator.validateFileName("CENTRO_01.RG203")) // case insensitive
        assertTrue(validator.validateFileName("exportacao_2026.rg203"))
    }

    @Test
    fun `rejeitar arquivos com extensoes diferentes de rg203`() {
        assertFalse(validator.validateFileName("dados.json"))
        assertFalse(validator.validateFileName("planilha.csv"))
        assertFalse(validator.validateFileName("backup.txt"))
        assertFalse(validator.validateFileName("dados_rg203.zip"))
        assertFalse(validator.validateFileName("arquivo_sem_extensao"))
    }

    @Test
    fun `verificar constantes de integracao oficiais do Desktop V4_8`() {
        assertEquals("rg203", Rg203Constants.FILE_EXTENSION)
        assertEquals(".rg203", Rg203Constants.OFFICIAL_EXTENSION_SUFFIX)
        assertEquals("4.8", Rg203Constants.DESKTOP_VERSION_COMPATIBILITY)
    }
}
