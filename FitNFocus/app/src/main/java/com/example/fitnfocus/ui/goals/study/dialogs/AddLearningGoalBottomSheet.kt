package com.example.fitnfocus.ui.goals.study.dialogs

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * BottomSheet zum Hinzufügen eines neuen Lernziels (ähnlich wie Onboarding: Modul + Themen + Prüfungsdatum).
 */

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun AddLearningGoalBottomSheet(
    moduleName: String,
    examDateText: String,
    topics: List<String>,
    currentTopic: String,
    isSaving: Boolean,
    onModuleNameChange: (String) -> Unit,
    onExamDateTextChange: (String) -> Unit,
    onCurrentTopicChange: (String) -> Unit,
    onAddTopic: () -> Unit,
    onRemoveTopic: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showDatePicker = remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Neues Lernziel",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Schließen")
                }
            }

            OutlinedTextField(
                value = moduleName,
                onValueChange = onModuleNameChange,
                label = { Text("Modul / Fach") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = examDateText,
                onValueChange = onExamDateTextChange,
                label = { Text("Prüfungsdatum") },
                placeholder = { Text("TT.MM.JJJJ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                showDatePicker.value = true
                            }
                        }
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Datum wählen")
                    }
                },
                supportingText = {
                    Text(
                        text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            "Optional. Du kannst tippen oder über das Kalender-Icon wählen."
                        } else {
                            "Optional. Datumsauswahl benötigt Android 8.0+ (API 26)."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Themen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = currentTopic,
                onValueChange = onCurrentTopicChange,
                label = { Text("Neues Thema") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddTopic() })
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onAddTopic, enabled = currentTopic.trim().isNotEmpty()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.height(0.dp))
                    Text("Thema hinzufügen")
                }
            }

            if (topics.isEmpty()) {
                Text(
                    text = "Noch keine Themen hinzugefügt.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                topics.forEach { topic ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "• $topic")
                        TextButton(onClick = { onRemoveTopic(topic) }) {
                            Text("Entfernen")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = moduleName.trim().isNotEmpty() && !isSaving
            ) {
                Text(if (isSaving) "Speichere…" else "Speichern")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showDatePicker.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formatted = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            onExamDateTextChange(formatted)
                        }
                        showDatePicker.value = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) { Text("Abbrechen") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

