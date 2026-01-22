package com.example.fitnfocus.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.domain.MotivationType
import com.example.fitnfocus.domain.UserRole
import com.example.fitnfocus.viewmodel.ProfileViewModel

/**
 * Profil-Screen zeigt die gespeicherten User-Daten aus dem Onboarding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val user by viewModel.user.collectAsState()
    val learningGoals by viewModel.learningGoals.collectAsState()

    // Dialog-State für Reset-Bestätigung (DEBUG)
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mein Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ----- PROFIL HEADER -----
            ProfileHeader(
                role = user.role,
                motivationType = user.personalityProfile.motivationType
            )

            // ----- PERSÖNLICHKEITSPROFIL -----
            ProfileSection(title = "Persönlichkeitsprofil") {
                PersonalityProfileCard(
                    planningPreference = user.personalityProfile.planningPreference,
                    socialPreference = user.personalityProfile.socialPreference,
                    structurePreference = user.personalityProfile.structurePreference
                )
            }

            // ----- MOTIVATION -----
            ProfileSection(title = "Motivation") {
                MotivationCard(motivationType = user.personalityProfile.motivationType)
            }

            // ----- LERNZIELE ÜBERSICHT -----
            if (learningGoals.isNotEmpty()) {
                ProfileSection(title = "Lernziele (${learningGoals.size})") {
                    learningGoals.forEach { goal ->
                        LearningGoalItem(
                            moduleName = goal.moduleName,
                            topicCount = goal.topics.size,
                            examDate = goal.examDate?.toString()
                        )
                    }
                }
            }

            // ----- PROFIL ZURÜCKSETZEN -----
            ProfileSection(title = "Einstellungen") {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Profil zurücksetzen",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Alle Daten löschen und neu starten",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Zurücksetzen")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Reset-Bestätigungs-Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Profil zurücksetzen?") },
            text = {
                Text(
                    "Folgende Daten werden unwiderruflich gelöscht:\n\n" +
                    "• Alle Lernziele und Themen\n" +
                    "• Alle Sessions und Fortschritte\n" +
                    "• Dein Persönlichkeitsprofil\n\n" +
                    "Das Onboarding wird beim nächsten App-Start erneut angezeigt."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetProfile()
                        showResetDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Alles löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

/**
 * Profil-Header mit Avatar und Grundinfos.
 */
@Composable
private fun ProfileHeader(
    role: UserRole,
    motivationType: MotivationType
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rolle
        Text(
            text = role.displayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Motivation Typ
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = motivationType.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Sektion mit Titel.
 */
@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

/**
 * Karte für Persönlichkeitsprofil mit Slidern.
 */
@Composable
private fun PersonalityProfileCard(
    planningPreference: Int,
    socialPreference: Int,
    structurePreference: Int
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PreferenceBar(
                label = "Arbeitsweise",
                leftLabel = "Spontan",
                rightLabel = "Geplant",
                value = planningPreference
            )

            PreferenceBar(
                label = "Zusammenarbeit",
                leftLabel = "Alleine",
                rightLabel = "Im Team",
                value = socialPreference
            )

            PreferenceBar(
                label = "Tagesablauf",
                leftLabel = "Flexibel",
                rightLabel = "Strukturiert",
                value = structurePreference
            )
        }
    }
}

/**
 * Einzelne Präferenz-Anzeige mit Balken.
 */
@Composable
private fun PreferenceBar(
    label: String,
    leftLabel: String,
    rightLabel: String,
    value: Int
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = rightLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * Karte für Motivationstyp.
 */
@Composable
private fun MotivationCard(motivationType: MotivationType) {
    val icon: ImageVector = when (motivationType) {
        MotivationType.STRUCTURED_PROGRESS -> Icons.Default.BarChart
        MotivationType.SELF_IMPROVEMENT -> Icons.Default.Lightbulb
        MotivationType.ADVENTURE -> Icons.Default.SportsEsports
        MotivationType.EMOTIONAL_WELLNESS -> Icons.Default.Favorite
        MotivationType.SOCIAL -> Icons.Default.Groups
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = motivationType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = motivationType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Kompakte Anzeige eines Lernziels.
 */
@Composable
private fun LearningGoalItem(
    moduleName: String,
    topicCount: Int,
    examDate: String?
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = moduleName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$topicCount Themen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    examDate?.let {
                        Text(
                            text = "• Prüfung: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

