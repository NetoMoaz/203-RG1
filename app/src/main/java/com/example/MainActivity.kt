package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.imovel.ImovelFormScreen
import com.example.ui.screens.localidade.LocalidadeListScreen
import com.example.ui.screens.quarteirao.QuarteiraoDetailScreen
import com.example.ui.screens.quarteirao.QuarteiraoListScreen
import com.example.ui.screens.rg.RgListScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.sync.SyncScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TerritoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationContainer()
                }
            }
        }
    }
}

@Composable
fun AppNavigationContainer(
    viewModel: TerritoryViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val globalStats by viewModel.globalStats.collectAsStateWithLifecycle()
    val rgsList by viewModel.rgsList.collectAsStateWithLifecycle()
    val localidadesList by viewModel.localidadesList.collectAsStateWithLifecycle()
    val quarteiroesList by viewModel.quarteiroesList.collectAsStateWithLifecycle()
    val quarteiraoSummary by viewModel.quarteiraoSummary.collectAsStateWithLifecycle()
    val imoveisList by viewModel.imoveisList.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    stats = globalStats,
                    onNavigate = { target -> viewModel.navigateTo(target) }
                )
            }

            is Screen.RgList -> {
                RgListScreen(
                    rgs = rgsList,
                    mode = screen.mode,
                    onNavigateBack = { viewModel.navigateBack() },
                    onSelectRg = { rgId, rgCodigo ->
                        viewModel.setSelectedRg(rgId)
                        viewModel.navigateTo(
                            Screen.LocalidadeList(rgId = rgId, rgCodigo = rgCodigo, mode = screen.mode)
                        )
                    },
                    onSaveRg = { id, codigo, municipio, descricao, onResult ->
                        viewModel.saveRg(id, codigo, municipio, descricao, onResult)
                    },
                    onDeleteRg = { id, codigo, municipio ->
                        viewModel.deleteRg(id, codigo, municipio)
                    }
                )
            }

            is Screen.LocalidadeList -> {
                LaunchedEffect(screen.rgId) {
                    viewModel.setSelectedRg(screen.rgId)
                }
                LocalidadeListScreen(
                    rgId = screen.rgId,
                    rgCodigo = screen.rgCodigo,
                    localidades = localidadesList,
                    mode = screen.mode,
                    onNavigateBack = { viewModel.navigateBack() },
                    onSelectLocalidade = { locId, locNome ->
                        viewModel.setSelectedLocalidade(locId)
                        viewModel.navigateTo(
                            Screen.QuarteiraoList(
                                rgId = screen.rgId,
                                rgCodigo = screen.rgCodigo,
                                localidadeId = locId,
                                localidadeNome = locNome,
                                mode = screen.mode
                            )
                        )
                    },
                    onSaveLocalidade = { id, rgId, nome, tipo, onResult ->
                        viewModel.saveLocalidade(id, rgId, nome, tipo, onResult)
                    },
                    onDeleteLocalidade = { id, rgId, nome ->
                        viewModel.deleteLocalidade(id, rgId, nome)
                    }
                )
            }

            is Screen.QuarteiraoList -> {
                LaunchedEffect(screen.localidadeId) {
                    viewModel.setSelectedLocalidade(screen.localidadeId)
                }
                QuarteiraoListScreen(
                    rgId = screen.rgId,
                    rgCodigo = screen.rgCodigo,
                    localidadeId = screen.localidadeId,
                    localidadeNome = screen.localidadeNome,
                    quarteiroes = quarteiroesList,
                    mode = screen.mode,
                    onNavigateBack = { viewModel.navigateBack() },
                    onNavigateToHome = { viewModel.navigateToHome() },
                    onSelectQuarteirao = { qId, qNumero ->
                        viewModel.setSelectedQuarteirao(qId)
                        viewModel.navigateTo(
                            Screen.QuarteiraoDetail(
                                rgId = screen.rgId,
                                rgCodigo = screen.rgCodigo,
                                localidadeId = screen.localidadeId,
                                localidadeNome = screen.localidadeNome,
                                quarteiraoId = qId,
                                quarteiraoNumero = qNumero
                            )
                        )
                    },
                    onSaveQuarteirao = { id, locId, rgId, numero, descricao, onResult ->
                        viewModel.saveQuarteirao(id, locId, rgId, numero, descricao, onResult)
                    },
                    onDeleteQuarteirao = { id, locId, rgId, numero ->
                        viewModel.deleteQuarteirao(id, locId, rgId, numero)
                    }
                )
            }

            is Screen.QuarteiraoDetail -> {
                LaunchedEffect(screen.quarteiraoId) {
                    viewModel.setSelectedQuarteirao(screen.quarteiraoId)
                }
                QuarteiraoDetailScreen(
                    rgId = screen.rgId,
                    rgCodigo = screen.rgCodigo,
                    localidadeId = screen.localidadeId,
                    localidadeNome = screen.localidadeNome,
                    quarteiraoId = screen.quarteiraoId,
                    quarteiraoNumero = screen.quarteiraoNumero,
                    summary = quarteiraoSummary,
                    imoveis = imoveisList,
                    onNavigateBack = { viewModel.navigateBack() },
                    onNavigateToLocalidade = {
                        viewModel.navigateBack()
                    },
                    onNavigateToHome = { viewModel.navigateToHome() },
                    onAddNewImovel = {
                        viewModel.navigateTo(
                            Screen.ImovelForm(
                                rgId = screen.rgId,
                                rgCodigo = screen.rgCodigo,
                                localidadeId = screen.localidadeId,
                                localidadeNome = screen.localidadeNome,
                                quarteiraoId = screen.quarteiraoId,
                                quarteiraoNumero = screen.quarteiraoNumero,
                                imovelId = null
                            )
                        )
                    },
                    onEditImovel = { imovelId ->
                        viewModel.navigateTo(
                            Screen.ImovelForm(
                                rgId = screen.rgId,
                                rgCodigo = screen.rgCodigo,
                                localidadeId = screen.localidadeId,
                                localidadeNome = screen.localidadeNome,
                                quarteiraoId = screen.quarteiraoId,
                                quarteiraoNumero = screen.quarteiraoNumero,
                                imovelId = imovelId
                            )
                        )
                    },
                    onDeleteImovel = { imovel ->
                        viewModel.deleteImovel(imovel)
                    }
                )
            }

            is Screen.ImovelForm -> {
                ImovelFormScreen(
                    rgId = screen.rgId,
                    rgCodigo = screen.rgCodigo,
                    localidadeId = screen.localidadeId,
                    localidadeNome = screen.localidadeNome,
                    quarteiraoId = screen.quarteiraoId,
                    quarteiraoNumero = screen.quarteiraoNumero,
                    imovelId = screen.imovelId,
                    onLoadImovel = { id -> viewModel.getImovelById(id) },
                    onNavigateBack = { viewModel.navigateBack() },
                    onSaveImovel = { id, qId, locId, rId, endereco, numero, tipo, habitacoes, habitantes, caes, gatos, obs, onResult ->
                        viewModel.saveImovel(
                            id,
                            qId,
                            locId,
                            rId,
                            endereco,
                            numero,
                            tipo,
                            habitacoes,
                            habitantes,
                            caes,
                            gatos,
                            obs,
                            onResult
                        )
                    }
                )
            }

            is Screen.Sync -> {
                SyncScreen(
                    stats = globalStats,
                    onNavigateBack = { viewModel.navigateBack() }
                )
            }

            is Screen.Settings -> {
                SettingsScreen(
                    onNavigateBack = { viewModel.navigateBack() }
                )
            }
        }
    }
}
