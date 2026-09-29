package com.example.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.dao.GlobalTerritoryStats
import com.example.ui.components.StatCard
import com.example.ui.navigation.NavigationMode
import com.example.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    stats: GlobalTerritoryStats,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho Institucional
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "203 RG Mobile",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Controle de Reconhecimento Geográfico",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Logo 203 RG",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Indicador de modo offline nativo
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OfflinePin,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Operação 100% Offline • Armazenamento Local",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        // Título da Seção de Resumo
        item {
            Text(
                text = "Resumo Territorial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        // Grid 2x2 com os 4 Cards de Resumo Solicitados
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "RGs Cadastrados",
                        value = stats.totalRgs.toString(),
                        icon = Icons.Default.Domain,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_card_rgs"
                    )
                    StatCard(
                        title = "Quarteirões",
                        value = stats.totalQuarteiroes.toString(),
                        icon = Icons.Default.GridOn,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_card_quarteiroes"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Imóveis",
                        value = stats.totalImoveis.toString(),
                        icon = Icons.Default.Home,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_card_imoveis"
                    )
                    StatCard(
                        title = "Habitantes",
                        value = stats.totalHabitantes.toString(),
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_card_habitantes"
                    )
                }
            }
        }

        // Título da Seção de Acessos Principais
        item {
            Text(
                text = "Principais Acessos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        // Botões de Acesso Principal
        item {
            HomeMenuActionCard(
                title = "Coleta de Campo",
                subtitle = "Acessar RG, bairro/localidade, quarteirão e imóveis para trabalho diário",
                icon = Icons.Default.Map,
                iconBackgroundColor = MaterialTheme.colorScheme.primary,
                testTag = "menu_coleta_campo",
                onClick = { onNavigate(Screen.RgList(NavigationMode.FIELD_WORK)) }
            )
        }

        item {
            HomeMenuActionCard(
                title = "Cadastros",
                subtitle = "Administrar a estrutura territorial (RGs, localidades e quarteirões)",
                icon = Icons.Default.LocationCity,
                iconBackgroundColor = MaterialTheme.colorScheme.secondary,
                testTag = "menu_cadastros",
                onClick = { onNavigate(Screen.RgList(NavigationMode.MANAGEMENT)) }
            )
        }

        item {
            HomeMenuActionCard(
                title = "Sincronização",
                subtitle = if (stats.totalPendentes > 0)
                    "${stats.totalPendentes} registro(s) para futura exportação ao 203 RG Desktop"
                else
                    "Preparado para integração com 203 RG Desktop",
                icon = Icons.Default.CloudSync,
                iconBackgroundColor = if (stats.totalPendentes > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.primary,
                badgeText = if (stats.totalPendentes > 0) "${stats.totalPendentes} pendentes" else null,
                testTag = "menu_sincronizacao",
                onClick = { onNavigate(Screen.Sync) }
            )
        }

        item {
            HomeMenuActionCard(
                title = "Configurações",
                subtitle = "Informações do aplicativo, dados locais e preferências",
                icon = Icons.Default.Settings,
                iconBackgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                testTag = "menu_configuracoes",
                onClick = { onNavigate(Screen.Settings) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeMenuActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    badgeText: String? = null,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = iconBackgroundColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBackgroundColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (badgeText != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
