package com.example.ui.screens.imovel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ImovelEntity
import com.example.ui.components.BreadcrumbBar
import com.example.ui.components.BreadcrumbItem

// Lista inicial oficial conforme especificação, extensível para novos tipos
val TIPOS_PADRAO_IMOVEL = listOf(
    "Residencial",
    "Comercial",
    "Terreno",
    "Institucional",
    "Outro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImovelFormScreen(
    rgId: Long,
    rgCodigo: String,
    localidadeId: Long,
    localidadeNome: String,
    quarteiraoId: Long,
    quarteiraoNumero: String,
    imovelId: Long?,
    onLoadImovel: suspend (Long) -> ImovelEntity?,
    onNavigateBack: () -> Unit,
    onSaveImovel: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var endereco by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Residencial") }
    var customTipo by remember { mutableStateOf("") }
    var habitacoes by remember { mutableIntStateOf(1) }
    var habitantes by remember { mutableIntStateOf(0) }
    var caes by remember { mutableIntStateOf(0) }
    var gatos by remember { mutableIntStateOf(0) }
    var observacoes by remember { mutableStateOf("") }

    var enderecoError by remember { mutableStateOf<String?>(null) }
    var numeroError by remember { mutableStateOf<String?>(null) }
    var formGeneralError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(imovelId != null) }

    LaunchedEffect(imovelId) {
        if (imovelId != null && imovelId > 0) {
            val loaded = onLoadImovel(imovelId)
            if (loaded != null) {
                endereco = loaded.endereco
                numero = loaded.numero
                if (TIPOS_PADRAO_IMOVEL.contains(loaded.tipo)) {
                    tipo = loaded.tipo
                } else {
                    tipo = "Outro"
                    customTipo = loaded.tipo
                }
                habitacoes = loaded.habitacoes.coerceAtLeast(0)
                habitantes = loaded.habitantes.coerceAtLeast(0)
                caes = loaded.caes.coerceAtLeast(0)
                gatos = loaded.gatos.coerceAtLeast(0)
                observacoes = loaded.observacoes
            }
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .testTag("imovel_form_screen"),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (imovelId == null) "Novo Imóvel" else "Editar Imóvel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Quarteirão $quarteiraoNumero • $localidadeNome",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("imovel_form_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar ao Quarteirão"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                BreadcrumbBar(
                    items = listOf(
                        BreadcrumbItem(label = "RG $rgCodigo"),
                        BreadcrumbItem(label = localidadeNome),
                        BreadcrumbItem(label = "Quart. $quarteiraoNumero", onClick = onNavigateBack),
                        BreadcrumbItem(label = if (imovelId == null) "Novo Imóvel" else "Editar")
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (formGeneralError != null) {
                        Text(
                            text = formGeneralError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            var valid = true
                            formGeneralError = null
                            if (endereco.isBlank()) {
                                enderecoError = "A rua/endereço é obrigatória"
                                valid = false
                            }
                            if (numero.isBlank()) {
                                numeroError = "O número é obrigatório (ex: 10, 10A, S/N)"
                                valid = false
                            }
                            if (habitacoes < 0 || habitantes < 0 || caes < 0 || gatos < 0) {
                                formGeneralError = "Valores quantitativos não podem ser negativos."
                                valid = false
                            }

                            if (valid) {
                                val finalTipo = if (tipo == "Outro" && customTipo.isNotBlank()) customTipo.trim() else tipo
                                onSaveImovel(
                                    imovelId ?: 0L,
                                    quarteiraoId,
                                    localidadeId,
                                    rgId,
                                    endereco.trim(),
                                    numero.trim(),
                                    finalTipo,
                                    habitacoes,
                                    habitantes,
                                    caes,
                                    gatos,
                                    observacoes.trim()
                                ) { success, errorMsg ->
                                    if (success) {
                                        onNavigateBack()
                                    } else {
                                        formGeneralError = errorMsg ?: "Erro ao salvar imóvel"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_imovel_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Salvar imóvel",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Localização e Endereço (Texto puro)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Endereço e Identificação",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = endereco,
                            onValueChange = {
                                endereco = it
                                if (enderecoError != null) enderecoError = null
                            },
                            label = { Text("Rua / Endereço *") },
                            placeholder = { Text("Ex: Rua das Palmeiras, Av. Central") },
                            singleLine = true,
                            isError = enderecoError != null,
                            supportingText = enderecoError?.let { { Text(it) } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("imovel_endereco_input")
                        )

                        OutlinedTextField(
                            value = numero,
                            onValueChange = {
                                numero = it
                                if (numeroError != null) numeroError = null
                            },
                            label = { Text("Número * (Texto - Ex: 10, 10A, S/N)") },
                            placeholder = { Text("Ex: 10, 10A, S/N") },
                            singleLine = true,
                            isError = numeroError != null,
                            supportingText = {
                                Text(numeroError ?: "Tratado como texto (nunca convertido para número)")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("imovel_numero_input")
                        )
                    }
                }
            }

            // Tipo do Imóvel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tipo do Imóvel",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(TIPOS_PADRAO_IMOVEL) { itemTipo ->
                                val selected = tipo.equals(itemTipo, ignoreCase = true)
                                FilterChip(
                                    selected = selected,
                                    onClick = { tipo = itemTipo },
                                    label = { Text(itemTipo) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("tipo_chip_$itemTipo")
                                )
                            }
                        }

                        if (tipo == "Outro") {
                            OutlinedTextField(
                                value = customTipo,
                                onValueChange = { customTipo = it },
                                label = { Text("Especifique o tipo (ex: Galpão, Chácara)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_tipo_input")
                            )
                        }
                    }
                }
            }

            // Contagens de Campo (Habitações, Habitantes, Cães, Gatos)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Dados do Imóvel e População (>= 0)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Habitações
                        SafeCounterField(
                            label = "Habitações",
                            value = habitacoes,
                            onValueChange = { habitacoes = it.coerceAtLeast(0) },
                            testTag = "counter_habitacoes"
                        )

                        // Habitantes
                        SafeCounterField(
                            label = "Habitantes / Moradores",
                            value = habitantes,
                            onValueChange = { habitantes = it.coerceAtLeast(0) },
                            testTag = "counter_habitantes"
                        )

                        // Cães
                        SafeCounterField(
                            label = "Quantidade de Cães",
                            value = caes,
                            onValueChange = { caes = it.coerceAtLeast(0) },
                            testTag = "counter_caes"
                        )

                        // Gatos
                        SafeCounterField(
                            label = "Quantidade de Gatos",
                            value = gatos,
                            onValueChange = { gatos = it.coerceAtLeast(0) },
                            testTag = "counter_gatos"
                        )
                    }
                }
            }

            // Observações
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Observações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = observacoes,
                            onValueChange = { observacoes = it },
                            placeholder = { Text("Pontos de referência, características do imóvel, morador ausente, etc.") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("imovel_observacoes_input")
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SafeCounterField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    testTag: String
) {
    var showDirectInputDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedIconButton(
                onClick = { if (value > 0) onValueChange(value - 1) },
                enabled = value > 0,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("${testTag}_decrement")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Diminuir"
                )
            }

            Box(
                modifier = Modifier
                    .width(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { showDirectInputDialog = true }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            OutlinedIconButton(
                onClick = { onValueChange(value + 1) },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("${testTag}_increment")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumentar"
                )
            }
        }
    }

    if (showDirectInputDialog) {
        var inputVal by remember { mutableStateOf(value.toString()) }
        AlertDialog(
            onDismissRequest = { showDirectInputDialog = false },
            title = { Text(text = "Definir $label") },
            text = {
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { raw ->
                        // Aceita apenas dígitos positivos ou vazio
                        if (raw.all { it.isDigit() }) {
                            inputVal = raw
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text("Quantidade (>= 0)") },
                    supportingText = { Text("Valores vazios são considerados 0") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = inputVal.toIntOrNull()?.coerceAtLeast(0) ?: 0
                        onValueChange(parsed)
                        showDirectInputDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDirectInputDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
