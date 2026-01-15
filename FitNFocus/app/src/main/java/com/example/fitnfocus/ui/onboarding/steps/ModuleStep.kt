package com.example.fitnfocus.ui.onboarding.steps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Step 2: Modul und Lernziel eingeben
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModuleStep(
    moduleName: String,
    topics: List<String>,
    currentTopic: String,
    examDate: LocalDate?,
    onModuleNameChanged: (String) -> Unit,
    onCurrentTopicChanged: (String) -> Unit,
    onAddTopic: () -> Unit,
    onRemoveTopic: (String) -> Unit,
    onExamDateChanged: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Dein erstes Lernziel",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Du kannst später weitere Lernziele hinzufügen.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Modulname
        OutlinedTextField(
            value = moduleName,
            onValueChange = onModuleNameChanged,
            label = { Text("Modulname / Fach") },
            placeholder = { Text("z.B. Mobile App Development") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Themen-Eingabe
        Text(text = "Themen (optional)", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentTopic,
                onValueChange = onCurrentTopicChanged,
                placeholder = { Text("Thema hinzufügen") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddTopic() })
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onAddTopic, enabled = currentTopic.isNotBlank()) {
                Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
            }
        }

        // Topic Chips
        if (topics.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            topics.forEach { topic ->
                InputChip(
                    selected = false,
                    onClick = { },
                    label = { Text(topic) },
                    trailingIcon = {
                        IconButton(
                            onClick = { onRemoveTopic(topic) },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Entfernen", modifier = Modifier.size(14.dp))
                        }
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prüfungstermin
        Text(text = "Prüfungstermin (optional)", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        val germanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        OutlinedTextField(
            value = examDate?.format(germanDateFormatter) ?: "",
            onValueChange = { },
            label = { Text("Datum wählen") },
            placeholder = { Text("TT.MM.JJJJ") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Datum wählen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDatePicker) {
        ModuleDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                onExamDateChanged(date)
                showDatePicker = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
