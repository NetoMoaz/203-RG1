package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.dao.GlobalTerritoryStats
import com.example.data.local.dao.LocalidadeWithCounts
import com.example.data.local.dao.QuarteiraoWithSummary
import com.example.data.local.dao.RgWithCounts
import com.example.data.local.entity.ImovelEntity
import com.example.data.local.entity.LocalidadeEntity
import com.example.data.local.entity.QuarteiraoEntity
import com.example.data.local.entity.RgEntity
import com.example.data.repository.TerritoryRepository
import com.example.ui.navigation.NavigationMode
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TerritoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TerritoryRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = TerritoryRepository(db)
    }

    // --- Navigation Stack ---
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val screenStack: StateFlow<List<Screen>> = _screenStack.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _screenStack.value = _screenStack.value + screen
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            val newStack = _screenStack.value.dropLast(1)
            _screenStack.value = newStack
            _currentScreen.value = newStack.last()
            return true
        }
        return false
    }

    fun navigateToHome() {
        _screenStack.value = listOf(Screen.Home)
        _currentScreen.value = Screen.Home
    }

    // --- Global Stats ---
    val globalStats: StateFlow<GlobalTerritoryStats> = repository.globalStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GlobalTerritoryStats(0, 0, 0, 0, 0, 0, 0, 0)
        )

    // --- RGs ---
    val rgsList: StateFlow<List<RgWithCounts>> = repository.getRgsWithCounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Localidades for Selected RG ---
    private val _selectedRgId = MutableStateFlow<Long?>(null)
    val localidadesList: StateFlow<List<LocalidadeWithCounts>> = _selectedRgId
        .flatMapLatest { rgId ->
            if (rgId != null) repository.getLocalidadesWithCounts(rgId)
            else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSelectedRg(rgId: Long) {
        _selectedRgId.value = rgId
    }

    // --- Quarteirões for Selected Localidade ---
    private val _selectedLocalidadeId = MutableStateFlow<Long?>(null)
    val quarteiroesList: StateFlow<List<QuarteiraoWithSummary>> = _selectedLocalidadeId
        .flatMapLatest { locId ->
            if (locId != null) repository.getQuarteiroesWithSummary(locId)
            else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSelectedLocalidade(locId: Long) {
        _selectedLocalidadeId.value = locId
    }

    // --- Quarteirão Detail & Summary ---
    private val _selectedQuarteiraoId = MutableStateFlow<Long?>(null)
    val quarteiraoSummary: StateFlow<QuarteiraoWithSummary?> = _selectedQuarteiraoId
        .flatMapLatest { qId ->
            if (qId != null) repository.getQuarteiraoSummary(qId)
            else flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val imoveisList: StateFlow<List<ImovelEntity>> = _selectedQuarteiraoId
        .flatMapLatest { qId ->
            if (qId != null) repository.getImoveisByQuarteirao(qId)
            else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSelectedQuarteirao(qId: Long) {
        _selectedQuarteiraoId.value = qId
    }

    // --- RG CRUD ---
    fun saveRg(
        id: Long,
        codigo: String,
        municipio: String,
        descricao: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveRg(
                    RgEntity(
                        id = id,
                        codigo = codigo.trim(),
                        municipio = municipio.trim(),
                        descricao = descricao.trim()
                    )
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro ao salvar RG")
            }
        }
    }

    fun deleteRg(id: Long, codigo: String, municipio: String) {
        viewModelScope.launch {
            repository.deleteRg(RgEntity(id = id, codigo = codigo, municipio = municipio))
        }
    }

    // --- Localidade CRUD ---
    fun saveLocalidade(
        id: Long,
        rgId: Long,
        nome: String,
        tipo: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveLocalidade(
                    LocalidadeEntity(
                        id = id,
                        rgId = rgId,
                        nome = nome.trim(),
                        tipo = tipo.trim()
                    )
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro ao salvar localidade")
            }
        }
    }

    fun deleteLocalidade(id: Long, rgId: Long, nome: String) {
        viewModelScope.launch {
            repository.deleteLocalidade(LocalidadeEntity(id = id, rgId = rgId, nome = nome))
        }
    }

    // --- Quarteirão CRUD (Tratando número como texto) ---
    fun saveQuarteirao(
        id: Long,
        localidadeId: Long,
        rgId: Long,
        numero: String,
        descricao: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveQuarteirao(
                    QuarteiraoEntity(
                        id = id,
                        localidadeId = localidadeId,
                        rgId = rgId,
                        numero = numero.trim(), // TEXTO EXATO: "01", "01/1", etc.
                        descricao = descricao.trim()
                    )
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro ao salvar quarteirão")
            }
        }
    }

    fun deleteQuarteirao(id: Long, localidadeId: Long, rgId: Long, numero: String) {
        viewModelScope.launch {
            repository.deleteQuarteirao(
                QuarteiraoEntity(
                    id = id,
                    localidadeId = localidadeId,
                    rgId = rgId,
                    numero = numero
                )
            )
        }
    }

    // --- Imóvel CRUD ---
    suspend fun getImovelById(id: Long): ImovelEntity? {
        return repository.getImovelById(id)
    }

    fun saveImovel(
        id: Long,
        quarteiraoId: Long,
        localidadeId: Long,
        rgId: Long,
        endereco: String,
        numero: String,
        tipo: String,
        habitacoes: Int,
        habitantes: Int,
        caes: Int,
        gatos: Int,
        observacoes: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveImovel(
                    ImovelEntity(
                        id = id,
                        quarteiraoId = quarteiraoId,
                        localidadeId = localidadeId,
                        rgId = rgId,
                        endereco = endereco.trim(),
                        numero = numero.trim(),
                        tipo = tipo.trim(),
                        habitacoes = habitacoes,
                        habitantes = habitantes,
                        caes = caes,
                        gatos = gatos,
                        observacoes = observacoes.trim()
                    )
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro ao salvar imóvel")
            }
        }
    }

    fun deleteImovel(imovel: ImovelEntity) {
        viewModelScope.launch {
            repository.deleteImovel(imovel)
        }
    }
}
