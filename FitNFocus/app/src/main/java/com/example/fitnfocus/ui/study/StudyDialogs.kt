package com.example.fitnfocus.ui.study

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.viewmodel.TopicItem

/**
 * Dialog zum Hinzufügen einer neuen Focus Session.
 * Ermöglicht:
 * - Auswahl eines Topics aus bestehenden Lernzielen
 * - Manuelles Eingeben eines neuen Topics
 * - Optional: Neues Topic zu einem Lernziel hinzufügen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionDialog(
    subject: String,
    duration: String,
    isLoading: Boolean,
    availableTopics: List<TopicItem>,
    learningGoals: List<LearningGoal>,
    selectedTopic: TopicItem?,
    isManualInput: Boolean,
    onSubjectChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onTopicSelected: (TopicItem) -> Unit,
    onManualInputToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: (addToCalendar: Boolean, goalIdForNewTopic: Int?) -> Unit
) {
    var addToCalendar by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedGoalForNewTopic by remember { mutableStateOf<LearningGoal?>(null) }
    var addTopicToGoal by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Focus Session erstellen") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Segmented Button: Topic auswählen oder manuell eingeben
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !isManualInput,
                        onClick = { onManualInputToggle(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        icon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                    ) {
                        Text("Auswählen")
                    }
                    SegmentedButton(
                        selected = isManualInput,
                        onClick = { onManualInputToggle(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    ) {
                        Text("Manuell")
                    }
                }

                // Topic-Auswahl oder manuelles Textfeld
                if (isManualInput) {
                    // Manuelles Eingabefeld
                    OutlinedTextField(
                        value = subject,
                        onValueChange = onSubjectChange,
                        label = { Text("Thema eingeben") },
                        placeholder = { Text("z.B. Jetpack Compose") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Option: Zu Lernziel hinzufügen
                    if (learningGoals.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { addTopicToGoal = !addTopicToGoal },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = addTopicToGoal,
                                onCheckedChange = { addTopicToGoal = it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Thema zu Lernziel hinzufügen",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Dropdown für Lernziel-Auswahl
                        if (addTopicToGoal) {
                            GoalSelector(
                                goals = learningGoals,
                                selectedGoal = selectedGoalForNewTopic,
                                onGoalSelected = { selectedGoalForNewTopic = it }
                            )
                        }
                    }
                } else {
                    // Dropdown für Topic-Auswahl
                    if (availableTopics.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "Keine Themen verfügbar.\nErstelle zuerst ein Lernziel im Onboarding oder wechsle zu 'Manuell'.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        TopicDropdown(
                            topics = availableTopics,
                            selectedTopic = selectedTopic,
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            onTopicSelected = {
                                onTopicSelected(it)
                                expanded = false
                            }
                        )
                    }
                }

                // Dauer-Eingabe
                OutlinedTextField(
                    value = duration,
                    onValueChange = onDurationChange,
                    label = { Text("Dauer (Minuten)") },
                    placeholder = { Text("z.B. 45") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Kalender-Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addToCalendar = !addToCalendar },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = addToCalendar,
                        onCheckedChange = { addToCalendar = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("In Kalender eintragen")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goalId = if (addTopicToGoal) selectedGoalForNewTopic?.id else null
                    onSave(addToCalendar, goalId)
                },
                enabled = !isLoading && (subject.isNotBlank() || selectedTopic != null)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}

/**
 * Dropdown für Topic-Auswahl aus Lernzielen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicDropdown(
    topics: List<TopicItem>,
    selectedTopic: TopicItem?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTopicSelected: (TopicItem) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedTopic?.let {
                if (it.goalName != null) "${it.name} (${it.goalName})"
                else it.name
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Thema auswählen") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            // Gruppiere nach Modul
            val groupedTopics = topics.groupBy { it.goalName ?: it.name }

            groupedTopics.forEach { (moduleName, moduleTopics) ->
                // Modul-Header
                if (moduleTopics.size > 1 || moduleTopics.first().goalName != null) {
                    Text(
                        text = moduleName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                moduleTopics.forEach { topic ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (topic.goalName != null) "  ${topic.name}" else topic.name
                            )
                        },
                        onClick = { onTopicSelected(topic) }
                    )
                }
            }
        }
    }
}

/**
 * Selector für Lernziel (wenn neues Topic hinzugefügt werden soll).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalSelector(
    goals: List<LearningGoal>,
    selectedGoal: LearningGoal?,
    onGoalSelected: (LearningGoal) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedGoal?.moduleName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Zu welchem Modul?") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            goals.forEach { goal ->
                DropdownMenuItem(
                    text = { Text(goal.moduleName) },
                    onClick = {
                        onGoalSelected(goal)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Dialog zum Bearbeiten einer bestehenden Session.
 */
@Composable
fun EditSessionDialog(
    session: StudySession,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (StudySession) -> Unit,
    onDelete: (StudySession) -> Unit
) {
    var editedTopic by remember(session.id) { mutableStateOf(session.topic) }
    var editedDuration by remember(session.id) { mutableStateOf(session.durationMinutes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Session bearbeiten") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = editedTopic,
                    onValueChange = { editedTopic = it },
                    label = { Text("Thema") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedDuration,
                    onValueChange = { editedDuration = it },
                    label = { Text("Dauer (Minuten)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = {
                    val minutes = editedDuration.toIntOrNull()
                    if (minutes != null && minutes > 0) {
                        onUpdate(
                            session.copy(
                                topic = editedTopic.trim(),
                                durationMinutes = minutes
                            )
                        )
                    }
                }
            ) { Text("Speichern") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    enabled = !isLoading,
                    onClick = { onDelete(session) }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Löschen")
                }
            }
        }
    )
}