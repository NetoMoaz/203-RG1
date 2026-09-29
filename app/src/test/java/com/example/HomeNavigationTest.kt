package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.navigation.NavigationMode
import com.example.ui.navigation.Screen
import com.example.ui.viewmodel.TerritoryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomeNavigationTest {

    private lateinit var viewModel: TerritoryViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = TerritoryViewModel(app)
    }

    @Test
    fun `tela inicial deve iniciar em Home`() {
        assertEquals(Screen.Home, viewModel.currentScreen.value)
        assertEquals(1, viewModel.screenStack.value.size)
    }

    @Test
    fun `clicar em Coleta de Campo deve navegar para RgList com modo FIELD_WORK`() {
        viewModel.navigateTo(Screen.RgList(NavigationMode.FIELD_WORK))
        val current = viewModel.currentScreen.value
        assertTrue(current is Screen.RgList)
        assertEquals(NavigationMode.FIELD_WORK, (current as Screen.RgList).mode)

        // Testar botão voltar
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen.value)
    }

    @Test
    fun `clicar em Cadastros deve navegar para RgList com modo MANAGEMENT`() {
        viewModel.navigateTo(Screen.RgList(NavigationMode.MANAGEMENT))
        val current = viewModel.currentScreen.value
        assertTrue(current is Screen.RgList)
        assertEquals(NavigationMode.MANAGEMENT, (current as Screen.RgList).mode)

        // Testar botão voltar
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen.value)
    }

    @Test
    fun `clicar em Sincronizacao deve navegar para Sync e voltar para Home`() {
        viewModel.navigateTo(Screen.Sync)
        assertEquals(Screen.Sync, viewModel.currentScreen.value)

        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen.value)
    }

    @Test
    fun `clicar em Configuracoes deve navegar para Settings e voltar para Home`() {
        viewModel.navigateTo(Screen.Settings)
        assertEquals(Screen.Settings, viewModel.currentScreen.value)

        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen.value)
    }

    @Test
    fun `navegacao aninhada deve desempilhar passo a passo ate a Home`() {
        viewModel.navigateTo(Screen.RgList(NavigationMode.MANAGEMENT))
        viewModel.navigateTo(Screen.LocalidadeList(rgId = 1L, rgCodigo = "01", mode = NavigationMode.MANAGEMENT))
        viewModel.navigateTo(Screen.QuarteiraoList(rgId = 1L, rgCodigo = "01", localidadeId = 10L, localidadeNome = "Centro", mode = NavigationMode.MANAGEMENT))

        assertTrue(viewModel.currentScreen.value is Screen.QuarteiraoList)

        // Voltar para Localidade
        assertTrue(viewModel.navigateBack())
        assertTrue(viewModel.currentScreen.value is Screen.LocalidadeList)

        // Voltar para RG
        assertTrue(viewModel.navigateBack())
        assertTrue(viewModel.currentScreen.value is Screen.RgList)

        // Voltar para Home
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Home, viewModel.currentScreen.value)
    }
}
