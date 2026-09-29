package com.example.ui.navigation

enum class NavigationMode {
    FIELD_WORK, // Coleta de Campo
    MANAGEMENT  // Cadastros
}

sealed class Screen {
    object Home : Screen()

    data class RgList(
        val mode: NavigationMode = NavigationMode.FIELD_WORK
    ) : Screen()

    data class LocalidadeList(
        val rgId: Long,
        val rgCodigo: String,
        val mode: NavigationMode = NavigationMode.FIELD_WORK
    ) : Screen()

    data class QuarteiraoList(
        val rgId: Long,
        val rgCodigo: String,
        val localidadeId: Long,
        val localidadeNome: String,
        val mode: NavigationMode = NavigationMode.FIELD_WORK
    ) : Screen()

    data class QuarteiraoDetail(
        val rgId: Long,
        val rgCodigo: String,
        val localidadeId: Long,
        val localidadeNome: String,
        val quarteiraoId: Long,
        val quarteiraoNumero: String
    ) : Screen()

    data class ImovelForm(
        val rgId: Long,
        val rgCodigo: String,
        val localidadeId: Long,
        val localidadeNome: String,
        val quarteiraoId: Long,
        val quarteiraoNumero: String,
        val imovelId: Long? = null
    ) : Screen()

    object Sync : Screen()

    object Settings : Screen()
}
