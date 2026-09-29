package com.example.ui.screens.quarteirao

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.dao.QuarteiraoWithSummary
import com.example.ui.components.BreadcrumbBar
import com.example.ui.components.BreadcrumbItem
import com.example.ui.components.ConfirmDialog
import com.example.ui.navigation.NavigationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuarteiraoListScreen(
    rgId: Long,
    rgCodigo: String,
    localidadeId: Long,
    localidadeNome: String,
    quarteiroes: List<QuarteiraoWithSummary>,
    mode: NavigationMode,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onSelectQuarteirao: (Long, String) -> Unit,
    onSaveQuarteirao: (Long, Long, Long, String, String, (Boolean, String?) -> Unit) -> Unit,
    onDeleteQuarteirao: (Long, Long, Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var showDialog by remember { mutableStateOf(false) }
    var editingQuarteirao by remember { mutableStateOf<QuarteiraoWithSummary?>(null) }
    var quarteiraoToDelete by remember { mutableStateOf<QuarteiraoWithSummary?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("quarteirao_list_screen"),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Quarteirões",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$localidadeNome • RG $rgCodigo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("quarteirao_list_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar às Localidades"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                // Breadcrumbs: RG -> Localidade
                BreadcrumbBar(
                    items = listOf(
                        BreadcrumbItem(label = "RG $rgCodigo", onClick = onNavigateBack),
                        BreadcrumbItem(label = localidadeNome, onClick = null)
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingQuarteirao = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_quarteirao_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text("Novo Quarteirão", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        if (quarteiroes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum Quarteirão Cadastrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cadastre os quarteirões desta localidade para iniciar o levantamento de imóveis.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                editingQuarteirao = null
                                showDialog = true
                            },
                            modifier = Modifier.testTag("empty_add_quarteirao_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cadastrar Quarteirão")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quarteiroes, key = { it.id }) { q ->
                    QuarteiraoItemCard(
                        quarteirao = q,
                        onOpen = { onSelectQuarteirao(q.id, q.numero) },
                        onEdit = {
                            editingQuarteirao = q
                            showDialog = true
                        },
                        onDelete = { quarteiraoToDelete = q }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (showDialog) {
        QuarteiraoEditDialog(
            quarteirao = editingQuarteirao,
            localidadeNome = localidadeNome,
            rgCodigo = rgCodigo,
            onDismiss = { showDialog = false },
            onSave = { numero, descricao, onError ->
                onSaveQuarteirao(
                    editingQuarteirao?.id ?: 0L,
                    localidadeId,
                    rgId,
                    numero,
                    descricao
                ) { success, errorMsg ->
                    if (success) {
                        showDialog = false
                    } else {
                        onError(errorMsg ?: "Erro ao salvar quarteirão")
                    }
                }
            }
        )
    }

    quarteiraoToDelete?.let { target ->
        val linkedMsg = buildString {
            if (target.totalImoveis > 0) {
                append("Atenção: O quarteirão '${target.numero}' possui ${target.totalImoveis} imóvel(is) cadastrado(s) que também serão excluídos permanentemente.\n\n")
            }
            append("Deseja realmente excluir o quarteirão '${target.numero}'?")
        }

        ConfirmDialog(
            show = true,
            title = "Excluir Quarteirão ${target.numero}?",
            message = linkedMsg,
            confirmButtonText = "Sim, Excluir",
            dismissButtonText = "Cancelar",
            onConfirm = {
                onDeleteQuarteirao(target.id, target.localidadeId, target.rgId, target.numero)
                quarteiraoToDelete = null
            },
            onDismiss = { quarteiraoToDelete = null }
        )
    }
}

@Composable
private fun QuarteiraoItemCard(
    quarteirao: QuarteiraoWithSummary,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quarteirao_card_${quarteirao.numero}")
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quarteirao.numero,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "Quarteirão ${quarteirao.numero}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (quarteirao.descricao.isNotBlank()) {
                            Text(
                                text = quarteirao.descricao,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("edit_quarteirao_${quarteirao.numero}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Quarteirão",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_quarteirao_${quarteirao.numero}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir Quarteirão",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${quarteirao.totalImoveis} Imóveis",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${quarteirao.totalHabitacoes} Hab.",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${quarteirao.totalHabitantes} Mor.",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${quarteirao.totalCaes} Cães / ${quarteirao.totalGatos} Gatos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onOpen,
                    modifier = Modifier.testTag("open_quarteirao_${quarteirao.numero}")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Imóveis")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuarteiraoEditDialog(
    quarteirao: QuarteiraoWithSummary?,
    localidadeNome: String,
    rgCodigo: String,
    onDismiss: () -> Unit,
    onSave: (String, String, (String) -> Unit) -> Unit
) {
    var numero by remember { mutableStateOf(quarteirao?.numero ?: "") }
    var descricao by remember { mutableStateOf(quarteirao?.descricao ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (quarteirao == null) "Novo Quarteirão" else "Editar Quarteirão ${quarteirao.numero}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Local: RG $rgCodigo > $localidadeNome",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = numero,
                    onValueChange = {
                        numero = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Número do Quarteirão *") },
                    placeholder = { Text("Ex: 01, 01/1, 02, A1") },
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = {
                        Text(errorText ?: "Tratado como texto exato (ex: '01' e '01/1' são distintos).")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quarteirao_numero_input")
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição / Delimitação") },
                    placeholder = { Text("Ex: Entre Rua A e Rua B") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quarteirao_descricao_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (numero.isBlank()) {
                        errorText = "O número do quarteirão é obrigatório"
                    } else {
                        onSave(numero, descricao) { err ->
                            errorText = err
                        }
                    }
                },
                modifier = Modifier.testTag("quarteirao_dialog_save_button")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("quarteirao_dialog_cancel_button")
            ) {
                Text("Cancelar")
            }
        },
        modifier = Modifier.testTag("quarteirao_edit_dialog")
    )
}
