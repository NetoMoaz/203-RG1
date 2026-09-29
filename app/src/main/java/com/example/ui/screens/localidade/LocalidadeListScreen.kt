package com.example.ui.screens.localidade

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
import androidx.compose.material.icons.filled.LocationCity
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
import com.example.data.local.dao.LocalidadeWithCounts
import com.example.ui.components.BreadcrumbBar
import com.example.ui.components.BreadcrumbItem
import com.example.ui.components.ConfirmDialog
import com.example.ui.navigation.NavigationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalidadeListScreen(
    rgId: Long,
    rgCodigo: String,
    localidades: List<LocalidadeWithCounts>,
    mode: NavigationMode,
    onNavigateBack: () -> Unit,
    onSelectLocalidade: (Long, String) -> Unit,
    onSaveLocalidade: (Long, Long, String, String, (Boolean, String?) -> Unit) -> Unit,
    onDeleteLocalidade: (Long, Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var showDialog by remember { mutableStateOf(false) }
    var editingLocalidade by remember { mutableStateOf<LocalidadeWithCounts?>(null) }
    var localidadeToDelete by remember { mutableStateOf<LocalidadeWithCounts?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("localidade_list_screen"),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Bairros e Localidades",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Estrutura do RG $rgCodigo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("localidade_list_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar aos RGs"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                // Breadcrumb
                BreadcrumbBar(
                    items = listOf(
                        BreadcrumbItem(label = "RG $rgCodigo", onClick = onNavigateBack)
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingLocalidade = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_localidade_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text("Nova Localidade", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        if (localidades.isEmpty()) {
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
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum Bairro / Localidade",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cadastre um bairro ou localidade rural neste RG para adicionar quarteirões.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                editingLocalidade = null
                                showDialog = true
                            },
                            modifier = Modifier.testTag("empty_add_localidade_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cadastrar Localidade")
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
                items(localidades, key = { it.id }) { loc ->
                    LocalidadeItemCard(
                        localidade = loc,
                        onOpen = { onSelectLocalidade(loc.id, loc.nome) },
                        onEdit = {
                            editingLocalidade = loc
                            showDialog = true
                        },
                        onDelete = { localidadeToDelete = loc }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (showDialog) {
        LocalidadeEditDialog(
            localidade = editingLocalidade,
            rgCodigo = rgCodigo,
            onDismiss = { showDialog = false },
            onSave = { nome, tipo, onError ->
                onSaveLocalidade(editingLocalidade?.id ?: 0L, rgId, nome, tipo) { success, errorMsg ->
                    if (success) {
                        showDialog = false
                    } else {
                        onError(errorMsg ?: "Erro ao salvar localidade")
                    }
                }
            }
        )
    }

    localidadeToDelete?.let { target ->
        val linkedMsg = buildString {
            if (target.quarteiroesCount > 0 || target.imoveisCount > 0) {
                append("Atenção: A localidade '${target.nome}' possui ")
                val parts = mutableListOf<String>()
                if (target.quarteiroesCount > 0) parts.add("${target.quarteiroesCount} quarteirão(ões)")
                if (target.imoveisCount > 0) parts.add("${target.imoveisCount} imóvel(is)")
                append(parts.joinToString(" e "))
                append(" associados que também serão excluídos permanentemente.\n\n")
            }
            append("Deseja realmente excluir a localidade '${target.nome}'?")
        }

        ConfirmDialog(
            show = true,
            title = "Excluir Localidade ${target.nome}?",
            message = linkedMsg,
            confirmButtonText = "Sim, Excluir",
            dismissButtonText = "Cancelar",
            onConfirm = {
                onDeleteLocalidade(target.id, target.rgId, target.nome)
                localidadeToDelete = null
            },
            onDismiss = { localidadeToDelete = null }
        )
    }
}

@Composable
private fun LocalidadeItemCard(
    localidade: LocalidadeWithCounts,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("localidade_card_${localidade.nome}")
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
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = localidade.nome,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = localidade.tipo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("edit_localidade_${localidade.nome}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Localidade",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_localidade_${localidade.nome}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir Localidade",
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
                        text = "${localidade.quarteiroesCount} Quarteirões",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${localidade.imoveisCount} Imóveis",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
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
                    modifier = Modifier.testTag("open_localidade_${localidade.nome}")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Quarteirões")
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
private fun LocalidadeEditDialog(
    localidade: LocalidadeWithCounts?,
    rgCodigo: String,
    onDismiss: () -> Unit,
    onSave: (String, String, (String) -> Unit) -> Unit
) {
    var nome by remember { mutableStateOf(localidade?.nome ?: "") }
    var tipo by remember { mutableStateOf(localidade?.tipo ?: "Bairro") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (localidade == null) "Novo Bairro/Localidade" else "Editar ${localidade.nome}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Vinculado ao RG $rgCodigo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Nome do Bairro / Localidade *") },
                    placeholder = { Text("Ex: Centro, Bairro Alto, Povoado Rural") },
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = {
                        Text(errorText ?: "Aceita bairros urbanos e localidades rurais.")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("localidade_nome_input")
                )
                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo (Ex: Bairro, Distrito, Povoado, Assentamento)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("localidade_tipo_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isBlank()) {
                        errorText = "O nome é obrigatório"
                    } else {
                        onSave(nome, tipo) { err ->
                            errorText = err
                        }
                    }
                },
                modifier = Modifier.testTag("localidade_dialog_save_button")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("localidade_dialog_cancel_button")
            ) {
                Text("Cancelar")
            }
        },
        modifier = Modifier.testTag("localidade_edit_dialog")
    )
}
