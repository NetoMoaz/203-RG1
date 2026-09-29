package com.example.ui.screens.rg

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
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
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
import com.example.data.local.dao.RgWithCounts
import com.example.ui.components.ConfirmDialog
import com.example.ui.navigation.NavigationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RgListScreen(
    rgs: List<RgWithCounts>,
    mode: NavigationMode,
    onNavigateBack: () -> Unit,
    onSelectRg: (Long, String) -> Unit,
    onSaveRg: (Long, String, String, String, (Boolean, String?) -> Unit) -> Unit,
    onDeleteRg: (Long, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var showRgDialog by remember { mutableStateOf(false) }
    var editingRg by remember { mutableStateOf<RgWithCounts?>(null) }
    var rgToDelete by remember { mutableStateOf<RgWithCounts?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("rg_list_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (mode == NavigationMode.FIELD_WORK) "Coleta de Campo" else "Cadastros de RGs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Selecione ou gerencie o Reconhecimento Geográfico",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("rg_list_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar para o Início"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingRg = null
                    showRgDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_rg_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text("Novo RG", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        if (rgs.isEmpty()) {
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
                            imageVector = Icons.Default.Domain,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum RG Cadastrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cadastre o primeiro Reconhecimento Geográfico para iniciar os trabalhos de campo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                editingRg = null
                                showRgDialog = true
                            },
                            modifier = Modifier.testTag("empty_add_rg_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cadastrar RG")
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
                items(rgs, key = { it.id }) { rg ->
                    RgItemCard(
                        rg = rg,
                        onOpen = { onSelectRg(rg.id, rg.codigo) },
                        onEdit = {
                            editingRg = rg
                            showRgDialog = true
                        },
                        onDelete = { rgToDelete = rg }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp)) // Espaço para o FAB
                }
            }
        }
    }

    // Diálogo de Cadastro / Edição de RG com validação de duplicidade
    if (showRgDialog) {
        RgEditDialog(
            rg = editingRg,
            onDismiss = { showRgDialog = false },
            onSave = { codigo, municipio, descricao, onError ->
                onSaveRg(editingRg?.id ?: 0L, codigo, municipio, descricao) { success, errorMsg ->
                    if (success) {
                        showRgDialog = false
                    } else {
                        onError(errorMsg ?: "Erro ao salvar RG")
                    }
                }
            }
        )
    }

    // Confirmação de exclusão com aviso explícito de dados vinculados
    rgToDelete?.let { target ->
        val linkedMsg = buildString {
            if (target.localidadesCount > 0 || target.quarteiroesCount > 0 || target.imoveisCount > 0) {
                append("Atenção: Este RG possui ")
                val parts = mutableListOf<String>()
                if (target.localidadesCount > 0) parts.add("${target.localidadesCount} localidade(s)")
                if (target.quarteiroesCount > 0) parts.add("${target.quarteiroesCount} quarteirão(ões)")
                if (target.imoveisCount > 0) parts.add("${target.imoveisCount} imóvel(is)")
                append(parts.joinToString(", "))
                append(" associados que também serão excluídos permanentemente.\n\n")
            }
            append("Esta ação não poderá ser desfeita. Deseja realmente excluir o RG ${target.codigo}?")
        }

        ConfirmDialog(
            show = true,
            title = "Excluir RG ${target.codigo}?",
            message = linkedMsg,
            confirmButtonText = "Sim, Excluir Tudo",
            dismissButtonText = "Cancelar",
            onConfirm = {
                onDeleteRg(target.id, target.codigo, target.municipio)
                rgToDelete = null
            },
            onDismiss = { rgToDelete = null }
        )
    }
}

@Composable
private fun RgItemCard(
    rg: RgWithCounts,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rg_card_${rg.codigo}")
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
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rg.codigo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "RG ${rg.codigo}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (rg.municipio.isNotBlank()) {
                            Text(
                                text = rg.municipio,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("edit_rg_${rg.codigo}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar RG",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_rg_${rg.codigo}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir RG",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (rg.descricao.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rg.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                        text = "${rg.localidadesCount} Loc.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${rg.quarteiroesCount} Quarteirões",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${rg.imoveisCount} Imóveis",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onOpen,
                    modifier = Modifier.testTag("open_rg_${rg.codigo}")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir RG")
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
private fun RgEditDialog(
    rg: RgWithCounts?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, (String) -> Unit) -> Unit
) {
    var codigo by remember { mutableStateOf(rg?.codigo ?: "") }
    var municipio by remember { mutableStateOf(rg?.municipio ?: "") }
    var descricao by remember { mutableStateOf(rg?.descricao ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (rg == null) "Novo RG" else "Editar RG ${rg.codigo}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Código do RG * (Texto - Ex: 01, 02)") },
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = {
                        Text(errorText ?: "O código é tratado como texto e deve ser único.")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rg_codigo_input")
                )
                OutlinedTextField(
                    value = municipio,
                    onValueChange = { municipio = it },
                    label = { Text("Município / Região") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rg_municipio_input")
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição / Observações") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rg_descricao_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (codigo.isBlank()) {
                        errorText = "O código do RG é obrigatório"
                    } else {
                        onSave(codigo, municipio, descricao) { err ->
                            errorText = err
                        }
                    }
                },
                modifier = Modifier.testTag("rg_dialog_save_button")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("rg_dialog_cancel_button")
            ) {
                Text("Cancelar")
            }
        },
        modifier = Modifier.testTag("rg_edit_dialog")
    )
}
