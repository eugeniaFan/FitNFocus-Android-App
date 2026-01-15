package com.example.fitnfocus.ui.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FnfCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(FnfDefaults.cardPadding),
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {

        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}